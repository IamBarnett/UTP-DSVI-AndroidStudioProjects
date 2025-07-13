package com.universidad.mindsparkai.utils

object Constants {

    // API Configuration
    const val OPENAI_BASE_URL = "https://api.openai.com/"
    const val DEFAULT_TIMEOUT = 30L
    const val MAX_TOKENS = 500
    const val DEFAULT_TEMPERATURE = 0.7

    // AI Models
    const val MODEL_GPT4 = "gpt-4"
    const val MODEL_CLAUDE3 = "claude-3"
    const val MODEL_GEMINI = "gemini-pro"
    const val MODEL_LLAMA = "llama-2"

    // File Upload
    const val MAX_FILE_SIZE = 10 * 1024 * 1024 // 10MB
    val SUPPORTED_FILE_TYPES = listOf("pdf", "docx", "txt")

    // Quiz Configuration
    const val DEFAULT_QUIZ_QUESTIONS = 5
    const val MAX_QUIZ_QUESTIONS = 20

    // Study Plan
    const val MIN_STUDY_HOURS = 1
    const val MAX_STUDY_HOURS = 12

    // Preferences Keys
    const val PREF_SELECTED_MODEL = "selected_model"
    const val PREF_STUDY_HOURS = "study_hours_per_day"
    const val PREF_DARK_MODE = "dark_mode"
    const val PREF_NOTIFICATIONS = "notifications_enabled"

    // Database Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_CHAT_HISTORY = "chat_history"
    const val COLLECTION_QUIZ_RESULTS = "quiz_results"
    const val COLLECTION_STUDY_PLANS = "study_plans"

    // Error Messages
    const val ERROR_NETWORK = "Error de conexión. Verifica tu internet."
    const val ERROR_AUTH = "Error de autenticación."
    const val ERROR_GENERIC = "Ocurrió un error inesperado."
    const val ERROR_FILE_SIZE = "El archivo es demasiado grande."
    const val ERROR_FILE_FORMAT = "Formato de archivo no soportado."

    // Success Messages
    const val SUCCESS_LOGIN = "Inicio de sesión exitoso"
    const val SUCCESS_REGISTER = "Cuenta creada exitosamente"
    const val SUCCESS_SUMMARY = "Resumen generado correctamente"
    const val SUCCESS_QUIZ = "Quiz generado exitosamente"
    const val SUCCESS_PLAN = "Plan de estudio creado"
}