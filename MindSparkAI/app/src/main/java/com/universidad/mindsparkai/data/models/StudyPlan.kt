package com.universidad.mindsparkai.data.models

data class StudyPlan(
    val id: String = "",
    val userId: String = "",
    val weekStart: Long = 0,
    val dailyPlans: List<DailyPlan> = emptyList(),
    val aiRecommendations: List<String> = emptyList(),
    val totalHours: Int = 0
)

data class DailyPlan(
    val date: String = "",
    val sessions: List<StudySession> = emptyList()
)

data class StudySession(
    val subject: String = "",
    val topic: String = "",
    val duration: Int = 0, // en minutos
    val type: String = "" // "study", "review", "practice"
)
