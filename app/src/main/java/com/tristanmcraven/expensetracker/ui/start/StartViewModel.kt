package com.tristanmcraven.expensetracker.ui.start

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StartViewModel: ViewModel() {
    private val _userFirstName = MutableLiveData<String>()
    val userFirstName: LiveData<String> = _userFirstName

    fun setUserFirstName(name: String) {
        _userFirstName.value = name
    }

    private val _avatarUri = MutableLiveData<Uri?>()
    val avatarUri: LiveData<Uri?> = _avatarUri

    fun setAvatarUri(uri: Uri) {
        _avatarUri.value = uri
    }

    private val _selectedAccounts = MutableStateFlow<List<String>>(emptyList())
    val selectedAccounts: StateFlow<List<String>> = _selectedAccounts

    fun toggleAccount(account: String, isSelected: Boolean) {
        _selectedAccounts.update { current ->
            if (isSelected) current + account
            else current - account
        }
    }

    private val _primaryCurrency = MutableStateFlow<String?>(null)
    val primaryCurrency: StateFlow<String?> = _primaryCurrency.asStateFlow()

    fun setPrimaryCurrency(currency: String) {
        _primaryCurrency.value = currency
    }

    private val _secondaryCurrencies = MutableStateFlow<Set<String>>(emptySet())
    val secondaryCurrencies: StateFlow<Set<String>> = _secondaryCurrencies.asStateFlow()

    fun toggleSecondaryCurrency(currency: String, isSelected: Boolean) {
        _secondaryCurrencies.update { current ->
            if (isSelected) current + currency
            else current - currency
        }
    }
}