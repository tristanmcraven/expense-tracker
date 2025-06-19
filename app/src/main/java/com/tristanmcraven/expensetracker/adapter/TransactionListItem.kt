package com.tristanmcraven.expensetracker.adapter

import com.tristanmcraven.expensetracker.model.Transaction

sealed class TransactionListItem {
    data class Header(val title: String, val sum: Double) : TransactionListItem()
    data class TxItem(val tx: Transaction) : TransactionListItem()
}