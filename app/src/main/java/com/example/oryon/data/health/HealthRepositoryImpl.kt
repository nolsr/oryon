package com.example.oryon.data.health
/*
import android.content.Context
import androidx.compose.ui.geometry.isEmpty
import androidx.core.text.util.LocalePreferences.FirstDayOfWeek.Days
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.metadata.Device
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Length
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.collections.containsAll
import kotlin.collections.filter
import kotlin.collections.firstOrNull
import kotlin.collections.map
import java.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.seconds
import kotlin.time.toJavaDuration

class HealthRepositoryImpl(private val context: Context): HealthRepository  {

    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    override suspend fun insertRunSession(startTimeMillis: Long, endTimeMillis: Long, distanceMeters: Float) {
        val startTime = Instant.ofEpochMilli(startTimeMillis)
        val endTime = Instant.ofEpochMilli(endTimeMillis)

        val permissions = setOf(
            HealthPermission.getWritePermission(ExerciseSessionRecord::class),
            HealthPermission.getWritePermission(DistanceRecord::class)
        )

        if (healthConnectClient.permissionController.getGrantedPermissions()
                .containsAll(permissions)
        ) {

            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val formattedDate = startTime.atZone(ZoneOffset.UTC).toLocalDate().format(formatter)

            val session = ExerciseSessionRecord(
                metadata = Metadata.manualEntry(),
                startTime = startTime,
                startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(startTime),
                endTime = endTime,
                endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(endTime),
                exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
                title = "Lauf am $formattedDate",
            )

            val distance = DistanceRecord(
                startTime = startTime,
                startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(startTime),
                endTime = endTime,
                endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(endTime),
                distance = Length.meters(distanceMeters.toDouble()),
                metadata = Metadata.manualEntry()
            )

            healthConnectClient.insertRecords(listOf(session, distance))
        } else{
            println("HealthRepository: Keine Berechtigung zum Schreiben von Laufdaten.")
        }
    }


    override suspend fun getAllRunSessions(): List<RunSessionData> {
        val readPermissions = setOf(
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class)
        )
        if (!healthConnectClient.permissionController.getGrantedPermissions().containsAll(readPermissions)) {
            println("HealthRepository: Keine Berechtigung zum Lesen von Laufdaten.")
            return emptyList()
        }

        val endTime = Instant.now()
        val startTime = endTime.minus(Duration.ofDays(30))

        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)

        // 1. Alle Lauf-Sessions im Zeitraum abrufen
        val sessionsResponse = try {
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = ExerciseSessionRecord::class,
                    timeRangeFilter = timeRangeFilter,
                    ascendingOrder = false,
                    pageSize = 1000
                )
            )
        } catch (e: Exception) {
            println("HealthRepository: Fehler beim Lesen von ExerciseSessionRecords: ${e.message}")
            return emptyList()
        }
        val exerciseSessions = sessionsResponse.records.filter {
            it.exerciseType == ExerciseSessionRecord.EXERCISE_TYPE_RUNNING
        }

        // 2. Alle Distanz-Records im gleichen Zeitraum abrufen
        val distanceResponse = try {
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = DistanceRecord::class,
                    timeRangeFilter = timeRangeFilter,
                    pageSize = 1000
                )
            )
        } catch (e: Exception) {
            println("HealthRepository: Fehler beim Lesen von DistanceRecords: ${e.message}")
            return exerciseSessions.map { session ->
                createRunSessionData(session, null)
            }
        }

        val distanceRecords = distanceResponse.records

        // 3. Distanz-Records den Sessions zuordnen
        val results = mutableListOf<RunSessionData>()
        for (session in exerciseSessions) {
            val associatedDistance = distanceRecords.firstOrNull { dist ->
                dist.startTime == session.startTime &&
                        dist.endTime == session.endTime &&
                        dist.metadata.dataOrigin == session.metadata.dataOrigin
            }

            results.add(createRunSessionData(session, associatedDistance))
        }

        return results.sortedByDescending { it.startTime }
    }

    private fun createRunSessionData(
        session: ExerciseSessionRecord,
        distanceRecord: DistanceRecord?
    ): RunSessionData {
        val distanceMeters = distanceRecord?.distance?.inMeters ?: 0.0
        val elapsedSeconds = Duration.between(session.startTime, session.endTime).seconds // java.time.Duration

        return RunSessionData(
            startTime = session.startTime,
            endTime = session.endTime,
            distanceMeters = distanceMeters,
            elapsedSeconds = elapsedSeconds,
            title = session.title.toString()
        )
    }

}

 */