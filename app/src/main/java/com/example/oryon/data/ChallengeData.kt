package com.example.oryon.data

data class ChallengeData(
    val id: String,
    val name: String,
    val type: String,
    val data: Map<String, Any>,
    val goal: ChallengeGoal,
    val participants: List<ChallengeParticipant>
)

data class ChallengeParticipant(
    val uid: String,
    val name: String? = null,
    val progress: Float,
)

data class ParticipantRanking(
    val name: String,
    val progress: Float
)

sealed class ChallengeGoal {
    data class Distance(val targetKm: Float) : ChallengeGoal()
    data class Duration(val targetMinutes: Int) : ChallengeGoal()
    data class RunCount(val targetRuns: Int) : ChallengeGoal()
    data class Days(val targetDays: Int) : ChallengeGoal()
}


fun getChallengeTypeText(type: String): String {
    return when (type) {
        "distance" -> "Distanz-Challenge"
        "duration" -> "Dauer-Challenge"
        "runcount" -> "Laufanzahl-Challenge"
        "days" -> "Tage-Challenge"
        else -> "Unbekannt"
    }
}


