package com.tristanmcraven.expensetracker.ie.importer.expense_tracker

import com.tristanmcraven.expensetracker.model.Transaction

data class ExpenseTrackerImportFile(
    val version: Int,
    val transactions: List<Transaction>
)
