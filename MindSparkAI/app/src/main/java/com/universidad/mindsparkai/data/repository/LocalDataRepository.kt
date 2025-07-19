package com.universidad.mindsparkai.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.universidad.mindsparkai.data.models.ChatMessage
import com.universidad.mindsparkai.data.models.QuizQuestion
import com.universidad.mindsparkai.data.models.StudyPlan
import com.universidad.mindsparkai.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val gson = Gson()

    // SharedPreferences normales para datos no sensibles
    private val preferences: SharedPreferences = context.getSharedPreferences(
        "mindspark_prefs", Context.MODE_PRIVATE
    )

    // SharedPreferences encriptadas para datos sensibles
    private val encryptedPreferences: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "mindspark_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // MARK: - User Preferences

    fun saveSelectedModel(model: String) {
        preferences.edit()
            .putString(Constants.PREF_SELECTED_MODEL, model)
            .apply()
    }

    fun getSelectedModel(): String {
        return preferences.getString(Constants.PREF_SELECTED_MODEL, "GPT-4") ?: "GPT-4"
    }

    fun saveStudyHoursPerDay(hours: Int) {
        preferences.edit()
            .putInt(Constants.PREF_STUDY_HOURS, hours)
            .apply()
    }

    fun getStudyHoursPerDay(): Int {
        return preferences.getInt(Constants.PREF_STUDY_HOURS, 4)
    }

    fun saveDarkModeEnabled(enabled: Boolean) {
        preferences.edit()
            .putBoolean(Constants.PREF_DARK_MODE, enabled)
            .apply()
    }

    fun isDarkModeEnabled(): Boolean {
        return preferences.getBoolean(Constants.PREF_DARK_MODE, false)
    }

    fun saveNotificationsEnabled(enabled: Boolean) {
        preferences.edit()
            .putBoolean(Constants.PREF_NOTIFICATIONS, enabled)
            .apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return preferences.getBoolean(Constants.PREF_NOTIFICATIONS, true)
    }

    // MARK: - Chat History

    fun saveChatHistory(messages: List<ChatMessage>) {
        val json = gson.toJson(messages)
        preferences.edit()
            .putString("chat_history", json)
            .apply()
    }

    fun getChatHistory(): List<ChatMessage> {
        val json = preferences.getString("chat_history", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<ChatMessage>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearChatHistory() {
        preferences.edit()
            .remove("chat_history")
            .apply()
    }

    // MARK: - Saved Summaries

    fun saveSummary(id: String, originalText: String, summary: String, model: String) {
        val summaryData = mapOf(
            "id" to id,
            "originalText" to originalText,
            "summary" to summary,
            "model" to model,
            "timestamp" to System.currentTimeMillis()
        )

        val existingSummaries = getSavedSummaries().toMutableList()
        existingSummaries.add(summaryData)

        // Mantener solo los últimos 50 resúmenes
        if (existingSummaries.size > 50) {
            existingSummaries.removeAt(0)
        }

        val json = gson.toJson(existingSummaries)
        preferences.edit()
            .putString("saved_summaries", json)
            .apply()
    }

    fun getSavedSummaries(): List<Map<String, Any>> {
        val json = preferences.getString("saved_summaries", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun deleteSummary(id: String) {
        val summaries = getSavedSummaries().toMutableList()
        summaries.removeAll { it["id"] == id }

        val json = gson.toJson(summaries)
        preferences.edit()
            .putString("saved_summaries", json)
            .apply()
    }

    // MARK: - Quiz Results

    fun saveQuizResult(subject: String, score: Int, total: Int, difficulty: String, model: String) {
        val result = mapOf(
            "id" to "${System.currentTimeMillis()}",
            "subject" to subject,
            "score" to score,
            "total" to total,
            "percentage" to ((score * 100) / total),
            "difficulty" to difficulty,
            "model" to model,
            "timestamp" to System.currentTimeMillis()
        )

        val existingResults = getQuizResults().toMutableList()
        existingResults.add(result)

        // Mantener solo los últimos 100 resultados
        if (existingResults.size > 100) {
            existingResults.removeAt(0)
        }

        val json = gson.toJson(existingResults)
        preferences.edit()
            .putString("quiz_results", json)
            .apply()
    }

    fun getQuizResults(): List<Map<String, Any>> {
        val json = preferences.getString("quiz_results", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getQuizResultsBySubject(subject: String): List<Map<String, Any>> {
        return getQuizResults().filter { it["subject"] == subject }
    }

    // MARK: - Study Plans

    fun saveStudyPlan(studyPlan: StudyPlan) {
        val json = gson.toJson(studyPlan)
        preferences.edit()
            .putString("current_study_plan", json)
            .apply()

        // También guardar en historial
        val planHistory = getStudyPlanHistory().toMutableList()
        planHistory.add(studyPlan)

        // Mantener solo los últimos 10 planes
        if (planHistory.size > 10) {
            planHistory.removeAt(0)
        }

        val historyJson = gson.toJson(planHistory)
        preferences.edit()
            .putString("study_plan_history", historyJson)
            .apply()
    }

    fun getCurrentStudyPlan(): StudyPlan? {
        val json = preferences.getString("current_study_plan", null) ?: return null
        return try {
            gson.fromJson(json, StudyPlan::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun getStudyPlanHistory(): List<StudyPlan> {
        val json = preferences.getString("study_plan_history", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<StudyPlan>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // MARK: - Usage Statistics

    fun incrementAIUsage(provider: String) {
        val key = "ai_usage_$provider"
        val currentCount = preferences.getInt(key, 0)
        preferences.edit()
            .putInt(key, currentCount + 1)
            .apply()
    }

    fun getAIUsageStats(): Map<String, Int> {
        val stats = mutableMapOf<String, Int>()
        stats["openai"] = preferences.getInt("ai_usage_openai", 0)
        stats["anthropic"] = preferences.getInt("ai_usage_anthropic", 0)
        stats["google"] = preferences.getInt("ai_usage_google", 0)
        return stats
    }

    fun incrementFeatureUsage(feature: String) {
        val key = "feature_usage_$feature"
        val currentCount = preferences.getInt(key, 0)
        preferences.edit()
            .putInt(key, currentCount + 1)
            .apply()
    }

    fun getFeatureUsageStats(): Map<String, Int> {
        val stats = mutableMapOf<String, Int>()
        stats["chat"] = preferences.getInt("feature_usage_chat", 0)
        stats["summary"] = preferences.getInt("feature_usage_summary", 0)
        stats["quiz"] = preferences.getInt("feature_usage_quiz", 0)
        stats["study_plan"] = preferences.getInt("feature_usage_study_plan", 0)
        return stats
    }

    fun saveStudySession(subject: String, duration: Int, type: String) {
        val session = mapOf(
            "subject" to subject,
            "duration" to duration,
            "type" to type,
            "timestamp" to System.currentTimeMillis()
        )

        val sessions = getStudySessions().toMutableList()
        sessions.add(session)

        // Mantener solo las últimas 200 sesiones
        if (sessions.size > 200) {
            sessions.removeAt(0)
        }

        val json = gson.toJson(sessions)
        preferences.edit()
            .putString("study_sessions", json)
            .apply()
    }

    fun getStudySessions(): List<Map<String, Any>> {
        val json = preferences.getString("study_sessions", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getTotalStudyTime(): Int {
        return getStudySessions().sumOf {
            (it["duration"] as? Double)?.toInt() ?: 0
        }
    }

    fun getStudyTimeBySubject(): Map<String, Int> {
        val sessions = getStudySessions()
        val timeBySubject = mutableMapOf<String, Int>()

        sessions.forEach { session ->
            val subject = session["subject"] as? String ?: "Unknown"
            val duration = (session["duration"] as? Double)?.toInt() ?: 0
            timeBySubject[subject] = (timeBySubject[subject] ?: 0) + duration
        }

        return timeBySubject
    }

    // MARK: - Offline Data

    fun saveOfflineQuestions(questions: List<QuizQuestion>) {
        val json = gson.toJson(questions)
        preferences.edit()
            .putString("offline_questions", json)
            .apply()
    }

    fun getOfflineQuestions(): List<QuizQuestion> {
        val json = preferences.getString("offline_questions", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<QuizQuestion>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // MARK: - App Settings

    fun setFirstTimeUser(isFirstTime: Boolean) {
        preferences.edit()
            .putBoolean("is_first_time_user", isFirstTime)
            .apply()
    }

    fun isFirstTimeUser(): Boolean {
        return preferences.getBoolean("is_first_time_user", true)
    }

    fun setOnboardingCompleted(completed: Boolean) {
        preferences.edit()
            .putBoolean("onboarding_completed", completed)
            .apply()
    }

    fun isOnboardingCompleted(): Boolean {
        return preferences.getBoolean("onboarding_completed", false)
    }

    fun saveLastSyncTime(timestamp: Long) {
        preferences.edit()
            .putLong("last_sync_time", timestamp)
            .apply()
    }

    fun getLastSyncTime(): Long {
        return preferences.getLong("last_sync_time", 0)
    }

    // MARK: - Favorites

    fun addFavoriteModel(model: String) {
        val favorites = getFavoriteModels().toMutableSet()
        favorites.add(model)
        saveFavoriteModels(favorites.toList())
    }

    fun removeFavoriteModel(model: String) {
        val favorites = getFavoriteModels().toMutableSet()
        favorites.remove(model)
        saveFavoriteModels(favorites.toList())
    }

    private fun saveFavoriteModels(models: List<String>) {
        val json = gson.toJson(models)
        preferences.edit()
            .putString("favorite_models", json)
            .apply()
    }

    fun getFavoriteModels(): List<String> {
        val json = preferences.getString("favorite_models", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun addFavoriteSubject(subject: String) {
        val favorites = getFavoriteSubjects().toMutableSet()
        favorites.add(subject)
        saveFavoriteSubjects(favorites.toList())
    }

    fun removeFavoriteSubject(subject: String) {
        val favorites = getFavoriteSubjects().toMutableSet()
        favorites.remove(subject)
        saveFavoriteSubjects(favorites.toList())
    }

    private fun saveFavoriteSubjects(subjects: List<String>) {
        val json = gson.toJson(subjects)
        preferences.edit()
            .putString("favorite_subjects", json)
            .apply()
    }

    fun getFavoriteSubjects(): List<String> {
        val json = preferences.getString("favorite_subjects", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // MARK: - Export/Import

    fun exportAllData(): String {
        val allData = mapOf(
            "chatHistory" to getChatHistory(),
            "summaries" to getSavedSummaries(),
            "quizResults" to getQuizResults(),
            "studyPlans" to getStudyPlanHistory(),
            "studySessions" to getStudySessions(),
            "preferences" to mapOf(
                "selectedModel" to getSelectedModel(),
                "studyHours" to getStudyHoursPerDay(),
                "darkMode" to isDarkModeEnabled(),
                "notifications" to areNotificationsEnabled()
            ),
            "favorites" to mapOf(
                "models" to getFavoriteModels(),
                "subjects" to getFavoriteSubjects()
            ),
            "exportTimestamp" to System.currentTimeMillis()
        )

        return gson.toJson(allData)
    }

    fun importData(jsonData: String): Boolean {
        return try {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val data: Map<String, Any> = gson.fromJson(jsonData, type)

            // Importar datos selectivamente
            // Por seguridad, solo importar datos no sensibles

            data["preferences"]?.let { prefs ->
                val prefsMap = prefs as Map<String, Any>
                prefsMap["selectedModel"]?.let { saveSelectedModel(it as String) }
                prefsMap["studyHours"]?.let { saveStudyHoursPerDay((it as Double).toInt()) }
                prefsMap["darkMode"]?.let { saveDarkModeEnabled(it as Boolean) }
                prefsMap["notifications"]?.let { saveNotificationsEnabled(it as Boolean) }
            }

            true
        } catch (e: Exception) {
            false
        }
    }

    // MARK: - Cache Management

    fun clearAllCache() {
        preferences.edit()
            .remove("chat_history")
            .remove("offline_questions")
            .apply()
    }

    fun clearUserData() {
        preferences.edit()
            .remove("saved_summaries")
            .remove("quiz_results")
            .remove("study_plan_history")
            .remove("study_sessions")
            .apply()
    }

    fun getCacheSize(): Long {
        // Calcular tamaño aproximado de los datos almacenados
        val chatHistorySize = preferences.getString("chat_history", "")?.length ?: 0
        val summariesSize = preferences.getString("saved_summaries", "")?.length ?: 0
        val quizResultsSize = preferences.getString("quiz_results", "")?.length ?: 0
        val studyPlansSize = preferences.getString("study_plan_history", "")?.length ?: 0
        val sessionsSize = preferences.getString("study_sessions", "")?.length ?: 0

        return (chatHistorySize + summariesSize + quizResultsSize + studyPlansSize + sessionsSize).toLong()
    }

    // MARK: - Security

    fun saveSecureData(key: String, value: String) {
        encryptedPreferences.edit()
            .putString(key, value)
            .apply()
    }

    fun getSecureData(key: String): String? {
        return encryptedPreferences.getString(key, null)
    }

    fun removeSecureData(key: String) {
        encryptedPreferences.edit()
            .remove(key)
            .apply()
    }

    fun clearAllSecureData() {
        encryptedPreferences.edit().clear().apply()
    }
}