package com.universidad.mindsparkai.data.repository

import com.universidad.mindsparkai.data.network.ChatCompletionRequest
import com.universidad.mindsparkai.data.network.ChatMessage
import com.universidad.mindsparkai.data.network.OpenAIService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIRepository @Inject constructor(
    private val openAIService: OpenAIService
) {

    private val apiKey = "sk-YOUR_OPENAI_API_KEY" // Cambia por tu API key

    suspend fun sendChatMessage(
        message: String,
        model: String = "gpt-4",
        context: List<ChatMessage> = emptyList()
    ): String? {
        return try {
            val messages = context + ChatMessage(role = "user", content = message)

            val request = ChatCompletionRequest(
                model = model,
                messages = messages,
                max_tokens = 500,
                temperature = 0.7
            )

            val response = openAIService.getChatCompletion(
                authorization = "Bearer $apiKey",
                request = request
            )

            if (response.isSuccessful) {
                response.body()?.choices?.firstOrNull()?.message?.content
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun generateSummary(text: String): String? {
        val prompt = "Resume el siguiente texto de manera clara y concisa, " +
                "extrayendo los puntos más importantes:\n\n$text"

        return sendChatMessage(prompt, "gpt-4")
    }

    suspend fun generateQuizQuestions(
        subject: String,
        difficulty: String,
        count: Int = 5
    ): String? {
        val prompt = "Genera $count preguntas de opción múltiple sobre $subject " +
                "con dificultad $difficulty. Incluye 4 opciones por pregunta y " +
                "marca la respuesta correcta. Formato JSON."

        return sendChatMessage(prompt, "gpt-4")
    }

    suspend fun generateStudyPlan(
        subjects: List<String>,
        hoursPerDay: Int,
        goals: String
    ): String? {
        val prompt = "Crea un plan de estudio semanal para las materias: ${subjects.joinToString(", ")}. " +
                "Disponibilidad: $hoursPerDay horas diarias. Objetivos: $goals. " +
                "Incluye horarios específicos y recomendaciones de estudio."

        return sendChatMessage(prompt, "gpt-4")
    }
}