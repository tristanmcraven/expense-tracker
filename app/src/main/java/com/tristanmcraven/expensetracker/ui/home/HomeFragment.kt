package com.tristanmcraven.expensetracker.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tristanmcraven.expensetracker.AddTransactionActivity
import com.tristanmcraven.expensetracker.adapter.TransactionAdapter
import com.tristanmcraven.expensetracker.databinding.FragmentHomeBinding
import com.tristanmcraven.expensetracker.utility.GroupBy
import com.tristanmcraven.expensetracker.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val vm: MainViewModel by activityViewModels()
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTransactionsRecyclerView()
        binding.fabAddTransaction.setOnClickListener {
            val intent = Intent(requireContext(), AddTransactionActivity::class.java)
            startActivity(intent)
        }
        binding.buttonGroupTransactions.setOnClickListener {
            val options = GroupBy.values().map {
                it.name.lowercase().replaceFirstChar(Char::uppercase)
            }.toTypedArray()
            val currentOption = vm.groupBy.value.ordinal

            AlertDialog.Builder(requireContext())
                .setTitle("Group by:")
                .setSingleChoiceItems(options, currentOption) { dialog, which ->
                    vm.setGrouping(GroupBy.values()[which])
                    dialog.dismiss()
                }

                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun setupTransactionsRecyclerView() {
        adapter = TransactionAdapter()
        binding.recyclerViewTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTransactions.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            combine(
               vm.transactionFlow,
               vm.groupBy
            ) { transactions, gb -> Pair(transactions, gb) }
                .collect { (transactions, gb) ->
                    adapter.submitTransactions(transactions, gb)
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            vm.groupBy.collectLatest { gb ->
                binding.buttonGroupTransactions.text =
                    gb.name.lowercase().replaceFirstChar(Char::uppercase)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}