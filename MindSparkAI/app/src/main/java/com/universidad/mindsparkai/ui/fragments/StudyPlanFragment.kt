package com.universidad.mindsparkai.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.universidad.mindsparkai.R
import com.universidad.mindsparkai.data.models.DailyPlan
import com.universidad.mindsparkai.data.models.StudySession
import com.universidad.mindsparkai.databinding.FragmentStudyPlanBinding
import com.universidad.mindsparkai.ui.viewmodels.StudyPlanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudyPlanFragment : Fragment() {

    private var _binding: FragmentStudyPlanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudyPlanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudyPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCreatePlan.setOnClickListener {
            viewModel.createNewPlan()
        }

        binding.btnPreviousWeek.setOnClickListener {
            viewModel.moveToPreviousWeek()
        }

        binding.btnNextWeek.setOnClickListener {
            viewModel.moveToNextWeek()
        }

        binding.fabAddSession.setOnClickListener {
            showAddSessionDialog()
        }

        binding.btnAddQuickSession.setOnClickListener {
            showAddSessionDialog()
        }

        binding.btnTemplate.setOnClickListener {
            showTemplateDialog()
        }

        binding.btnWeekOptions.setOnClickListener {
            showWeekOptionsMenu()
        }

        binding.btnStats.setOnClickListener {
            showWeekStatsDialog()
        }

        // Agregar menú contextual para opciones adicionales
        binding.btnStats.setOnLongClickListener {
            showWeekOptionsMenu()
            true
        }
    }

    private fun showWeekOptionsMenu() {
        val options = arrayOf(
            "📊 Ver estadísticas",
            "📤 Exportar plan",
            "📋 Duplicar semana",
            "🗑️ Limpiar semana"
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Opciones de la semana")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showWeekStatsDialog()
                    1 -> exportPlan()
                    2 -> duplicateWeek()
                    3 -> clearWeek()
                }
            }
            .show()
    }

    private fun showTemplateDialog() {
        val templates = arrayOf(
            "📚 Plan de estudio básico (2h/día)",
            "🎯 Preparación de exámenes (4h/día)",
            "⚡ Plan intensivo (6h/día)",
            "🌱 Plan ligero (1h/día)"
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Plantillas de Estudio")
            .setItems(templates) { _, which ->
                when (which) {
                    0 -> applyBasicTemplate()
                    1 -> applyExamTemplate()
                    2 -> applyIntensiveTemplate()
                    3 -> applyLightTemplate()
                }
            }
            .show()
    }

    private fun applyBasicTemplate() {
        // Plan básico: 2 horas por día, distribuidas en 2 sesiones
        val basicSessions = listOf(
            Triple("Matemáticas", "Álgebra y cálculo", 60),
            Triple("Química", "Química orgánica", 60)
        )

        applyTemplate(basicSessions, "Plan básico aplicado")
    }

    private fun applyExamTemplate() {
        // Plan de exámenes: 4 horas por día, enfocado en repaso
        val examSessions = listOf(
            Triple("Matemáticas", "Repaso general", 90),
            Triple("Química", "Ejercicios de práctica", 90),
            Triple("Repaso", "Simulacro de examen", 60)
        )

        applyTemplate(examSessions, "Plan de exámenes aplicado")
    }

    private fun applyIntensiveTemplate() {
        // Plan intensivo: 6 horas por día
        val intensiveSessions = listOf(
            Triple("Matemáticas", "Teoría y práctica", 120),
            Triple("Química", "Laboratorio virtual", 90),
            Triple("Física", "Resolución de problemas", 90),
            Triple("Repaso", "Consolidación", 60)
        )

        applyTemplate(intensiveSessions, "Plan intensivo aplicado")
    }

    private fun applyLightTemplate() {
        // Plan ligero: 1 hora por día
        val lightSessions = listOf(
            Triple("Matemáticas", "Conceptos básicos", 60)
        )

        applyTemplate(lightSessions, "Plan ligero aplicado")
    }

    private fun applyTemplate(sessions: List<Triple<String, String, Int>>, successMessage: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Aplicar Plantilla")
            .setMessage("¿Quieres aplicar esta plantilla a todos los días de la semana? Esto reemplazará las sesiones existentes.")
            .setPositiveButton("Aplicar") { _, _ ->
                // Aplicar sesiones a cada día de la semana (lunes a viernes)
                for (dayIndex in 0..4) { // Solo días laborables
                    sessions.forEach { (subject, topic, duration) ->
                        viewModel.addStudySession(dayIndex, subject, topic, duration, "study")
                    }
                }
                showSuccess(successMessage)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun duplicateWeek() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Duplicar Semana")
            .setMessage("¿Quieres copiar todas las sesiones de esta semana a la siguiente?")
            .setPositiveButton("Duplicar") { _, _ ->
                viewModel.duplicateWeekPlan()
                showSuccess("Plan duplicado para la próxima semana")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun clearWeek() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Limpiar Semana")
            .setMessage("¿Estás seguro de que quieres eliminar todas las sesiones de esta semana?")
            .setPositiveButton("Limpiar") { _, _ ->
                viewModel.clearWeekPlan()
                showSuccess("Semana limpiada")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            if (isEmpty) {
                showEmptyState()
            } else {
                hideEmptyState()
            }
        }

        viewModel.studyPlan.observe(viewLifecycleOwner) { plan ->
            plan?.let {
                updatePlanContent(it.dailyPlans)
                updateWeekSummary()
            }
        }

        viewModel.currentWeekStart.observe(viewLifecycleOwner) {
            updateWeekNavigation()
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // Mostrar/ocultar loading si es necesario
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showError(it)
                viewModel.clearError()
            }
        }
    }

    private fun showEmptyState() {
        binding.emptyState.visibility = View.VISIBLE
        binding.planContent.visibility = View.GONE
        binding.fabAddSession.visibility = View.GONE
    }

    private fun hideEmptyState() {
        binding.emptyState.visibility = View.GONE
        binding.planContent.visibility = View.VISIBLE
        binding.fabAddSession.visibility = View.VISIBLE
        binding.quickActions.visibility = View.VISIBLE
    }

    private fun updateWeekNavigation() {
        val weekTitle = viewModel.getWeekTitle()
        val weekDates = viewModel.getWeekDates()

        binding.tvWeekTitle.text = weekTitle
        binding.tvWeekDates.text = "${weekDates.first} - ${weekDates.second}"
    }

    private fun updateWeekSummary() {
        val summary = viewModel.getWeekSummary()

        binding.tvTotalHours.text = "${summary["totalHours"]}h"
        binding.tvSubjectsCount.text = summary["subjectsCount"].toString()
        binding.tvSessionsCount.text = summary["sessionsCount"].toString()
    }

    private fun updatePlanContent(dailyPlans: List<DailyPlan>) {
        binding.dailyPlansContainer.removeAllViews()

        dailyPlans.forEachIndexed { dayIndex, dailyPlan ->
            val dayView = createDayView(dayIndex, dailyPlan)
            binding.dailyPlansContainer.addView(dayView)
        }
    }

    private fun createDayView(dayIndex: Int, dailyPlan: DailyPlan): View {
        val dayView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_daily_plan, binding.dailyPlansContainer, false)

        val tvDayName = dayView.findViewById<android.widget.TextView>(R.id.tv_day_name)
        val sessionsContainer = dayView.findViewById<LinearLayout>(R.id.sessions_container)
        val btnAddSession = dayView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_add_session)

        tvDayName.text = dailyPlan.date

        // Limpiar sesiones existentes
        sessionsContainer.removeAllViews()

        // Agregar sesiones
        dailyPlan.sessions.forEachIndexed { sessionIndex, session ->
            val sessionView = createSessionView(dayIndex, sessionIndex, session)
            sessionsContainer.addView(sessionView)
        }

        // Mostrar mensaje si no hay sesiones
        if (dailyPlan.sessions.isEmpty()) {
            val emptyView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_empty_day, sessionsContainer, false)
            sessionsContainer.addView(emptyView)
        }

        btnAddSession.setOnClickListener {
            showAddSessionDialog(dayIndex)
        }

        return dayView
    }

    private fun createSessionView(dayIndex: Int, sessionIndex: Int, session: StudySession): View {
        val sessionView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_study_session, null, false)

        val tvSubject = sessionView.findViewById<android.widget.TextView>(R.id.tv_subject)
        val tvTopic = sessionView.findViewById<android.widget.TextView>(R.id.tv_topic)
        val tvDuration = sessionView.findViewById<android.widget.TextView>(R.id.tv_duration)
        val tvType = sessionView.findViewById<android.widget.TextView>(R.id.tv_type)
        val subjectIcon = sessionView.findViewById<android.widget.TextView>(R.id.tv_subject_icon)

        tvSubject.text = session.subject
        tvTopic.text = session.topic
        tvDuration.text = "${session.duration}min"

        val typeText = viewModel.sessionTypes.find { it.first == session.type }?.second ?: session.type
        tvType.text = typeText

        subjectIcon.text = viewModel.getSubjectEmoji(session.subject)

        // Configurar click listeners
        sessionView.setOnClickListener {
            showEditSessionDialog(dayIndex, sessionIndex, session)
        }

        sessionView.setOnLongClickListener {
            showDeleteSessionDialog(dayIndex, sessionIndex, session)
            true
        }

        return sessionView
    }

    private fun showAddSessionDialog(dayIndex: Int = -1) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_session, null)

        val etSubject = dialogView.findViewById<TextInputEditText>(R.id.et_subject)
        val etTopic = dialogView.findViewById<TextInputEditText>(R.id.et_topic)
        val etDuration = dialogView.findViewById<TextInputEditText>(R.id.et_duration)
        val spinnerType = dialogView.findViewById<android.widget.Spinner>(R.id.spinner_type)
        val spinnerDay = dialogView.findViewById<android.widget.Spinner>(R.id.spinner_day)
        val tvDayLabel = dialogView.findViewById<android.widget.TextView>(R.id.tv_day_label)

        // Botones de duración rápida
        val btn30min = dialogView.findViewById<android.widget.Button>(R.id.btn_30min)
        val btn60min = dialogView.findViewById<android.widget.Button>(R.id.btn_60min)
        val btn90min = dialogView.findViewById<android.widget.Button>(R.id.btn_90min)
        val btn120min = dialogView.findViewById<android.widget.Button>(R.id.btn_120min)

        // Configurar botones de duración rápida
        btn30min.setOnClickListener { etDuration.setText("30") }
        btn60min.setOnClickListener { etDuration.setText("60") }
        btn90min.setOnClickListener { etDuration.setText("90") }
        btn120min.setOnClickListener { etDuration.setText("120") }

        // Configurar spinners
        setupSubjectAutoComplete(etSubject)
        setupTypeSpinner(spinnerType)

        // Mostrar/ocultar selector de día según el contexto
        if (dayIndex == -1) {
            setupDaySpinner(spinnerDay, dayIndex)
            tvDayLabel.visibility = View.VISIBLE
            spinnerDay.visibility = View.VISIBLE
        } else {
            tvDayLabel.visibility = View.GONE
            spinnerDay.visibility = View.GONE
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Agregar Sesión de Estudio")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val subject = etSubject.text.toString().trim()
                val topic = etTopic.text.toString().trim()
                val durationStr = etDuration.text.toString().trim()
                val selectedDay = if (dayIndex == -1) spinnerDay.selectedItemPosition else dayIndex
                val selectedType = viewModel.sessionTypes[spinnerType.selectedItemPosition].first

                if (validateSessionInput(subject, topic, durationStr)) {
                    val duration = durationStr.toInt()
                    viewModel.addStudySession(selectedDay, subject, topic, duration, selectedType)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditSessionDialog(dayIndex: Int, sessionIndex: Int, session: StudySession) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_session, null)

        val etSubject = dialogView.findViewById<TextInputEditText>(R.id.et_subject)
        val etTopic = dialogView.findViewById<TextInputEditText>(R.id.et_topic)
        val etDuration = dialogView.findViewById<TextInputEditText>(R.id.et_duration)
        val spinnerType = dialogView.findViewById<android.widget.Spinner>(R.id.spinner_type)

        // Rellenar con datos existentes
        etSubject.setText(session.subject)
        etTopic.setText(session.topic)
        etDuration.setText(session.duration.toString())

        setupSubjectAutoComplete(etSubject)
        setupTypeSpinner(spinnerType)

        // Seleccionar tipo actual
        val currentTypeIndex = viewModel.sessionTypes.indexOfFirst { it.first == session.type }
        if (currentTypeIndex != -1) {
            spinnerType.setSelection(currentTypeIndex)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Editar Sesión de Estudio")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val subject = etSubject.text.toString().trim()
                val topic = etTopic.text.toString().trim()
                val durationStr = etDuration.text.toString().trim()
                val selectedType = viewModel.sessionTypes[spinnerType.selectedItemPosition].first

                if (validateSessionInput(subject, topic, durationStr)) {
                    val duration = durationStr.toInt()
                    viewModel.updateStudySession(dayIndex, sessionIndex, subject, topic, duration, selectedType)
                }
            }
            .setNeutralButton("Eliminar") { _, _ ->
                showDeleteSessionDialog(dayIndex, sessionIndex, session)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDeleteSessionDialog(dayIndex: Int, sessionIndex: Int, session: StudySession) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Eliminar Sesión")
            .setMessage("¿Estás seguro de que quieres eliminar la sesión de ${session.subject}?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.removeStudySession(dayIndex, sessionIndex)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showWeekStatsDialog() {
        val summary = viewModel.getWeekSummary()
        val subjectDistribution = summary["subjectDistribution"] as Map<String, Int>

        val message = buildString {
            appendLine("📊 Resumen de la Semana")
            appendLine()
            appendLine("⏰ Horas totales: ${summary["totalHours"]}h")
            appendLine("📚 Materias: ${summary["subjectsCount"]}")
            appendLine("📝 Sesiones: ${summary["sessionsCount"]}")
            appendLine()

            if (subjectDistribution.isNotEmpty()) {
                appendLine("📋 Distribución por materia:")
                subjectDistribution.forEach { (subject, hours) ->
                    val emoji = viewModel.getSubjectEmoji(subject)
                    appendLine("$emoji $subject: ${hours}h")
                }
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Estadísticas")
            .setMessage(message)
            .setPositiveButton("Cerrar", null)
            .setNeutralButton("Exportar") { _, _ ->
                exportPlan()
            }
            .show()
    }

    private fun setupSubjectAutoComplete(editText: TextInputEditText) {
        val adapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            viewModel.availableSubjects
        )

        // Convertir a AutoCompleteTextView si es necesario
        if (editText.parent is TextInputLayout) {
            val autoComplete = android.widget.AutoCompleteTextView(requireContext())
            autoComplete.setText(editText.text)
            autoComplete.setAdapter(adapter)
            autoComplete.threshold = 1
        }
    }

    private fun setupTypeSpinner(spinner: android.widget.Spinner) {
        val typeNames = viewModel.sessionTypes.map { it.second }
        val adapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            typeNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupDaySpinner(spinner: android.widget.Spinner, selectedDay: Int = -1) {
        val plan = viewModel.studyPlan.value ?: return
        val dayNames = plan.dailyPlans.map { it.date }

        val adapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            dayNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        if (selectedDay != -1 && selectedDay < dayNames.size) {
            spinner.setSelection(selectedDay)
        }
    }

    private fun validateSessionInput(subject: String, topic: String, duration: String): Boolean {
        when {
            subject.isEmpty() -> {
                showError("Por favor ingresa una materia")
                return false
            }
            topic.isEmpty() -> {
                showError("Por favor ingresa un tema")
                return false
            }
            duration.isEmpty() -> {
                showError("Por favor ingresa la duración")
                return false
            }
            duration.toIntOrNull() == null || duration.toInt() <= 0 -> {
                showError("La duración debe ser un número mayor a 0")
                return false
            }
            duration.toInt() > 480 -> { // Máximo 8 horas
                showError("La duración máxima es 480 minutos (8 horas)")
                return false
            }
        }
        return true
    }

    private fun exportPlan() {
        val exportText = viewModel.exportPlan()

        // Crear intent para compartir
        val shareIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, exportText)
            putExtra(android.content.Intent.EXTRA_SUBJECT, "Mi Plan de Estudio - ${viewModel.getWeekTitle()}")
            type = "text/plain"
        }

        startActivity(android.content.Intent.createChooser(shareIntent, "Exportar Plan de Estudio"))
    }

    private fun showError(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_LONG).show()
    }

    private fun showSuccess(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}