package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // === Child Profiles ===
    @Query("SELECT * FROM children")
    fun getAllChildren(): Flow<List<Child>>

    @Query("SELECT * FROM children WHERE id = :id")
    suspend fun getChildById(id: Int): Child?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChild(child: Child): Long

    @Update
    suspend fun updateChild(child: Child)

    @Delete
    suspend fun deleteChild(child: Child)


    // === Interventions ===
    @Query("SELECT * FROM interventions WHERE childId = :childId ORDER BY addedAt DESC")
    fun getInterventionsForChild(childId: Int): Flow<List<Intervention>>

    @Query("SELECT * FROM interventions WHERE isReviewed = 0 ORDER BY addedAt DESC")
    fun getPendingInterventions(): Flow<List<Intervention>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntervention(intervention: Intervention): Long

    @Update
    suspend fun updateIntervention(intervention: Intervention)

    @Delete
    suspend fun deleteIntervention(intervention: Intervention)


    // === Master Library ===
    @Query("SELECT * FROM master_library")
    fun getMasterLibrary(): Flow<List<MasterLibraryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMasterLibraryItem(item: MasterLibraryItem)


    // === Tips Library ===
    @Query("SELECT * FROM tips_library")
    fun getTipsLibrary(): Flow<List<TipsLibraryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTipItem(item: TipsLibraryItem)


    // === Media Library ===
    @Query("SELECT * FROM media_library WHERE tipId = :tipId")
    fun getMediaForTip(tipId: Int): Flow<List<MediaItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(item: MediaItem)


    // === Qualitative Event Logs ===
    @Query("SELECT * FROM event_logs WHERE childId = :childId ORDER BY timestamp DESC")
    fun getEventLogsForChild(childId: Int): Flow<List<EventLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventLog(log: EventLog)


    // === Quantitative Event Metrics Logs ===
    @Query("SELECT * FROM event_metrics_logs WHERE childId = :childId ORDER BY timestamp DESC")
    fun getMetricsLogsForChild(childId: Int): Flow<List<EventMetricsLog>>

    @Query("SELECT * FROM event_metrics_logs WHERE childId = :childId AND metricName = :metricName ORDER BY timestamp ASC")
    fun getTrendLogsForMetric(childId: Int, metricName: String): Flow<List<EventMetricsLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetricsLog(log: EventMetricsLog)


    // === Parent Preferences ===
    @Query("SELECT * FROM parent_preferences WHERE childId = :childId LIMIT 1")
    fun getPreferencesForChild(childId: Int): Flow<ParentPreference?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(pref: ParentPreference)


    // === Audit Logs ===
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAuditLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog)


    // === Patient Reports ===
    @Query("SELECT * FROM patient_reports WHERE childId = :childId ORDER BY uploadTimestamp DESC")
    fun getReportsForChild(childId: Int): Flow<List<PatientReport>>

    @Query("SELECT * FROM patient_reports ORDER BY uploadTimestamp DESC")
    fun getAllReports(): Flow<List<PatientReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: PatientReport): Long
}
