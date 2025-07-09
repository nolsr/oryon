package com.example.oryon.data.firebase

import android.location.Location
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
import com.google.firebase.firestore.GeoPoint
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
    private val challengeCollection = firestore.collection("challenges")

    //Fügt weitere dem User zugehörig Daten in Firestore hinzu
    override suspend fun createUser(user: UserData) {
        usersCollection.document(user.id).set(user).await()
    }

    //Sucht den User anhand seiner UID
    override suspend fun getUserById(id: String): UserData? {
        return try {
            val snapshot = usersCollection.document(id).get().await()
            snapshot.toObject(UserData::class.java)
        } catch (e: Exception) {
            null
        }
    }

    //Sucht den User anhand seiner Email
    override suspend fun findUserByEmail(email: String): UserData? {
        val snapshot = firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.toObject(UserData::class.java)
    }

    //Speichert einen Lauf in Firestore
    override suspend fun saveRunSession(distanceMeters: Float, durationSec: Long, pace: Float, routePoints: List<Location>) {
        //Wandelt Android.Location in Firebase.GeoPoints um
        val geoPoints = routePoints.map { location ->
            GeoPoint(location.latitude, location.longitude)
        }

        val session = RunSession(
            date = Timestamp.now(),
            distanceMeters = distanceMeters,
            durationSeconds = durationSec,
            pace = pace,
            route = geoPoints
        )

        userRunsCollection()?.add(session)
    }

    //Sucht alle Läufe eines Benutzers in Firestore
    override suspend fun getAllRunSessionsForUser(userId: String): Flow<List<RunSession>> = callbackFlow {
        val collectionRef = usersCollection
            .document(userId)
            .collection("runs")
            .orderBy("date", Query.Direction.DESCENDING)

        //Fügt einen EventListener hinzu um die Daten zu aktualisieren
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

    //Fügt Challenge in Firestore hinzu
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
        challengeCollection
            .add(challengeData)
            .await()
    }

    //Sucht alle Challenge eines Benutzers in Firestore und gibt sie als Flow zurück
    override suspend fun getUserChallenges(): Flow<List<ChallengeData>> = callbackFlow {
        val uid = authRepository.getUID()
        if (uid == null) {
            close(IllegalStateException("User not logged in"))
            return@callbackFlow
        }

        //Sucht alle Challenge die dem User gehören
        val query = challengeCollection.whereArrayContains("participantIds", uid)

        //Bekommt die Daten der Challenge
        //Fügt einen EventListener hinzu um die Daten zu aktualisieren
        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val documents = snapshot.documents

                //Verarbeitet die Daten und gibt sie als Flow zurück
                launch {
                    val challenges = documents.mapNotNull { doc ->
                        val id = doc.id
                        val name = doc.getString("name") ?: return@mapNotNull null
                        val type = doc.getString("type") ?: return@mapNotNull null
                        val data = doc.get("data") as? Map<String, Any> ?: emptyMap()
                        val goal = parseGoal(type, data) ?: return@mapNotNull null

                        val participantIds = doc.get("participantIds") as? List<String> ?: emptyList()
                        val userArray = doc.get("participants") as? List<Map<String, Any>> ?: emptyList()

                        val participants = coroutineScope {
                            participantIds.map { uid ->
                                async {
                                    //Sucht die UserDaten anhand der UID in Firestore
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

    //Fügt den User zu einer Challenge hinzu
    override suspend fun addUserToChallenge(challengeId: String, userId: String) {
        val challengeRef = challengeCollection.document(challengeId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(challengeRef)
            val currentIds = snapshot.get("participantIds") as? List<String> ?: emptyList()
            val updatedIds = currentIds + userId

            //Updated Challenge Daten mit neuem User
            val currentParticipants = snapshot.get("participants") as? List<Map<String, Any>> ?: emptyList()
            val updatedParticipants = currentParticipants + mapOf(
                "uid" to userId,
                "progress" to 0f
            )

            //Updatet Challenge mit neuem User in Firestore
            transaction.update(challengeRef, mapOf(
                "participantIds" to updatedIds.distinct(),
                "participants" to updatedParticipants
            ))
        }
    }

    //Ändert den Fortschritt einer Challenge
    //Wird von TrackRunUseCase aufgerufen
    override suspend fun updateChallengeProgressAfterRun(distanceMeters: Float, durationSec: Long) {
        val uid = authRepository.getUID() ?: return

        //Fetcht alle Challenge die dem User gehören
        val challengeDocs = challengeCollection
            .whereArrayContains("participantIds", uid)
            .get()
            .await()

        for (doc in challengeDocs.documents) {
            val type = doc.getString("type") ?: continue
            val data = doc.get("data") as? Map<String, Any> ?: continue
            val participants = doc.get("participants") as? List<Map<String, Any>> ?: continue

            //Verareitet Daten mit Helpferfunktion je nach ChallengeTyp
            val goal = parseGoal(type, data) ?: continue

            val updatedParticipants = participants.map { participant ->
                val participantId = participant["id"] as? String ?: return@map participant
                val existingProgress = (participant["progress"] as? Number)?.toFloat() ?: 0f

                //Berechnet den neuen Fortschritt je nach ChallengeTyp
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

                mapOf(
                    "id" to participantId,
                    "progress" to updatedProgress
                )
            }

            //Speichert die neuen Daten in Firestore
            challengeCollection.document(doc.id).update("participants", updatedParticipants)
        }
    }

    //Hilfsfunktion um Daten zu parsen
    private fun parseGoal(type: String, data: Map<String, Any>): ChallengeGoal? {
        return when (type) {
            "distance" -> ChallengeGoal.Distance((data["target"] as? Number)?.toFloat() ?: return null)
            "duration" -> ChallengeGoal.Duration((data["target"] as? Number)?.toInt() ?: return null)
            "runcount" -> ChallengeGoal.RunCount((data["target"] as? Number)?.toInt() ?: return null)
            "days" -> ChallengeGoal.Days((data["target"] as? Number)?.toInt() ?: return null)
            else -> null
        }
    }

    //Hilfsfunktion um die RunCollection von User zu erhalten, die mit ihm verschachtelt ist
    private fun userRunsCollection(): CollectionReference? {
        val userId = authRepository.getUID()
        return if (userId != null)
            usersCollection.document(userId).collection("runs")
        else null
    }

}