package com.tristanmcraven.expensetracker.ui.settings

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.tristanmcraven.expensetracker.ExpenseTrackerApp
import com.tristanmcraven.expensetracker.R
import com.tristanmcraven.expensetracker.databinding.FragmentImportDataBinding
import com.tristanmcraven.expensetracker.databinding.FragmentNameBinding
import com.tristanmcraven.expensetracker.ie.importer.ImporterApp
import com.tristanmcraven.expensetracker.ie.importer.expense_tracker.ExpenseTrackerImportFile
import com.tristanmcraven.expensetracker.ie.importer.my_tab.MyTabImportFile
import com.tristanmcraven.expensetracker.model.Transaction
import com.tristanmcraven.expensetracker.utility.GenericHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant

class ImportDataFragment : Fragment() {

    private var _binding: FragmentImportDataBinding? = null
    private val binding get () = _binding!!

    private val pickJson = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { handleJsonUri(it) }
    }
    private val db by lazy {
        (requireContext().applicationContext as ExpenseTrackerApp).db
    }

    private var selectedApp: ImporterApp = ImporterApp.MY_TAB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImportDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonMyTab.setOnClickListener {
            selectedApp = ImporterApp.MY_TAB
            pickJson.launch(arrayOf("application/json"))
        }
        binding.buttonExpenseTracker.setOnClickListener {
            selectedApp = ImporterApp.EXPENSE_TRACKER
            pickJson.launch(arrayOf("application/json"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleJsonUri(uri: Uri) {
        lifecycleScope.launch {
            try {
               val json = requireContext().contentResolver
                   .openInputStream(uri)!!
                   .bufferedReader()
                   .use { it.readText() } 
               val account = db.accountDao().get().first().firstOrNull()

               val newTransactions = when (selectedApp) {
                   ImporterApp.MY_TAB -> {
                       val importFile = Gson().fromJson(json, MyTabImportFile::class.java)
                       importFile.expenses.mapNotNull { ie ->
                           val time = ie.time
                           //TODO implement white-man solution, get rid of ts ↓↓↓
                           val instant = Instant.parse(time.removeRange(time.length - 4, time.length) + "Z")
                           Transaction(
                               id = 0,
                               name = ie.name,
                               amount = if (ie.type == "expense") -(ie.currency)
                               else ie.currency,
                               description = ie.description,
                               timestamp = instant.toEpochMilli(),
                               accountId = account?.id ?: 1
                           )
                       }
                   }

                   ImporterApp.EXPENSE_TRACKER -> {
                       val importFile = Gson().fromJson(json, ExpenseTrackerImportFile::class.java)
                       if (importFile.version != GenericHelper.getCurrentDbVersion(requireContext()))
                           throw Exception("Version Incompatible")
                       importFile.transactions
                   }
               }

               withContext(Dispatchers.IO) {
                   db.transactionDao().deleteAll()
                   newTransactions.forEach { db.transactionDao().insert(it) }
               }

               Toast.makeText(
                   requireContext(),
                   "Success (maybe)",
                   Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("Import", "Failed to import json", e)
                Toast.makeText(
                    requireContext(),
                    "Failed to import file: ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

}