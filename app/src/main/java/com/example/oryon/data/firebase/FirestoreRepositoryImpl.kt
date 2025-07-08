package com.example.oryon.data.firebase

import com.example.oryon.data.ChallengeData
import com.example.oryon.data.ChallengeGoal
import com.example.oryon.data.ChallengeParticipant
import com.example.oryon.data.RunSession
import com.example.oryon.data.UserData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.Instant
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
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

    override suspend fun findUserByEmail(email: String): UserData? {
        val snapshot = firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.toObject(UserData::class.java)
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
                val runs = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(RunSession::class.java)?.copy(id = doc.id)
                }
                trySend(runs)
            } else {
                trySend(emptyList())
            }
        }

        awaitClose { registration.remove() }
    }

    override suspend fun addChallenge(name: String, type: String, target: Float, creatorUid: String) {
        val challengeData = mapOf(
            "name" to name,
            "type" to type,
            "data" to mapOf("target" to target),
            "participantIds" to listOf(creatorUid),
            "participants" to listOf(
                mapOf("id" to creatorUid, "progress" to 0f)
            )
        )
        firestore.collection("challenges")
            .add(challengeData)
            .await()
    }

    override suspend fun getUserChallenges(): Flow<List<ChallengeData>> = callbackFlow {
        val uid = authRepository.getUID()
        if (uid == null) {
            close(IllegalStateException("User not logged in"))
            return@callbackFlow
        }

        val query = firestore.collection("challenges")
            .whereArrayContains("participantIds", uid)

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val documents = snapshot.documents

                launch {
                    val challenges = documents.mapNotNull { doc ->
                        val id = doc.id
                        val name = doc.getString("name") ?: return@mapNotNull null
                        val type = doc.getString("type") ?: return@mapNotNull null
                        val data = doc.get("data") as? Map<String, Any> ?: emptyMap()
                        val goal = parseGoal(type, data) ?: return@mapNotNull null

                        val participantIds = doc.get("participantIds") as? List<String> ?: emptyList()
                        val userArray = doc.get("user") as? List<Map<String, Any>> ?: emptyList()

                        val participants = coroutineScope {
                            participantIds.map { uid ->
                                async {
                                    val userData = userArray.find { it["id"] == uid }
                                    val progress = (userData?.get("progress") as? Number)?.toFloat() ?: 0f

                                    val name = try {
                                        usersCollection.document(uid).get().await().getString("name")
                                    } catch (e: Exception) {
                                        null
                                    }

                                    ChallengeParticipant(
                                        uid = uid,
                                        name = name,
                                        progress = progress
                                    )
                                }
                            }.awaitAll()
                        }

                        println("Participants: $participants")

                        ChallengeData(
                            id = id,
                            name = name,
                            type = type,
                            data = data,
                            goal = goal,
                            participants = participants
                        )
                    }

                    trySend(challenges).isSuccess
                }
            }
        }

        awaitClose { registration.remove() }
    }

    override suspend fun addUserToChallenge(challengeId: String, userId: String) {
        val challengeRef = firestore.collection("challenges").document(challengeId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(challengeRef)
            val currentIds = snapshot.get("participantIds") as? List<String> ?: emptyList()
            val updatedIds = currentIds + userId

            val currentParticipants = snapshot.get("participants") as? List<Map<String, Any>> ?: emptyList()
            val updatedParticipants = currentParticipants + mapOf(
                "uid" to userId,
                "progress" to 0f
            )

            transaction.update(challengeRef, mapOf(
                "participantIds" to updatedIds.distinct(),
                "participants" to updatedParticipants
            ))
        }
    }

    private fun parseGoal(type: String, data: Map<String, Any>): ChallengeGoal? {
        return when (type) {
            "distance" -> ChallengeGoal.Distance((data["target"] as? Number)?.toFloat() ?: return null)
            "duration" -> ChallengeGoal.Duration((data["target"] as? Number)?.toInt() ?: return null)
            "runcount" -> ChallengeGoal.RunCount((data["target"] as? Number)?.toInt() ?: return null)
            "days" -> ChallengeGoal.Days((data["target"] as? Number)?.toInt() ?: return null)
            else -> null
        }
    }

    override suspend fun updateChallengeProgressAfterRun(distanceMeters: Float, durationSec: Long) {
        println("updateChallengeProgressAfterRun called")
        val uid = authRepository.getUID() ?: return

        val challengeDocs = firestore.collection("challenges")
            .whereArrayContains("participantIds", uid)
            .get()
            .await()

        println("Challenge documents: ${challengeDocs.documents}")

        for (doc in challengeDocs.documents) {
            val type = doc.getString("type") ?: continue
            val data = doc.get("data") as? Map<String, Any> ?: continue
            val participants = doc.get("user") as? List<Map<String, Any>> ?: continue

            val goal = parseGoal(type, data) ?: continue

            val updatedParticipants = participants.map { participant ->
                val participantId = participant["id"] as? String ?: return@map participant
                val existingProgress = (participant["progress"] as? Number)?.toFloat() ?: 0f

                val updatedProgress = if (participantId == uid) {
                    existingProgress + when (goal) {
                        is ChallengeGoal.Distance -> (distanceMeters / 1000)
                        is ChallengeGoal.Duration -> (durationSec / 60).toFloat()
                        is ChallengeGoal.RunCount -> 1f
                        is ChallengeGoal.Days -> {
                            // Optional: Prüfen, ob der letzte Lauf an einem anderen Tag war
                            1f
                        }
                    }
                } else {
                    existingProgress
                }

                println("Participant ID: $participantId, Existing Progress: $existingProgress, Updated Progress: $updatedProgress")

                mapOf(
                    "id" to participantId,
                    "progress" to updatedProgress
                )
            }

            firestore.collection("challenges")
                .document(doc.id)
                .update("user", updatedParticipants)

            println("Updated participants: $updatedParticipants")

        }
    }

}