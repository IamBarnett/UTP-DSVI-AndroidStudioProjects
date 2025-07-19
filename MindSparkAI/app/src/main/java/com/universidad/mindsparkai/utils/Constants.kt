package com.universidad.mindsparkai.utils

object Constants {

    // API Configuration
    const val OPENAI_BASE_URL = "https://api.openai.com/"
    const val CLAUDE_BASE_URL = "https://api.anthropic.com/"
    const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"
    const val DEFAULT_TIMEOUT = 60L
    const val MAX_TOKENS = 1000
    const val DEFAULT_TEMPERATURE = 0.7

    // AI Models
    const val MODEL_GPT4 = "gpt-4"
    const val MODEL_GPT35_TURBO = "gpt-3.5-turbo"
    const val MODEL_CLAUDE3_SONNET = "claude-3-sonnet-20240229"
    const val MODEL_CLAUDE3_HAIKU = "claude-3-haiku-20240307"
    const val MODEL_GEMINI_PRO = "gemini-pro"
    const val MODEL_GEMINI_PRO_VISION = "gemini-pro-vision"

    // File Upload
    const val MAX_FILE_SIZE = 10 * 1024 * 1024 // 10MB
    const val MAX_TEXT_LENGTH = 50000 // 50k characters
    val SUPPORTED_FILE_TYPES = listOf("pdf", "docx", "txt", "md")
    val SUPPORTED_IMAGE_TYPES = listOf("jpg", "jpeg", "png", "webp")

    // Quiz Configuration
    const val DEFAULT_QUIZ_QUESTIONS = 5
    const val MIN_QUIZ_QUESTIONS = 3
    const val MAX_QUIZ_QUESTIONS = 20
    const val QUIZ_TIMEOUT_MINUTES = 30

    // Study Plan
    const val MIN_STUDY_HOURS = 1
    const val MAX_STUDY_HOURS = 12
    const val DEFAULT_STUDY_HOURS = 4
    const val MAX_SUBJECTS_PER_PLAN = 8
    const val SESSION_MIN_DURATION = 15 // minutes
    const val SESSION_MAX_DURATION = 180 // minutes

    // Preferences Keys
    const val PREF_SELECTED_MODEL = "selected_model"
    const val PREF_STUDY_HOURS = "study_hours_per_day"
    const val PREF_DARK_MODE = "dark_mode"
    const val PREF_NOTIFICATIONS = "notifications_enabled"
    const val PREF_AUTO_BACKUP = "auto_backup_enabled"
    const val PREF_BACKUP_WIFI_ONLY = "backup_wifi_only"
    const val PREF_FIRST_TIME_USER = "is_first_time_user"
    const val PREF_ONBOARDING_COMPLETED = "onboarding_completed"
    const val PREF_LAST_SYNC_TIME = "last_sync_time"
    const val PREF_LANGUAGE = "app_language"
    const val PREF_REMINDER_TIME = "reminder_time"
    const val PREF_REMINDER_ENABLED = "reminder_enabled"

    // Database Collections (Firebase)
    const val COLLECTION_USERS = "users"
    const val COLLECTION_CHAT_HISTORY = "chat_history"
    const val COLLECTION_QUIZ_RESULTS = "quiz_results"
    const val COLLECTION_STUDY_PLANS = "study_plans"
    const val COLLECTION_SUMMARIES = "summaries"
    const val COLLECTION_FEEDBACK = "feedback"
    const val COLLECTION_ANALYTICS = "analytics"

    // Local Database
    const val DATABASE_NAME = "mindspark_database"
    const val DATABASE_VERSION = 1

    // Cache Configuration
    const val CACHE_SIZE = 10 * 1024 * 1024 // 10MB
    const val CACHE_MAX_AGE = 24 * 60 * 60 // 24 hours in seconds
    const val OFFLINE_CACHE_SIZE = 50 // number of items

    // Network Configuration
    const val CONNECT_TIMEOUT = 30L // seconds
    const val READ_TIMEOUT = 60L // seconds
    const val WRITE_TIMEOUT = 60L // seconds
    const val MAX_RETRIES = 3
    const val RETRY_DELAY_MS = 1000L

    // Error Messages
    const val ERROR_NETWORK = "Error de conexión. Verifica tu internet."
    const val ERROR_AUTH = "Error de autenticación."
    const val ERROR_GENERIC = "Ocurrió un error inesperado."
    const val ERROR_FILE_SIZE = "El archivo es demasiado grande."
    const val ERROR_FILE_FORMAT = "Formato de archivo no soportado."
    const val ERROR_API_KEY_MISSING = "API key no configurada."
    const val ERROR_API_QUOTA_EXCEEDED = "Cuota de API excedida."
    const val ERROR_TEXT_TOO_SHORT = "El texto es demasiado corto para procesar."
    const val ERROR_NO_SUBJECTS_SELECTED = "Selecciona al menos una materia."
    const val ERROR_INVALID_HOURS = "Las horas deben estar entre 1 y 12."

    // Success Messages
    const val SUCCESS_LOGIN = "Inicio de sesión exitoso"
    const val SUCCESS_REGISTER = "Cuenta creada exitosamente"
    const val SUCCESS_SUMMARY = "Resumen generado correctamente"
    const val SUCCESS_QUIZ = "Quiz generado exitosamente"
    const val SUCCESS_PLAN = "Plan de estudio creado"
    const val SUCCESS_DATA_EXPORTED = "Datos exportados exitosamente"
    const val SUCCESS_SETTINGS_SAVED = "Configuración guardada"

    // Notification Channels
    const val NOTIFICATION_CHANNEL_STUDY = "study_reminders"
    const val NOTIFICATION_CHANNEL_GENERAL = "general_notifications"
    const val NOTIFICATION_CHANNEL_UPDATES = "app_updates"

    // Notification IDs
    const val NOTIFICATION_ID_STUDY_REMINDER = 1001
    const val NOTIFICATION_ID_DAILY_GOAL = 1002
    const val NOTIFICATION_ID_WEEKLY_SUMMARY = 1003
    const val NOTIFICATION_ID_UPDATE_AVAILABLE = 1004

    // Intent Extras
    const val EXTRA_CHAT_MESSAGE = "extra_chat_message"
    const val EXTRA_SUBJECT = "extra_subject"
    const val EXTRA_DIFFICULTY = "extra_difficulty"
    const val EXTRA_QUIZ_RESULT = "extra_quiz_result"
    const val EXTRA_STUDY_PLAN_ID = "extra_study_plan_id"
    const val EXTRA_SUMMARY_TEXT = "extra_summary_text"
    const val EXTRA_FILE_URI = "extra_file_uri"

    // Animation Durations
    const val ANIMATION_DURATION_SHORT = 200L
    const val ANIMATION_DURATION_MEDIUM = 300L
    const val ANIMATION_DURATION_LONG = 500L
    const val TYPING_INDICATOR_DELAY = 1500L

    // Pagination
    const val PAGE_SIZE = 20
    const val PREFETCH_DISTANCE = 5

    // Text Limits
    const val CHAT_MESSAGE_MAX_LENGTH = 2000
    const val SUMMARY_MIN_LENGTH = 100
    const val QUIZ_QUESTION_MAX_LENGTH = 500
    const val STUDY_GOAL_MAX_LENGTH = 200
    const val USER_NAME_MAX_LENGTH = 50

    // Reading Speed (words per minute)
    const val AVERAGE_READING_SPEED = 250
    const val FAST_READING_SPEED = 400
    const val SLOW_READING_SPEED = 150

    // Study Session Types
    const val SESSION_TYPE_STUDY = "study"
    const val SESSION_TYPE_PRACTICE = "practice"
    const val SESSION_TYPE_REVIEW = "review"
    const val SESSION_TYPE_EXAM_PREP = "exam_prep"
    const val SESSION_TYPE_HOMEWORK = "homework"
    const val SESSION_TYPE_RESEARCH = "research"

    // Difficulty Levels
    const val DIFFICULTY_EASY = "easy"
    const val DIFFICULTY_MEDIUM = "medium"
    const val DIFFICULTY_HARD = "hard"

    // Quiz Performance Thresholds
    const val PERFORMANCE_EXCELLENT_THRESHOLD = 90
    const val PERFORMANCE_VERY_GOOD_THRESHOLD = 80
    const val PERFORMANCE_GOOD_THRESHOLD = 70
    const val PERFORMANCE_REGULAR_THRESHOLD = 60

    // Study Streak
    const val MIN_STUDY_TIME_FOR_STREAK = 30 // minutes
    const val STREAK_RESET_HOURS = 48 // hours without studying to reset streak

    // Export/Import
    const val EXPORT_FILE_PREFIX = "mindspark_backup_"
    const val EXPORT_FILE_EXTENSION = ".json"
    const val IMPORT_MAX_FILE_SIZE = 50 * 1024 * 1024 // 50MB

    // Analytics Events
    const val EVENT_CHAT_MESSAGE_SENT = "chat_message_sent"
    const val EVENT_SUMMARY_GENERATED = "summary_generated"
    const val EVENT_QUIZ_COMPLETED = "quiz_completed"
    const val EVENT_STUDY_PLAN_CREATED = "study_plan_created"
    const val EVENT_USER_REGISTERED = "user_registered"
    const val EVENT_AI_MODEL_SWITCHED = "ai_model_switched"
    const val EVENT_FILE_UPLOADED = "file_uploaded"
    const val EVENT_FEATURE_USED = "feature_used"

    // Feature Flags
    const val FEATURE_OFFLINE_MODE = "offline_mode_enabled"
    const val FEATURE_VOICE_INPUT = "voice_input_enabled"
    const val FEATURE_ADVANCED_ANALYTICS = "advanced_analytics_enabled"
    const val FEATURE_PREMIUM_MODELS = "premium_models_enabled"
    const val FEATURE_COLLABORATIVE_STUDY = "collaborative_study_enabled"

    // URL Patterns
    const val URL_PRIVACY_POLICY = "https://mindsparkai.com/privacy"
    const val URL_TERMS_OF_SERVICE = "https://mindsparkai.com/terms"
    const val URL_SUPPORT = "https://mindsparkai.com/support"
    const val URL_DOCUMENTATION = "https://docs.mindsparkai.com"
    const val URL_FEEDBACK = "https://mindsparkai.com/feedback"

    // Deep Links
    const val DEEP_LINK_CHAT = "mindspark://chat"
    const val DEEP_LINK_SUMMARY = "mindspark://summary"
    const val DEEP_LINK_QUIZ = "mindspark://quiz"
    const val DEEP_LINK_STUDY_PLAN = "mindspark://study-plan"
    const val DEEP_LINK_PROFILE = "mindspark://profile"

    // Backup Configuration
    const val BACKUP_INTERVAL_HOURS = 24
    const val MAX_BACKUP_FILES = 5
    const val BACKUP_COMPRESSION_LEVEL = 6

    // Security
    const val SESSION_TIMEOUT_MINUTES = 30
    const val MAX_LOGIN_ATTEMPTS = 5
    const val LOCKOUT_DURATION_MINUTES = 15
    const val PASSWORD_MIN_LENGTH = 6
    const val PASSWORD_MAX_LENGTH = 128

    // Rate Limiting
    const val RATE_LIMIT_CHAT_PER_MINUTE = 10
    const val RATE_LIMIT_SUMMARY_PER_HOUR = 20
    const val RATE_LIMIT_QUIZ_PER_HOUR = 15
    const val RATE_LIMIT_PLAN_PER_DAY = 5

    // UI Constants
    const val BOTTOM_SHEET_PEEK_HEIGHT = 200 // dp
    const val FAB_MARGIN = 16 // dp
    const val CARD_CORNER_RADIUS = 12 // dp
    const val ELEVATION_CARD = 2 // dp
    const val ELEVATION_APP_BAR = 4 // dp

    // Accessibility
    const val MIN_TOUCH_TARGET_SIZE = 48 // dp
    const val FOCUS_BORDER_WIDTH = 2 // dp

    // Performance
    const val IMAGE_CACHE_SIZE = 50 * 1024 * 1024 // 50MB
    const val RECYCLER_VIEW_CACHE_SIZE = 20
    const val DEBOUNCE_DELAY_MS = 300L

    // Testing
    const val TEST_TIMEOUT_SECONDS = 30
    const val TEST_ANIMATION_DURATION = 100L

    // Versioning
    const val API_VERSION = "v1"
    const val MIN_SUPPORTED_API_LEVEL = 26
    const val TARGET_API_LEVEL = 34

    // Logging
    const val LOG_TAG = "MindSparkAI"
    const val MAX_LOG_FILE_SIZE = 5 * 1024 * 1024 // 5MB
    const val MAX_LOG_FILES = 3

    // Default Values
    object Defaults {
        const val STUDY_HOURS_PER_DAY = 4
        const val QUIZ_QUESTIONS_COUNT = 5
        const val SUMMARY_TYPE = "detailed"
        const val AI_MODEL = "GPT-4"
        const val DIFFICULTY = "medium"
        const val SESSION_DURATION = 60 // minutes
        const val BREAK_DURATION = 15 // minutes
        const val NOTIFICATION_TIME = "20:00"
        val SUBJECTS = listOf("Matemáticas", "Química", "Física")
    }

    // Color Codes for Subjects
    object SubjectColors {
        const val MATHEMATICS = "#FF6B6B"
        const val CHEMISTRY = "#4ECDC4"
        const val PHYSICS = "#45B7D1"
        const val HISTORY = "#96CEB4"
        const val LITERATURE = "#FECA57"
        const val BIOLOGY = "#FF9FF3"
        const val ENGLISH = "#54A0FF"
        const val PHILOSOPHY = "#5F27CD"
        const val ECONOMICS = "#00D2D3"
        const val PROGRAMMING = "#FF9F43"
        const val DEFAULT = "#6C7CE7"
    }

    // Emoji Sets
    object Emojis {
        const val BRAIN = "🧠"
        const val BOOK = "📚"
        const val LIGHTBULB = "💡"
        const val ROCKET = "🚀"
        const val STAR = "⭐"
        const val FIRE = "🔥"
        const val TROPHY = "🏆"
        const val TARGET = "🎯"
        const val CHART = "📊"
        const val CALENDAR = "📅"
        const val TIME = "⏰"
        const val CHECK = "✅"
        const val CROSS = "❌"
        const val WARNING = "⚠️"
        const val INFO = "ℹ️"
    }
}