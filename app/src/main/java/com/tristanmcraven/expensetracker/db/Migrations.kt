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