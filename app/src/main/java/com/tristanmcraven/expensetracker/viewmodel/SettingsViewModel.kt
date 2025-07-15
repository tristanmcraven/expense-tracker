package com.tristanmcraven.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tristanmcraven.expensetracker.ExpenseTrackerApp
import com.tristanmcraven.expensetracker.utility.GenericHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDao = (application as ExpenseTrackerApp).db.settingsDao()
    private val _settingsFlow = settingsDao.getInstance()

    val isFingerprintRequired: StateFlow<Boolean> = _settingsFlow
        .map { it.fingerprintRequired }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun setFingerprintRequired(value: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        settingsDao.setFingerprintRequired(value)
    }

    fun setGroupedSumColor(value: String) = viewModelScope.launch(Dispatchers.IO) {
        settingsDao.setGroupedSumColor(value)
        GenericHelper.GroupedSumColor = settingsDao.getGroupedSumColor()
    }
}