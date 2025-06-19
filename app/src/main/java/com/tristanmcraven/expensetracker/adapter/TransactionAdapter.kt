package com.tristanmcraven.expensetracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tristanmcraven.expensetracker.R
import com.tristanmcraven.expensetracker.databinding.ItemGroupedTransactionsHeaderBinding
import com.tristanmcraven.expensetracker.databinding.ItemTransactionBinding
import com.tristanmcraven.expensetracker.model.Transaction
import com.tristanmcraven.expensetracker.utility.GroupBy
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionAdapter :
    ListAdapter<TransactionListItem, RecyclerView.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<TransactionListItem>() {
            override fun areItemsTheSame(
                old: TransactionListItem,
                new: TransactionListItem) = when {
                    old is TransactionListItem.Header && new is TransactionListItem.Header ->
                        old.title == new.title
                    old is TransactionListItem.TxItem && new is TransactionListItem.TxItem ->
                        old.tx.id == new.tx.id
                    else -> false
                }

            override fun areContentsTheSame(
                old: TransactionListItem,
                new: TransactionListItem) = old == new
        }
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    fun submitTransactions(
        transactions: List<Transaction>,
        groupBy: GroupBy
    ) {
        val groups = when (groupBy) {
            GroupBy.ALL -> mapOf("All" to transactions)
            GroupBy.DAY -> transactions.groupBy { tx ->
                SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    .format(Date(tx.timestamp))
            }
            GroupBy.WEEK -> transactions.groupBy { tx ->
                val cal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
                val week  = cal.get(Calendar.WEEK_OF_MONTH)
                val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(cal.time)
                val year  = cal.get(Calendar.YEAR)
                "Week $week of $month $year"
            }
            GroupBy.MONTH -> transactions.groupBy { tx ->
                SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                    .format(Date(tx.timestamp))
            }
            GroupBy.YEAR -> transactions.groupBy { tx ->
                SimpleDateFormat("yyyy", Locale.getDefault())
                    .format(Date(tx.timestamp))
            }
        }

        val items = mutableListOf<TransactionListItem>()
//        groups.toSortedMap(compareByDescending<String> { it })
//            .forEach{ (title, list) ->
//                val sum = list.sumOf { it.amount }
//                items += TransactionListItem.Header(title, sum)
//                list.sortedByDescending { it.timestamp }
//                    .forEach { tx -> items += TransactionListItem.TxItem(tx) }
//            }
        groups.entries.sortedByDescending { entry ->
            entry.value.maxOf { tx -> tx.timestamp }
        }.forEach { (title, list) ->
            val sum = list.sumOf { it.amount }
            items += TransactionListItem.Header(title, sum)
            list.sortedByDescending { it.timestamp }
                .forEach { tx -> items += TransactionListItem.TxItem(tx) }
        }
        super.submitList(items)
    }

    override fun getItemViewType(position: Int) =
        when (getItem(position)) {
            is TransactionListItem.Header -> TYPE_HEADER
            is TransactionListItem.TxItem -> TYPE_ITEM
        }

//    inner class VH(private val binding: ItemTransactionBinding)
//        : RecyclerView.ViewHolder(binding.root) {
//            fun bind(tx: Transaction) {
//                val context = binding.root.context
//
//                binding.textViewAmount.text = String.format("%.2f", tx.amount)
//                binding.textViewTransactionName.text = tx.name
//                binding.textViewCategoryName.text = context.getString(R.string.category)
//                binding.textViewDate.text = tx.dateString
//                binding.textViewTime.text = tx.timeString
//            }
//        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val binding = ItemGroupedTransactionsHeaderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            HeaderVH(binding)
        } else {
            val binding = ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ItemVH(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is TransactionListItem.Header -> (holder as HeaderVH).bind(item)
            is TransactionListItem.TxItem -> (holder as ItemVH).bind(item.tx)
        }
    }

    inner class HeaderVH(private val binding: ItemGroupedTransactionsHeaderBinding)
        : RecyclerView.ViewHolder(binding.root) {
            fun bind(header: TransactionListItem.Header) {
                binding.textViewTime.text = header.title
                binding.textViewTotalSum.text = String.format("%.2f", header.sum)
            }
        }

    inner class ItemVH(private val binding: ItemTransactionBinding)
        : RecyclerView.ViewHolder(binding.root) {
            fun bind(tx: Transaction) {
                val context = binding.root.context

                binding.textViewAmount.text = String.format("%.2f", tx.amount)
                binding.textViewTransactionName.text = if (tx.name.isNullOrBlank()) "Transaction without name" else tx.name
                binding.textViewCategoryName.text = context.getString(R.string.category)
                binding.textViewDate.text = tx.dateString
                binding.textViewTime.text = tx.timeString
            }
        }
}