package com.universidad.mindsparkai.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.format.DateUtils
import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    /**
     * Check if device has internet connection
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    /**
     * Validate email format
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Validate password strength
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    /**
     * Enhanced password validation
     */
    fun validatePasswordStrength(password: String): PasswordStrength {
        return when {
            password.length < 6 -> PasswordStrength.WEAK
            password.length < 8 -> PasswordStrength.MEDIUM
            password.length >= 8 && password.contains(Regex("[A-Z]")) &&
                    password.contains(Regex("[a-z]")) && password.contains(Regex("[0-9]")) -> PasswordStrength.STRONG
            else -> PasswordStrength.MEDIUM
        }
    }

    enum class PasswordStrength(val message: String, val color: String) {
        WEAK("Débil - Mínimo 6 caracteres", "#F44336"),
        MEDIUM("Media - Agrega mayúsculas y números", "#FF9800"),
        STRONG("Fuerte - Excelente seguridad", "#4CAF50")
    }

    /**
     * Format timestamp to relative time
     */
    fun formatTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        return DateUtils.getRelativeTimeSpanString(
            timestamp,
            now,
            DateUtils.MINUTE_IN_MILLIS
        ).toString()
    }

    /**
     * Format date for display
     */
    fun formatDate(timestamp: Long, pattern: String = "dd/MM/yyyy"): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    /**
     * Format date with day name
     */
    fun formatDateWithDay(timestamp: Long): String {
        val formatter = SimpleDateFormat("EEEE, dd 'de' MMMM", Locale("es", "ES"))
        return formatter.format(Date(timestamp))
    }

    /**
     * Get file extension from filename
     */
    fun getFileExtension(filename: String): String {
        return filename.substringAfterLast('.', "").lowercase()
    }

    /**
     * Check if file type is supported
     */
    fun isSupportedFileType(filename: String): Boolean {
        val extension = getFileExtension(filename)
        return Constants.SUPPORTED_FILE_TYPES.contains(extension)
    }

    /**
     * Format file size to human readable format
     */
    fun formatFileSize(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0

        return when {
            gb >= 1 -> String.format("%.1f GB", gb)
            mb >= 1 -> String.format("%.1f MB", mb)
            kb >= 1 -> String.format("%.1f KB", kb)
            else -> "$bytes B"
        }
    }

    /**
     * Truncate text to specified length
     */
    fun truncateText(text: String, maxLength: Int): String {
        return if (text.length <= maxLength) {
            text
        } else {
            text.take(maxLength - 3) + "..."
        }
    }

    /**
     * Generate random ID
     */
    fun generateId(): String {
        return UUID.randomUUID().toString()
    }

    /**
     * Calculate reading time estimate
     */
    fun estimateReadingTime(text: String, wordsPerMinute: Int = 250): Int {
        val wordCount = text.split("\\s+".toRegex()).size
        return maxOf(1, (wordCount / wordsPerMinute))
    }

    /**
     * Get greeting based on time of day
     */
    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Buenos días"
            in 12..17 -> "Buenas tardes"
            else -> "Buenas noches"
        }
    }

    /**
     * Get personalized greeting with name
     */
    fun getPersonalizedGreeting(name: String): String {
        return "${getGreeting()}, $name"
    }

    /**
     * Get icon for subject
     */
    fun getSubjectIcon(subject: String): String {
        return when (subject.lowercase()) {
            "matemáticas", "matematicas", "math" -> "📐"
            "química", "quimica", "chemistry" -> "🧪"
            "física", "fisica", "physics" -> "⚛️"
            "biología", "biologia", "biology" -> "🧬"
            "historia", "history" -> "📚"
            "literatura", "literature" -> "📖"
            "inglés", "ingles", "english" -> "🇬🇧"
            "filosofía", "filosofia", "philosophy" -> "🤔"
            "economía", "economia", "economics" -> "💰"
            "programación", "programacion", "programming" -> "💻"
            "psicología", "psicologia", "psychology" -> "🧠"
            "sociología", "sociologia", "sociology" -> "👥"
            "geografia", "geography" -> "🌍"
            "arte", "art" -> "🎨"
            "música", "musica", "music" -> "🎵"
            else -> "📋"
        }
    }

    /**
     * Get difficulty color
     */
    fun getDifficultyColor(difficulty: String): String {
        return when (difficulty.lowercase()) {
            "easy", "fácil", "facil" -> "#4CAF50" // Green
            "medium", "intermedio", "medio" -> "#FF9800" // Orange
            "hard", "difícil", "dificil" -> "#F44336" // Red
            else -> "#2196F3" // Blue
        }
    }

    /**
     * Get AI model color
     */
    fun getModelColor(model: String): String {
        return when (model.lowercase()) {
            "gpt-4", "gpt-3.5" -> "#10A37F" // OpenAI Green
            "claude-3", "claude" -> "#CC785C" // Anthropic Orange
            "gemini", "gemini pro" -> "#4285F4" // Google Blue
            else -> "#6B73FF" // Default Purple
        }
    }

    /**
     * Format study time
     */
    fun formatStudyTime(minutes: Int): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60

        return when {
            hours > 0 && remainingMinutes > 0 -> "${hours}h ${remainingMinutes}min"
            hours > 0 -> "${hours}h"
            else -> "${remainingMinutes}min"
        }
    }

    /**
     * Calculate percentage
     */
    fun calculatePercentage(correct: Int, total: Int): Int {
        return if (total > 0) (correct * 100) / total else 0
    }

    /**
     * Format percentage with context
     */
    fun formatPercentageWithContext(percentage: Int): Pair<String, String> {
        val description = when {
            percentage >= 90 -> "Excelente"
            percentage >= 80 -> "Muy bueno"
            percentage >= 70 -> "Bueno"
            percentage >= 60 -> "Regular"
            else -> "Necesita mejora"
        }
        return Pair("$percentage%", description)
    }

    /**
     * Validate study session data
     */
    fun validateStudySession(subject: String, duration: Int, type: String): Boolean {
        return subject.isNotBlank() &&
                duration > 0 &&
                type in listOf("study", "practice", "review", "exam_prep")
    }

    /**
     * Generate study session ID
     */
    fun generateStudySessionId(subject: String, timestamp: Long): String {
        return "${subject.lowercase().replace(" ", "_")}_${timestamp}"
    }

    /**
     * Clean text for AI processing
     */
    fun cleanTextForAI(text: String): String {
        return text.trim()
            .replace(Regex("\\s+"), " ") // Replace multiple spaces with single space
            .replace(Regex("[\\r\\n]+"), "\n") // Normalize line breaks
    }

    /**
     * Extract keywords from text
     */
    fun extractKeywords(text: String, maxKeywords: Int = 10): List<String> {
        val stopWords = setOf(
            "el", "la", "de", "que", "y", "a", "en", "un", "es", "se", "no", "te", "lo", "le", "da", "su", "por", "son", "con", "para", "al", "una", "del", "las", "los", "pero", "su", "me", "si", "sin", "sobre", "esta", "entre", "cuando", "muy", "sin", "hasta", "desde", "entre"
        )

        return text.lowercase()
            .split(Regex("[\\s.,;:!?()\\[\\]{}\"'-]+"))
            .filter { it.length > 3 && !stopWords.contains(it) }
            .groupBy { it }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .take(maxKeywords)
            .map { it.first }
    }

    /**
     * Generate color based on string hash
     */
    fun generateColorFromString(input: String): String {
        val colors = listOf(
            "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FECA57",
            "#FF9FF3", "#54A0FF", "#5F27CD", "#00D2D3", "#FF9F43"
        )
        val hash = input.hashCode()
        val index = Math.abs(hash) % colors.size
        return colors[index]
    }

    /**
     * Safe string to int conversion
     */
    fun safeStringToInt(value: String, default: Int = 0): Int {
        return try {
            value.toInt()
        } catch (e: NumberFormatException) {
            default
        }
    }

    /**
     * Format quiz score
     */
    fun formatQuizScore(correct: Int, total: Int): String {
        val percentage = calculatePercentage(correct, total)
        val (percentageStr, description) = formatPercentageWithContext(percentage)
        return "$correct/$total ($percentageStr) - $description"
    }

    /**
     * Get motivational message based on score
     */
    fun getMotivationalMessage(percentage: Int): String {
        return when {
            percentage >= 90 -> "¡Excelente trabajo! 🌟 Dominas el tema perfectamente."
            percentage >= 80 -> "¡Muy bien! 👏 Tienes un buen dominio del tema."
            percentage >= 70 -> "¡Buen trabajo! 👍 Vas por el camino correcto."
            percentage >= 60 -> "Bien hecho 📚 Sigue practicando para mejorar."
            else -> "¡No te rindas! 💪 La práctica hace al maestro."
        }
    }

    /**
     * Check if text is likely to be academic content
     */
    fun isAcademicContent(text: String): Boolean {
        val academicKeywords = listOf(
            "teoría", "concepto", "definición", "análisis", "estudio", "investigación",
            "método", "proceso", "sistema", "estructura", "función", "principio",
            "aplicación", "resultado", "conclusión", "hipótesis", "evidencia"
        )

        val lowercaseText = text.lowercase()
        val keywordCount = academicKeywords.count { lowercaseText.contains(it) }

        return keywordCount >= 3 || text.length > 500
    }
}