package com.universidad.mindsparkai.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universidad.mindsparkai.data.models.StudyPlan
import com.universidad.mindsparkai.data.models.DailyPlan
import com.universidad.mindsparkai.data.models.StudySession
import com.universidad.mindsparkai.data.repository.LocalDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StudyPlanViewModel @Inject constructor(
    private val localDataRepository: LocalDataRepository
) : ViewModel() {

    private val _studyPlan = MutableLiveData<StudyPlan?>()
    val studyPlan: LiveData<StudyPlan?> = _studyPlan

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    private val _currentWeekStart = MutableLiveData<Long>()
    val currentWeekStart: LiveData<Long> = _currentWeekStart

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Materias disponibles para seleccionar
    val availableSubjects = listOf(
        "Matemáticas", "Química", "Historia", "Física", "Literatura",
        "Biología", "Inglés", "Filosofía", "Economía", "Programación",
        "Psicología", "Sociología", "Arte", "Música", "Geografia",
        "Derecho", "Medicina", "Ingeniería", "Arquitectura", "Administración"
    )

    // Tipos de sesión disponibles
    val sessionTypes = listOf(
        "study" to "📚 Estudio",
        "practice" to "🎯 Práctica",
        "review" to "🔄 Repaso",
        "exam_prep" to "📝 Prep. Examen",
        "homework" to "📋 Tarea",
        "research" to "🔍 Investigación"
    )

    init {
        _currentWeekStart.value = getStartOfWeek()
        loadStudyPlan()
    }

    private fun getStartOfWeek(): Long {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun loadStudyPlan() {
        viewModelScope.launch {
            try {
                _loading.value = true

                // Intentar cargar plan existente de almacenamiento local
                val savedPlan = localDataRepository.getCurrentStudyPlan()

                if (savedPlan != null && isCurrentWeek(savedPlan.weekStart)) {
                    _studyPlan.value = savedPlan
                    _isEmpty.value = false
                } else {
                    // No hay plan para esta semana, mostrar estado vacío
                    _studyPlan.value = null
                    _isEmpty.value = true
                }

            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar plan de estudio"
                _isEmpty.value = true
            } finally {
                _loading.value = false
            }
        }
    }

    private fun isCurrentWeek(weekStart: Long): Boolean {
        val currentWeekStart = _currentWeekStart.value ?: System.currentTimeMillis()
        val weekDiff = Math.abs(weekStart - currentWeekStart)
        return weekDiff < (7 * 24 * 60 * 60 * 1000) // Menos de 7 días de diferencia
    }

    fun createNewPlan() {
        val weekStart = _currentWeekStart.value ?: System.currentTimeMillis()

        // Crear plan vacío para la semana actual
        val newPlan = StudyPlan(
            id = "plan_${System.currentTimeMillis()}",
            userId = "current_user",
            weekStart = weekStart,
            dailyPlans = createEmptyWeek(),
            aiRecommendations = emptyList(),
            totalHours = 0
        )

        _studyPlan.value = newPlan
        _isEmpty.value = false

        // Guardar en almacenamiento local
        savePlan(newPlan)
    }

    private fun createEmptyWeek(): List<DailyPlan> {
        val weekStart = _currentWeekStart.value ?: System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = weekStart

        val dateFormat = SimpleDateFormat("EEEE dd 'de' MMMM", Locale("es", "ES"))
        val days = mutableListOf<DailyPlan>()

        for (i in 0 until 7) {
            val dayName = dateFormat.format(calendar.time)
            days.add(
                DailyPlan(
                    date = dayName,
                    sessions = emptyList()
                )
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return days
    }

    fun addStudySession(
        dayIndex: Int,
        subject: String,
        topic: String,
        duration: Int, // en minutos
        type: String,
        startTime: String? = null
    ) {
        val currentPlan = _studyPlan.value ?: return

        if (dayIndex < 0 || dayIndex >= currentPlan.dailyPlans.size) {
            _error.value = "Día inválido"
            return
        }

        val newSession = StudySession(
            subject = subject,
            topic = topic,
            duration = duration,
            type = type
        )

        val updatedDailyPlans = currentPlan.dailyPlans.toMutableList()
        val currentDay = updatedDailyPlans[dayIndex]
        val updatedSessions = currentDay.sessions.toMutableList()
        updatedSessions.add(newSession)

        updatedDailyPlans[dayIndex] = currentDay.copy(sessions = updatedSessions)

        val updatedPlan = currentPlan.copy(
            dailyPlans = updatedDailyPlans,
            totalHours = calculateTotalHours(updatedDailyPlans)
        )

        _studyPlan.value = updatedPlan
        savePlan(updatedPlan)
    }

    fun removeStudySession(dayIndex: Int, sessionIndex: Int) {
        val currentPlan = _studyPlan.value ?: return

        if (dayIndex < 0 || dayIndex >= currentPlan.dailyPlans.size) return

        val updatedDailyPlans = currentPlan.dailyPlans.toMutableList()
        val currentDay = updatedDailyPlans[dayIndex]

        if (sessionIndex < 0 || sessionIndex >= currentDay.sessions.size) return

        val updatedSessions = currentDay.sessions.toMutableList()
        updatedSessions.removeAt(sessionIndex)

        updatedDailyPlans[dayIndex] = currentDay.copy(sessions = updatedSessions)

        val updatedPlan = currentPlan.copy(
            dailyPlans = updatedDailyPlans,
            totalHours = calculateTotalHours(updatedDailyPlans)
        )

        _studyPlan.value = updatedPlan
        savePlan(updatedPlan)
    }

    fun updateStudySession(
        dayIndex: Int,
        sessionIndex: Int,
        subject: String,
        topic: String,
        duration: Int,
        type: String
    ) {
        val currentPlan = _studyPlan.value ?: return

        if (dayIndex < 0 || dayIndex >= currentPlan.dailyPlans.size) return

        val updatedDailyPlans = currentPlan.dailyPlans.toMutableList()
        val currentDay = updatedDailyPlans[dayIndex]

        if (sessionIndex < 0 || sessionIndex >= currentDay.sessions.size) return

        val updatedSessions = currentDay.sessions.toMutableList()
        updatedSessions[sessionIndex] = StudySession(
            subject = subject,
            topic = topic,
            duration = duration,
            type = type
        )

        updatedDailyPlans[dayIndex] = currentDay.copy(sessions = updatedSessions)

        val updatedPlan = currentPlan.copy(
            dailyPlans = updatedDailyPlans,
            totalHours = calculateTotalHours(updatedDailyPlans)
        )

        _studyPlan.value = updatedPlan
        savePlan(updatedPlan)
    }

    private fun calculateTotalHours(dailyPlans: List<DailyPlan>): Int {
        return dailyPlans.sumOf { dailyPlan ->
            dailyPlan.sessions.sumOf { it.duration }
        } / 60 // Convertir minutos a horas
    }

    private fun savePlan(plan: StudyPlan) {
        viewModelScope.launch {
            try {
                localDataRepository.saveStudyPlan(plan)
            } catch (e: Exception) {
                _error.value = "Error al guardar plan: ${e.message}"
            }
        }
    }

    fun moveToNextWeek() {
        val currentWeek = _currentWeekStart.value ?: System.currentTimeMillis()
        val nextWeek = currentWeek + (7 * 24 * 60 * 60 * 1000) // +7 días
        _currentWeekStart.value = nextWeek
        loadStudyPlan()
    }

    fun moveToPreviousWeek() {
        val currentWeek = _currentWeekStart.value ?: System.currentTimeMillis()
        val previousWeek = currentWeek - (7 * 24 * 60 * 60 * 1000) // -7 días
        _currentWeekStart.value = previousWeek
        loadStudyPlan()
    }

    fun getWeekDates(): Pair<String, String> {
        val weekStart = _currentWeekStart.value ?: System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = weekStart

        val startFormat = SimpleDateFormat("dd MMM", Locale("es", "ES"))
        val startDate = startFormat.format(calendar.time)

        calendar.add(Calendar.DAY_OF_MONTH, 6)
        val endDate = startFormat.format(calendar.time)

        return Pair(startDate, endDate)
    }

    fun getWeekTitle(): String {
        val weekStart = _currentWeekStart.value ?: System.currentTimeMillis()
        val now = System.currentTimeMillis()
        val currentWeekStart = getStartOfWeek()

        return when {
            weekStart == currentWeekStart -> "Esta semana"
            weekStart < currentWeekStart -> "Semana anterior"
            else -> "Próxima semana"
        }
    }

    fun getWeekSummary(): Map<String, Any> {
        val plan = _studyPlan.value ?: return mapOf(
            "totalHours" to 0,
            "subjectsCount" to 0,
            "sessionsCount" to 0,
            "subjectDistribution" to emptyMap<String, Int>()
        )

        val subjects = mutableSetOf<String>()
        var totalSessions = 0
        val subjectHours = mutableMapOf<String, Int>()

        plan.dailyPlans.forEach { day ->
            day.sessions.forEach { session ->
                subjects.add(session.subject)
                totalSessions++
                val currentHours = subjectHours[session.subject] ?: 0
                subjectHours[session.subject] = currentHours + (session.duration / 60)
            }
        }

        return mapOf(
            "totalHours" to plan.totalHours,
            "subjectsCount" to subjects.size,
            "sessionsCount" to totalSessions,
            "subjectDistribution" to subjectHours
        )
    }

    fun duplicateWeekPlan() {
        val currentPlan = _studyPlan.value ?: return

        // Mover a la siguiente semana
        moveToNextWeek()

        // Crear nuevo plan con las mismas sesiones
        val nextWeekStart = _currentWeekStart.value ?: System.currentTimeMillis()
        val duplicatedPlan = currentPlan.copy(
            id = "plan_${System.currentTimeMillis()}",
            weekStart = nextWeekStart,
            dailyPlans = createDuplicatedWeek(currentPlan.dailyPlans)
        )

        _studyPlan.value = duplicatedPlan
        _isEmpty.value = false
        savePlan(duplicatedPlan)
    }

    private fun createDuplicatedWeek(originalDays: List<DailyPlan>): List<DailyPlan> {
        val weekStart = _currentWeekStart.value ?: System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = weekStart

        val dateFormat = SimpleDateFormat("EEEE dd 'de' MMMM", Locale("es", "ES"))

        return originalDays.mapIndexed { index, originalDay ->
            val newDate = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)

            originalDay.copy(date = newDate)
        }
    }

    fun clearWeekPlan() {
        val currentPlan = _studyPlan.value ?: return

        val clearedPlan = currentPlan.copy(
            dailyPlans = createEmptyWeek(),
            totalHours = 0
        )

        _studyPlan.value = clearedPlan
        savePlan(clearedPlan)
    }

    fun exportPlan(): String {
        val plan = _studyPlan.value ?: return "No hay plan para exportar"

        val weekDates = getWeekDates()
        val weekTitle = getWeekTitle()

        val header = """
            # Plan de Estudio Personal
            **Semana:** $weekTitle (${weekDates.first} - ${weekDates.second})
            **Horas totales:** ${plan.totalHours}h
            **Materias:** ${getWeekSummary()["subjectsCount"]}
            **Sesiones:** ${getWeekSummary()["sessionsCount"]}
            
            ---
            
        """.trimIndent()

        val dailyPlansText = plan.dailyPlans.joinToString("\n\n") { day ->
            val dayHeader = "## ${day.date}"

            if (day.sessions.isEmpty()) {
                "$dayHeader\n- Día libre"
            } else {
                val sessions = day.sessions.joinToString("\n") { session ->
                    val typeEmoji = when (session.type) {
                        "study" -> "📚"
                        "practice" -> "🎯"
                        "review" -> "🔄"
                        "exam_prep" -> "📝"
                        "homework" -> "📋"
                        "research" -> "🔍"
                        else -> "📖"
                    }
                    "- $typeEmoji **${session.subject}** (${session.duration}min): ${session.topic}"
                }
                "$dayHeader\n$sessions"
            }
        }

        return header + dailyPlansText
    }

    fun clearError() {
        _error.value = null
    }

    // Función para obtener colores por materia
    fun getSubjectColor(subject: String): String {
        val colors = listOf(
            "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FECA57",
            "#FF9FF3", "#54A0FF", "#5F27CD", "#00D2D3", "#FF9F43"
        )
        val index = Math.abs(subject.hashCode()) % colors.size
        return colors[index]
    }

    // Función para obtener emoji por materia
    fun getSubjectEmoji(subject: String): String {
        return when (subject.lowercase()) {
            "matemáticas", "matematicas" -> "📐"
            "química", "quimica" -> "🧪"
            "física", "fisica" -> "⚛️"
            "biología", "biologia" -> "🧬"
            "historia" -> "📚"
            "literatura" -> "📖"
            "inglés", "ingles" -> "🇬🇧"
            "filosofía", "filosofia" -> "🤔"
            "economía", "economia" -> "💰"
            "programación", "programacion" -> "💻"
            "psicología", "psicologia" -> "🧠"
            "sociología", "sociologia" -> "👥"
            "geografia" -> "🌍"
            "arte" -> "🎨"
            "música", "musica" -> "🎵"
            "derecho" -> "⚖️"
            "medicina" -> "🏥"
            "ingeniería", "ingenieria" -> "🔧"
            "arquitectura" -> "🏗️"
            "administración", "administracion" -> "📊"
            else -> "📚"
        }
    }
}