package com.tristanmcraven.expensetracker.ie.importer.my_tab

data class MyTabImportExpense(
    val name: String?,
    val currency: Double,
    val time: String,      // ISO8601
    val type: String,
    val accountId: Int,
    val categoryId: Int,
    val superId: Int,
    val description: String?
)
