package com.universidad.mindsparkai.data.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val university: String = "",
    val career: String = "",
    val studyHours: Int = 0,
    val aiQueries: Int = 0,
    val averageQuizScore: Float = 0f,
    val subjects: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)