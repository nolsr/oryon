package com.example.oryon.data.firebase


import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await


class AuthRepositoryImpl() : AuthRepository {

    override val currentUser: Flow<String?>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    trySend(auth.currentUser?.uid)
                }
            Firebase.auth.addAuthStateListener(listener)
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }

    override suspend fun login(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()

    }

    override fun getUID(): String? {
        return Firebase.auth.currentUser?.uid
    }

    override suspend fun signUp(email: String, password: String): String {
        Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        return Firebase.auth.currentUser?.uid
            ?: throw IllegalStateException("Benutzer konnte nicht erstellt werden")
    }

    override suspend fun logout() {
        Firebase.auth.signOut()
    }

    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

}