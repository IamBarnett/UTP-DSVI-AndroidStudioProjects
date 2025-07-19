package com.universidad.mindsparkai

import com.universidad.mindsparkai.data.models.ChatMessage
import com.universidad.mindsparkai.data.models.User
import com.universidad.mindsparkai.data.models.QuizQuestion
import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit tests, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun chat_message_creation() {
        val message = ChatMessage(
            id = "test123",
            content = "Test message",
            isFromUser = true,
            aiModel = "GPT-4"
        )

        assertEquals("Test message", message.content)
        assertTrue(message.isFromUser)
        assertEquals("GPT-4", message.aiModel)
    }

    @Test
    fun user_model_creation() {
        val user = User(
            id = "user123",
            name = "Test User",
            email = "test@example.com",
            university = "Test University",
            career = "Computer Science",
            studyHours = 100,
            aiQueries = 50,
            averageQuizScore = 85.5f,
            subjects = listOf("Math", "Physics", "Chemistry")
        )

        assertEquals("Test User", user.name)
        assertEquals("test@example.com", user.email)
        assertEquals(3, user.subjects.size)
        assertEquals(85.5f, user.averageQuizScore, 0.001f)
    }

    @Test
    fun quiz_question_validation() {
        val question = QuizQuestion(
            id = "q1",
            question = "What is 2+2?",
            options = listOf("3", "4", "5", "6"),
            correctAnswer = 1,
            explanation = "2+2 equals 4",
            subject = "Math",
            difficulty = "easy"
        )

        assertEquals("What is 2+2?", question.question)
        assertEquals(4, question.options.size)
        assertEquals(1, question.correctAnswer)
        assertTrue(question.correctAnswer < question.options.size)
    }

    @Test
    fun email_validation() {
        assertTrue(isValidEmail("test@university.edu"))
        assertTrue(isValidEmail("student@gmail.com"))
        assertFalse(isValidEmail("invalid-email"))
        assertFalse(isValidEmail(""))
        assertFalse(isValidEmail("test@"))
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}