package com.example.oryon.data

/*
Alle Daten und Klassen die für die Challenge benötigt werden
 */

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

//Hilfsfunc fürs parsen von Goal für Challengetyp
fun ChallengeGoal.getUnitLabel(progress: Float): String = when (this) {
    is ChallengeGoal.Distance -> "km"
    is ChallengeGoal.Duration -> if (progress == 1f) "Min" else "Min"
    is ChallengeGoal.RunCount -> if (progress == 1f) "Lauf" else "Läufe"
    is ChallengeGoal.Days -> if (progress == 1f) "Tag" else "Tage"
    else -> ""
}

fun ChallengeGoal.getUnitShort(): String = when (this) {
    is ChallengeGoal.Distance -> "km"
    is ChallengeGoal.Duration -> "Min"
    is ChallengeGoal.RunCount -> "x"
    is ChallengeGoal.Days -> "T"
    else -> ""
}

fun extractNumericTarget(goal: ChallengeGoal): Float = when (goal) {
    is ChallengeGoal.Distance -> goal.targetKm
    is ChallengeGoal.Duration -> goal.targetMinutes.toFloat()
    is ChallengeGoal.RunCount -> goal.targetRuns.toFloat()
    is ChallengeGoal.Days -> goal.targetDays.toFloat()
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


