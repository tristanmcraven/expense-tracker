package com.tristanmcraven.expensetracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tristanmcraven.expensetracker.R
import com.tristanmcraven.expensetracker.databinding.ItemTransactionBinding
import com.tristanmcraven.expensetracker.model.Transaction

class TransactionAdapter : ListAdapter<Transaction, TransactionAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class VH(private val binding: ItemTransactionBinding)
        : RecyclerView.ViewHolder(binding.root) {
            fun bind(tx: Transaction) {
                val context = binding.root.context
                
                binding.textViewAmount.text = String.format("%.2f", tx.amount)
                binding.textViewTransactionName.text = tx.name
                binding.textViewCategoryName.text = context.getString(R.string.category)
                binding.textViewDate.text = tx.dateString
                binding.textViewTime.text = tx.timeString
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
}