package com.tristanmcraven.expensetracker

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.tristanmcraven.expensetracker.db.AppDb
import com.tristanmcraven.expensetracker.db.MIGRATION_3_4
import kotlinx.coroutines.flow.first

class ExpenseTrackerApp : Application() {

    lateinit var db: AppDb
        private set

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(
                applicationContext,
                AppDb::class.java,
                "expense_tracker.db"
            )
            .addMigrations(MIGRATION_3_4)
            .build()

        db.openHelper.writableDatabase

        val dbFile = applicationContext.getDatabasePath("expense_tracker.db")
        Log.d("DB_CHECK", "\n\n\nEXISTS? ${dbFile.exists()} at ${dbFile.absolutePath}\n\n\n")
    }
}