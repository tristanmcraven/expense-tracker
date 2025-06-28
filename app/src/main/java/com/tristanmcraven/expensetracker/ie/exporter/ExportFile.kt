package com.tristanmcraven.expensetracker.ie.exporter

import com.tristanmcraven.expensetracker.model.Transaction

data class ExportFile(
    val version: Int,
    val transactions: List<Transaction>
)
