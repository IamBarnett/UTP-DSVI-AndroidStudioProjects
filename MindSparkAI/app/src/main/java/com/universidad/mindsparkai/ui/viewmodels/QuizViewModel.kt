package com.universidad.mindsparkai.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universidad.mindsparkai.data.models.QuizQuestion
import com.universidad.mindsparkai.data.repository.AIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _questions = MutableLiveData<List<QuizQuestion>>()
    val questions: LiveData<List<QuizQuestion>> = _questions

    private val _currentQuestion = MutableLiveData<QuizQuestion?>()
    val currentQuestion: LiveData<QuizQuestion?> = _currentQuestion

    private val _currentQuestionIndex = MutableLiveData<Int>()
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

    private val _selectedAnswer = MutableLiveData<Int?>()
    val selectedAnswer: LiveData<Int?> = _selectedAnswer

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> = _score

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _selectedModel = MutableLiveData<String>()
    val selectedModel: LiveData<String> = _selectedModel

    init {
        _selectedModel.value = "GPT-4"
        _currentQuestionIndex.value = 0
        _score.value = 0
        loadSampleQuestion()
    }

    private fun loadSampleQuestion() {
        val sampleQuestion = QuizQuestion(
            id = "sample_1",
            question = "¿Cuál es la principal función de las mitocondrias en la célula?",
            options = listOf(
                "Síntesis de proteínas",
                "Producción de energía (ATP)",
                "Almacenamiento de ADN",
                "Digestión celular"
            ),
            correctAnswer = 1,
            explanation = "Las mitocondrias son conocidas como las 'centrales energéticas' de la célula porque producen ATP (adenosín trifosfato), que es la principal fuente de energía celular.",
            subject = "Biología",
            difficulty = "medium"
        )

        _currentQuestion.value = sampleQuestion
        _questions.value = listOf(sampleQuestion)
    }

    fun generateQuestions(subject: String, difficulty: String, count: Int = 5) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val response = aiRepository.generateQuizQuestions(subject, difficulty, count)

                if (response != null) {
                    // TODO: Parse JSON response and create QuizQuestion objects
                    // For now, we'll use sample questions
                    val generatedQuestions = generateSampleQuestions(subject, difficulty, count)
                    _questions.value = generatedQuestions
                    _currentQuestionIndex.value = 0
                    _currentQuestion.value = generatedQuestions.firstOrNull()
                } else {
                    _error.value = "No se pudieron generar las preguntas. Intenta de nuevo."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al generar preguntas"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun generateSampleQuestions(subject: String, difficulty: String, count: Int): List<QuizQuestion> {
        // Sample questions - in real implementation, parse AI response
        return listOf(
            QuizQuestion(
                id = "q1",
                question = "¿Cuál es la principal función de las mitocondrias?",
                options = listOf("Síntesis", "Energía", "ADN", "Digestión"),
                correctAnswer = 1,
                explanation = "Las mitocondrias producen ATP.",
                subject = subject,
                difficulty = difficulty
            )
        )
    }

    fun selectAnswer(answerIndex: Int) {
        _selectedAnswer.value = answerIndex
    }

    fun submitAnswer() {
        val selectedIndex = _selectedAnswer.value
        val currentQ = _currentQuestion.value

        if (selectedIndex != null && currentQ != null) {
            if (selectedIndex == currentQ.correctAnswer) {
                _score.value = (_score.value ?: 0) + 1
            }

            // Move to next question
            nextQuestion()
        }
    }

    private fun nextQuestion() {
        val questions = _questions.value ?: return
        val currentIndex = _currentQuestionIndex.value ?: 0

        if (currentIndex < questions.size - 1) {
            val nextIndex = currentIndex + 1
            _currentQuestionIndex.value = nextIndex
            _currentQuestion.value = questions[nextIndex]
            _selectedAnswer.value = null
        } else {
            // Quiz completed
            _currentQuestion.value = null
        }
    }

    fun selectModel(model: String) {
        _selectedModel.value = model
    }

    fun resetQuiz() {
        _currentQuestionIndex.value = 0
        _score.value = 0
        _selectedAnswer.value = null
        val questions = _questions.value
        if (!questions.isNullOrEmpty()) {
            _currentQuestion.value = questions.first()
        }
    }

    fun clearError() {
        _error.value = null
    }
}