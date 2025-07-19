package com.universidad.mindsparkai.utils

object APIKeysConfig {

    // En producción, estas keys deberían venir de:
    // 1. BuildConfig (configuradas en build.gradle)
    // 2. Variables de entorno
    // 3. Servicio de configuración remota como Firebase Remote Config
    // 4. Nunca hardcodeadas en el código

    // Para desarrollo/demo - REEMPLAZA CON TUS KEYS REALES
    const val OPENAI_API_KEY = "sk-your-openai-api-key-here"
    const val CLAUDE_API_KEY = "your-claude-api-key-here"
    const val GEMINI_API_KEY = "your-gemini-api-key-here"

    // URLs base de las APIs
    const val OPENAI_BASE_URL = "https://api.openai.com/"
    const val CLAUDE_BASE_URL = "https://api.anthropic.com/"
    const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"

    // Función para validar si las keys están configuradas
    fun areKeysConfigured(): Boolean {
        return OPENAI_API_KEY != "sk-your-openai-api-key-here" ||
                CLAUDE_API_KEY != "your-claude-api-key-here" ||
                GEMINI_API_KEY != "your-gemini-api-key-here"
    }

    // Función para obtener la key según el modelo
    fun getApiKey(provider: String): String? {
        return when (provider.lowercase()) {
            "openai" -> if (OPENAI_API_KEY.startsWith("sk-")) OPENAI_API_KEY else null
            "anthropic", "claude" -> if (CLAUDE_API_KEY != "your-claude-api-key-here") CLAUDE_API_KEY else null
            "google", "gemini" -> if (GEMINI_API_KEY != "your-gemini-api-key-here") GEMINI_API_KEY else null
            else -> null
        }
    }

    // Función para verificar qué modelos están disponibles
    fun getAvailableProviders(): List<String> {
        val providers = mutableListOf<String>()

        if (getApiKey("openai") != null) providers.add("openai")
        if (getApiKey("claude") != null) providers.add("anthropic")
        if (getApiKey("gemini") != null) providers.add("google")

        return providers
    }
}