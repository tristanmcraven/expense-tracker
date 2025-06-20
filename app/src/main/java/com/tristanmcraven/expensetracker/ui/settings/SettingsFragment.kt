package com.tristanmcraven.expensetracker.ui.settings

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.tristanmcraven.expensetracker.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)

        findPreference<Preference>("pref_data_import")?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_SettingsFragment_to_ImportDataFragment)
            true
        }
    }
}