package com.universidad.mindsparkai.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.universidad.mindsparkai.R
import com.universidad.mindsparkai.databinding.FragmentChatBinding
import com.universidad.mindsparkai.ui.adapters.ChatAdapter
import com.universidad.mindsparkai.ui.viewmodels.ChatViewModel
import com.universidad.mindsparkai.data.repository.AIRepository
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter

    private val availableModels = listOf(
        AIRepository.AIModel.GPT_4,
        AIRepository.AIModel.CLAUDE_3_SONNET,
        AIRepository.AIModel.GEMINI_PRO,
        AIRepository.AIModel.GPT_3_5
    )

    private val suggestionChips = listOf(
        "Explica la fotosíntesis",
        "Ayuda con cálculo",
        "Resume este texto",
        "Genera un quiz"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupModelChips()
        setupSuggestionChips()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()

        binding.rvMessages.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
        }
    }

    private fun setupModelChips() {
        binding.chipGroupModels.removeAllViews()

        availableModels.forEach { model ->
            val chip = Chip(requireContext()).apply {
                text = model.displayName
                isCheckable = true
                setOnClickListener {
                    viewModel.selectModel(model)
                    updateModelChipsSelection(model.displayName)
                }
            }
            binding.chipGroupModels.addView(chip)
        }

        // Select first model by default
        if (binding.chipGroupModels.childCount > 0) {
            val firstChip = binding.chipGroupModels.getChildAt(0) as Chip
            firstChip.isChecked = true
            viewModel.selectModel(availableModels.first())
        }
    }

    private fun setupSuggestionChips() {
        binding.chipGroupSuggestions.removeAllViews()

        suggestionChips.forEach { suggestion ->
            val chip = Chip(requireContext()).apply {
                text = suggestion
                setOnClickListener {
                    binding.etMessage.setText(suggestion)
                    sendMessage()
                }
            }
            binding.chipGroupSuggestions.addView(chip)
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSend.setOnClickListener {
            sendMessage()
        }

        binding.etMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }

    private fun observeViewModel() {
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            chatAdapter.submitList(messages)
            if (messages.isNotEmpty()) {
                binding.rvMessages.scrollToPosition(messages.size - 1)
            }
        }

        viewModel.isTyping.observe(viewLifecycleOwner) { isTyping ->
            chatAdapter.setTyping(isTyping)
            if (isTyping) {
                binding.rvMessages.scrollToPosition(chatAdapter.itemCount - 1)
            }
        }

        viewModel.selectedModel.observe(viewLifecycleOwner) { model ->
            // Update UI to reflect selected model if needed
            updateModelChipsSelection(model.displayName)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Show error message to user
                showErrorMessage(it)
                viewModel.clearError()
            }
        }
    }

    private fun sendMessage() {
        val message = binding.etMessage.text.toString().trim()
        if (message.isNotEmpty()) {
            viewModel.sendMessage(message)
            binding.etMessage.text?.clear()

            // Hide suggestions after first message
            binding.chipGroupSuggestions.visibility = View.GONE
        }
    }

    private fun updateModelChipsSelection(selectedModelName: String) {
        for (i in 0 until binding.chipGroupModels.childCount) {
            val chip = binding.chipGroupModels.getChildAt(i) as Chip
            chip.isChecked = chip.text == selectedModelName
        }
    }

    private fun showErrorMessage(message: String) {
        // You can implement this with a Snackbar or Toast
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
