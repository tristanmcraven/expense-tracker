package com.tristanmcraven.expensetracker.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tristanmcraven.expensetracker.model.Account
import com.tristanmcraven.expensetracker.model.Settings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings")
    fun get(): Flow<List<Settings>>

    @Query("SELECT * FROM settings WHERE id = :id")
    fun getById(id: Int): Flow<Settings>

    @Insert
    suspend fun insert(settings: Settings)

    @Update
    suspend fun update(settings: Settings)

    @Delete
    suspend fun delete(settings: Settings)

    @Query("UPDATE settings SET primary_currency_id = :currencyId WHERE id = 1")
    suspend fun setPrimaryCurrency(currencyId: Int)

    @Query("SELECT first_launch FROM settings WHERE id = 1")
    suspend fun getFirstLaunch(): Boolean?
}