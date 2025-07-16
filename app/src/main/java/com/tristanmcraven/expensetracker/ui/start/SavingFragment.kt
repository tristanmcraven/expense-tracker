package com.tristanmcraven.expensetracker.ui.start

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.color.utilities.DislikeAnalyzer
import com.tristanmcraven.expensetracker.ExpenseTrackerApp
import com.tristanmcraven.expensetracker.MainActivity
import com.tristanmcraven.expensetracker.databinding.FragmentSavingBinding
import com.tristanmcraven.expensetracker.db.SeedData
import com.tristanmcraven.expensetracker.model.ProfileInfo
import com.tristanmcraven.expensetracker.model.Settings
import com.tristanmcraven.expensetracker.model.UserAccounts
import com.tristanmcraven.expensetracker.model.UserSecondaryCurrencies
import com.tristanmcraven.expensetracker.utility.GenericHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavingFragment : Fragment() {

    private var _binding: FragmentSavingBinding? = null
    val binding get() = _binding!!

    private val viewModel: StartViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = (requireActivity().application as ExpenseTrackerApp).db
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

            // seed the data
            if (db.accountDao().get().first().isEmpty()) {
                SeedData.accounts.forEach { db.accountDao().insert(it) }
            }
            if (db.currencyDao().get().first().isEmpty()) {
                SeedData.currencies.forEach { db.currencyDao().insert(it) }
            }
            if (db.transactionTypeDao().get().first().isEmpty()) {
                SeedData.transactionTypes.forEach { db.transactionTypeDao().insert(it) }
            }

            // add profile info
            val name = viewModel.userFirstName.value.orEmpty()
            val selectedAvatarUri = viewModel.avatarUri.value
            val savedAvatarUriString: String? = selectedAvatarUri
                ?.let {
                    GenericHelper.saveImageToAppStorage(requireContext(), selectedAvatarUri).toString()
                }
            db.profileInfoDao().insert(
                ProfileInfo(0, viewModel.userFirstName.toString(), savedAvatarUriString)
            )

            // add user chosen accounts
            val selectedAccounts = viewModel.selectedAccounts.first()
            selectedAccounts.forEach { acc ->
                val id = when (acc) {
                    "cash" -> 1
                    "card" -> 2
                    "crypto" -> 3
                    else -> 1
                }
                db.userAccountsDao().insert(UserAccounts(id))
            }

            // add secondary currencies
            val secondaryCurrencies = viewModel.secondaryCurrencies.first()
            secondaryCurrencies.forEach { cur ->
                val id = when (cur) {
                    "Russian Ruble" -> 1
                    "US Dollar" -> 2
                    "Euro" -> 3
                    else -> 1
                }
                db.userSecondaryCurrenciesDao().insert(UserSecondaryCurrencies(id))
            }

            // add settings
            val selectedPrimaryCurrencyId = when (viewModel.primaryCurrency.first().orEmpty()) {
                "Russian Ruble" -> 1
                "US Dollar" -> 2
                "Euro" -> 3
                else -> 1
            }
            db.settingsDao().insert(Settings(0, false, selectedPrimaryCurrencyId, false, "default"))

            GenericHelper.MainCurrencySymbol = db.currencyDao().getById(
                db.settingsDao().getInstance().first().primaryCurrencyId
            ).first().symbol

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    "Done",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }
    }
}