package com.tristanmcraven.expensetracker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tristanmcraven.expensetracker.dao.AccountDao
import com.tristanmcraven.expensetracker.dao.CurrencyDao
import com.tristanmcraven.expensetracker.dao.ProfileInfoDao
import com.tristanmcraven.expensetracker.dao.SettingsDao
import com.tristanmcraven.expensetracker.dao.TransactionDao
import com.tristanmcraven.expensetracker.dao.TransactionTypeDao
import com.tristanmcraven.expensetracker.dao.UserAccountsDao
import com.tristanmcraven.expensetracker.dao.UserSecondaryCurrenciesDao
import com.tristanmcraven.expensetracker.model.Account
import com.tristanmcraven.expensetracker.model.Currency
import com.tristanmcraven.expensetracker.model.ProfileInfo
import com.tristanmcraven.expensetracker.model.Settings
import com.tristanmcraven.expensetracker.model.Transaction
import com.tristanmcraven.expensetracker.model.TransactionType
import com.tristanmcraven.expensetracker.model.UserAccounts
import com.tristanmcraven.expensetracker.model.UserSecondaryCurrencies

@Database(
    entities = [
       Account::class,
       Currency::class,
       ProfileInfo::class,
       Settings::class,
       Transaction::class,
       TransactionType::class,
       UserAccounts::class,
       UserSecondaryCurrencies::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun currencyDao(): CurrencyDao
    abstract fun profileInfoDao(): ProfileInfoDao
    abstract fun settingsDao(): SettingsDao
    abstract fun transactionDao(): TransactionDao
    abstract fun transactionTypeDao(): TransactionTypeDao
    abstract fun userAccountsDao(): UserAccountsDao
    abstract fun userSecondaryCurrenciesDao(): UserSecondaryCurrenciesDao
}