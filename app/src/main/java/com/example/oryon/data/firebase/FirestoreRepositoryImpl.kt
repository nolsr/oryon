package com.example.oryon.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class FirestoreRepositoryImpl : FirestoreRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

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


}