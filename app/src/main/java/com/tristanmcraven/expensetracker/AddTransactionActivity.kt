package com.tristanmcraven.expensetracker

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.transition.Visibility
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tristanmcraven.expensetracker.databinding.ActivityAddTransactionBinding
import com.tristanmcraven.expensetracker.model.Account
import com.tristanmcraven.expensetracker.model.UserAccounts
import com.tristanmcraven.expensetracker.utility.UiUtility
import com.tristanmcraven.expensetracker.viewmodel.AddTransactionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTransactionActivity : AppCompatActivity() {

    private var _binding: ActivityAddTransactionBinding? = null
    private val binding get() = _binding!!

    private val vm: AddTransactionViewModel by viewModels()

    private lateinit var userAccounts: List<UserAccounts>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val transactionId = intent.getIntExtra("TX_ID", -666)
        if (transactionId != -666) {
            vm.loadTransaction(transactionId)
            vm.loaded.observe(this) {
                if (it) {
                    initAsEditing()
                    initViews()
                }
            }
        } else {
            initViews()
        }


    }

    private fun initViews() {
        initAccountChips()
        initTransactionTypeChips()
        initDateTimePickers()
        initNotifications()
        initClickListeners()
    }

    private fun initAsEditing() {
        binding.buttonDelete.visibility = View.VISIBLE
        binding.buttonAdd.text = "Update transaction"
        binding.textViewAddTransaction.text = "Update Existing Transaction"
        binding.editTextTransactionName.setText(vm.name.value)
        binding.editTextTransactionAmount.setText(kotlin.math.abs(vm.amount.value ?: 100.0).toString())
        binding.editTextTransactionDescription.setText(vm.description.value)
        vm.dateAsString.observe(this@AddTransactionActivity) { binding.buttonSelectDate.text = it }
        vm.timeAsString.observe(this@AddTransactionActivity) { binding.buttonSelectTime.text = it }
    }

    private fun initClickListeners() {
        binding.buttonAdd.setOnClickListener {
            lifecycleScope.launch {
                val transaction = vm.transactionObject
                if (vm.isEditing.value == true) {
                    (application as ExpenseTrackerApp).db.transactionDao().update(transaction)
                    Toast.makeText(this@AddTransactionActivity, "Updated", Toast.LENGTH_SHORT).show()
                } else {
                    (application as ExpenseTrackerApp).db.transactionDao().insert(transaction)
                    Toast.makeText(this@AddTransactionActivity, "Added", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        }
        binding.buttonDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.confirmation)
                .setMessage(R.string.delete_confirmation)
                .setPositiveButton(R.string.yes) { _, _ ->
                    lifecycleScope.launch {
                        val transaction = vm.transactionObject
                        (application as ExpenseTrackerApp).db.transactionDao().delete(transaction)
                        finish()
                        Toast.makeText(this@AddTransactionActivity, "Deleted", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton(R.string.no) { dialog, id ->
                    dialog.dismiss()
                }.show()
        }
    }

    private fun initNotifications() {
        binding.editTextTransactionName.doAfterTextChanged {
            vm.setName(it.toString().trim())
        }
        binding.editTextTransactionAmount.doAfterTextChanged {
            vm.setAmount(it.toString().trim())
        }
        binding.editTextTransactionDescription.doAfterTextChanged {
            vm.setDescription(it.toString().trim())
        }
    }

    private fun initAccountChips() {
        lifecycleScope.launch {
            userAccounts = vm.userAccounts.first()
            userAccounts.forEach { uAcc ->
                val account = (application as ExpenseTrackerApp).db.accountDao().getById(uAcc.accountId)
                createUserAccountChip(account.first(), binding.chipGroupAccounts)
            }
        }
    }

    private fun initTransactionTypeChips() {
        lifecycleScope.launch {
            vm.transactionTypes.first().forEach { tType ->
                createTransactionTypeChip(tType.name, tType.id, binding.chipGroupTransactionType)
            }
        }
    }

    private fun createUserAccountChip(account: Account, parent: ChipGroup) {
        val chip = layoutInflater.inflate(R.layout.chip_filter, parent, false) as Chip

        chip.apply {
            text = account.name
            chipIcon = ContextCompat.getDrawable(this@AddTransactionActivity, account.drawableId)
            tag = account.id
            isChecked = vm.selectedAccountId.value == tag
            setOnCheckedChangeListener{ _, isChecked ->
                vm.setSelectedAccountId(tag.toString().toInt())
            }
        }
        if (vm.isEditing.value == false && userAccounts.count() == chip.tag.toString().toInt()) {
            chip.performClick()
        }
        parent.addView(chip)
    }

//    private fun createUserAccountChip(displayText: String, id: Int, parent: ChipGroup) {
//        val chip = layoutInflater.inflate(R.layout.chip_filter, parent, false) as Chip
//
//        chip.apply {
//            text = displayText
//            chipIcon = ContextCompat.getDrawable(this@AddTransactionActivity, UiUtility.getChipIcon(displayText))
//            tag = id
//            isChecked = vm.selectedAccountId.value == tag
//            setOnCheckedChangeListener{ _, isChecked ->
//                vm.setSelectedAccountId(tag.toString().toInt())
//            }
//        }
//        if (vm.isEditing.value == false && userAccounts.count() == chip.tag.toString().toInt()) {
//            chip.performClick()
//        }
//        parent.addView(chip)
//    }

    private fun createTransactionTypeChip(displayText: String, id: Int, parent: ChipGroup) {
        val chip = layoutInflater.inflate(R.layout.chip_filter, parent, false) as Chip

        chip.apply {
            text = displayText
            chipIcon = ContextCompat.getDrawable(
                this@AddTransactionActivity, if (id == 1) R.drawable.income else R.drawable.expense)
            tag = id
            isChecked = vm.selectedTransactionTypeId.value == tag
            setOnCheckedChangeListener{ _, isChecked ->
                vm.setSelectedTransactionTypeId(tag.toString().toInt())
            }
        }
        if (vm.isEditing.value == false && chip.tag.toString().toInt() == 1) chip.performClick()

        parent.addView(chip)
    }

    private fun initDateTimePickers() {
        vm.dateAsString.observe(this@AddTransactionActivity) {
            binding.buttonSelectDate.text = it
        }
        binding.buttonSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    vm.setDate(calendar.timeInMillis)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePicker.show()
        }

        vm.timeAsString.observe(this@AddTransactionActivity) {
            binding.buttonSelectTime.text = it
        }
        binding.buttonSelectTime.setOnClickListener {
            val calendar = Calendar.getInstance()

            val timePicker = TimePickerDialog(
                this,
                { _, hours, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hours)
                    calendar.set(Calendar.MINUTE, minute)
                    vm.setTime(calendar.timeInMillis)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )

            timePicker.show()
        }
    }
}