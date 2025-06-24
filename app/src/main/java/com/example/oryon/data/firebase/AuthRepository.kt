package com.example.oryon.data.firebase

import android.annotation.SuppressLint
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<String?>

    suspend fun login(email: String, password: String)

    suspend fun signUp(email: String, password: String): String

    suspend fun logout()

    fun hasUser(): Boolean
}