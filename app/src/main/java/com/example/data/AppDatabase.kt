package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Child::class,
        Intervention::class,
        MasterLibraryItem::class,
        TipsLibraryItem::class,
        MediaItem::class,
        EventLog::class,
        EventMetricsLog::class,
        ParentPreference::class,
        AuditLog::class,
        PatientReport::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "autism_tracker_database"
                )
                    .build()
                INSTANCE = instance
                
                scope.launch(Dispatchers.IO) {
                    try {
                        val dao = instance.appDao()
                        if (dao.getChildById(1) == null) {
                            populateDatabase(dao)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                instance
            }
        }

        private suspend fun populateDatabase(dao: AppDao) {
            // 1. Seed Children
            val leoId = dao.insertChild(
                Child(
                    name = "Leo",
                    age = 4,
                    developmentalStage = "Preschooler",
                    communicationProfile = "Emerging Verbal",
                    sensoryProfile = "Sensory Seeking",
                    focusAreas = "Sleep,Transitions,Sensory"
                )
            ).toInt()

            val mayaId = dao.insertChild(
                Child(
                    name = "Maya",
                    age = 7,
                    developmentalStage = "School Age",
                    communicationProfile = "AAC User",
                    sensoryProfile = "Sensory Avoiding",
                    focusAreas = "Speech,Sensory,Transitions"
                )
            ).toInt()

            // 2. Seed Master Library
            dao.insertMasterLibraryItem(
                MasterLibraryItem(
                    name = "SLP - Speech Language Therapy",
                    category = "Speech Support",
                    description = "Professional sessions targeting speech sound production, receptive understanding, or introduction/refinement of AAC aids.",
                    referenceOrTips = "Recommend implementing consistent visual cards/board to ease expressive language barriers."
                )
            )
            dao.insertMasterLibraryItem(
                MasterLibraryItem(
                    name = "Weighted Blanket Proprioception",
                    category = "Sensory Support",
                    description = "A standard 5lb-7lb medical weighted lap blanket used during seating routines to provide calming proprioceptive feedback.",
                    referenceOrTips = "Never leave on unattended. Target use is 15-20 min in high stim periods."
                )
            )
            dao.insertMasterLibraryItem(
                MasterLibraryItem(
                    name = "Visual Transition Schedule",
                    category = "Routine Support",
                    description = "Creating a structured visual timetable (First-Then cards) on Velcro boards to manage anxiety during changes.",
                    referenceOrTips = "Move a token from 'First' to 'Then' in direct physical cooperation with the child."
                )
            )
            dao.insertMasterLibraryItem(
                MasterLibraryItem(
                    name = "OT - Occupational Gym",
                    category = "Sensory Support",
                    description = "Directed swing, slide, and balance programs within sensory gyms to satisfy and integrate vestibulary seeking habits.",
                    referenceOrTips = "Monitor arousal levels; supplement with deep compression if child gets over-excited."
                )
            )
            dao.insertMasterLibraryItem(
                MasterLibraryItem(
                    name = "Probiotics & High-Fiber Diet",
                    category = "Diet Support",
                    description = "Gentle, parent-logged gastrointestinal tracking and gradual addition of gut biome aids to promote digestive ease.",
                    referenceOrTips = "GI discomfort correlates heavily with increased sensory meltdowns."
                )
            )

            // 3. Seed Tips Library
            dao.insertTipItem(
                TipsLibraryItem(
                    title = "Transition Visual Timers",
                    content = "To facilitate difficult transitions (e.g. tablet screen-time to bath), construct a visual timer using analog circles. Give clear prompts: 'In 3 minutes, timer turns red, iPad sleeping.'",
                    domain = "OT",
                    eligibleAges = "all",
                    eligibleComm = "all",
                    eligibleFocus = "Transitions"
                )
            )
            dao.insertTipItem(
                TipsLibraryItem(
                    title = "Model Action-Verb Pairs",
                    content = "For children showing emerging verbal cues, pair high-contrast action verbs with physical feedback. Say 'Open' while twisting open sensory jars or say 'Up' as they transition screens count.",
                    domain = "SLP",
                    eligibleAges = "2-5",
                    eligibleComm = "Emerging Verbal",
                    eligibleFocus = "Speech"
                )
            )
            dao.insertTipItem(
                TipsLibraryItem(
                    title = "Blue-Light Sleep Shielding",
                    content = "Gently suppress cortical arousal by removing all smart screens 90 minutes prior to bed. Replace with soft ambient yellow nightlights and 15 minutes of calm, slow deep-squeeze massage.",
                    domain = "Sleep",
                    eligibleAges = "all",
                    eligibleFocus = "Sleep",
                    eligibleComm = "all"
                )
            )
            dao.insertTipItem(
                TipsLibraryItem(
                    title = "Low-Arousal Calm Pod",
                    content = "When sensory pressure reaches overload, utilize a localized pop-up tent filled with soft cushions and weighted pillows. Keep illumination inside to near-zero. Give the child total agency to enter.",
                    domain = "Sensory",
                    eligibleAges = "all",
                    eligibleFocus = "Sensory",
                    eligibleComm = "all"
                )
            )
            dao.insertTipItem(
                TipsLibraryItem(
                    title = "Expressive Visual Comm Boards",
                    content = "If the child struggles when requesting items, prepare clear laminated core boards printed with primary requests (Water, More, Bathroom, Go, Book). Keep it within arm's reach.",
                    domain = "SLP",
                    eligibleAges = "all",
                    eligibleFocus = "Speech",
                    eligibleComm = "Non-Verbal"
                )
            )

            // 4. Seed Media Items linked to preloaded Tips (using tip ID 1, 2, 3, 4, 5)
            dao.insertMediaItem(
                MediaItem(
                    tipId = 1,
                    title = "Transition Flow Demo",
                    type = "StepCard",
                    contentDescription = "Diagram highlighting 3 steps: First Timer, Second Action, Third Reward",
                    assetPlaceholder = "ic_step_timer"
                )
            )
            dao.insertMediaItem(
                MediaItem(
                    tipId = 4,
                    title = "Sensory Corner Setup Diagram",
                    type = "Diagram",
                    contentDescription = "Visual diagram showing standard placement of sensory pods, soft floor padding, and noise cancelling earmuffs.",
                    assetPlaceholder = "ic_setup_sensory"
                )
            )

            // 5. Seed Interventions for Leo
            dao.insertIntervention(
                Intervention(
                    childId = leoId,
                    name = "Visual Transition Schedule",
                    category = "Routine Support",
                    description = "Velcro timetable loaded with morning routines.",
                    source = "Curated Master",
                    status = "Active"
                )
            )
            dao.insertIntervention(
                Intervention(
                    childId = leoId,
                    name = "Weighted Blanket Proprioception",
                    category = "Sensory Support",
                    description = "5lb blanket used in reading corner.",
                    source = "Curated Master",
                    status = "Active"
                )
            )

            // Seed Interventions for Maya
            dao.insertIntervention(
                Intervention(
                    childId = mayaId,
                    name = "SLP - Speech Language Therapy",
                    category = "Speech Support",
                    description = "Twice weekly therapist sessions + Proloquo2Go AAC practice.",
                    source = "Curated Master",
                    status = "Active"
                )
            )

            // 6. Seed Event Logs (Qualitative Context Logs) - Leo
            val nowMs = System.currentTimeMillis()
            val dayMs = 24 * 60 * 60 * 1000L

            dao.insertEventLog(
                EventLog(
                    childId = leoId,
                    timestamp = nowMs - (1 * dayMs),
                    noteText = "Very visual today. Handled transitions with the timer excellently. He independently crawled to the quiet corner when vacuuming occurred.",
                    category = "Transition Success",
                    authorName = "Jane Doe",
                    authorRole = "Parent"
                )
            )
            dao.insertEventLog(
                EventLog(
                    childId = leoId,
                    timestamp = nowMs - (3 * dayMs),
                    noteText = "Moderate meltdown during bedtime transition. Refused to set aside tablet. Took about 40 minutes of quiet room time and deep compression to soothe.",
                    category = "Meltdown",
                    authorName = "Mark",
                    authorRole = "Caregiver"
                )
            )
            dao.insertEventLog(
                EventLog(
                    childId = leoId,
                    timestamp = nowMs - (5 * dayMs),
                    noteText = "Leo struggled to sit during speech exercise. Preferred stacking blocks instead. He managed to request 'Help' using PECS card.",
                    category = "Speech Support",
                    authorName = "Sarah SLP",
                    authorRole = "Therapist"
                )
            )

            // Seed Event Logs (Maya)
            dao.insertEventLog(
                EventLog(
                    childId = mayaId,
                    timestamp = nowMs - (2 * dayMs),
                    noteText = "Maya displayed phenomenal AAC interaction. Indicated a sore tummy independently which explains some minor behavioral distress yesterday.",
                    category = "Speech Support",
                    authorName = "Jane Doe",
                    authorRole = "Parent"
                )
            )

            // 7. Seed Event Metrics Logs (Quantitative) - Leo (Past 7 Days of data)
            val metricNames = listOf("Sleep Quality", "Mood Regulation", "Transition Ease", "Sensory Sensitivities", "Engagement")
            // Leo values (past 7 days)
            val leoData = mapOf(
                "Sleep Quality" to listOf(4f, 3f, 5f, 2f, 4f, 5f, 4f),
                "Mood Regulation" to listOf(3f, 4f, 2f, 3f, 5f, 4f, 4f),
                "Transition Ease" to listOf(4f, 3f, 2f, 4f, 5f, 3f, 4f),
                "Sensory Sensitivities" to listOf(3f, 2f, 5f, 4f, 2f, 3f, 2f),
                "Engagement" to listOf(4f, 3f, 3f, 4f, 5f, 4f, 5f)
            )
            for ((metric, values) in leoData) {
                values.forEachIndexed { idx, value ->
                    dao.insertMetricsLog(
                        EventMetricsLog(
                            childId = leoId,
                            metricName = metric,
                            value = value,
                            timestamp = nowMs - ((6 - idx) * dayMs),
                            authorName = "Jane Doe",
                            authorRole = "Parent"
                        )
                    )
                }
            }

            // Maya values (past 7 days)
            val mayaData = mapOf(
                "Sleep Quality" to listOf(5f, 5f, 4f, 4f, 5f, 4f, 5f),
                "Mood Regulation" to listOf(4f, 3f, 4f, 5f, 3f, 5f, 4f),
                "Transition Ease" to listOf(5f, 4f, 5f, 4f, 4f, 5f, 4f),
                "Sensory Sensitivities" to listOf(2f, 3f, 4f, 2f, 3f, 1f, 2f),
                "Engagement" to listOf(4f, 5f, 5f, 4f, 5f, 5f, 5f)
            )
            for ((metric, values) in mayaData) {
                values.forEachIndexed { idx, value ->
                    dao.insertMetricsLog(
                        EventMetricsLog(
                            childId = mayaId,
                            metricName = metric,
                            value = value,
                            timestamp = nowMs - ((6 - idx) * dayMs),
                            authorName = "Jane Doe",
                            authorRole = "Parent"
                        )
                    )
                }
            }

            // 8. Seed Default Parent Preferences
            dao.insertPreferences(
                ParentPreference(
                    id = 1,
                    childId = leoId,
                    trackedMetrics = "Sleep Quality,Mood Regulation,Transition Ease,Sensory Sensitivities,Engagement",
                    notificationFrequency = "Daily",
                    shareCode = "LEO-POV-992"
                )
            )
            dao.insertPreferences(
                ParentPreference(
                    id = 2,
                    childId = mayaId,
                    trackedMetrics = "Sleep Quality,Mood Regulation,Transition Ease,Sensory Sensitivities,Engagement",
                    notificationFrequency = "Weekly",
                    shareCode = "MAY-CLI-402"
                )
            )

            // 9. Seed Audit Logs
            dao.insertAuditLog(
                AuditLog(
                    timestamp = nowMs - (4 * dayMs),
                    userName = "Jane Doe",
                    userRole = "Parent",
                    actionType = "LOG_DATA",
                    details = "Logged daily metrics (Sleep, Mood, Sensory) for Leo"
                )
            )
            dao.insertAuditLog(
                AuditLog(
                    timestamp = nowMs - (3 * dayMs),
                    userName = "Sarah SLP",
                    userRole = "Therapist",
                    actionType = "VIEW_DATA",
                    details = "Observed historical transition tracking for Leo ahead of SLP appointment"
                )
            )
            dao.insertAuditLog(
                AuditLog(
                    timestamp = nowMs - (1 * dayMs),
                    userName = "Dr. Robert Carter",
                    userRole = "Healthcare Professional",
                    actionType = "EXPORT_SUMMARY",
                    details = "Clinician generated and exported Leo's 7-day appointment summary report"
                )
            )

            // 10. Seed Patient Report
            dao.insertReport(
                PatientReport(
                    childId = leoId,
                    title = "Children's Developmental Center OT Review",
                    fileName = "cdc_ot_eval_leo.pdf",
                    fileContent = "Patient: Leo, Age: 4y2m. Evaluator: Dr. Robert Carter, OTR/L.\nFindings: Patient exhibits substantial vestibular and sensory-seeking behaviors, manifesting in intensive motor stimming (spinning, jumping) during auditory transitions. Hyper-sensitive to vacuum cleaners and blender sounds. Recommends structural Deep Proprioceptive Squeezes twice daily, timed transitions using 3-minute visual prompts, and localized low-arousal quiet corner buffers.",
                    uploadTimestamp = nowMs - (1 * dayMs),
                    aiFindingsSummary = "Clinical OT evaluation outlines extreme auditory hyper-reactivity (vacuum/blenders) and heavy vestibular seeking. Core recommended interventions: (1) 3-minute visual transition grids, (2) Deep pressure compression holds, (3) Free access to a specified quiet sensory rest box. Daily data indicates high sensory sensitivity logs align with vacuum cleaner noises noted in event logs.",
                    status = "Analyzed"
                )
            )
        }
    }
}
