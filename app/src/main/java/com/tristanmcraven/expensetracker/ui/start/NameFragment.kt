package com.tristanmcraven.expensetracker.ui.start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.tristanmcraven.expensetracker.R
import com.tristanmcraven.expensetracker.databinding.FragmentNameBinding

class NameFragment : Fragment() {

    private var _binding: FragmentNameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNameBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonNext.setOnClickListener {
            findNavController().navigate(R.id.action_NameFragment_to_AvatarFragment)
        }
        binding.editTextName.doAfterTextChanged { editable ->
            val name = editable.toString().trim()
            viewModel.setUserFirstName(name)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}