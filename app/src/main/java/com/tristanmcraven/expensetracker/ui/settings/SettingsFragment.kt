package com.tristanmcraven.expensetracker.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tristanmcraven.expensetracker.R
import com.tristanmcraven.expensetracker.databinding.FragmentHomeBinding
import com.tristanmcraven.expensetracker.ui.home.HomeViewModel

class SettingsFragment  : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        return root
    }
}