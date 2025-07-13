package com.universidad.mindsparkai.data.models

data class QuizQuestion(
    val id: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: Int = 0,
    val explanation: String = "",
    val subject: String = "",
    val difficulty: String = "medium", // easy, medium, hard
    val aiModel: String = "GPT-4"
)