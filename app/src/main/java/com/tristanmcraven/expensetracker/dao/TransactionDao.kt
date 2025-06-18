package com.tristanmcraven.expensetracker.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tristanmcraven.expensetracker.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM `transaction`")
    fun get(): Flow<List<Transaction>>

    @Query("SELECT * FROM `transaction` ORDER BY timestamp DESC")
    fun getDescending(): Flow<List<Transaction>>

    @Query("SELECT * FROM `transaction` WHERE id = :id")
    fun getById(id: Int): Flow<Transaction>

    @Insert
    suspend fun insert(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)
}