package com.tristanmcraven.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class Currency(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "symbol") val symbol: String,
    @ColumnInfo(name = "drawable_id") val drawableId: Int,
    @ColumnInfo(name = "usd_value") val usdValue: Double
)
