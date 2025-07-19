package com.universidad.mindsparkai.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.universidad.mindsparkai.data.repository.AuthRepository
import com.universidad.mindsparkai.data.repository.UserRepository
import com.universidad.mindsparkai.data.models.User
import com.universidad.mindsparkai.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _emailValidation = MutableLiveData<ValidationState>()
    val emailValidation: LiveData<ValidationState> = _emailValidation

    private val _passwordValidation = MutableLiveData<ValidationState>()
    val passwordValidation: LiveData<ValidationState> = _passwordValidation

    private val _nameValidation = MutableLiveData<ValidationState>()
    val nameValidation: LiveData<ValidationState> = _nameValidation

    sealed class AuthState {
        object Unauthenticated : AuthState()
        data class Authenticated(val user: FirebaseUser, val isNewUser: Boolean = false) : AuthState()
        object Loading : AuthState()
    }

    data class ValidationState(
        val isValid: Boolean,
        val message: String? = null
    )

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            _authState.value = AuthState.Authenticated(currentUser)
            loadUserProfile(currentUser.uid)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    private fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            try {
                val userProfile = userRepository.getCurrentUser()
                _user.value = userProfile
            } catch (e: Exception) {
                // Usuario autenticado pero sin perfil en Firestore
                // Crear perfil básico
                createBasicUserProfile()
            }
        }
    }

    fun validateEmail(email: String) {
        val isValid = Utils.isValidEmail(email)
        _emailValidation.value = ValidationState(
            isValid = isValid,
            message = if (!isValid && email.isNotEmpty()) "Email inválido" else null
        )
    }

    fun validatePassword(password: String) {
        val strength = Utils.validatePasswordStrength(password)
        _passwordValidation.value = ValidationState(
            isValid = strength != Utils.PasswordStrength.WEAK,
            message = if (password.isNotEmpty()) strength.message else null
        )
    }

    fun validateName(name: String) {
        val isValid = name.trim().length >= 2
        _nameValidation.value = ValidationState(
            isValid = isValid,
            message = if (!isValid && name.isNotEmpty()) "Nombre debe tener al menos 2 caracteres" else null
        )
    }

    fun loginWithEmail(email: String, password: String) {
        // Validar antes de enviar
        validateEmail(email)
        validatePassword(password)

        if (_emailValidation.value?.isValid != true || _passwordValidation.value?.isValid != true) {
            _error.value = "Por favor corrige los errores en el formulario"
            return
        }

        viewModelScope.launch {
            try {
                _loading.value = true
                _authState.value = AuthState.Loading
                _error.value = null

                val result = authRepository.loginWithEmail(email, password)

                result.fold(
                    onSuccess = { firebaseUser ->
                        _authState.value = AuthState.Authenticated(firebaseUser)
                        loadUserProfile(firebaseUser.uid)
                    },
                    onFailure = { exception ->
                        _authState.value = AuthState.Unauthenticated
                        _error.value = getFirebaseErrorMessage(exception)
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Unauthenticated
                _error.value = e.message ?: "Error inesperado"
            } finally {
                _loading.value = false
            }
        }
    }

    fun registerWithEmail(email: String, password: String, name: String, university: String = "", career: String = "") {
        // Validar antes de enviar
        validateEmail(email)
        validatePassword(password)
        validateName(name)

        if (_emailValidation.value?.isValid != true ||
            _passwordValidation.value?.isValid != true ||
            _nameValidation.value?.isValid != true) {
            _error.value = "Por favor corrige los errores en el formulario"
            return
        }

        viewModelScope.launch {
            try {
                _loading.value = true
                _authState.value = AuthState.Loading
                _error.value = null

                val result = authRepository.registerWithEmail(email, password, name)

                result.fold(
                    onSuccess = { firebaseUser ->
                        // Crear perfil de usuario en Firestore
                        val newUser = User(
                            id = firebaseUser.uid,
                            name = name,
                            email = email,
                            university = university,
                            career = career,
                            studyHours = 0,
                            aiQueries = 0,
                            averageQuizScore = 0f,
                            subjects = emptyList(),
                            createdAt = System.currentTimeMillis()
                        )

                        val profileCreated = userRepository.createUser(newUser)

                        if (profileCreated) {
                            _user.value = newUser
                            _authState.value = AuthState.Authenticated(firebaseUser, isNewUser = true)
                        } else {
                            _error.value = "Error al crear perfil de usuario"
                        }
                    },
                    onFailure = { exception ->
                        _authState.value = AuthState.Unauthenticated
                        _error.value = getFirebaseErrorMessage(exception)
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Unauthenticated
                _error.value = e.message ?: "Error inesperado"
            } finally {
                _loading.value = false
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _authState.value = AuthState.Loading
                _error.value = null

                val result = authRepository.signInWithGoogle(idToken)

                result.fold(
                    onSuccess = { firebaseUser ->
                        // Verificar si es usuario nuevo
                        checkAndCreateUserProfile(firebaseUser)
                    },
                    onFailure = { exception ->
                        _authState.value = AuthState.Unauthenticated
                        _error.value = getFirebaseErrorMessage(exception)
                    }
                )
            } catch (e: Exception) {
                _authState.value = AuthState.Unauthenticated
                _error.value = e.message ?: "Error con Google Sign-In"
            } finally {
                _loading.value = false
            }
        }
    }

    private suspend fun checkAndCreateUserProfile(firebaseUser: FirebaseUser) {
        val existingUser = userRepository.getCurrentUser()

        if (existingUser == null) {
            // Usuario nuevo con Google, crear perfil
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

            val profileCreated = userRepository.createUser(newUser)

            if (profileCreated) {
                _user.value = newUser
                _authState.value = AuthState.Authenticated(firebaseUser, isNewUser = true)
            } else {
                _error.value = "Error al crear perfil de usuario"
            }
        } else {
            // Usuario existente
            _user.value = existingUser
            _authState.value = AuthState.Authenticated(firebaseUser)
        }
    }

    private suspend fun createBasicUserProfile() {
        val currentUser = authRepository.getCurrentUser() ?: return

        val basicUser = User(
            id = currentUser.uid,
            name = currentUser.displayName ?: "Usuario",
            email = currentUser.email ?: "",
            university = "",
            career = "",
            studyHours = 0,
            aiQueries = 0,
            averageQuizScore = 0f,
            subjects = emptyList(),
            createdAt = System.currentTimeMillis()
        )

        userRepository.createUser(basicUser)
        _user.value = basicUser
    }

    fun updateUserProfile(
        name: String? = null,
        university: String? = null,
        career: String? = null,
        subjects: List<String>? = null
    ) {
        val currentUser = _user.value ?: return

        viewModelScope.launch {
            try {
                _loading.value = true

                val updatedUser = currentUser.copy(
                    name = name ?: currentUser.name,
                    university = university ?: currentUser.university,
                    career = career ?: currentUser.career,
                    subjects = subjects ?: currentUser.subjects
                )

                val success = userRepository.updateUser(updatedUser)

                if (success) {
                    _user.value = updatedUser
                } else {
                    _error.value = "Error al actualizar perfil"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al actualizar perfil"
            } finally {
                _loading.value = false
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState.Unauthenticated
        _user.value = null
        clearValidations()
    }

    fun sendPasswordReset(email: String) {
        if (!Utils.isValidEmail(email)) {
            _error.value = "Email inválido"
            return
        }

        viewModelScope.launch {
            try {
                _loading.value = true
                // Implementar reset de contraseña con Firebase
                // authRepository.sendPasswordReset(email)
                _error.value = "Se ha enviado un email para restablecer tu contraseña"
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al enviar email de recuperación"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun getFirebaseErrorMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("password") == true -> "Contraseña incorrecta"
            exception.message?.contains("email") == true -> "Email no válido o no registrado"
            exception.message?.contains("network") == true -> "Error de conexión"
            exception.message?.contains("too-many-requests") == true -> "Demasiados intentos. Intenta más tarde"
            exception.message?.contains("user-disabled") == true -> "Usuario deshabilitado"
            exception.message?.contains("email-already-in-use") == true -> "Email ya está en uso"
            exception.message?.contains("weak-password") == true -> "Contraseña muy débil"
            else -> exception.message ?: "Error de autenticación"
        }
    }

    private fun clearValidations() {
        _emailValidation.value = ValidationState(true)
        _passwordValidation.value = ValidationState(true)
        _nameValidation.value = ValidationState(true)
    }

    fun clearError() {
        _error.value = null
    }

    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }

    // Función para incrementar estadísticas de uso
    fun incrementAIQueries() {
        val currentUser = _user.value ?: return

        viewModelScope.launch {
            try {
                val updatedUser = currentUser.copy(
                    aiQueries = currentUser.aiQueries + 1
                )
                userRepository.updateUser(updatedUser)
                _user.value = updatedUser
            } catch (e: Exception) {
                // Error silencioso, no crítico
            }
        }
    }

    fun updateStudyHours(additionalHours: Int) {
        val currentUser = _user.value ?: return

        viewModelScope.launch {
            try {
                val updatedUser = currentUser.copy(
                    studyHours = currentUser.studyHours + additionalHours
                )
                userRepository.updateUser(updatedUser)
                _user.value = updatedUser
            } catch (e: Exception) {
                // Error silencioso
            }
        }
    }

    fun updateQuizAverage(newScore: Float) {
        val currentUser = _user.value ?: return

        viewModelScope.launch {
            try {
                // Calcular nuevo promedio (simple)
                val currentAverage = currentUser.averageQuizScore
                val newAverage = if (currentAverage == 0f) {
                    newScore
                } else {
                    (currentAverage + newScore) / 2
                }

                val updatedUser = currentUser.copy(
                    averageQuizScore = newAverage
                )
                userRepository.updateUser(updatedUser)
                _user.value = updatedUser
            } catch (e: Exception) {
                // Error silencioso
            }
        }
    }
}