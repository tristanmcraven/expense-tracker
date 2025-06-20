package com.tristanmcraven.expensetracker.ie.importer.my_tab

data class MyTabImportFile(
    val version: String,
    val expenses: List<MyTabImportExpense>,
    val accounts: List<Any>?,
    val categories: List<Any>?
)
