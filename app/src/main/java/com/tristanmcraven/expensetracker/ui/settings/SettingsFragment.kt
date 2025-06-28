package com.tristanmcraven.expensetracker.ui.settings

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.gson.Gson
import com.tristanmcraven.expensetracker.ExpenseTrackerApp
import com.tristanmcraven.expensetracker.R
import com.tristanmcraven.expensetracker.db.AppDb
import com.tristanmcraven.expensetracker.ie.exporter.ExportFile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsFragment : PreferenceFragmentCompat() {

    private val createJsonDump = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { writeJsonDumpToUri(it) }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)

        findPreference<Preference>("pref_data_export")?.setOnPreferenceClickListener {
            exportTransactions()
            true
        }
        findPreference<Preference>("pref_data_import")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_SettingsFragment_to_ImportDataFragment)
            true
        }
    }

    private fun exportTransactions() {
        lifecycleScope.launch {
            val db = (requireContext().applicationContext as ExpenseTrackerApp).db

            val sqliteDb = db.openHelper.writableDatabase
            val schemaVersion = sqliteDb.version

            val export = ExportFile(
                version = schemaVersion,
                transactions = db.transactionDao().get().first()
            )

            val json = Gson().toJson(export)

            val timestamp = SimpleDateFormat("yyyy-MM-dd.HH:mm:ss", Locale.getDefault())
                .format(Date())
            val fileName = "dump_$timestamp.json"

            createJsonDump.launch(fileName)

            pendingJson = json
        }
    }

    private var pendingJson: String? = null

    private fun writeJsonDumpToUri(uri: Uri) {
        pendingJson?.let { text ->
            try {
                requireContext().contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(text.toByteArray())
                    Toast.makeText(requireContext(),
                        "Saved at: ${uri.lastPathSegment}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("FAIL", "saving dump", e)
                Toast.makeText(requireContext(),
                    "Export failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}