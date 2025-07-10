package com.example.moodymusic.data

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(private val auth: FirebaseAuth,
                     private val firestore: FirebaseFirestore,
                     private val context: Context // ✅ Pass context to save login state
){

    suspend fun signUp(email: String, password: String, firstName: String, lastName: String): Result<Boolean> =
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            //add user to firestore
            val user = User(firstName,lastName,email)
            saveUserToFirestore(user)
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }

    private suspend fun saveUserToFirestore(user: User) {
        firestore.collection("users").document(user.email).set(user).await()
    }

    suspend fun login(email: String, password: String): Result<Boolean> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()

            // ✅ Save login state
            val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("is_logged_in", true).apply()

            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }

    suspend fun getUserFirstName(): String {
        val email = auth.currentUser?.email ?: return ""
        val snapshot = firestore.collection("users").document(email).get().await()
        return snapshot.getString("firstName") ?: ""
    }


    fun logout() {

        auth.signOut()
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE).edit().clear().apply()
    }


}