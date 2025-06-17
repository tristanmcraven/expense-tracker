package com.tristanmcraven.expensetracker.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tristanmcraven.expensetracker.AddTransactionActivity
import com.tristanmcraven.expensetracker.R
import com.tristanmcraven.expensetracker.adapter.TransactionAdapter
import com.tristanmcraven.expensetracker.databinding.FragmentHomeBinding
import com.tristanmcraven.expensetracker.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
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
    }

    private fun setupTransactionsRecyclerView() {
        adapter = TransactionAdapter()
        binding.recyclerViewTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTransactions.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.transactionFlow.collectLatest { list ->
                adapter.submitList(list)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}