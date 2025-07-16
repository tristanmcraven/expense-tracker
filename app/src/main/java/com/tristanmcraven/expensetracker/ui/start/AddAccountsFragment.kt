package com.tristanmcraven.expensetracker.ui.start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.tristanmcraven.expensetracker.R
import com.tristanmcraven.expensetracker.databinding.FragmentAddAccountsBinding
import com.tristanmcraven.expensetracker.utility.UiUtility

class AddAccountsFragment : Fragment() {

    private var _binding: FragmentAddAccountsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chips = listOf("cash","card","crypto")
        chips.forEach { chipText ->
            val chip = layoutInflater.inflate(R.layout.chip_filter, binding.chipGroupSelectedAccounts, false) as Chip

            chip.apply {
                text = chipText.replaceFirstChar { it.uppercase() }
                isChecked = viewModel.selectedAccounts.value.contains(chipText)
                chipIcon = ContextCompat.getDrawable(requireContext(), UiUtility.getChipIcon(chipText))

                setOnCheckedChangeListener { _, isChecked ->
                    viewModel.toggleAccount(chipText, isChecked)
                }
            }
            binding.chipGroupSelectedAccounts.addView(chip)
        }

        binding.buttonNext.setOnClickListener {
            if (binding.chipGroupSelectedAccounts.checkedChipIds.isEmpty()) {
                Toast.makeText(requireContext(), "Select at least 1 account", Toast.LENGTH_SHORT).show()
            } else {
                findNavController().navigate(R.id.action_AddAccountsFragment_to_SelectCurrencyFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}