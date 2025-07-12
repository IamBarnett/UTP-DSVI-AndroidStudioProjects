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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter

    private val availableModels = listOf("GPT-4", "Claude-3", "Gemini Pro", "LLaMA 2")
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
        availableModels.forEach { model ->
            val chip = Chip(requireContext()).apply {
                text = model
                isCheckable = true
                setOnClickListener {
                    viewModel.selectModel(model)
                    updateModelChipsSelection(model)
                }
            }
            binding.chipGroupModels.addView(chip)
        }

        // Select first model by default
        (binding.chipGroupModels.getChildAt(0) as Chip).isChecked = true
    }

    private fun setupSuggestionChips() {
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
        }

        viewModel.selectedModel.observe(viewLifecycleOwner) { model ->
            // Update UI to reflect selected model
        }
    }

    private fun sendMessage() {
        val message = binding.etMessage.text.toString().trim()
        if (message.isNotEmpty()) {
            viewModel.sendMessage(message)
            binding.etMessage.text.clear()
        }
    }

    private fun updateModelChipsSelection(selectedModel: String) {
        for (i in 0 until binding.chipGroupModels.childCount) {
            val chip = binding.chipGroupModels.getChildAt(i) as Chip
            chip.isChecked = chip.text == selectedModel
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
