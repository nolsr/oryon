package com.example.oryon.data.firebase

import com.example.oryon.data.RunSession
import com.example.oryon.data.UserData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.Instant
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Date


class FirestoreRepositoryImpl(private val authRepository: AuthRepository) : FirestoreRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    private fun userRunsCollection(): CollectionReference? {
        val userId = authRepository.getUID()
        return if (userId != null)
            usersCollection.document(userId).collection("runs")
        else null
    }


    override suspend fun createUser(user: UserData) {
        usersCollection.document(user.id).set(user).await()

    }

    override suspend fun getUserById(id: String): UserData? {
        return try {
            val snapshot = usersCollection.document(id).get().await()
            snapshot.toObject(UserData::class.java)
        } catch (e: Exception) {
            null
        }
    }


    override suspend fun saveRunSession(distanceMeters: Float, durationSec: Long, pace: Float) {

        val session = RunSession(
            date = Timestamp.now(),
            distanceMeters = distanceMeters,
            durationSeconds = durationSec,
            pace = pace
        )

        userRunsCollection()?.add(session)
    }

    override suspend fun getAllRunSessionsForUser(userId: String): Flow<List<RunSession>> = callbackFlow {
        val collectionRef = firestore.collection("users")
            .document(userId)
            .collection("runs")
            .orderBy("date", Query.Direction.DESCENDING)

        val registration: ListenerRegistration = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val runs = snapshot.toObjects(RunSession::class.java)
                trySend(runs)
            } else {
                trySend(emptyList())
            }
        }

        awaitClose { registration.remove() }
    }


}