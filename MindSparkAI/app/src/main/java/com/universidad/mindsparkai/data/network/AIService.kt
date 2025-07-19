package com.universidad.mindsparkai.data.network

import retrofit2.Response
import retrofit2.http.*

// Interfaz para múltiples proveedores de IA
interface AIService {

    // OpenAI GPT
    @POST("v1/chat/completions")
    suspend fun getOpenAICompletion(
        @Header("Authorization") authorization: String,
        @Body request: OpenAIRequest
    ): Response<OpenAIResponse>

    // Anthropic Claude
    @POST("v1/messages")
    suspend fun getClaudeCompletion(
        @Header("x-api-key") apiKey: String,
        @Header("anthropic-version") version: String = "2023-06-01",
        @Body request: ClaudeRequest
    ): Response<ClaudeResponse>

    // Google Gemini
    @POST("v1beta/models/{model}:generateContent")
    suspend fun getGeminiCompletion(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}

// OpenAI Models
data class OpenAIRequest(
    val model: String = "gpt-4",
    val messages: List<OpenAIMessage>,
    val max_tokens: Int = 1000,
    val temperature: Double = 0.7,
    val stream: Boolean = false
)

data class OpenAIMessage(
    val role: String, // "user", "assistant", "system"
    val content: String
)

data class OpenAIResponse(
    val id: String,
    val choices: List<OpenAIChoice>,
    val usage: OpenAIUsage
)

data class OpenAIChoice(
    val index: Int,
    val message: OpenAIMessage,
    val finish_reason: String
)

data class OpenAIUsage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

// Claude Models
data class ClaudeRequest(
    val model: String = "claude-3-sonnet-20240229",
    val max_tokens: Int = 1000,
    val messages: List<ClaudeMessage>
)

data class ClaudeMessage(
    val role: String, // "user", "assistant"
    val content: String
)

data class ClaudeResponse(
    val id: String,
    val type: String,
    val role: String,
    val content: List<ClaudeContent>,
    val model: String,
    val stop_reason: String,
    val usage: ClaudeUsage
)

data class ClaudeContent(
    val type: String,
    val text: String
)

data class ClaudeUsage(
    val input_tokens: Int,
    val output_tokens: Int
)

// Gemini Models
data class GeminiRequest(
    val contents: List<GeminiContent>
)

data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String = "user"
)

data class GeminiPart(
    val text: String
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>,
    val usageMetadata: GeminiUsage?
)

data class GeminiCandidate(
    val content: GeminiContent,
    val finishReason: String?,
    val index: Int
)

data class GeminiUsage(
    val promptTokenCount: Int,
    val candidatesTokenCount: Int,
    val totalTokenCount: Int
)
