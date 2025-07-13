package com.universidad.mindsparkai.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.universidad.mindsparkai.databinding.FragmentStudyPlanBinding

class StudyPlanFragment : Fragment() {

    private var _binding: FragmentStudyPlanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudyPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnGenerate.setOnClickListener {
            generateStudyPlan()
        }
    }

    private fun generateStudyPlan() {
        binding.btnGenerate.text = "🤖 Creando plan..."
        binding.btnGenerate.isEnabled = false

        // Simulate AI processing
        binding.root.postDelayed({
            binding.btnGenerate.text = "🧠 Generar plan con IA"
            binding.btnGenerate.isEnabled = true
            // TODO: Show generated plan
        }, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}