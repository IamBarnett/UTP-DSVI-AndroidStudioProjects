package com.universidad.mindsparkai.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.universidad.mindsparkai.R
import com.universidad.mindsparkai.data.models.QuizQuestion
import com.universidad.mindsparkai.data.repository.AIRepository
import com.universidad.mindsparkai.databinding.FragmentQuizBinding
import com.universidad.mindsparkai.ui.viewmodels.QuizViewModel
import com.universidad.mindsparkai.utils.QuizUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizViewModel by viewModels()
    private var currentQuestionIndex = 0
    private var selectedAnswerIndex: Int? = null
    private var currentQuestions: List<QuizQuestion> = emptyList()
    private var isShowingExplanation = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
        observeViewModel()
        setupSpinners()
    }

    private fun setupUI() {
        setupModelChips()
        updateQuestionDisplay()
        updateQuizStats()
    }

    private fun setupModelChips() {
        binding.chipGroupModels.removeAllViews()

        viewModel.availableModels.forEach { model ->
            val chip = Chip(requireContext()).apply {
                text = model.displayName
                isCheckable = true
                setOnClickListener {
                    viewModel.selectModel(model)
                    updateModelChipsSelection(model.displayName)
                }
            }
            binding.chipGroupModels.addView(chip)
        }

        // Seleccionar primer modelo por defecto
        if (binding.chipGroupModels.childCount > 0) {
            val firstChip = binding.chipGroupModels.getChildAt(0) as Chip
            firstChip.isChecked = true
            viewModel.selectModel(viewModel.availableModels.first())
        }
    }

    private fun updateModelChipsSelection(selectedModelName: String) {
        for (i in 0 until binding.chipGroupModels.childCount) {
            val chip = binding.chipGroupModels.getChildAt(i) as Chip
            chip.isChecked = chip.text == selectedModelName
        }
    }

    private fun setupSpinners() {
        // Setup subject spinner
        val subjectAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            viewModel.availableSubjects
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerSubject.adapter = subjectAdapter

        binding.spinnerSubject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSubject = viewModel.availableSubjects[position]
                viewModel.selectSubject(selectedSubject)
                updateSubjectColor(selectedSubject)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Setup difficulty spinner
        val difficultyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            viewModel.availableDifficulties
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spinnerDifficulty.adapter = difficultyAdapter

        binding.spinnerDifficulty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDifficulty = viewModel.availableDifficulties[position]
                viewModel.selectDifficulty(selectedDifficulty)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Generar preguntas con IA
        binding.btnGenerate.setOnClickListener {
            val selectedSubject = viewModel.selectedSubject.value ?: "Matemáticas"
            val selectedDifficulty = viewModel.selectedDifficulty.value ?: "Intermedio"
            viewModel.generateQuestions(selectedSubject, selectedDifficulty, 5)
        }

        // Generar preguntas random (sin IA)
        binding.btnGenerateRandom.setOnClickListener {
            val selectedSubject = viewModel.selectedSubject.value ?: "Matemáticas"
            val selectedDifficulty = viewModel.selectedDifficulty.value ?: "Intermedio"
            generateRandomQuestions(selectedSubject, selectedDifficulty)
        }

        // Respuestas de las opciones
        binding.optionA.setOnClickListener { selectAnswer(0) }
        binding.optionB.setOnClickListener { selectAnswer(1) }
        binding.optionC.setOnClickListener { selectAnswer(2) }
        binding.optionD.setOnClickListener { selectAnswer(3) }

        // Navegación
        binding.btnNext.setOnClickListener {
            if (isShowingExplanation) {
                goToNextQuestion()
            } else {
                submitAnswer()
            }
        }

        binding.btnPrevious.setOnClickListener {
            goToPreviousQuestion()
        }

        binding.btnSubmitQuiz.setOnClickListener {
            finishQuiz()
        }
    }
//
    private fun observeViewModel() {
        viewModel.questions.observe(viewLifecycleOwner) { questions ->
            currentQuestions = questions
            currentQuestionIndex = 0
            updateQuestionDisplay()
        }

        viewModel.currentQuestion.observe(viewLifecycleOwner) { question ->
            question?.let { updateQuestionUI(it) }
        }

        viewModel.currentQuestionIndex.observe(viewLifecycleOwner) { index ->
            currentQuestionIndex = index
            updateNavigationButtons()
        }

        viewModel.selectedAnswer.observe(viewLifecycleOwner) { answerIndex ->
            selectedAnswerIndex = answerIndex
            updateAnswerSelection()
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            updateLoadingState(isLoading)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.isQuizCompleted.observe(viewLifecycleOwner) { isCompleted ->
            if (isCompleted) {
                showQuizResults()
            }
        }

        viewModel.showExplanation.observe(viewLifecycleOwner) { showExplanation ->
            isShowingExplanation = showExplanation
            updateExplanationVisibility()
        }

        viewModel.quizStats.observe(viewLifecycleOwner) { stats ->
            updateQuizStats(stats)
        }
    }

    private fun generateRandomQuestions(subject: String, difficulty: String) {
        val randomQuestions = QuizUtils.generateRandomQuestions(subject, difficulty, 5)

        if (randomQuestions.isNotEmpty()) {
            currentQuestions = randomQuestions
            currentQuestionIndex = 0
            isShowingExplanation = false
            selectedAnswerIndex = null
            updateQuestionDisplay()

            val icon = QuizUtils.getSubjectIcon(subject)
            Toast.makeText(
                requireContext(),
                "$icon ${randomQuestions.size} preguntas de $subject generadas",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                requireContext(),
                "No hay preguntas disponibles para esta materia",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun selectAnswer(answerIndex: Int) {
        if (isShowingExplanation) return

        selectedAnswerIndex = answerIndex
        viewModel.selectAnswer(answerIndex)
        updateAnswerSelection()
    }

    private fun updateAnswerSelection() {
        val optionViews = listOf(binding.optionA, binding.optionB, binding.optionC, binding.optionD)

        optionViews.forEachIndexed { index, textView ->
            textView.isSelected = index == selectedAnswerIndex
            if (index == selectedAnswerIndex) {
                textView.setBackgroundColor(resources.getColor(R.color.primary_blue, null))
                textView.setTextColor(resources.getColor(R.color.white, null))
            } else {
                textView.setBackgroundResource(R.drawable.bg_quiz_option)
                textView.setTextColor(resources.getColor(R.color.text_primary, null))
            }
        }
    }

    private fun submitAnswer() {
        if (selectedAnswerIndex == null) {
            Toast.makeText(requireContext(), "Selecciona una respuesta", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.submitAnswer()
        showCorrectAnswer()
    }

    private fun showCorrectAnswer() {
        val currentQuestion = currentQuestions.getOrNull(currentQuestionIndex) ?: return
        val optionViews = listOf(binding.optionA, binding.optionB, binding.optionC, binding.optionD)

        optionViews.forEachIndexed { index, textView ->
            when {
                index == currentQuestion.correctAnswer -> {
                    textView.setBackgroundColor(resources.getColor(R.color.secondary_green, null))
                    textView.setTextColor(resources.getColor(R.color.white, null))
                }
                index == selectedAnswerIndex && index != currentQuestion.correctAnswer -> {
                    textView.setBackgroundColor(resources.getColor(R.color.accent_red, null))
                    textView.setTextColor(resources.getColor(R.color.white, null))
                }
                else -> {
                    textView.setBackgroundResource(R.drawable.bg_quiz_option)
                    textView.setTextColor(resources.getColor(R.color.text_primary, null))
                }
            }
        }

        // Mostrar explicación
        isShowingExplanation = true
        updateExplanationVisibility()
    }

    private fun resetAnswerSelection() {
        val optionViews = listOf(binding.optionA, binding.optionB, binding.optionC, binding.optionD)

        optionViews.forEach { textView ->
            textView.isSelected = false
            textView.setBackgroundResource(R.drawable.bg_quiz_option)
            textView.setTextColor(resources.getColor(R.color.text_primary, null))
        }

        selectedAnswerIndex = null
    }

    private fun goToNextQuestion() {
        if (currentQuestionIndex < currentQuestions.size - 1) {
            currentQuestionIndex++
            updateQuestionDisplay()
            resetAnswerSelection()
            isShowingExplanation = false
            updateExplanationVisibility()
        } else {
            finishQuiz()
        }
    }

    private fun goToPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--
            updateQuestionDisplay()
            resetAnswerSelection()
            isShowingExplanation = false
            updateExplanationVisibility()
        }
    }

    private fun updateQuestionDisplay() {
        if (currentQuestions.isEmpty()) {
            // Mostrar estado inicial
            binding.tvQuestionCounter.text = "Selecciona una materia para comenzar"
            binding.tvQuestion.text = "¿Cuál es la principal función de las mitocondrias en la célula?"
            updateNavigationButtons()
            return
        }

        val question = currentQuestions[currentQuestionIndex]
        updateQuestionUI(question)
        updateNavigationButtons()
    }

    private fun updateQuestionUI(question: QuizQuestion) {
        binding.tvQuestionCounter.text = "Pregunta ${currentQuestionIndex + 1} de ${currentQuestions.size}"
        binding.tvQuestion.text = question.question

        val options = question.options
        if (options.size >= 4) {
            binding.optionA.text = options[0]
            binding.optionB.text = options[1]
            binding.optionC.text = options[2]
            binding.optionD.text = options[3]
        }

        binding.tvExplanation.text = question.explanation

        // Agregar icono de la materia
        val icon = QuizUtils.getSubjectIcon(question.subject)
        binding.tvQuestionCounter.text = "$icon Pregunta ${currentQuestionIndex + 1} de ${currentQuestions.size}"
    }

    private fun updateNavigationButtons() {
        binding.btnPrevious.isEnabled = currentQuestionIndex > 0 && currentQuestions.isNotEmpty()

        binding.btnNext.text = when {
            currentQuestions.isEmpty() -> "Confirmar"
            isShowingExplanation -> {
                if (currentQuestionIndex < currentQuestions.size - 1) "Siguiente" else "Finalizar"
            }
            else -> "Confirmar"
        }

        binding.btnSubmitQuiz.visibility = if (currentQuestionIndex == currentQuestions.size - 1 &&
            isShowingExplanation &&
            currentQuestions.isNotEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun updateExplanationVisibility() {
        binding.cardExplanation.visibility = if (isShowingExplanation) View.VISIBLE else View.GONE
        updateNavigationButtons()
    }

    private fun finishQuiz() {
        if (currentQuestions.isEmpty()) {
            Toast.makeText(requireContext(), "Primero genera un quiz", Toast.LENGTH_SHORT).show()
            return
        }

        val stats = QuizUtils.calculateQuizStats(currentQuestions, getSelectedAnswers())
        showQuizResults(stats)
    }

    private fun getSelectedAnswers(): List<Int?> {
        // Esta función debería retornar las respuestas seleccionadas por el usuario
        // Por simplicidad, retornamos una lista de respuestas aleatorias para demostración
        return currentQuestions.map { selectedAnswerIndex }
    }

    private fun showQuizResults(stats: Map<String, Any>? = null) {
        val calculatedStats = stats ?: QuizUtils.calculateQuizStats(currentQuestions, getSelectedAnswers())

        val score = calculatedStats["correctAnswers"] as Int
        val totalQuestions = calculatedStats["totalQuestions"] as Int
        val percentage = calculatedStats["percentage"] as Int
        val grade = calculatedStats["grade"] as String
        val subject = viewModel.selectedSubject.value ?: "General"

        val icon = QuizUtils.getSubjectIcon(subject)
        val motivationalMessage = QuizUtils.getMotivationalMessage(percentage)

        val message = """
            $icon ¡Quiz Completado!
            
            📊 Resultados:
            • Materia: $subject
            • Puntuación: $score/$totalQuestions ($percentage%)
            • Calificación: $grade
            
            $motivationalMessage
        """.trimIndent()

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Resultados del Quiz")
            .setMessage(message)
            .setPositiveButton("Nuevo Quiz") { _, _ ->
                resetQuiz()
            }
            .setNegativeButton("Salir") { _, _ ->
                findNavController().navigateUp()
            }
            .setNeutralButton("Ver Detalles") { _, _ ->
                showDetailedResults(calculatedStats)
            }
            .setCancelable(false)
            .show()
    }

    private fun showDetailedResults(stats: Map<String, Any>) {
        val totalQuestions = stats["totalQuestions"] as Int
        val correctAnswers = stats["correctAnswers"] as Int
        val incorrectAnswers = stats["incorrectAnswers"] as Int
        val unanswered = stats["unanswered"] as Int
        val percentage = stats["percentage"] as Int

        val details = """
            📋 Análisis Detallado:
            
            ✅ Respuestas correctas: $correctAnswers
            ❌ Respuestas incorrectas: $incorrectAnswers
            ⏭️ Sin responder: $unanswered
            📊 Precisión: $percentage%
            
            💡 Recomendaciones:
            ${getStudyRecommendations(percentage)}
        """.trimIndent()

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Análisis Detallado")
            .setMessage(details)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    private fun getStudyRecommendations(percentage: Int): String {
        return when {
            percentage >= 90 -> "• Excelente dominio del tema\n• Considera temas más avanzados\n• Ayuda a otros estudiantes"
            percentage >= 70 -> "• Buen conocimiento general\n• Repasa los temas fallados\n• Practica más ejercicios"
            percentage >= 50 -> "• Necesitas más práctica\n• Revisa conceptos básicos\n• Dedica más tiempo de estudio"
            else -> "• Empieza con conceptos fundamentales\n• Busca ayuda adicional\n• Practica con quizzes más fáciles"
        }
    }

    private fun resetQuiz() {
        currentQuestions = emptyList()
        currentQuestionIndex = 0
        selectedAnswerIndex = null
        isShowingExplanation = false
        viewModel.resetQuiz()
        updateQuestionDisplay()
        resetAnswerSelection()
        updateExplanationVisibility()
    }

    private fun updateLoadingState(isLoading: Boolean) {
        binding.btnGenerate.isEnabled = !isLoading
        binding.btnGenerateRandom.isEnabled = !isLoading

        if (isLoading) {
            binding.btnGenerate.text = "🧠 Generando preguntas..."
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.btnGenerate.text = "🤖 Generar con IA"
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun updateSubjectColor(subject: String) {
        val color = QuizUtils.getSubjectColor(subject)
        val icon = QuizUtils.getSubjectIcon(subject)

        // Actualizar UI con el color e icono de la materia
        // Aquí puedes personalizar más elementos visuales según la materia
    }

    private fun updateQuizStats(stats: Map<String, Any>? = null) {
        stats?.let {
            val completedQuizzes = it["completedQuizzes"] as? Int ?: 0
            val averageScore = it["averageScore"] as? Int ?: 0
            val bestSubject = it["bestSubject"] as? String ?: "N/A"

            binding.tvQuizzesCompleted.text = completedQuizzes.toString()
            binding.tvAverageScore.text = "${averageScore}%"
            binding.tvBestSubject.text = if (bestSubject.length > 8) {
                bestSubject.take(8) + "..."
            } else {
                bestSubject
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}