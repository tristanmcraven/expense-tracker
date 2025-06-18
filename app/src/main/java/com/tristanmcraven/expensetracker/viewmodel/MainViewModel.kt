package com.tristanmcraven.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.tristanmcraven.expensetracker.ExpenseTrackerApp
import com.tristanmcraven.expensetracker.model.Transaction
import kotlinx.coroutines.flow.Flow

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionDao = (application as ExpenseTrackerApp).db.transactionDao()

    val transactionFlow: Flow<List<Transaction>> = transactionDao.getDescending()
}