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

    private val _selectedModel = MutableLiveData<AIRepository.AIModel>()
    val selectedModel: LiveData<AIRepository.AIModel> = _selectedModel

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val messageHistory = mutableListOf<ChatMessage>()

    // Lista de modelos disponibles
    val availableModels = listOf(
        AIRepository.AIModel.GPT_4,
        AIRepository.AIModel.GPT_3_5,
        AIRepository.AIModel.CLAUDE_3_SONNET,
        AIRepository.AIModel.CLAUDE_3_HAIKU,
        AIRepository.AIModel.GEMINI_PRO
    )

    init {
        _selectedModel.value = AIRepository.AIModel.GPT_4
        initializeChat()
    }

    private fun initializeChat() {
        val welcomeMessage = ChatMessage(
            id = "welcome_${System.currentTimeMillis()}",
            content = """
                ¡Hola! 👋 Soy tu asistente de estudio inteligente powered by ${_selectedModel.value?.displayName}.
                
                **Puedo ayudarte con:**
                • Resolver dudas de cualquier materia 📚
                • Explicar conceptos complejos de forma simple 🧠
                • Crear resúmenes y esquemas 📄
                • Generar ejercicios de práctica 🎯
                • Planificar tu tiempo de estudio 📅
                
                ¿En qué te puedo ayudar hoy?
            """.trimIndent(),
            isFromUser = false,
            aiModel = _selectedModel.value?.displayName ?: "AI"
        )

        messageHistory.add(welcomeMessage)
        _messages.value = messageHistory.toList()
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        // Agregar mensaje del usuario
        val userMessage = ChatMessage(
            id = "user_${System.currentTimeMillis()}",
            content = content,
            isFromUser = true
        )

        messageHistory.add(userMessage)
        _messages.value = messageHistory.toList()

        // Obtener respuesta de la IA
        getAIResponse(content)
    }

    private fun getAIResponse(userMessage: String) {
        viewModelScope.launch {
            try {
                _isTyping.value = true
                _error.value = null

                // Preparar contexto de conversación (últimos 10 mensajes)
                val context = messageHistory
                    .takeLast(10)
                    .filter { it.id != "welcome_${System.currentTimeMillis()}" }
                    .map { it.content }

                val result = aiRepository.sendChatMessage(
                    message = userMessage,
                    model = _selectedModel.value ?: AIRepository.AIModel.GPT_4,
                    context = context
                )

                result.fold(
                    onSuccess = { response ->
                        val aiMessage = ChatMessage(
                            id = "ai_${System.currentTimeMillis()}",
                            content = response,
                            isFromUser = false,
                            aiModel = _selectedModel.value?.displayName ?: "AI"
                        )

                        messageHistory.add(aiMessage)
                        _messages.value = messageHistory.toList()
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Error al obtener respuesta"

                        // Agregar mensaje de error
                        val errorMessage = ChatMessage(
                            id = "error_${System.currentTimeMillis()}",
                            content = "Lo siento, ocurrió un error al procesar tu mensaje. Por favor intenta de nuevo.",
                            isFromUser = false,
                            aiModel = _selectedModel.value?.displayName ?: "AI"
                        )

                        messageHistory.add(errorMessage)
                        _messages.value = messageHistory.toList()
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isTyping.value = false
            }
        }
    }

    fun selectModel(model: AIRepository.AIModel) {
        val previousModel = _selectedModel.value
        _selectedModel.value = model

        if (previousModel != model) {
            // Notificar cambio de modelo
            val changeMessage = ChatMessage(
                id = "system_${System.currentTimeMillis()}",
                content = "📱 Modelo cambiado a ${model.displayName}",
                isFromUser = false,
                aiModel = model.displayName
            )

            messageHistory.add(changeMessage)
            _messages.value = messageHistory.toList()
        }
    }

    fun clearChat() {
        messageHistory.clear()
        _messages.value = emptyList()
        _error.value = null
        initializeChat()
    }

    fun retryLastMessage() {
        if (messageHistory.isNotEmpty()) {
            val lastUserMessage = messageHistory.findLast { it.isFromUser }
            lastUserMessage?.let { message ->
                // Remover último mensaje de error si existe
                if (messageHistory.lastOrNull()?.id?.startsWith("error_") == true) {
                    messageHistory.removeLastOrNull()
                    _messages.value = messageHistory.toList()
                }

                getAIResponse(message.content)
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    // Función para obtener sugerencias rápidas
    fun getSuggestions(): List<String> {
        return listOf(
            "Explica la fotosíntesis de manera simple",
            "Ayúdame con derivadas en cálculo",
            "¿Qué es la programación orientada a objetos?",
            "Resume las causas de la Primera Guerra Mundial",
            "Explica la Teoría de la Relatividad",
            "¿Cómo funciona el ADN?",
            "Diferencias entre mitosis y meiosis",
            "Explica la Revolución Industrial"
        )
    }

    // Función para generar un resumen de la conversación
    fun generateConversationSummary(): String {
        val conversationText = messageHistory
            .filter { !it.id.startsWith("welcome_") && !it.id.startsWith("system_") }
            .joinToString("\n") { "${if (it.isFromUser) "Usuario" else "IA"}: ${it.content}" }

        return if (conversationText.isNotEmpty()) {
            "Resumen de la conversación:\n$conversationText"
        } else {
            "No hay conversación para resumir"
        }
    }

    // Función para exportar conversación
    fun exportConversation(): String {
        val timestamp = System.currentTimeMillis()
        val modelName = _selectedModel.value?.displayName ?: "AI"

        val header = """
            # Conversación con MindSpark AI
            **Modelo:** $modelName
            **Fecha:** ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(timestamp))}
            
            ---
            
        """.trimIndent()

        val conversation = messageHistory
            .filter { !it.id.startsWith("welcome_") && !it.id.startsWith("system_") }
            .joinToString("\n\n") { message ->
                val role = if (message.isFromUser) "👤 **Usuario**" else "🤖 **${message.aiModel}**"
                "$role\n${message.content}"
            }

        return header + conversation
    }
}

