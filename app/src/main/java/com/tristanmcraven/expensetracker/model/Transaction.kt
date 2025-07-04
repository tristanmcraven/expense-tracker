package com.tristanmcraven.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(
    tableName = "transaction",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "account_id") val accountId: Int
) {
    @get:Ignore
    val date: Date
        get() = Date(timestamp)

    @get:Ignore
    val dateString: String
        get() = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
            .format(date)

    @get:Ignore
    val timeString: String
        get() = SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(date)
}
