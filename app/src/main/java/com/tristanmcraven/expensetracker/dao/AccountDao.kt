package com.tristanmcraven.expensetracker.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tristanmcraven.expensetracker.model.Account
import com.tristanmcraven.expensetracker.model.Currency
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM account")
    fun get(): Flow<List<Account>>

    @Query("SELECT * FROM account WHERE id = :id")
    fun getById(id: Int): Flow<Account>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(account: Account)

    @Update
    suspend fun update(account: Account)

    @Delete
    suspend fun delete(account: Account)
}