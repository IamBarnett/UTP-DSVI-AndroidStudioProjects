package com.universidad.mindsparkai.utils

import com.universidad.mindsparkai.BuildConfig

object APIKeysConfig {

    // API Keys desde BuildConfig (configuradas en build.gradle)
    val OPENAI_API_KEY: String get() = BuildConfig.OPENAI_API_KEY
    val CLAUDE_API_KEY: String get() = BuildConfig.CLAUDE_API_KEY
    val GEMINI_API_KEY: String get() = BuildConfig.GEMINI_API_KEY

    // URLs base de las APIs
    const val OPENAI_BASE_URL = "https://api.openai.com/"
    const val CLAUDE_BASE_URL = "https://api.anthropic.com/"
    const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"

    // Función para validar si las keys están configuradas
    fun areKeysConfigured(): Boolean {
        return try {
            OPENAI_API_KEY.isNotEmpty() ||
                    CLAUDE_API_KEY.isNotEmpty() ||
                    GEMINI_API_KEY.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    // Función para obtener la key según el modelo
    fun getApiKey(provider: String): String? {
        return try {
            when (provider.lowercase()) {
                "openai" -> if (OPENAI_API_KEY.isNotEmpty()) OPENAI_API_KEY else null
                "anthropic", "claude" -> if (CLAUDE_API_KEY.isNotEmpty()) CLAUDE_API_KEY else null
                "google", "gemini" -> if (GEMINI_API_KEY.isNotEmpty()) GEMINI_API_KEY else null
                else -> null
            }
        } catch (e: Exception) {
            // Fallback para desarrollo si BuildConfig no está disponible
            when (provider.lowercase()) {
                "openai" -> "demo-key-openai"
                "anthropic", "claude" -> "demo-key-claude"
                "google", "gemini" -> "demo-key-gemini"
                else -> null
            }
        }
    }

    // Función para verificar qué modelos están disponibles
    fun getAvailableProviders(): List<String> {
        val providers = mutableListOf<String>()

        try {
            if (getApiKey("openai") != null) providers.add("openai")
            if (getApiKey("claude") != null) providers.add("anthropic")
            if (getApiKey("gemini") != null) providers.add("google")
        } catch (e: Exception) {
            // En caso de error, agregar todos para testing
            providers.addAll(listOf("openai", "anthropic", "google"))
        }

        return providers
    }

    // Función para testing - usar keys de demostración
    fun getDemoKey(provider: String): String {
        return when (provider.lowercase()) {
            "openai" -> "demo-openai-key-for-testing"
            "anthropic", "claude" -> "demo-claude-key-for-testing"
            "google", "gemini" -> "demo-gemini-key-for-testing"
            else -> "demo-key"
        }
    }

    // Función para verificar si estamos en modo demo
    fun isDemoMode(): Boolean {
        return try {
            !areKeysConfigured()
        } catch (e: Exception) {
            true
        }
    }
}