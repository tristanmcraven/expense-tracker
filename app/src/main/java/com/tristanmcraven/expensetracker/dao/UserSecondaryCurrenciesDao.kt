package com.tristanmcraven.expensetracker.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tristanmcraven.expensetracker.model.UserSecondaryCurrencies
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSecondaryCurrenciesDao {
    @Query("SELECT * FROM user_secondary_currencies")
    fun get(): Flow<List<UserSecondaryCurrencies>>

    @Query("SELECT * FROM user_secondary_currencies WHERE secondary_currency_id = :secondaryCurrencyId")
    fun getById(secondaryCurrencyId: Int): Flow<UserSecondaryCurrencies>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(userSecondaryCurrencies: UserSecondaryCurrencies)

    @Update
    suspend fun update(userSecondaryCurrencies: UserSecondaryCurrencies)

    @Delete
    suspend fun delete(userSecondaryCurrencies: UserSecondaryCurrencies)
}