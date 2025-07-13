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
}