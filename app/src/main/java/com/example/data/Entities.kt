package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class Child(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val age: Int,
    val developmentalStage: String, // Toddler, Preschool, School Age, Adolescent
    val communicationProfile: String, // Verbal, Emerging, Non-Verbal, AAC User
    val sensoryProfile: String, // Sensory Seeking, Sensory Avoiding, Mixed Profiling, Balanced
    val focusAreas: String // Comma separated list of focus areas: Sleep, Speech, Transitions, Attention, Eating, Sensory
)

@Entity(tableName = "interventions")
data class Intervention(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val childId: Int,
    val name: String,
    val category: String, // ABA, OT, Speech, Routine, Diet, Supplement, Sensory Support
    val description: String,
    val source: String, // Curated Master, Parent-Added, Tip-Assigned
    val status: String, // Active, Inactive, Try-This (for converting tips)
    val isReviewed: Boolean = false, // true for approved Master, false for user pending library
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "master_library")
data class MasterLibraryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val description: String,
    val referenceOrTips: String
)

@Entity(tableName = "tips_library")
data class TipsLibraryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val domain: String, // OT, SLP, ABA, Sensory, Sleep, Nutrition
    val eligibleAges: String, // e.g. "all", or "2-5", "6-12"
    val eligibleComm: String, // e.g. "all", or "Non-Verbal", "AAC User"
    val eligibleFocus: String // e.g. "Sensory", "Transitions", "Sleep", "Speech"
)

@Entity(tableName = "media_library")
data class MediaItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipId: Int,
    val title: String,
    val type: String, // Video, Diagram, StepCard
    val contentDescription: String,
    val assetPlaceholder: String // Name of graphic or symbol
)

@Entity(tableName = "event_logs")
data class EventLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val childId: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val noteText: String, // Qualitative Notes
    val category: String, // Meltdown, Routine Deviation, Sleep Accent, Transition Success, Feeding
    val authorName: String,
    val authorRole: String // Parent, Caregiver, Therapist, Medical Professional
)

@Entity(tableName = "event_metrics_logs")
data class EventMetricsLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val childId: Int,
    val metricName: String, // Sleep, Mood, Attention, Sensory Sensitivity, Stimming
    val value: Float, // Quantitative slider (e.g. 1.0 to 5.0)
    val timestamp: Long = System.currentTimeMillis(),
    val authorName: String,
    val authorRole: String // Parent, Caregiver, Therapist, Medical Professional
)

@Entity(tableName = "parent_preferences")
data class ParentPreference(
    @PrimaryKey val id: Int = 1, // Global or per patient
    val childId: Int,
    val trackedMetrics: String, // Comma-separated: Sleep,Mood,Attention,Sensory,Stimming
    val notificationFrequency: String, // Daily, Weekly, None
    val shareCode: String = "SHARE-123-ABC"
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val userName: String,
    val userRole: String,
    val actionType: String, // VIEW_DATA, LOG_DATA, UPLOAD_REPORT, EXPORT_SUMMARY
    val details: String
)

@Entity(tableName = "patient_reports")
data class PatientReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val childId: Int,
    val title: String,
    val fileName: String,
    val fileContent: String, // File summary or uploaded text content
    val uploadTimestamp: Long = System.currentTimeMillis(),
    val aiFindingsSummary: String, // Extracted medical insights integrated with daily data
    val status: String // Pending, Analyzed, Failed
)
