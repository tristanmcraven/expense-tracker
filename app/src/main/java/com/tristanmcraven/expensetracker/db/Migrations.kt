package com.tristanmcraven.expensetracker.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
                ALTER TABLE settings
                ADD COLUMN fingerprint_required INTEGER NOT NULL DEFAULT 0
            """.trimIndent())
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            ALTER TABLE settings
            ADD COLUMN grouped_sum_color TEXT NOT NULL DEFAULT "default"
        """.trimIndent())
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            ALTER TABLE settings
            ADD COLUMN group_by_value INTEGER NOT NULL DEFAULT 1
        """.trimIndent())
    }
}