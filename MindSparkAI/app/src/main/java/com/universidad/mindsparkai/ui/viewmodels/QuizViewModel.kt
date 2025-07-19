package com.universidad.mindsparkai.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
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

    private val _totalQuestions = MutableLiveData<Int>()
    val totalQuestions: LiveData<Int> = _totalQuestions

    private val _isQuizCompleted = MutableLiveData<Boolean>()
    val isQuizCompleted: LiveData<Boolean> = _isQuizCompleted

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _selectedModel = MutableLiveData<AIRepository.AIModel>()
    val selectedModel: LiveData<AIRepository.AIModel> = _selectedModel

    private val _selectedSubject = MutableLiveData<String>()
    val selectedSubject: LiveData<String> = _selectedSubject

    private val _selectedDifficulty = MutableLiveData<String>()
    val selectedDifficulty: LiveData<String> = _selectedDifficulty

    private val _showExplanation = MutableLiveData<Boolean>()
    val showExplanation: LiveData<Boolean> = _showExplanation

    private val _userAnswers = mutableListOf<Int?>()
    private val _questionStartTime = MutableLiveData<Long>()

    // Materias disponibles
    val availableSubjects = listOf(
        "Matemáticas", "Química", "Física", "Biología", "Historia",
        "Literatura", "Inglés", "Filosofía", "Economía", "Programación",
        "Psicología", "Sociología", "Geografia", "Arte", "Música"
    )

    // Niveles de dificultad
    val availableDifficulties = listOf("Fácil", "Intermedio", "Difícil")

    // Modelos recomendados para quiz
    val availableModels = listOf(
        AIRepository.AIModel.GPT_4,
        AIRepository.AIModel.CLAUDE_3_SONNET,
        AIRepository.AIModel.GEMINI_PRO,
        AIRepository.AIModel.GPT_3_5
    )

    init {
        _selectedModel.value = AIRepository.AIModel.GPT_4
        _selectedSubject.value = "Matemáticas"
        _selectedDifficulty.value = "Intermedio"
        _currentQuestionIndex.value = 0
        _score.value = 0
        _isQuizCompleted.value = false
        _showExplanation.value = false

        loadSampleQuestion()
    }

    private fun loadSampleQuestion() {
        val sampleQuestion = QuizQuestion(
            id = "sample_1",
            question = "¿Cuál es la principal función de las mitocondrias en la célula?",
            options = listOf(
                "A) Síntesis de proteínas",
                "B) Producción de energía (ATP)",
                "C) Almacenamiento de ADN",
                "D) Digestión celular"
            ),
            correctAnswer = 1,
            explanation = "Las mitocondrias son conocidas como las 'centrales energéticas' de la célula porque producen ATP (adenosín trifosfato), que es la principal fuente de energía celular.",
            subject = "Biología",
            difficulty = "Intermedio"
        )

        _currentQuestion.value = sampleQuestion
        _questions.value = listOf(sampleQuestion)
        _totalQuestions.value = 1
    }

    fun generateQuestions(subject: String, difficulty: String, count: Int = 5) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                _selectedSubject.value = subject
                _selectedDifficulty.value = difficulty

                val result = aiRepository.generateQuizQuestions(
                    subject = subject,
                    difficulty = difficulty,
                    count = count,
                    model = _selectedModel.value ?: AIRepository.AIModel.GPT_4
                )

                result.fold(
                    onSuccess = { jsonResponse ->
                        try {
                            val quizData = parseQuizResponse(jsonResponse)
                            val questions = quizData.questions.mapIndexed { index, q ->
                                QuizQuestion(
                                    id = "generated_${System.currentTimeMillis()}_$index",
                                    question = q.question,
                                    options = q.options,
                                    correctAnswer = q.correctAnswer,
                                    explanation = q.explanation,
                                    subject = subject,
                                    difficulty = difficulty,
                                    aiModel = _selectedModel.value?.displayName ?: "AI"
                                )
                            }

                            if (questions.isNotEmpty()) {
                                _questions.value = questions
                                _totalQuestions.value = questions.size
                                _currentQuestionIndex.value = 0
                                _currentQuestion.value = questions.first()
                                _score.value = 0
                                _isQuizCompleted.value = false
                                _userAnswers.clear()
                                repeat(questions.size) { _userAnswers.add(null) }
                                startQuestionTimer()
                            } else {
                                _error.value = "No se pudieron generar preguntas válidas"
                            }
                        } catch (e: JsonSyntaxException) {
                            _error.value = "Error al procesar la respuesta de la IA"
                        }
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Error al generar preguntas"
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Error inesperado"
            } finally {
                _loading.value = false
            }
        }
    }

    private data class QuizResponse(
        val questions: List<QuestionData>
    )

    private data class QuestionData(
        val question: String,
        val options: List<String>,
        val correctAnswer: Int,
        val explanation: String
    )

    private fun parseQuizResponse(jsonResponse: String): QuizResponse {
        return try {
            Gson().fromJson(jsonResponse, QuizResponse::class.java)
        } catch (e: Exception) {
            // Si no es JSON válido, intentar crear preguntas de ejemplo
            QuizResponse(
                questions = listOf(
                    QuestionData(
                        question = "¿Cuál es la fórmula del agua?",
                        options = listOf("A) H2O", "B) CO2", "C) NaCl", "D) O2"),
                        correctAnswer = 0,
                        explanation = "H2O es la fórmula química del agua, compuesta por dos átomos de hidrógeno y uno de oxígeno."
                    )
                )
            )
        }
    }

    fun selectAnswer(answerIndex: Int) {
        _selectedAnswer.value = answerIndex
        val currentIndex = _currentQuestionIndex.value ?: 0
        if (currentIndex < _userAnswers.size) {
            _userAnswers[currentIndex] = answerIndex
        }
    }

    fun submitAnswer() {
        val selectedIndex = _selectedAnswer.value
        val currentQ = _currentQuestion.value
        val currentIndex = _currentQuestionIndex.value ?: 0

        if (selectedIndex != null && currentQ != null) {
            // Mostrar explicación
            _showExplanation.value = true

            // Actualizar puntuación
            if (selectedIndex == currentQ.correctAnswer) {
                _score.value = (_score.value ?: 0) + 1
            }
        }
    }

    fun nextQuestion() {
        val questions = _questions.value ?: return
        val currentIndex = _currentQuestionIndex.value ?: 0

        _showExplanation.value = false
        _selectedAnswer.value = null

        if (currentIndex < questions.size - 1) {
            val nextIndex = currentIndex + 1
            _currentQuestionIndex.value = nextIndex
            _currentQuestion.value = questions[nextIndex]

            // Cargar respuesta previa si existe
            if (nextIndex < _userAnswers.size) {
                _selectedAnswer.value = _userAnswers[nextIndex]
            }

            startQuestionTimer()
        } else {
            completeQuiz()
        }
    }

    fun previousQuestion() {
        val currentIndex = _currentQuestionIndex.value ?: 0
        val questions = _questions.value ?: return

        if (currentIndex > 0) {
            val prevIndex = currentIndex - 1
            _currentQuestionIndex.value = prevIndex
            _currentQuestion.value = questions[prevIndex]

            // Cargar respuesta previa
            if (prevIndex < _userAnswers.size) {
                _selectedAnswer.value = _userAnswers[prevIndex]
            }

            _showExplanation.value = false
            startQuestionTimer()
        }
    }

    private fun completeQuiz() {
        _isQuizCompleted.value = true
        _currentQuestion.value = null
    }

    private fun startQuestionTimer() {
        _questionStartTime.value = System.currentTimeMillis()
    }

    fun getQuestionTimeSpent(): Long {
        val startTime = _questionStartTime.value ?: return 0
        return System.currentTimeMillis() - startTime
    }

    fun selectModel(model: AIRepository.AIModel) {
        _selectedModel.value = model
    }

    fun selectSubject(subject: String) {
        _selectedSubject.value = subject
    }

    fun selectDifficulty(difficulty: String) {
        _selectedDifficulty.value = difficulty
    }

    fun resetQuiz() {
        _currentQuestionIndex.value = 0
        _score.value = 0
        _selectedAnswer.value = null
        _isQuizCompleted.value = false
        _showExplanation.value = false
        _userAnswers.clear()

        val questions = _questions.value
        if (!questions.isNullOrEmpty()) {
            _currentQuestion.value = questions.first()
            repeat(questions.size) { _userAnswers.add(null) }
            startQuestionTimer()
        }
    }

    fun getQuizResults(): Map<String, Any> {
        val totalQuestions = _totalQuestions.value ?: 0
        val score = _score.value ?: 0
        val percentage = if (totalQuestions > 0) {
            (score.toFloat() / totalQuestions * 100).toInt()
        } else 0

        return mapOf(
            "score" to score,
            "totalQuestions" to totalQuestions,
            "percentage" to percentage,
            "subject" to (_selectedSubject.value ?: ""),
            "difficulty" to (_selectedDifficulty.value ?: ""),
            "model" to (_selectedModel.value?.displayName ?: "")
        )
    }

    fun clearError() {
        _error.value = null
    }

    // Generar quiz personalizado con tema específico
    fun generateCustomQuiz(customTopic: String, count: Int = 5) {
        generateQuestions(
            subject = customTopic,
            difficulty = _selectedDifficulty.value ?: "Intermedio",
            count = count
        )
    }

    // Función para obtener estadísticas detalladas
    fun getDetailedStats(): Map<String, Any> {
        val questions = _questions.value ?: emptyList()
        val correctAnswers = questions.mapIndexed { index, question ->
            val userAnswer = _userAnswers.getOrNull(index)
            userAnswer == question.correctAnswer
        }

        return mapOf(
            "totalQuestions" to questions.size,
            "correctAnswers" to correctAnswers.count { it },
            "incorrectAnswers" to correctAnswers.count { !it },
            "unanswered" to _userAnswers.count { it == null },
            "accuracy" to if (questions.isNotEmpty()) {
                (correctAnswers.count { it }.toFloat() / questions.size * 100).toInt()
            } else 0,
            "questionsData" to questions.mapIndexed { index, question ->
                mapOf(
                    "question" to question.question,
                    "userAnswer" to _userAnswers.getOrNull(index),
                    "correctAnswer" to question.correctAnswer,
                    "isCorrect" to ((_userAnswers.getOrNull(index) ?: -1) == question.correctAnswer)
                )
            }
        )
    }
}