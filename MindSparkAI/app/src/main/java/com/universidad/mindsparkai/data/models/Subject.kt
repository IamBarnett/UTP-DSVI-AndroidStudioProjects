package com.universidad.mindsparkai.data.models

data class Subject(
    val id: String = "",
    val name: String = "",
    val icon: String = "",
    val progress: Float = 0f,
    val studyHours: Int = 0,
    val quizAverage: Float = 0f
)