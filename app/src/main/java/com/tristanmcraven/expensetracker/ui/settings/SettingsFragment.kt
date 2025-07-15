package com.tristanmcraven.expensetracker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.*
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.gson.Gson
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.tristanmcraven.expensetracker.ExpenseTrackerApp
import com.tristanmcraven.expensetracker.R
import com.tristanmcraven.expensetracker.ie.exporter.ExportFile
import com.tristanmcraven.expensetracker.viewmodel.SettingsViewModel
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

    private val vm : SettingsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findPreference<Preference>("pref_grouped_sum_color")?.setOnPreferenceClickListener {

            val cpd = ColorPickerDialog.Builder(requireContext())
                .setTitle("Choose color")
                .setPreferenceName("GroupedSumColorDialog")
                .setPositiveButton(
                    getString(R.string.select),
                    ColorEnvelopeListener { envelope, fromUser ->
                        Log.d("COLOR", "${envelope.hexCode}")
                    })
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { dialogInterface, i -> dialogInterface.dismiss() }
                .attachAlphaSlideBar(true) // the default value is true.
                .attachBrightnessSlideBar(true) // the default value is true.
                .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
            cpd.show()

            true
        }
        findPreference<Preference>("pref_data_export")?.setOnPreferenceClickListener {
            exportTransactions()
            true
        }
        findPreference<Preference>("pref_data_import")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_SettingsFragment_to_ImportDataFragment)
            true
        }
        val fingerprintRequiredSwitch = findPreference<SwitchPreference>("pref_fingerprint")!!
        viewLifecycleOwner.lifecycleScope.launch {
            vm.isFingerprintRequired.collect { required ->
                fingerprintRequiredSwitch.isChecked = required
            }
        }
        fingerprintRequiredSwitch.setOnPreferenceChangeListener { _, newValue ->
            val biometricManager = BiometricManager.from(requireContext())
            if (biometricManager.canAuthenticate(BIOMETRIC_STRONG) != BIOMETRIC_SUCCESS) {
                Toast.makeText(requireContext(), "Biometrics unavailable.", Toast.LENGTH_SHORT).show()
                return@setOnPreferenceChangeListener false
            }

            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            }
            startActivityForResult(enrollIntent, 200)

            val checked = newValue as Boolean
            vm.setFingerprintRequired(checked)
            return@setOnPreferenceChangeListener true
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
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