package com.universidad.mindsparkai.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.universidad.mindsparkai.R
import com.universidad.mindsparkai.data.repository.AIRepository
import com.universidad.mindsparkai.databinding.FragmentSummaryBinding
import com.universidad.mindsparkai.ui.viewmodels.SummaryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SummaryFragment : Fragment() {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SummaryViewModel by viewModels()

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                viewModel.processUploadedFile(requireContext(), uri)
            }
        }
    }

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
        setupUI()
        setupListeners()
        observeViewModel()
    }

    private fun setupUI() {
        setupModelChips()
        updateGenerateButtonState()
    }

    private fun setupModelChips() {
        binding.chipGroupModels.removeAllViews()

        viewModel.availableModels.forEach { model ->
            val chip = Chip(requireContext()).apply {
                text = model.displayName
                isCheckable = true
                setOnClickListener {
                    viewModel.selectModel(model)
                    updateModelChipsSelection(model)
                }
            }
            binding.chipGroupModels.addView(chip)
        }

        // Seleccionar primer modelo por defecto
        val firstChip = binding.chipGroupModels.getChildAt(0) as? Chip
        firstChip?.isChecked = true
    }

    private fun updateModelChipsSelection(selectedModel: AIRepository.AIModel) {
        for (i in 0 until binding.chipGroupModels.childCount) {
            val chip = binding.chipGroupModels.getChildAt(i) as Chip
            chip.isChecked = chip.text == selectedModel.displayName
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAttach.setOnClickListener {
            openFilePicker()
        }

        binding.uploadArea.setOnClickListener {
            openFilePicker()
        }

        binding.btnGenerate.setOnClickListener {
            val inputText = binding.etTextInput.text.toString().trim()
            if (inputText.isNotEmpty()) {
                viewModel.generateSummary(inputText)
            } else {
                Toast.makeText(requireContext(), "Por favor ingresa texto para resumir", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnGenerateDetailed?.setOnClickListener {
            val inputText = binding.etTextInput.text.toString().trim()
            if (inputText.isNotEmpty()) {
                viewModel.generateDetailedSummary(inputText)
            }
        }

        binding.btnGenerateBrief?.setOnClickListener {
            val inputText = binding.etTextInput.text.toString().trim()
            if (inputText.isNotEmpty()) {
                viewModel.generateBriefSummary(inputText)
            }
        }

        binding.btnGenerateBullets?.setOnClickListener {
            val inputText = binding.etTextInput.text.toString().trim()
            if (inputText.isNotEmpty()) {
                viewModel.generateBulletPoints(inputText)
            }
        }

        binding.etTextInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.uploadArea.visibility = View.GONE
            }
        }

        // Text watcher para contar palabras
        binding.etTextInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                updateWordCount(s.toString())
                updateGenerateButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updateWordCount(text: String) {
        val wordCount = if (text.isBlank()) 0 else text.split("\\s+".toRegex()).size
        binding.tvWordCount?.text = "$wordCount palabras"
    }

    private fun updateGenerateButtonState() {
        val hasText = binding.etTextInput.text.toString().trim().isNotEmpty()
        binding.btnGenerate.isEnabled = hasText && !(viewModel.loading.value == true)

        val alpha = if (hasText) 1.0f else 0.5f
        binding.btnGenerate.alpha = alpha
        binding.btnGenerateDetailed?.alpha = alpha
        binding.btnGenerateBrief?.alpha = alpha
        binding.btnGenerateBullets?.alpha = alpha
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "text/plain",
                "application/pdf",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            ))
        }
        filePickerLauncher.launch(intent)
    }

    private fun observeViewModel() {
        viewModel.summary.observe(viewLifecycleOwner) { summary ->
            if (summary != null) {
                showSummaryResult(summary)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            updateLoadingState(isLoading)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.uploadedFileName.observe(viewLifecycleOwner) { fileName ->
            fileName?.let {
                binding.tvUploadedFile?.text = "Archivo: $it"
                binding.tvUploadedFile?.visibility = View.VISIBLE
                binding.uploadArea.visibility = View.GONE
            }
        }

        viewModel.wordCount.observe(viewLifecycleOwner) { count ->
            binding.tvWordCount?.text = "$count palabras"
        }

        viewModel.estimatedTime.observe(viewLifecycleOwner) { time ->
            binding.tvEstimatedTime?.text = "~$time min lectura"
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        binding.btnGenerate.text = if (isLoading) "✨ Generando..." else "✨ Generar resumen"
        binding.btnGenerate.isEnabled = !isLoading && binding.etTextInput.text.toString().trim().isNotEmpty()

        binding.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE

        // Deshabilitar otros botones durante la carga
        binding.btnGenerateDetailed?.isEnabled = !isLoading
        binding.btnGenerateBrief?.isEnabled = !isLoading
        binding.btnGenerateBullets?.isEnabled = !isLoading
    }

    private fun showSummaryResult(summary: String) {
        // Crear dialog o navegar a pantalla de resultado
        val bundle = Bundle().apply {
            putString("summary_text", summary)
            putString("original_text", binding.etTextInput.text.toString())
            putSerializable("summary_stats", HashMap(viewModel.getSummaryStats()))
        }

        // Si tienes un fragmento de resultado, navegar a él
        // findNavController().navigate(R.id.action_summary_to_result, bundle)

        // Por ahora, mostrar en un dialog simple
        showSummaryDialog(summary)
    }

    private fun showSummaryDialog(summary: String) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("📄 Resumen Generado")
            .setMessage(summary)
            .setPositiveButton("Copiar") { _, _ ->
                copyToClipboard(summary)
            }
            .setNegativeButton("Cerrar", null)
            .setNeutralButton("Compartir") { _, _ ->
                shareText(summary)
            }
            .show()
    }

    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Resumen", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "Resumen copiado", Toast.LENGTH_SHORT).show()
    }

    private fun shareText(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, "Resumen generado por MindSpark AI")
        }
        startActivity(Intent.createChooser(intent, "Compartir resumen"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}