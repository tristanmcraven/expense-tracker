package com.tristanmcraven.expensetracker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog
import com.tristanmcraven.expensetracker.databinding.ActivityAddTransactionBinding
import com.tristanmcraven.expensetracker.utility.UiUtility
import com.tristanmcraven.expensetracker.viewmodel.AddTransactionViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {

    private var _binding: ActivityAddTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTransactionViewModel by viewModels()

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

        initViews()
    }

    private fun initViews() {
        initAccountChips()
        initTransactionTypeChips()
        initDateTimePickers()
        initNotifications()
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.buttonAdd.setOnClickListener {
            lifecycleScope.launch {
                val transaction = viewModel.transactionObject
                (application as ExpenseTrackerApp).db.transactionDao().insert(transaction)
                Toast.makeText(this@AddTransactionActivity, "Added", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun initNotifications() {
        binding.editTextTransactionName.doAfterTextChanged {
            viewModel.setName(it.toString().trim())
        }
        binding.editTextTransactionAmount.doAfterTextChanged {
            viewModel.setAmount(it.toString().trim())
        }
        binding.editTextTransactionDescription.doAfterTextChanged {
            viewModel.setDescription(it.toString().trim())
        }
    }

    private fun initAccountChips() {
        lifecycleScope.launch {
            viewModel.userAccounts.first().forEach { uAcc ->
                createUserAccountChip(when (uAcc.accountId) {
                    1 -> "Cash"
                    2 -> "Card"
                    3 -> "Crypto"
                    else -> "Cash"
                }, uAcc.accountId, binding.chipGroupAccounts)
            }
        }
    }

    private fun initTransactionTypeChips() {
        lifecycleScope.launch {
            viewModel.transactionTypes.first().forEach { tType ->
                createTransactionTypeChip(tType.name, tType.id, binding.chipGroupTransactionType)
            }
        }
    }

    private fun createUserAccountChip(displayText: String, id: Int, parent: ChipGroup) {
        val chip = layoutInflater.inflate(R.layout.chip_filter, parent, false) as Chip

        chip.apply {
            text = displayText
            chipIcon = ContextCompat.getDrawable(this@AddTransactionActivity, UiUtility.getChipIcon(displayText))
            tag = id
            isChecked = viewModel.selectedAccountId.value == tag
            setOnCheckedChangeListener{ _, isChecked ->
                viewModel.setSelectedAccountId(tag.toString().toInt())
            }
        }
        chip.performClick()
        parent.addView(chip)
    }

    private fun createTransactionTypeChip(displayText: String, id: Int, parent: ChipGroup) {
        val chip = layoutInflater.inflate(R.layout.chip_filter, parent, false) as Chip

        chip.apply {
            text = displayText
            chipIcon = ContextCompat.getDrawable(this@AddTransactionActivity, UiUtility.getChipIcon(displayText))
            tag = id
            isChecked = viewModel.selectedTransactionTypeId.value == tag
            setOnCheckedChangeListener{ _, isChecked ->
                viewModel.setSelectedTransactionTypeId(tag.toString().toInt())
            }
        }
        if (chip.tag.toString().toInt() == 1) chip.performClick()
        parent.addView(chip)
    }

    private fun initDateTimePickers() {
        viewModel.dateAsString.observe(this@AddTransactionActivity) {
            binding.buttonSelectDate.text = it
        }
        viewModel.timeAsString.observe(this@AddTransactionActivity) {
            binding.buttonSelectTime.text = it
        }
        binding.buttonSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    viewModel.setDate(calendar.timeInMillis)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePicker.show()
        }

        binding.buttonSelectTime.setOnClickListener {
            val calendar = Calendar.getInstance()

            val timePicker = TimePickerDialog(
                this,
                { _, hours, minute ->
                    calendar.set(hours, minute)
                    viewModel.setTime(calendar.timeInMillis)
                },
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                false
            )

            timePicker.show()
        }
    }

}