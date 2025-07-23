package com.universidad.mindsparkai.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.universidad.mindsparkai.data.models.QuizQuestion
import com.universidad.mindsparkai.data.repository.AIRepository
import com.universidad.mindsparkai.data.repository.LocalDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val aiRepository: AIRepository,
    private val localDataRepository: LocalDataRepository
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

    private val _quizStats = MutableLiveData<Map<String, Any>>()
    val quizStats: LiveData<Map<String, Any>> = _quizStats

    private val _userAnswers = mutableListOf<Int?>()
    private val _questionStartTime = MutableLiveData<Long>()

    // Materias disponibles expandidas
    val availableSubjects = listOf(
        "Matemáticas", "Química", "Física", "Biología", "Historia",
        "Literatura", "Inglés", "Filosofía", "Economía", "Programación",
        "Psicología", "Sociología", "Geografia", "Arte", "Música",
        "Derecho", "Medicina", "Ingeniería", "Arquitectura", "Administración"
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

        loadQuizStats()
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

    private fun loadQuizStats() {
        val results = localDataRepository.getQuizResults()
        val completedQuizzes = results.size
        val averageScore = if (results.isNotEmpty()) {
            results.map { (it["percentage"] as? Double)?.toInt() ?: 0 }.average().toInt()
        } else 0

        val subjectScores = results.groupBy { it["subject"] as? String ?: "Unknown" }
            .mapValues { (_, scores) ->
                scores.map { (it["percentage"] as? Double)?.toInt() ?: 0 }.average().toInt()
            }
        val bestSubject = subjectScores.maxByOrNull { it.value }?.key ?: "N/A"

        _quizStats.value = mapOf(
            "completedQuizzes" to completedQuizzes,
            "averageScore" to averageScore,
            "bestSubject" to bestSubject
        )
    }

    fun generateQuestions(subject: String, difficulty: String, count: Int = 5) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                _selectedSubject.value = subject
                _selectedDifficulty.value = difficulty

                // Incrementar uso de IA
                localDataRepository.incrementFeatureUsage("quiz")
                localDataRepository.incrementAIUsage(_selectedModel.value?.provider ?: "openai")

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
                                setQuestions(questions)
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

    private fun setQuestions(questions: List<QuizQuestion>) {
        _questions.value = questions
        _totalQuestions.value = questions.size
        _currentQuestionIndex.value = 0
        _currentQuestion.value = questions.firstOrNull()
        _score.value = 0
        _isQuizCompleted.value = false
        _showExplanation.value = false
        _userAnswers.clear()
        repeat(questions.size) { _userAnswers.add(null) }
        startQuestionTimer()
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
                    ),
                    QuestionData(
                        question = "¿Cuánto es 2 + 2?",
                        options = listOf("A) 3", "B) 4", "C) 5", "D) 6"),
                        correctAnswer = 1,
                        explanation = "2 + 2 = 4, es una operación básica de suma."
                    ),
                    QuestionData(
                        question = "¿Cuál es la capital de Francia?",
                        options = listOf("A) Londres", "B) París", "C) Roma", "D) Madrid"),
                        correctAnswer = 1,
                        explanation = "París es la capital y ciudad más poblada de Francia."
                    ),
                    QuestionData(
                        question = "¿Cuántos días tiene un año bisiesto?",
                        options = listOf("A) 364", "B) 365", "C) 366", "D) 367"),
                        correctAnswer = 2,
                        explanation = "Un año bisiesto tiene 366 días, con un día extra en febrero."
                    ),
                    QuestionData(
                        question = "¿Cuál es el planeta más cercano al Sol?",
                        options = listOf("A) Venus", "B) Mercurio", "C) Tierra", "D) Marte"),
                        correctAnswer = 1,
                        explanation = "Mercurio es el planeta más cercano al Sol en nuestro sistema solar."
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

        // Guardar resultados
        saveQuizResults()

        // Actualizar estadísticas
        loadQuizStats()
    }

    private fun saveQuizResults() {
        val score = _score.value ?: 0
        val total = _totalQuestions.value ?: 0
        val subject = _selectedSubject.value ?: "General"
        val difficulty = _selectedDifficulty.value ?: "Intermedio"
        val model = _selectedModel.value?.displayName ?: "AI"

        localDataRepository.saveQuizResult(subject, score, total, difficulty, model)
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

    // Función para obtener recomendaciones de estudio
    fun getStudyRecommendations(): List<String> {
        val results = getQuizResults()
        val percentage = results["percentage"] as Int
        val subject = results["subject"] as String

        val recommendations = mutableListOf<String>()

        when {
            percentage >= 90 -> {
                recommendations.add("🎉 ¡Excelente dominio de $subject!")
                recommendations.add("📈 Considera aumentar la dificultad para mayor desafío")
                recommendations.add("🎯 Podrías ayudar a otros estudiantes con esta materia")
            }
            percentage >= 70 -> {
                recommendations.add("👍 Buen conocimiento de $subject")
                recommendations.add("📚 Repasa los temas donde tuviste errores")
                recommendations.add("💪 Con un poco más de práctica dominarás la materia")
            }
            percentage >= 50 -> {
                recommendations.add("📖 Necesitas repasar más $subject")
                recommendations.add("🎯 Enfócate en los conceptos fundamentales")
                recommendations.add("⏰ Dedica más tiempo de estudio a esta materia")
            }
            else -> {
                recommendations.add("🚀 ¡No te desanimes! Todos empezamos desde algún lugar")
                recommendations.add("📚 Considera repasar los conceptos básicos de $subject")
                recommendations.add("👨‍🏫 Busca ayuda adicional o tutorías para esta materia")
                recommendations.add("🎯 Practica con quizzes más fáciles primero")
            }
        }

        return recommendations
    }

    // Función para obtener el progreso por materia
    fun getSubjectProgress(): Map<String, Int> {
        val results = localDataRepository.getQuizResults()
        return results.groupBy { it["subject"] as? String ?: "Unknown" }
            .mapValues { (_, scores) ->
                if (scores.isNotEmpty()) {
                    scores.map { (it["percentage"] as? Double)?.toInt() ?: 0 }.average().toInt()
                } else 0
            }
    }

    // Función para generar estadísticas de rendimiento
    fun getPerformanceAnalysis(): Map<String, Any> {
        val results = localDataRepository.getQuizResults()
        val recentResults = results.takeLast(10) // Últimos 10 quizzes

        val analysis = mutableMapOf<String, Any>()

        if (recentResults.isNotEmpty()) {
            val scores = recentResults.map { (it["percentage"] as? Double)?.toInt() ?: 0 }
            val averageScore = scores.average().toInt()
            val trend = if (scores.size > 1) {
                val firstHalf = scores.take(scores.size / 2).average()
                val secondHalf = scores.drop(scores.size / 2).average()
                when {
                    secondHalf > firstHalf + 5 -> "Mejorando"
                    secondHalf < firstHalf - 5 -> "Necesita atención"
                    else -> "Estable"
                }
            } else "Insuficientes datos"

            analysis["averageScore"] = averageScore
            analysis["trend"] = trend
            analysis["totalQuizzes"] = results.size
            analysis["recentQuizzes"] = recentResults.size
        }

        return analysis
    }
}
