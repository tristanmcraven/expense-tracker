package com.tristanmcraven.expensetracker.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tristanmcraven.expensetracker.model.Transaction
import com.tristanmcraven.expensetracker.model.UserAccounts
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountsDao {
    @Query("SELECT * FROM user_accounts")
    fun get(): Flow<List<UserAccounts>>

    @Query("SELECT * FROM user_accounts WHERE account_id = :accountId")
    fun getById(accountId: Int): Flow<UserAccounts>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(userAccounts: UserAccounts)

    @Update
    suspend fun update(userAccounts: UserAccounts)

    @Delete
    suspend fun delete(userAccounts: UserAccounts)
}