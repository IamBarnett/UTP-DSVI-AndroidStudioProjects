package com.universidad.mindsparkai.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universidad.mindsparkai.data.models.StudyPlan
import com.universidad.mindsparkai.data.repository.AIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyPlanViewModel @Inject constructor(
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _studyPlan = MutableLiveData<StudyPlan?>()
    val studyPlan: LiveData<StudyPlan?> = _studyPlan

    private val _selectedSubjects = MutableLiveData<List<String>>()
    val selectedSubjects: LiveData<List<String>> = _selectedSubjects

    private val _hoursPerDay = MutableLiveData<Int>()
    val hoursPerDay: LiveData<Int> = _hoursPerDay

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val availableSubjects = listOf(
        "Matemáticas", "Química", "Historia", "Física", "Literatura",
        "Biología", "Inglés", "Filosofía", "Economía", "Programación"
    )

    init {
        _selectedSubjects.value = listOf("Matemáticas", "Química")
        _hoursPerDay.value = 4
        loadSamplePlan()
    }

    private fun loadSamplePlan() {
        // Load sample study plan for demo
        val samplePlan = StudyPlan(
            id = "sample_plan",
            userId = "user123",
            weekStart = System.currentTimeMillis(),
            aiRecommendations = listOf(
                "Dedica más tiempo a Química esta semana (rendimiento: 78%)",
                "Tu mejor horario de concentración: 9:00-11:00 AM",
                "Toma descansos cada 45 minutos",
                "Repasa Matemáticas el viernes antes del examen"
            ),
            totalHours = 28
        )
        _studyPlan.value = samplePlan
    }

    fun getAvailableSubjects(): List<String> = availableSubjects

    fun toggleSubject(subject: String) {
        val currentList = _selectedSubjects.value?.toMutableList() ?: mutableListOf()

        if (currentList.contains(subject)) {
            currentList.remove(subject)
        } else {
            currentList.add(subject)
        }

        _selectedSubjects.value = currentList
    }

    fun setHoursPerDay(hours: Int) {
        _hoursPerDay.value = hours
    }

    fun generateStudyPlan() {
        val subjects = _selectedSubjects.value ?: return
        val hours = _hoursPerDay.value ?: return

        if (subjects.isEmpty()) {
            _error.value = "Selecciona al menos una materia"
            return
        }

        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val goals = "Mejorar rendimiento académico y preparación para exámenes"
                val response = aiRepository.generateStudyPlan(subjects, hours, goals)

                if (response != null) {
                    // TODO: Parse AI response and create StudyPlan object
                    // For now, we'll update the sample plan
                    val updatedPlan = _studyPlan.value?.copy(
                        totalHours = hours * 7,
                        aiRecommendations = listOf(
                            "Plan personalizado generado para: ${subjects.joinToString(", ")}",
                            "Distribución de $hours horas diarias optimizada",
                            "Recomendaciones basadas en tu historial de estudio",
                            "Recuerda tomar descansos regulares"
                        )
                    )
                    _studyPlan.value = updatedPlan
                } else {
                    _error.value = "No se pudo generar el plan. Intenta de nuevo."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al generar plan de estudio"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}