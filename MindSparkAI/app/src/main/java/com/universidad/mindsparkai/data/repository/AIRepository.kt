package com.universidad.mindsparkai.data.repository

import com.universidad.mindsparkai.BuildConfig
import com.universidad.mindsparkai.data.network.*
import com.universidad.mindsparkai.utils.APIKeysConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIRepository @Inject constructor(
    private val aiService: AIService
) {

    // API Keys - Usando BuildConfig con fallback
    private val openAIKey: String get() = try {
        BuildConfig.OPENAI_API_KEY.takeIf { it.isNotEmpty() }
            ?: "demo-key"
    } catch (e: Exception) {
        "demo-key"
    }

    private val claudeKey: String get() = try {
        BuildConfig.CLAUDE_API_KEY.takeIf { it.isNotEmpty() }
            ?: "demo-key"
    } catch (e: Exception) {
        "demo-key"
    }

    private val geminiKey: String get() = try {
        BuildConfig.GEMINI_API_KEY.takeIf { it.isNotEmpty() }
            ?: "demo-key"
    } catch (e: Exception) {
        "demo-key"
    }

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
            // En caso de error, devolver respuesta demo
            Result.success(generateDemoResponse(message, model))
        }
    }

    private fun generateDemoResponse(message: String, model: AIModel): String {
        val responses = listOf(
            "Esta es una respuesta de demostración del modelo ${model.displayName}. Tu pregunta sobre '$message' sería procesada por la IA real cuando configures las API keys.",
            "Excelente pregunta. El modelo ${model.displayName} te ayudaría a resolver: '$message'. Para obtener respuestas reales, configura las API keys en local.properties.",
            "🤖 Respuesta simulada de ${model.displayName}: He analizado tu consulta '$message'. Esta funcionalidad estará disponible cuando configures las claves de API reales.",
            "Basándome en tu pregunta sobre '$message', ${model.displayName} proporcionaría una respuesta detallada. Actualmente ejecutándose en modo demostración."
        )
        return responses.random()
    }

    private suspend fun sendOpenAIMessage(
        message: String,
        model: AIModel,
        context: List<String>
    ): Result<String> {
        return try {
            // Verificar API key
            if (openAIKey == "demo-key" || openAIKey.length < 20) {
                return Result.success(generateDemoResponse(message, model))
            }

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
                AIModel.GPT_4 -> "gpt-3.5-turbo" // Usar 3.5 si no tienes créditos para GPT-4
                AIModel.GPT_3_5 -> "gpt-3.5-turbo"
                else -> "gpt-3.5-turbo"
            }

            val request = OpenAIRequest(
                model = modelName,
                messages = messages,
                max_tokens = 500, // Reducir tokens para evitar rate limits
                temperature = 0.7
            )

            val response = aiService.getOpenAICompletion(
                authorization = "Bearer $openAIKey",
                request = request
            )

            if (response.isSuccessful) {
                val content = response.body()?.choices?.firstOrNull()?.message?.content
                if (content != null) {
                    Result.success(content)
                } else {
                    Result.success(generateDemoResponse(message, model))
                }
            } else {
                val errorCode = response.code()
                when (errorCode) {
                    404 -> Result.success("Error 404: Endpoint no encontrado. Usando respuesta demo: ${generateDemoResponse(message, model)}")
                    429 -> Result.success("Rate limit excedido. Usando respuesta demo: ${generateDemoResponse(message, model)}")
                    else -> Result.success(generateDemoResponse(message, model))
                }
            }
        } catch (e: Exception) {
            Result.success("Error de conexión: ${e.message}. Respuesta demo: ${generateDemoResponse(message, model)}")
        }
    }

    private suspend fun sendClaudeMessage(
        message: String,
        model: AIModel,
        context: List<String>
    ): Result<String> {
        // Claude siempre en modo demo por ahora
        return Result.success(generateDemoResponse(message, model))
    }

    private suspend fun sendGeminiMessage(
        message: String,
        model: AIModel,
        context: List<String>
    ): Result<String> {
        return try {
            // Verificar API key
            if (geminiKey == "demo-key" || geminiKey.length < 20) {
                return Result.success(generateDemoResponse(message, model))
            }

            val contents = mutableListOf<GeminiContent>()

            // Agregar mensaje actual
            contents.add(GeminiContent(
                parts = listOf(GeminiPart(message)),
                role = "user"
            ))

            val request = GeminiRequest(contents = contents)

            val response = aiService.getGeminiCompletion(
                model = "gemini-pro",
                apiKey = geminiKey,
                request = request
            )

            if (response.isSuccessful) {
                val content = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (content != null) {
                    Result.success(content)
                } else {
                    Result.success(generateDemoResponse(message, model))
                }
            } else {
                Result.success(generateDemoResponse(message, model))
            }
        } catch (e: Exception) {
            Result.success(generateDemoResponse(message, model))
        }
    }

    suspend fun generateSummary(
        text: String,
        model: AIModel = AIModel.GPT_3_5 // Usar GPT-3.5 por defecto
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
        model: AIModel = AIModel.GPT_3_5
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
        model: AIModel = AIModel.GPT_3_5
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

    // Función para verificar estado de APIs
    fun getApiStatus(): Map<String, String> {
        return mapOf(
            "openai" to if (openAIKey.length > 20) "Configurada" else "Demo",
            "claude" to "Demo",
            "gemini" to if (geminiKey.length > 20) "Configurada" else "Demo",
            "mode" to "Hybrid"
        )
    }
}