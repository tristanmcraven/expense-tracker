package com.tristanmcraven.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class AddTransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val _transactionTypes = listOf("Income", "Expense")
    val transactionTypes get() = _transactionTypes

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
            _amount.value = conversion!!
        }
    }

    private val _description = MutableLiveData<String>()
    val description = _description

    fun setDescription(value: String) {
        _description.value = value
    }
}