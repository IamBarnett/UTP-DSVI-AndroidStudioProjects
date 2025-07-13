package com.universidad.mindsparkai.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.universidad.mindsparkai.databinding.FragmentQuizBinding

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
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
            generateQuestions()
        }
    }

    private fun generateQuestions() {
        binding.btnGenerate.text = "🧠 Generando preguntas..."
        binding.btnGenerate.isEnabled = false

        // Simulate AI processing
        binding.root.postDelayed({
            binding.btnGenerate.text = "🤖 Generar nuevas preguntas"
            binding.btnGenerate.isEnabled = true
            // TODO: Show generated questions
        }, 2500)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}