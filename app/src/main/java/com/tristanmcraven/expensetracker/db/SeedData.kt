package com.tristanmcraven.expensetracker.db

import com.tristanmcraven.expensetracker.R
import com.tristanmcraven.expensetracker.model.Account
import com.tristanmcraven.expensetracker.model.Currency
import com.tristanmcraven.expensetracker.model.TransactionType

object SeedData {
    val currencies = listOf(
        Currency(1, "Russian Ruble", "RUB", "₽", R.drawable.ruble, 0.013),
        Currency(2, "US Dollar", "USD", "＄", R.drawable.usd, 1.00),
        Currency(3, "Euro", "EUR", "€", R.drawable.euro, 1.16)
    )

    val accounts = listOf(
        Account(1, "Cash",   R.drawable.cash),
        Account(2, "Card",   R.drawable.card),
        Account(3, "Crypto", R.drawable.crypto)
    )

    val transactionTypes = listOf(
        TransactionType(1, "Income"),
        TransactionType(2, "Expense")
    )
}