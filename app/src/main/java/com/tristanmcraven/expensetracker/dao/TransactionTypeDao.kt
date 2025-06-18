package com.tristanmcraven.expensetracker.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tristanmcraven.expensetracker.model.Transaction
import com.tristanmcraven.expensetracker.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionTypeDao {
    @Query("SELECT * FROM transaction_type")
    fun get(): Flow<List<TransactionType>>

    @Query("SELECT * FROM transaction_type WHERE id = :id")
    fun getById(id: Int): Flow<TransactionType>

    @Insert
    suspend fun insert(transactionType: TransactionType)

    @Update
    suspend fun update(transactionType: TransactionType)

    @Delete
    suspend fun delete(transactionType: TransactionType)
}