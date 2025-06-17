package com.tristanmcraven.expensetracker.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tristanmcraven.expensetracker.model.ProfileInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileInfoDao {
    @Query("SELECT * FROM profile_info")
    fun get(): Flow<List<ProfileInfo>>

    @Query("SELECT * FROM profile_info WHERE id = :id")
    fun getById(id: Int): Flow<ProfileInfo>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(profileInfo: ProfileInfo)

    @Update
    suspend fun update(profileInfo: ProfileInfo)

    @Delete
    suspend fun delete(profileInfo: ProfileInfo)
}