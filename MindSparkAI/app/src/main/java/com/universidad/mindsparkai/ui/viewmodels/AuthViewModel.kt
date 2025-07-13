package com.universidad.mindsparkai.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.universidad.mindsparkai.data.repository.AuthRepository
import com.universidad.mindsparkai.data.repository.UserRepository
import com.universidad.mindsparkai.data.models.User
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

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            _authState.value = AuthState.Authenticated(currentUser)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val result = authRepository.loginWithEmail(email, password)

                if (result.isSuccess) {
                    val firebaseUser = result.getOrNull()!!
                    _authState.value = AuthState.Authenticated(firebaseUser)
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error de autenticación"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun registerWithEmail(email: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val result = authRepository.registerWithEmail(email, password, name)

                if (result.isSuccess) {
                    val firebaseUser = result.getOrNull()!!
                    _authState.value = AuthState.Authenticated(firebaseUser)
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error de registro"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val result = authRepository.signInWithGoogle(idToken)

                if (result.isSuccess) {
                    val firebaseUser = result.getOrNull()!!
                    _authState.value = AuthState.Authenticated(firebaseUser)
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error con Google Sign-In"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun clearError() {
        _error.value = null
    }

    sealed class AuthState {
        object Unauthenticated : AuthState()
        data class Authenticated(val user: FirebaseUser) : AuthState()
    }
}