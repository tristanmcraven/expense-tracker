package com.tristanmcraven.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "settings",
    foreignKeys = [
        ForeignKey(
            entity = Currency::class,
            parentColumns = ["id"],
            childColumns = ["primary_currency_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Settings(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "first_launch") val firstLaunch: Boolean,
    @ColumnInfo(name = "primary_currency_id") val primaryCurrencyId: Int,
    @ColumnInfo(name = "fingerprint_required") val fingerprintRequired: Boolean,
    @ColumnInfo(name = "grouped_sum_color") val groupedSumColor: String,
    @ColumnInfo(name = "group_by_value") val groupByValue: Int,
)
