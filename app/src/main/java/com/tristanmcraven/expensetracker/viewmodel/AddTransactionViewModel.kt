package com.tristanmcraven.expensetracker.viewmodel

import android.app.Application
import android.icu.util.Calendar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tristanmcraven.expensetracker.ExpenseTrackerApp
import com.tristanmcraven.expensetracker.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as ExpenseTrackerApp).db
    val transactionDao = db.transactionDao()

    private val _transactionTypes = (application as ExpenseTrackerApp).db.transactionTypeDao()
    val transactionTypes get() = _transactionTypes.get()
    private val _selectedTransactionTypeId = MutableLiveData<Int>()
    val selectedTransactionTypeId get() = _selectedTransactionTypeId

    fun setSelectedTransactionTypeId(value: Int) {
        _selectedTransactionTypeId.value = value
    }

    private val userAccountsDao = (application as ExpenseTrackerApp).db.userAccountsDao()
    val userAccounts get() = userAccountsDao.get()
    private val _selectedAccountId = MutableLiveData<Int>()
    val selectedAccountId = _selectedAccountId

    fun setSelectedAccountId(value: Int) {
        _selectedAccountId.value = value
    }

    private val _name = MutableLiveData<String>()
    val name get() = _name

    fun setName(value: String) {
        _name.value = value
    }

    private val _amount = MutableLiveData<Double>()
    val amount get() = _amount

    fun setAmount(value: String) {
        val conversion = value.toDoubleOrNull()
        conversion?.let {
            _amount.value = it
        }
    }

    private val _description = MutableLiveData<String>()
    val description get() = _description

    fun setDescription(value: String) {
        _description.value = value
    }

    private val now = Calendar.getInstance().timeInMillis

    private val _date = MutableLiveData<Long>(now)
    val date get() = _date
    private val _dateAsString = MutableLiveData<String>("Select")
    val dateAsString get() = _dateAsString

    fun setDate(value: Long) {
        _date.value = value
        _dateAsString.value = SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(Date(value))
    }

    private val _time = MutableLiveData<Long>(now)
    val time get() = _time
    private val _timeAsString = MutableLiveData<String>("Select")
    val timeAsString get() = _timeAsString

    fun setTime(value: Long) {
        _time.value = value
        _timeAsString.value = SimpleDateFormat("HH:mm", Locale.GERMANY).format(_time.value)
    }

    val timestamp get() = combineDateTime(_date.value, _time.value)
    private fun combineDateTime(dateMillis: Long?, timeMillis: Long?): Long? {
        if (dateMillis == null || timeMillis == null) return null

        val date = Calendar.getInstance().apply { timeInMillis = dateMillis }
        val time = Calendar.getInstance().apply { timeInMillis = timeMillis }

        val merged = Calendar.getInstance().apply {
            set(Calendar.YEAR, date.get(Calendar.YEAR))
            set(Calendar.MONTH, date.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, time.get(Calendar.MINUTE))
        }
        return merged.timeInMillis
    }

    /* Editing Logic */

    private val _isEditing = MutableLiveData(false)
    val isEditing: LiveData<Boolean> get() = _isEditing

    private val _transactionId = MutableLiveData<Int?>(null)
    val transactionId: LiveData<Int?> get() = _transactionId

    var loaded = MutableLiveData(false) // check if transaction has loaded

    fun loadTransaction(id: Int) = viewModelScope.launch {
        val tx = withContext(Dispatchers.IO) { transactionDao.getById(id).first() } ?: return@launch

        withContext(Dispatchers.Main) {
            _isEditing.value = true
            _transactionId.value = tx.id

            setName(tx.name ?: "")
            setSelectedTransactionTypeId(if (tx.amount < 0) 2 else 1)
            setAmount(kotlin.math.abs(tx.amount).toString())
            setDescription(tx.description ?: "")
            setDate(tx.timestamp)
            setTime(tx.timestamp)
            setSelectedAccountId(tx.accountId)

            loaded.value = true
        }
    }

    val transactionObject get() =
        Transaction(
            _transactionId.value ?: 0,
            _name.value,
            _amount.value?.let {
                if (_selectedTransactionTypeId.value == 2) -kotlin.math.abs(it) else it
            } ?: if (_selectedTransactionTypeId.value == 2) -100.0 else 100.0,
            _description.value,
            timestamp ?: Calendar.getInstance().timeInMillis,
            _selectedAccountId.value!!
        )
}