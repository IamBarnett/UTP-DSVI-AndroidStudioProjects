package com.universidad.mindsparkai.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.universidad.mindsparkai.databinding.FragmentSummaryBinding

class SummaryFragment : Fragment() {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)
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
            // TODO: Implement summary generation
            generateSummary()
        }

        binding.uploadArea.setOnClickListener {
            // TODO: Implement file upload
        }
    }

    private fun generateSummary() {
        binding.btnGenerate.text = "⏳ Generando resumen..."
        binding.btnGenerate.isEnabled = false

        // Simulate AI processing
        binding.root.postDelayed({
            binding.btnGenerate.text = "✨ Generar resumen"
            binding.btnGenerate.isEnabled = true
            // TODO: Show generated summary
        }, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}