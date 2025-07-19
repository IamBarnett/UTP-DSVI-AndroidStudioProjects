package com.universidad.mindsparkai.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.universidad.mindsparkai.data.models.StudyPlan
import com.universidad.mindsparkai.data.models.DailyPlan
import com.universidad.mindsparkai.data.models.StudySession
import com.universidad.mindsparkai.data.repository.AIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StudyPlanViewModel @Inject constructor(
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _studyPlan = MutableLiveData<StudyPlan?>()
    val studyPlan: LiveData<StudyPlan?> = _studyPlan

    private val _selectedSubjects = MutableLiveData<List<String>>()
    val selectedSubjects: LiveData<List<String>> = _selectedSubjects

    private val _hoursPerDay = MutableLiveData<Int>()
    val hoursPerDay: LiveData<Int> = _hoursPerDay

    private val _studyGoals = MutableLiveData<String>()
    val studyGoals: LiveData<String> = _studyGoals

    private val _selectedModel = MutableLiveData<AIRepository.AIModel>()
    val selectedModel: LiveData<AIRepository.AIModel> = _selectedModel

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _currentWeekStart = MutableLiveData<Long>()
    val currentWeekStart: LiveData<Long> = _currentWeekStart

    private val availableSubjects = listOf(
        "Matemáticas", "Química", "Historia", "Física", "Literatura",
        "Biología", "Inglés", "Filosofía", "Economía", "Programación",
        "Psicología", "Sociología", "Arte", "Música", "Geografia",
        "Derecho", "Medicina", "Ingeniería", "Arquitectura", "Administración"
    )

    private val availableModels = listOf(
        AIRepository.AIModel.GPT_4,
        AIRepository.AIModel.CLAUDE_3_SONNET,
        AIRepository.AIModel.GEMINI_PRO
    )

    init {
        _selectedSubjects.value = listOf("Matemáticas", "Química")
        _hoursPerDay.value = 4
        _selectedModel.value = AIRepository.AIModel.GPT_4
        _studyGoals.value = "Mejorar rendimiento académico y preparación para exámenes"
        _currentWeekStart.value = getStartOfWeek()
        loadSamplePlan()
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

    private fun loadSamplePlan() {
        val sampleSessions = listOf(
            StudySession(
                subject = "Matemáticas",
                topic = "Cálculo diferencial",
                duration = 120,
                type = "study"
            ),
            StudySession(
                subject = "Química",
                topic = "Química orgánica",
                duration = 90,
                type = "study"
            ),
            StudySession(
                subject = "Repaso general",
                topic = "Temas anteriores",
                duration = 30,
                type = "review"
            )
        )

        val dailyPlans = getDaysOfWeek().map { day ->
            DailyPlan(
                date = day,
                sessions = when (day) {
                    "Lunes", "Miércoles", "Viernes" -> sampleSessions
                    "Martes", "Jueves" -> sampleSessions.take(2)
                    else -> listOf(sampleSessions.last())
                }
            )
        }

        val samplePlan = StudyPlan(
            id = "sample_plan_${System.currentTimeMillis()}",
            userId = "current_user",
            weekStart = _currentWeekStart.value ?: System.currentTimeMillis(),
            dailyPlans = dailyPlans,
            aiRecommendations = listOf(
                "📚 Dedica más tiempo a Química esta semana (rendimiento: 78%)",
                "⏰ Tu mejor horario de concentración: 9:00-11:00 AM",
                "☕ Toma descansos cada 45 minutos para mantener la concentración",
                "📝 Repasa Matemáticas el viernes antes del examen",
                "🎯 Enfócate en práctica más que en teoría para Química",
                "💡 Usa técnicas de memoria visual para Historia"
            ),
            totalHours = calculateTotalHours(dailyPlans)
        )

        _studyPlan.value = samplePlan
    }

    private fun getDaysOfWeek(): List<String> {
        val calendar = Calendar.getInstance()
        val weekStart = _currentWeekStart.value ?: System.currentTimeMillis()
        calendar.timeInMillis = weekStart

        val dateFormat = SimpleDateFormat("EEEE dd MMM", Locale("es", "ES"))
        val days = mutableListOf<String>()

        for (i in 0 until 7) {
            days.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return days
    }

    private fun calculateTotalHours(dailyPlans: List<DailyPlan>): Int {
        return dailyPlans.sumOf { dailyPlan ->
            dailyPlan.sessions.sumOf { it.duration }
        } / 60 // Convertir minutos a horas
    }

    fun getAvailableSubjects(): List<String> = availableSubjects

    fun getAvailableModels(): List<AIRepository.AIModel> = availableModels

    fun toggleSubject(subject: String) {
        val currentList = _selectedSubjects.value?.toMutableList() ?: mutableListOf()

        if (currentList.contains(subject)) {
            currentList.remove(subject)
        } else {
            if (currentList.size < 8) { // Máximo 8 materias
                currentList.add(subject)
            } else {
                _error.value = "Máximo 8 materias permitidas"
                return
            }
        }

        _selectedSubjects.value = currentList
    }

    fun setHoursPerDay(hours: Int) {
        if (hours in 1..12) {
            _hoursPerDay.value = hours
        } else {
            _error.value = "Las horas deben estar entre 1 y 12"
        }
    }

    fun setStudyGoals(goals: String) {
        _studyGoals.value = goals
    }

    fun selectModel(model: AIRepository.AIModel) {
        _selectedModel.value = model
    }

    fun generateStudyPlan() {
        val subjects = _selectedSubjects.value ?: return
        val hours = _hoursPerDay.value ?: return
        val goals = _studyGoals.value ?: ""

        if (subjects.isEmpty()) {
            _error.value = "Selecciona al menos una materia"
            return
        }

        if (hours < 1) {
            _error.value = "Debes estudiar al menos 1 hora por día"
            return
        }

        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val result = aiRepository.generateStudyPlan(
                    subjects = subjects,
                    hoursPerDay = hours,
                    goals = goals,
                    model = _selectedModel.value ?: AIRepository.AIModel.GPT_4
                )

                result.fold(
                    onSuccess = { jsonResponse ->
                        try {
                            val planData = parseStudyPlanResponse(jsonResponse, subjects, hours, goals)
                            _studyPlan.value = planData
                        } catch (e: JsonSyntaxException) {
                            // Si falla el parsing, crear un plan básico
                            createBasicPlan(subjects, hours, goals)
                        }
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Error al generar plan de estudio"
                        // Crear plan básico como fallback
                        createBasicPlan(subjects, hours, goals)
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Error inesperado"
                createBasicPlan(subjects, hours, goals)
            } finally {
                _loading.value = false
            }
        }
    }

    private data class StudyPlanResponse(
        val weeklyPlan: WeeklyPlanData,
        val recommendations: List<String>
    )

    private data class WeeklyPlanData(
        val totalHours: Int,
        val days: List<DayData>
    )

    private data class DayData(
        val day: String,
        val sessions: List<SessionData>
    )

    private data class SessionData(
        val subject: String,
        val topic: String,
        val duration: Int,
        val type: String,
        val timeSlot: String? = null
    )

    private fun parseStudyPlanResponse(
        jsonResponse: String,
        subjects: List<String>,
        hours: Int,
        goals: String
    ): StudyPlan {
        return try {
            val response = Gson().fromJson(jsonResponse, StudyPlanResponse::class.java)

            val dailyPlans = response.weeklyPlan.days.map { dayData ->
                DailyPlan(
                    date = dayData.day,
                    sessions = dayData.sessions.map { sessionData ->
                        StudySession(
                            subject = sessionData.subject,
                            topic = sessionData.topic,
                            duration = sessionData.duration,
                            type = sessionData.type
                        )
                    }
                )
            }

            StudyPlan(
                id = "ai_generated_${System.currentTimeMillis()}",
                userId = "current_user",
                weekStart = _currentWeekStart.value ?: System.currentTimeMillis(),
                dailyPlans = dailyPlans,
                aiRecommendations = response.recommendations,
                totalHours = response.weeklyPlan.totalHours
            )
        } catch (e: Exception) {
            throw JsonSyntaxException("Error parsing AI response")
        }
    }

    private fun createBasicPlan(subjects: List<String>, hours: Int, goals: String) {
        val dailyPlans = createBasicDailyPlans(subjects, hours)
        val recommendations = generateBasicRecommendations(subjects, hours, goals)

        val basicPlan = StudyPlan(
            id = "basic_plan_${System.currentTimeMillis()}",
            userId = "current_user",
            weekStart = _currentWeekStart.value ?: System.currentTimeMillis(),
            dailyPlans = dailyPlans,
            aiRecommendations = recommendations,
            totalHours = hours * 7
        )

        _studyPlan.value = basicPlan
    }

    private fun createBasicDailyPlans(subjects: List<String>, hoursPerDay: Int): List<DailyPlan> {
        val days = getDaysOfWeek()
        val minutesPerDay = hoursPerDay * 60
        val minutesPerSubject = if (subjects.isNotEmpty()) minutesPerDay / subjects.size else 60

        return days.map { day ->
            val sessions = subjects.mapIndexed { index, subject ->
                val sessionType = when (index % 3) {
                    0 -> "study"
                    1 -> "practice"
                    else -> "review"
                }

                val topic = getDefaultTopic(subject)

                StudySession(
                    subject = subject,
                    topic = topic,
                    duration = minutesPerSubject,
                    type = sessionType
                )
            }

            DailyPlan(
                date = day,
                sessions = sessions
            )
        }
    }

    private fun getDefaultTopic(subject: String): String {
        return when (subject.lowercase()) {
            "matemáticas" -> "Ejercicios y problemas"
            "química" -> "Conceptos fundamentales"
            "física" -> "Leyes y fórmulas"
            "biología" -> "Sistemas biológicos"
            "historia" -> "Eventos importantes"
            "literatura" -> "Análisis de textos"
            "inglés" -> "Gramática y vocabulario"
            "filosofía" -> "Conceptos filosóficos"
            "economía" -> "Principios económicos"
            "programación" -> "Práctica de código"
            else -> "Conceptos generales"
        }
    }

    private fun generateBasicRecommendations(
        subjects: List<String>,
        hours: Int,
        goals: String
    ): List<String> {
        val recommendations = mutableListOf<String>()

        recommendations.add("📚 Plan personalizado para: ${subjects.joinToString(", ")}")
        recommendations.add("⏰ Distribución de $hours horas diarias optimizada")

        if (hours >= 6) {
            recommendations.add("🎯 Con $hours horas diarias tienes excelente dedicación")
        } else if (hours >= 4) {
            recommendations.add("👍 $hours horas diarias es una buena base de estudio")
        } else {
            recommendations.add("💪 Considera aumentar gradualmente las horas de estudio")
        }

        recommendations.add("☕ Toma descansos de 15 minutos cada hora")
        recommendations.add("🧠 Alterna entre materias para mantener la concentración")

        if (subjects.size > 5) {
            recommendations.add("📋 Con ${subjects.size} materias, organiza por prioridades")
        }

        if (goals.contains("examen", ignoreCase = true)) {
            recommendations.add("📝 Enfócate en práctica y repaso para exámenes")
        }

        recommendations.add("🌟 Revisa y ajusta el plan según tu progreso")

        return recommendations
    }

    fun moveToNextWeek() {
        val currentWeek = _currentWeekStart.value ?: System.currentTimeMillis()
        val nextWeek = currentWeek + (7 * 24 * 60 * 60 * 1000) // +7 días
        _currentWeekStart.value = nextWeek

        // Regenerar plan para la nueva semana
        generateStudyPlan()
    }

    fun moveToPreviousWeek() {
        val currentWeek = _currentWeekStart.value ?: System.currentTimeMillis()
        val previousWeek = currentWeek - (7 * 24 * 60 * 60 * 1000) // -7 días
        _currentWeekStart.value = previousWeek

        // Regenerar plan para la semana anterior
        generateStudyPlan()
    }

    fun markSessionCompleted(dayIndex: Int, sessionIndex: Int) {
        val currentPlan = _studyPlan.value ?: return
        // Aquí podrías implementar lógica para marcar sesiones como completadas
        // Por ahora, solo actualizamos el plan
        _studyPlan.value = currentPlan
    }

    fun getWeekSummary(): Map<String, Any> {
        val plan = _studyPlan.value ?: return emptyMap()

        val subjectHours = mutableMapOf<String, Int>()
        var totalSessions = 0

        plan.dailyPlans.forEach { day ->
            day.sessions.forEach { session ->
                val currentHours = subjectHours[session.subject] ?: 0
                subjectHours[session.subject] = currentHours + (session.duration / 60)
                totalSessions++
            }
        }

        return mapOf(
            "totalHours" to plan.totalHours,
            "totalSessions" to totalSessions,
            "subjectDistribution" to subjectHours,
            "averageSessionDuration" to if (totalSessions > 0) {
                plan.dailyPlans.flatMap { it.sessions }.map { it.duration }.average().toInt()
            } else 0,
            "subjectsCount" to subjectHours.size
        )
    }

    fun optimizePlan() {
        val subjects = _selectedSubjects.value ?: return
        val hours = _hoursPerDay.value ?: return

        // Regenerar con parámetros optimizados
        val optimizedGoals = _studyGoals.value + " - Plan optimizado con distribución equilibrada"
        _studyGoals.value = optimizedGoals
        generateStudyPlan()
    }

    fun exportPlan(): String {
        val plan = _studyPlan.value ?: return "No hay plan generado"
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val weekStart = Date(_currentWeekStart.value ?: System.currentTimeMillis())

        val header = """
            # Plan de Estudio Semanal
            **Generado por:** MindSpark AI
            **Semana del:** ${dateFormat.format(weekStart)}
            **Materias:** ${_selectedSubjects.value?.joinToString(", ") ?: ""}
            **Horas diarias:** ${_hoursPerDay.value}h
            **Total semanal:** ${plan.totalHours}h
            
            ---
            
        """.trimIndent()

        val dailyPlansText = plan.dailyPlans.joinToString("\n\n") { day ->
            val dayHeader = "## ${day.date}"
            val sessions = day.sessions.joinToString("\n") { session ->
                "- **${session.subject}** (${session.duration}min): ${session.topic} [${session.type}]"
            }
            "$dayHeader\n$sessions"
        }

        val recommendations = "\n\n## 💡 Recomendaciones IA\n" +
                plan.aiRecommendations.joinToString("\n") { "- $it" }

        return header + dailyPlansText + recommendations
    }

    fun clearError() {
        _error.value = null
    }

    fun resetPlan() {
        _selectedSubjects.value = listOf("Matemáticas", "Química")
        _hoursPerDay.value = 4
        _studyGoals.value = "Mejorar rendimiento académico y preparación para exámenes"
        _currentWeekStart.value = getStartOfWeek()
        loadSamplePlan()
    }
}