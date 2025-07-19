package com.universidad.mindsparkai.ui.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universidad.mindsparkai.data.repository.AIRepository
import com.universidad.mindsparkai.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
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

    private val _selectedModel = MutableLiveData<AIRepository.AIModel>()
    val selectedModel: LiveData<AIRepository.AIModel> = _selectedModel

    private val _uploadedFileName = MutableLiveData<String?>()
    val uploadedFileName: LiveData<String?> = _uploadedFileName

    private val _wordCount = MutableLiveData<Int>()
    val wordCount: LiveData<Int> = _wordCount

    private val _estimatedTime = MutableLiveData<Int>()
    val estimatedTime: LiveData<Int> = _estimatedTime

    // Modelos preferidos para resúmenes
    val availableModels = listOf(
        AIRepository.AIModel.CLAUDE_3_SONNET,
        AIRepository.AIModel.GPT_4,
        AIRepository.AIModel.GPT_3_5,
        AIRepository.AIModel.GEMINI_PRO
    )

    init {
        _selectedModel.value = AIRepository.AIModel.CLAUDE_3_SONNET
    }

    fun generateSummary(text: String) {
        if (text.isBlank()) {
            _error.value = "Por favor, ingresa el texto a resumir"
            return
        }

        if (text.length < 100) {
            _error.value = "El texto debe tener al menos 100 caracteres para generar un resumen útil"
            return
        }

        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                // Calcular estadísticas del texto
                updateTextStatistics(text)

                val result = aiRepository.generateSummary(
                    text = text,
                    model = _selectedModel.value ?: AIRepository.AIModel.CLAUDE_3_SONNET
                )

                result.fold(
                    onSuccess = { summary ->
                        _summary.value = summary
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Error al generar resumen"
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Error inesperado"
            } finally {
                _loading.value = false
            }
        }
    }

    fun processUploadedFile(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val fileName = getFileName(context, uri)
                _uploadedFileName.value = fileName

                // Validar tipo de archivo
                val extension = Utils.getFileExtension(fileName ?: "")
                if (!Utils.isSupportedFileType(fileName ?: "")) {
                    _error.value = "Formato de archivo no soportado. Usa PDF, DOCX o TXT"
                    return@launch
                }

                // Leer contenido del archivo
                val text = withContext(Dispatchers.IO) {
                    readFileContent(context, uri, extension)
                }

                if (text.isNotEmpty()) {
                    generateSummary(text)
                } else {
                    _error.value = "No se pudo extraer texto del archivo"
                }

            } catch (e: Exception) {
                _error.value = "Error al procesar el archivo: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        return try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (it.moveToFirst()) {
                    it.getString(nameIndex)
                } else null
            }
        } catch (e: Exception) {
            "archivo_${System.currentTimeMillis()}.txt"
        }
    }

    private suspend fun readFileContent(context: Context, uri: Uri, extension: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream: InputStream = context.contentResolver.openInputStream(uri)
                    ?: throw Exception("No se pudo abrir el archivo")

                when (extension.lowercase()) {
                    "txt" -> {
                        inputStream.bufferedReader().use { it.readText() }
                    }
                    "pdf" -> {
                        // Para PDF necesitarías una librería como iText o PDFBox
                        // Por ahora, mensaje de no implementado
                        throw Exception("Lectura de PDF no implementada aún. Usa TXT por ahora.")
                    }
                    "docx" -> {
                        // Para DOCX necesitarías Apache POI
                        throw Exception("Lectura de DOCX no implementada aún. Usa TXT por ahora.")
                    }
                    else -> throw Exception("Formato no soportado")
                }
            } catch (e: Exception) {
                throw Exception("Error al leer archivo: ${e.message}")
            }
        }
    }

    private fun updateTextStatistics(text: String) {
        val wordCount = text.split("\\s+".toRegex()).size
        _wordCount.value = wordCount
        _estimatedTime.value = Utils.estimateReadingTime(text)
    }

    fun selectModel(model: AIRepository.AIModel) {
        _selectedModel.value = model
    }

    fun clearSummary() {
        _summary.value = null
        _uploadedFileName.value = null
        _wordCount.value = 0
        _estimatedTime.value = 0
    }

    fun clearError() {
        _error.value = null
    }

    fun regenerateSummary() {
        val currentSummary = _summary.value
        if (currentSummary != null) {
            // Si ya hay un resumen, intentar regenerar con el texto original
            // Por simplicidad, mostrar mensaje para que el usuario vuelva a pegar el texto
            _error.value = "Para regenerar, por favor vuelve a pegar el texto original"
        }
    }

    // Funciones de utilidad para diferentes tipos de resumen
    fun generateDetailedSummary(text: String) {
        generateCustomSummary(text, "detallado")
    }

    fun generateBriefSummary(text: String) {
        generateCustomSummary(text, "breve")
    }

    fun generateBulletPoints(text: String) {
        generateCustomSummary(text, "puntos")
    }

    private fun generateCustomSummary(text: String, type: String) {
        if (text.isBlank()) {
            _error.value = "Por favor, ingresa el texto a resumir"
            return
        }

        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val customPrompt = when (type) {
                    "detallado" -> """
                        Crea un resumen detallado del siguiente texto, manteniendo todos los puntos importantes y organizándolos de manera clara:
                        
                        $text
                        
                        El resumen debe:
                        - Incluir todos los conceptos principales
                        - Mantener el contexto importante
                        - Estar bien estructurado con subtemas
                        - Ser comprensible pero completo
                    """.trimIndent()

                    "breve" -> """
                        Crea un resumen muy breve y conciso del siguiente texto, extrayendo solo lo más esencial:
                        
                        $text
                        
                        El resumen debe:
                        - Ser máximo 3-4 oraciones
                        - Capturar la idea principal
                        - Ser directo y claro
                        - No incluir detalles secundarios
                    """.trimIndent()

                    "puntos" -> """
                        Convierte el siguiente texto en una lista de puntos clave organizados:
                        
                        $text
                        
                        Formato requerido:
                        • Punto principal 1
                        • Punto principal 2
                        • etc.
                        
                        Incluye solo los puntos más importantes y relevantes.
                    """.trimIndent()

                    else -> text
                }

                val result = aiRepository.sendChatMessage(
                    message = customPrompt,
                    model = _selectedModel.value ?: AIRepository.AIModel.CLAUDE_3_SONNET
                )

                result.fold(
                    onSuccess = { summary ->
                        _summary.value = summary
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Error al generar resumen"
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Error inesperado"
            } finally {
                _loading.value = false
            }
        }
    }

    // Función para obtener estadísticas del resumen
    fun getSummaryStats(): Map<String, Any> {
        val summary = _summary.value ?: return emptyMap()
        val originalWordCount = _wordCount.value ?: 0
        val summaryWordCount = summary.split("\\s+".toRegex()).size
        val compressionRatio = if (originalWordCount > 0) {
            ((originalWordCount - summaryWordCount).toFloat() / originalWordCount * 100).toInt()
        } else 0

        return mapOf(
            "originalWords" to originalWordCount,
            "summaryWords" to summaryWordCount,
            "compressionRatio" to compressionRatio,
            "estimatedReadingTime" to Utils.estimateReadingTime(summary)
        )
    }
}
