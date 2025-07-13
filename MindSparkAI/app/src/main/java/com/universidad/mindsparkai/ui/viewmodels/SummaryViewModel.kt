package com.universidad.mindsparkai.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universidad.mindsparkai.data.repository.AIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _summary = MutableLiveData<String?>()
    val summary: LiveData<String?> = _summary

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _selectedModel = MutableLiveData<String>()
    val selectedModel: LiveData<String> = _selectedModel

    init {
        _selectedModel.value = "Claude-3"
    }

    fun generateSummary(text: String) {
        if (text.isBlank()) {
            _error.value = "Por favor, ingresa el texto a resumir"
            return
        }

        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val summary = aiRepository.generateSummary(text)

                if (summary != null) {
                    _summary.value = summary
                } else {
                    _error.value = "No se pudo generar el resumen. Intenta de nuevo."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al generar resumen"
            } finally {
                _loading.value = false
            }
        }
    }

    fun selectModel(model: String) {
        _selectedModel.value = model
    }

    fun clearSummary() {
        _summary.value = null
    }

    fun clearError() {
        _error.value = null
    }
}