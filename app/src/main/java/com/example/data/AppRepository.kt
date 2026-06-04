package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val dao: AppDao) {

    // === Flows of List (Reactive data) ===
    val allChildren: Flow<List<Child>> = dao.getAllChildren()
    val allAuditLogs: Flow<List<AuditLog>> = dao.getAuditLogs()
    val allReports: Flow<List<PatientReport>> = dao.getAllReports()
    val masterLibrary: Flow<List<MasterLibraryItem>> = dao.getMasterLibrary()
    val tipsLibrary: Flow<List<TipsLibraryItem>> = dao.getTipsLibrary()
    val pendingInterventions: Flow<List<Intervention>> = dao.getPendingInterventions()

    fun getInterventionsForChild(childId: Int): Flow<List<Intervention>> =
        dao.getInterventionsForChild(childId)

    fun getEventLogsForChild(childId: Int): Flow<List<EventLog>> =
        dao.getEventLogsForChild(childId)

    fun getMetricsLogsForChild(childId: Int): Flow<List<EventMetricsLog>> =
        dao.getMetricsLogsForChild(childId)

    fun getTrendLogsForMetric(childId: Int, metricName: String): Flow<List<EventMetricsLog>> =
        dao.getTrendLogsForMetric(childId, metricName)

    fun getPreferencesForChild(childId: Int): Flow<ParentPreference?> =
        dao.getPreferencesForChild(childId)

    fun getReportsForChild(childId: Int): Flow<List<PatientReport>> =
        dao.getReportsForChild(childId)

    // === Mutating Functions (suspend) ===
    suspend fun insertChild(child: Child): Long = withContext(Dispatchers.IO) {
        dao.insertChild(child)
    }

    suspend fun updateChild(child: Child) = withContext(Dispatchers.IO) {
        dao.updateChild(child)
    }

    suspend fun deleteChild(child: Child) = withContext(Dispatchers.IO) {
        dao.deleteChild(child)
    }

    suspend fun insertIntervention(intervention: Intervention) = withContext(Dispatchers.IO) {
        dao.insertIntervention(intervention)
    }

    suspend fun updateIntervention(intervention: Intervention) = withContext(Dispatchers.IO) {
        dao.updateIntervention(intervention)
    }

    suspend fun deleteIntervention(intervention: Intervention) = withContext(Dispatchers.IO) {
        dao.deleteIntervention(intervention)
    }

    suspend fun insertEventLog(log: EventLog) = withContext(Dispatchers.IO) {
        dao.insertEventLog(log)
        // Auto-audit this logging event
        insertAuditInternal(
            userName = log.authorName,
            userRole = log.authorRole,
            actionType = "LOG_DATA",
            details = "Logged qualitative event (${log.category}) for Child ID: ${log.childId}"
        )
    }

    suspend fun insertMetricsLog(log: EventMetricsLog) = withContext(Dispatchers.IO) {
        dao.insertMetricsLog(log)
    }

    suspend fun insertPreferences(pref: ParentPreference) = withContext(Dispatchers.IO) {
        dao.insertPreferences(pref)
    }

    suspend fun insertAudit(userName: String, userRole: String, actionType: String, details: String) =
        withContext(Dispatchers.IO) {
            insertAuditInternal(userName, userRole, actionType, details)
        }

    private suspend fun insertAuditInternal(userName: String, userRole: String, actionType: String, details: String) {
        dao.insertAuditLog(
            AuditLog(
                userName = userName,
                userRole = userRole,
                actionType = actionType,
                details = details
            )
        )
    }

    // === Patient Report Upload & AI Insights ===
    suspend fun uploadAndAnalyzeReport(
        childId: Int,
        title: String,
        fileName: String,
        fileContent: String,
        childName: String,
        focusAreas: String,
        recentLogs: List<String>,
        uploadedBy: String,
        uploaderRole: String
    ): Long = withContext(Dispatchers.IO) {
        val rawReportId = dao.insertReport(
            PatientReport(
                childId = childId,
                title = title,
                fileName = fileName,
                fileContent = fileContent,
                aiFindingsSummary = "Clinical AI analysis is initiating...",
                status = "Pending"
            )
        )

        // Perform the AI analysis in background IO thread
        val aiSummaryOutput = GeminiService.analyzeClinicalReport(
            reportTitle = title,
            reportContent = fileContent,
            childName = childName,
            focusAreas = focusAreas,
            recentLogs = recentLogs
        )

        dao.insertReport(
            PatientReport(
                id = rawReportId.toInt(),
                childId = childId,
                title = title,
                fileName = fileName,
                fileContent = fileContent,
                aiFindingsSummary = aiSummaryOutput,
                status = "Analyzed"
            )
        )

        insertAuditInternal(
            userName = uploadedBy,
            userRole = uploaderRole,
            actionType = "UPLOAD_REPORT",
            details = "Uploaded & analyzed document ($fileName) for patient $childName"
        )

        rawReportId
    }
}
