package com.example.oryon.data

data class ChallengeData(
    val id: String,
    val name: String,
    val type: String,
    val data: Map<String, Any>,
    val participants: List<ChallengeParticipant>
)

data class ChallengeParticipant(
    val uid: String,
    val name: String? = null,
    //val progress: Float,
)

