package com.universidad.mindsparkai.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.universidad.mindsparkai.data.models.User
// import dagger.hilt.android.lifecycle.HiltViewModel  // ← COMENTADO
import kotlinx.coroutines.launch
// import javax.inject.Inject  // ← COMENTADO

// @HiltViewModel  // ← COMENTADO TEMPORALMENTE
class DashboardViewModel(
    // Sin @Inject - crear dependencias manualmente
    // private val userRepository: UserRepository
) : ViewModel() {

    // Crear Firebase instances manualmente
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

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

                // Datos demo para testing
                val demoUser = User(
                    id = "demo_user",
                    name = "María González",
                    email = "maria@universidad.edu",
                    university = "Universidad Nacional",
                    career = "Ingeniería de Sistemas",
                    studyHours = 247,
                    aiQueries = 156,
                    averageQuizScore = 89f,
                    subjects = listOf("Matemáticas", "Química", "Física", "Historia", "Literatura")
                )

                _user.value = demoUser

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun refreshData() {
        loadUserData()
    }
}
