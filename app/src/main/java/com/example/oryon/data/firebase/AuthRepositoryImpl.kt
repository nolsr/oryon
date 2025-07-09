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

    // gibt den aktuellen Benutzer als Flow zurück
    // Benzuzt die AuthStateListener um den aktuellen Benutzer zu erhalten auch wenn der Benutzer sich neu angemeldet hat
    override val currentUser: Flow<String?>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    trySend(auth.currentUser?.uid)
                }
            Firebase.auth.addAuthStateListener(listener)
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }

    //Firebase LogIn Func
    override suspend fun login(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()
    }

    // Gitb die UID des aktuellen Benutzers zurück
    override fun getUID(): String? {
        return Firebase.auth.currentUser?.uid
    }

    //Firebase SigngUp Func
    override suspend fun signUp(email: String, password: String): String {
        Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        return Firebase.auth.currentUser?.uid
            ?: throw IllegalStateException("Benutzer konnte nicht erstellt werden")
    }

    //Firebase LogOut Func
    override suspend fun logout() {
        Firebase.auth.signOut()
    }

    //Check ob ein User eingeloggt ist
    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

}