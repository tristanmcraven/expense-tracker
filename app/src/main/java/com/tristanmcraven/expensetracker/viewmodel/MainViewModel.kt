package com.tristanmcraven.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.tristanmcraven.expensetracker.ExpenseTrackerApp
import com.tristanmcraven.expensetracker.model.Transaction
import com.tristanmcraven.expensetracker.utility.GroupBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as ExpenseTrackerApp).db

    private val transactionDao = db.transactionDao()
    private val profileInfoDao = db.profileInfoDao()

    private val _groupBy = MutableStateFlow(GroupBy.ALL)
    val groupBy: StateFlow<GroupBy> = _groupBy.asStateFlow()

    fun setGrouping(value: GroupBy) {
        _groupBy.value = value
    }

    val transactionFlow: Flow<List<Transaction>> = _groupBy
        .flatMapLatest { gb ->
            when (gb) {
                GroupBy.ALL -> transactionDao.getDescending()
                GroupBy.DAY -> transactionDao.getDescending()
                GroupBy.WEEK_OF_MONTH -> transactionDao.getDescending()
                GroupBy.WEEK_OF_YEAR -> transactionDao.getDescending()
                GroupBy.MONTH -> transactionDao.getDescending()
                GroupBy.YEAR -> transactionDao.getDescending()
            }
        }

}