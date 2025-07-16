package com.tristanmcraven.expensetracker.viewmodel

import android.app.Application
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tristanmcraven.expensetracker.ExpenseTrackerApp
import com.tristanmcraven.expensetracker.model.Transaction
import com.tristanmcraven.expensetracker.utility.GroupBy
import com.tristanmcraven.expensetracker.utility.toGroupBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as ExpenseTrackerApp).db

    private val transactionDao = db.transactionDao()
    private val profileInfoDao = db.profileInfoDao()

    private val _groupBy = MutableStateFlow(GroupBy.ALL)
    val groupBy: StateFlow<GroupBy> = _groupBy.asStateFlow() // required: StateFlow<GroupBy> | found: StateFlow<Job>

    init {
        viewModelScope.launch {
            val savedGroupBy = db.settingsDao().getGroupByValue()
            _groupBy.value = GroupBy.values()[savedGroupBy]
        }
    }

    fun setGrouping(value: GroupBy) {
        _groupBy.value = value
        viewModelScope.launch {
            db.settingsDao().setGroupByValue(value.ordinal)
        }
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