package com.universidad.mindsparkai.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.universidad.mindsparkai.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        loadUserData()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun loadUserData() {
        // TODO: Load user data from repository
        binding.tvUserName.text = "María González"
        binding.tvUserCareer.text = "Ingeniería de Sistemas • Universidad Nacional"
        binding.tvStudyHours.text = "247h"
        binding.tvAiQueries.text = "156"
        binding.tvAvgQuiz.text = "89%"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

