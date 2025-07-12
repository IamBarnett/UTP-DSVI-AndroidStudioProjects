package com.universidad.mindsparkai.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.universidad.mindsparkai.data.models.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    suspend fun getCurrentUser(): User? {
        val currentUser = auth.currentUser ?: return null

        return try {
            val document = firestore.collection("users")
                .document(currentUser.uid)
                .get()
                .await()

            document.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUser(user: User): Boolean {
        return try {
            val currentUser = auth.currentUser ?: return false

            firestore.collection("users")
                .document(currentUser.uid)
                .set(user)
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun createUser(user: User): Boolean {
        return try {
            val currentUser = auth.currentUser ?: return false

            val newUser = user.copy(id = currentUser.uid)

            firestore.collection("users")
                .document(currentUser.uid)
                .set(newUser)
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }
}