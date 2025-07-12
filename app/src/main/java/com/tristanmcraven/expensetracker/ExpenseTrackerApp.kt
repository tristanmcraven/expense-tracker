package com.tristanmcraven.expensetracker

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.tristanmcraven.expensetracker.db.AppDb
import com.tristanmcraven.expensetracker.db.MIGRATION_3_4
import com.tristanmcraven.expensetracker.utility.GenericHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

        CoroutineScope(Dispatchers.IO).launch {
            val settingsList = db.settingsDao().get().first()
            val settings = settingsList.firstOrNull()

            if (settings != null) {
                GenericHelper.MainCurrencySymbol = db.currencyDao().getById(
                    db.settingsDao().getInstance().first().primaryCurrencyId
                ).first().symbol
            }
        }

        val dbFile = applicationContext.getDatabasePath("expense_tracker.db")
        Log.d("DB_CHECK", "\n\n\nEXISTS? ${dbFile.exists()} at ${dbFile.absolutePath}\n\n\n")
    }
}