package com.universidad.mindsparkai.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universidad.mindsparkai.data.models.ChatMessage
import com.universidad.mindsparkai.data.repository.AIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _isTyping = MutableLiveData<Boolean>()
    val isTyping: LiveData<Boolean> = _isTyping

    private val _selectedModel = MutableLiveData<String>()
    val selectedModel: LiveData<String> = _selectedModel

    private val messageHistory = mutableListOf<ChatMessage>()

    init {
        _selectedModel.value = "GPT-4"
        // Mensaje de bienvenida
        val welcomeMessage = ChatMessage(
            content = "¡Hola! 👋 Soy tu asistente de estudio inteligente. Puedo ayudarte con:\n\n• Resolver dudas de cualquier materia\n• Explicar conceptos complejos\n• Crear resúmenes y esquemas\n• Generar ejercicios de práctica\n\n¿En qué te puedo ayudar hoy?",
            isFromUser = false,
            aiModel = "GPT-4"
        )
        messageHistory.add(welcomeMessage)
        _messages.value = messageHistory.toList()
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        // Agregar mensaje del usuario
        val userMessage = ChatMessage(
            content = content,
            isFromUser = true
        )
        messageHistory.add(userMessage)
        _messages.value = messageHistory.toList()

        // Mostrar indicador de escritura
        _isTyping.value = true

        // Enviar a la IA
        viewModelScope.launch {
            try {
                val response = aiRepository.sendChatMessage(
                    message = content,
                    model = _selectedModel.value ?: "gpt-4"
                )

                val aiMessage = ChatMessage(
                    content = response ?: "Lo siento, no pude procesar tu mensaje. Intenta de nuevo.",
                    isFromUser = false,
                    aiModel = _selectedModel.value ?: "GPT-4"
                )

                messageHistory.add(aiMessage)
                _messages.value = messageHistory.toList()

            } catch (e: Exception) {
                val errorMessage = ChatMessage(
                    content = "Ocurrió un error al conectar con la IA. Verifica tu conexión.",
                    isFromUser = false,
                    aiModel = _selectedModel.value ?: "GPT-4"
                )
                messageHistory.add(errorMessage)
                _messages.value = messageHistory.toList()
            } finally {
                _isTyping.value = false
            }
        }
    }

    fun selectModel(model: String) {
        _selectedModel.value = model
    }

    fun clearChat() {
        messageHistory.clear()
        _messages.value = emptyList()
    }
}

