package com.tristanmcraven.expensetracker.ui.start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.tristanmcraven.expensetracker.R
import com.tristanmcraven.expensetracker.databinding.FragmentSelectCurrencyBinding
import com.tristanmcraven.expensetracker.utility.UiUtility

class SelectCurrencyFragment : Fragment() {

    private var _binding: FragmentSelectCurrencyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StartViewModel by activityViewModels()

    private val currencies = listOf("Russian Ruble","US Dollar","Euro")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currencies.forEach { chipText ->
            createChip(chipText, binding.chipGroupPrimaryCurrency, true)
            createChip(chipText, binding.chipGroupSecondaryCurrencies, false)
        }

        binding.buttonNext
    }

    private fun createChip(displayText: String, parent: ChipGroup, single: Boolean) {
        val chip = layoutInflater.inflate(R.layout.chip_filter, parent, false) as Chip

        chip.apply {
            text = displayText
            chipIcon = ContextCompat.getDrawable(requireContext(), UiUtility.getChipIcon(displayText))

            when {
                single ->
                    isChecked = viewModel.primaryCurrency.value == text
                else ->
                    isChecked = viewModel.secondaryCurrencies.value.contains(text)
            }

            setOnCheckedChangeListener { _, isChecked ->
                if (single) viewModel.setPrimaryCurrency(displayText)
                else viewModel.toggleSecondaryCurrency(displayText, isChecked)
            }
        }

        parent.addView(chip)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}