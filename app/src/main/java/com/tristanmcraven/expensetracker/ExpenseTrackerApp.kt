package com.tristanmcraven.expensetracker

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.tristanmcraven.expensetracker.db.AppDb
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
            ).fallbackToDestructiveMigration(false).build()

        db.openHelper.writableDatabase

        val dbFile = applicationContext.getDatabasePath("expense_tracker.db")
        Log.d("DB_CHECK", "\n\n\nEXISTS? ${dbFile.exists()} at ${dbFile.absolutePath}\n\n\n")
    }
}