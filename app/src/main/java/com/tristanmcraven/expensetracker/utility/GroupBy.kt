package com.tristanmcraven.expensetracker.utility

enum class GroupBy(val displayName: String) {
    ALL("All"),
    DAY("Day"),
    WEEK_OF_MONTH("Week of Month"),
    WEEK_OF_YEAR("Week of Year"),
    MONTH("Month"),
    YEAR("Year")
}