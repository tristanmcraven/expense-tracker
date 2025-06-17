package com.tristanmcraven.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "user_secondary_currencies",
    primaryKeys = ["secondary_currency_id"],
    foreignKeys = [
        ForeignKey(
            entity = Currency::class,
            parentColumns = ["id"],
            childColumns = ["secondary_currency_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserSecondaryCurrencies(
    @ColumnInfo(name = "secondary_currency_id") val secondaryCurrencyId: Int
)
