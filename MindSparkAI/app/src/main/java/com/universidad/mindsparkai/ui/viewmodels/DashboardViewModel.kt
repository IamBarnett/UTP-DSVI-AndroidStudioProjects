package com.universidad.mindsparkai.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.universidad.mindsparkai.data.models.User
import com.universidad.mindsparkai.data.repository.AuthRepository
import com.universidad.mindsparkai.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            try {
                _loading.value = true

                // Obtener usuario actual autenticado
                val currentUser = userRepository.getCurrentUser()

                if (currentUser != null) {
                    // Usuario encontrado en Firestore
                    _user.value = currentUser
                } else {
                    // Usuario autenticado pero sin datos en Firestore, crear perfil básico
                    val firebaseUser = authRepository.getCurrentUser()
                    if (firebaseUser != null) {
                        val newUser = User(
                            id = firebaseUser.uid,
                            name = firebaseUser.displayName ?: "Usuario",
                            email = firebaseUser.email ?: "",
                            university = "",
                            career = "",
                            studyHours = 0,
                            aiQueries = 0,
                            averageQuizScore = 0f,
                            subjects = emptyList(),
                            createdAt = System.currentTimeMillis()
                        )

                        // Guardar en Firestore
                        val success = userRepository.createUser(newUser)
                        if (success) {
                            _user.value = newUser
                        } else {
                            _error.value = "Error al crear perfil de usuario"
                        }
                    } else {
                        _error.value = "Usuario no autenticado"
                    }
                }

            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar datos del usuario"

                // Fallback con datos básicos si hay error
                val firebaseUser = authRepository.getCurrentUser()
                if (firebaseUser != null) {
                    _user.value = User(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "Usuario",
                        email = firebaseUser.email ?: "",
                        university = "",
                        career = "",
                        studyHours = 0,
                        aiQueries = 0,
                        averageQuizScore = 0f,
                        subjects = listOf("Matemáticas", "Química", "Física"),
                        createdAt = System.currentTimeMillis()
                    )
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun refreshData() {
        loadUserData()
    }

    fun updateUserStats(
        studyHours: Int? = null,
        aiQueries: Int? = null,
        averageQuizScore: Float? = null
    ) {
        val currentUser = _user.value ?: return

        viewModelScope.launch {
            try {
                val updatedUser = currentUser.copy(
                    studyHours = studyHours ?: currentUser.studyHours,
                    aiQueries = aiQueries ?: currentUser.aiQueries,
                    averageQuizScore = averageQuizScore ?: currentUser.averageQuizScore
                )

                val success = userRepository.updateUser(updatedUser)
                if (success) {
                    _user.value = updatedUser
                }
            } catch (e: Exception) {
                _error.value = "Error al actualizar estadísticas"
            }
        }
    }
}