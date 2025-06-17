package com.tristanmcraven.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    tableName = "user_accounts",
    primaryKeys = ["account_id"],
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = CASCADE
        )
    ]
)
data class UserAccounts(
    @ColumnInfo(name = "account_id") val accountId: Int
)
