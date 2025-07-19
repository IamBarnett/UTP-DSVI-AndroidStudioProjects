package com.universidad.mindsparkai.data.repository

import com.universidad.mindsparkai.data.network.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIRepository @Inject constructor(
    private val aiService: AIService
) {

    // API Keys - En producción estos deberían estar en BuildConfig o variables de entorno
    private val openAIKey = "sk-your-openai-key"
    private val claudeKey = "your-claude-key"
    private val geminiKey = "your-gemini-key"

    enum class AIModel(val displayName: String, val provider: String) {
        GPT_4("GPT-4", "openai"),
        GPT_3_5("GPT-3.5 Turbo", "openai"),
        CLAUDE_3_SONNET("Claude-3 Sonnet", "anthropic"),
        CLAUDE_3_HAIKU("Claude-3 Haiku", "anthropic"),
        GEMINI_PRO("Gemini Pro", "google"),
        GEMINI_PRO_VISION("Gemini Pro Vision", "google")
    }

    suspend fun sendChatMessage(
        message: String,
        model: AIModel = AIModel.GPT_4,
        context: List<String> = emptyList()
    ): Result<String> {
        return try {
            when (model.provider) {
                "openai" -> sendOpenAIMessage(message, model, context)
                "anthropic" -> sendClaudeMessage(message, model, context)
                "google" -> sendGeminiMessage(message, model, context)
                else -> Result.failure(Exception("Modelo no soportado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun sendOpenAIMessage(
        message: String,
        model: AIModel,
        context: List<String>
    ): Result<String> {
        val messages = mutableListOf<OpenAIMessage>()

        // Agregar contexto de conversación
        context.chunked(2) { pair ->
            if (pair.size == 2) {
                messages.add(OpenAIMessage("user", pair[0]))
                messages.add(OpenAIMessage("assistant", pair[1]))
            }
        }

        // Agregar mensaje actual
        messages.add(OpenAIMessage("user", message))

        val modelName = when (model) {
            AIModel.GPT_4 -> "gpt-4"
            AIModel.GPT_3_5 -> "gpt-3.5-turbo"
            else -> "gpt-4"
        }

        val request = OpenAIRequest(
            model = modelName,
            messages = messages,
            max_tokens = 1000,
            temperature = 0.7
        )

        val response = aiService.getOpenAICompletion(
            authorization = "Bearer $openAIKey",
            request = request
        )

        return if (response.isSuccessful) {
            val content = response.body()?.choices?.firstOrNull()?.message?.content
            if (content != null) {
                Result.success(content)
            } else {
                Result.failure(Exception("Respuesta vacía"))
            }
        } else {
            Result.failure(Exception("Error API: ${response.code()}"))
        }
    }

    private suspend fun sendClaudeMessage(
        message: String,
        model: AIModel,
        context: List<String>
    ): Result<String> {
        val messages = mutableListOf<ClaudeMessage>()

        // Agregar contexto
        context.chunked(2) { pair ->
            if (pair.size == 2) {
                messages.add(ClaudeMessage("user", pair[0]))
                messages.add(ClaudeMessage("assistant", pair[1]))
            }
        }

        messages.add(ClaudeMessage("user", message))

        val modelName = when (model) {
            AIModel.CLAUDE_3_SONNET -> "claude-3-sonnet-20240229"
            AIModel.CLAUDE_3_HAIKU -> "claude-3-haiku-20240307"
            else -> "claude-3-sonnet-20240229"
        }

        val request = ClaudeRequest(
            model = modelName,
            max_tokens = 1000,
            messages = messages
        )

        val response = aiService.getClaudeCompletion(
            apiKey = claudeKey,
            request = request
        )

        return if (response.isSuccessful) {
            val content = response.body()?.content?.firstOrNull()?.text
            if (content != null) {
                Result.success(content)
            } else {
                Result.failure(Exception("Respuesta vacía"))
            }
        } else {
            Result.failure(Exception("Error API: ${response.code()}"))
        }
    }

    private suspend fun sendGeminiMessage(
        message: String,
        model: AIModel,
        context: List<String>
    ): Result<String> {
        val contents = mutableListOf<GeminiContent>()

        // Agregar contexto
        context.chunked(2) { pair ->
            if (pair.size == 2) {
                contents.add(GeminiContent(
                    parts = listOf(GeminiPart(pair[0])),
                    role = "user"
                ))
                contents.add(GeminiContent(
                    parts = listOf(GeminiPart(pair[1])),
                    role = "model"
                ))
            }
        }

        // Agregar mensaje actual
        contents.add(GeminiContent(
            parts = listOf(GeminiPart(message)),
            role = "user"
        ))

        val modelName = when (model) {
            AIModel.GEMINI_PRO -> "gemini-pro"
            AIModel.GEMINI_PRO_VISION -> "gemini-pro-vision"
            else -> "gemini-pro"
        }

        val request = GeminiRequest(contents = contents)

        val response = aiService.getGeminiCompletion(
            model = modelName,
            apiKey = geminiKey,
            request = request
        )

        return if (response.isSuccessful) {
            val content = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (content != null) {
                Result.success(content)
            } else {
                Result.failure(Exception("Respuesta vacía"))
            }
        } else {
            Result.failure(Exception("Error API: ${response.code()}"))
        }
    }

    suspend fun generateSummary(
        text: String,
        model: AIModel = AIModel.CLAUDE_3_SONNET
    ): Result<String> {
        val prompt = """
            Resume el siguiente texto de manera clara y concisa, extrayendo los puntos más importantes:
            
            TEXTO:
            $text
            
            INSTRUCCIONES:
            - Extrae los puntos principales
            - Mantén la información más relevante
            - Usa bullet points si es apropiado
            - Sé conciso pero completo
        """.trimIndent()

        return sendChatMessage(prompt, model)
    }

    suspend fun generateQuizQuestions(
        subject: String,
        difficulty: String,
        count: Int = 5,
        model: AIModel = AIModel.GPT_4
    ): Result<String> {
        val prompt = """
            Genera $count preguntas de opción múltiple sobre $subject con dificultad $difficulty.
            
            FORMATO REQUERIDO (JSON):
            {
              "questions": [
                {
                  "question": "Pregunta aquí",
                  "options": ["A) Opción 1", "B) Opción 2", "C) Opción 3", "D) Opción 4"],
                  "correctAnswer": 1,
                  "explanation": "Explicación de por qué es correcta"
                }
              ]
            }
            
            REQUISITOS:
            - 4 opciones por pregunta
            - Una sola respuesta correcta
            - Explicación clara
            - Nivel de dificultad: $difficulty
            - Tema: $subject
        """.trimIndent()

        return sendChatMessage(prompt, model)
    }

    suspend fun generateStudyPlan(
        subjects: List<String>,
        hoursPerDay: Int,
        goals: String,
        model: AIModel = AIModel.GPT_4
    ): Result<String> {
        val prompt = """
            Crea un plan de estudio semanal detallado con las siguientes especificaciones:
            
            MATERIAS: ${subjects.joinToString(", ")}
            HORAS DISPONIBLES: $hoursPerDay horas diarias
            OBJETIVOS: $goals
            
            FORMATO REQUERIDO (JSON):
            {
              "weeklyPlan": {
                "totalHours": 28,
                "days": [
                  {
                    "day": "Lunes",
                    "sessions": [
                      {
                        "subject": "Matemáticas",
                        "topic": "Cálculo diferencial",
                        "duration": 120,
                        "type": "study",
                        "timeSlot": "9:00-11:00"
                      }
                    ]
                  }
                ]
              },
              "recommendations": [
                "Recomendación 1",
                "Recomendación 2"
              ]
            }
            
            TIPOS DE SESIÓN: study, review, practice, exam_prep
            DURACIÓN: en minutos
        """.trimIndent()

        return sendChatMessage(prompt, model)
    }
}