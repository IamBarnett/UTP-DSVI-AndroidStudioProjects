package com.universidad.mindsparkai.data.models

data class ChatMessage(
    val id: String = "",
    val content: String = "",
    val isFromUser: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
    val aiModel: String = "GPT-4"
)