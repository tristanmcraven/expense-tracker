package com.tristanmcraven.expensetracker.utility

import com.tristanmcraven.expensetracker.R

object UiUtility {
    fun getChipIcon(text: String): Int = when (text) {
        "cash" -> R.drawable.cash
        "card" -> R.drawable.card
        "crypto" -> R.drawable.crypto
        else -> R.drawable.not_found
    }
}