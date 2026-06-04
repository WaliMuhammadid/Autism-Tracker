package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = AppRepository(db.appDao())

    private val prefs = application.getSharedPreferences("autism_tracker_prefs", android.content.Context.MODE_PRIVATE)

    // === Global Role State ===
    // Supported Roles: "Parent", "Caregiver", "Therapist", "Admin"
    private val _currentUserRole = MutableStateFlow(prefs.getString("current_user_role", "Parent") ?: "Parent")
    val currentUserRole: StateFlow<String> = _currentUserRole.asStateFlow()

    private val _currentUserName = MutableStateFlow(prefs.getString("current_user_name", "Jane Doe") ?: "Jane Doe")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    fun switchRole(role: String, userName: String) {
        _currentUserRole.value = role
        _currentUserName.value = userName
        prefs.edit()
            .putString("current_user_role", role)
            .putString("current_user_name", userName)
            .apply()
        viewModelScope.launch {
            repository.insertAudit(
                userName = userName,
                userRole = role,
                actionType = "VIEW_DATA",
                details = "Switched active user session to $role role ($userName)"
            )
        }
    }

    // === Patient Registry Navigation (for Admin Portal) ===
    private val _adminSelectedChildId = MutableStateFlow<Int?>(null)
    val adminSelectedChildId: StateFlow<Int?> = _adminSelectedChildId.asStateFlow()

    fun selectAdminPatient(childId: Int?) {
        _adminSelectedChildId.value = childId
        if (childId != null) {
            viewModelScope.launch {
                val child = repository.allChildren.firstOrNull()?.find { it.id == childId }
                repository.insertAudit(
                    userName = _currentUserName.value,
                    userRole = _currentUserRole.value,
                    actionType = "VIEW_DATA",
                    details = "Admin selected and viewed deep historical patient dossier for child: ${child?.name ?: "Unknown"}"
                )
            }
        }
    }

    // === Selected Child State ===
    private val _selectedChildId = MutableStateFlow<Int>(prefs.getInt("selected_child_id", 1)) // Default to first preseeded
    val selectedChildId: StateFlow<Int> = _selectedChildId.asStateFlow()

    fun selectChild(id: Int) {
        _selectedChildId.value = id
        prefs.edit().putInt("selected_child_id", id).apply()
        viewModelScope.launch {
            val child = repository.allChildren.firstOrNull()?.find { it.id == id }
            repository.insertAudit(
                userName = _currentUserName.value,
                userRole = _currentUserRole.value,
                actionType = "VIEW_DATA",
                details = "Owner selected active profile: ${child?.name ?: "Unknown"}"
            )
        }
    }

    // === Room Live Data Observables ===
    val allChildren: StateFlow<List<Child>> = repository.allChildren
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLog>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val masterLibrary: StateFlow<List<MasterLibraryItem>> = repository.masterLibrary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tipsLibrary: StateFlow<List<TipsLibraryItem>> = repository.tipsLibrary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingInterventions: StateFlow<List<Intervention>> = repository.pendingInterventions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports: StateFlow<List<PatientReport>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamic Observables based on Selected Child
    val activeChild: StateFlow<Child?> = combine(allChildren, _selectedChildId) { children, id ->
        children.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeInterventions: StateFlow<List<Intervention>> = _selectedChildId.flatMapLatest { id ->
        repository.getInterventionsForChild(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeEventLogs: StateFlow<List<EventLog>> = _selectedChildId.flatMapLatest { id ->
        repository.getEventLogsForChild(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeMetricsLogs: StateFlow<List<EventMetricsLog>> = _selectedChildId.flatMapLatest { id ->
        repository.getMetricsLogsForChild(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activePreferences: StateFlow<ParentPreference?> = _selectedChildId.flatMapLatest { id ->
        repository.getPreferencesForChild(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeReports: StateFlow<List<PatientReport>> = _selectedChildId.flatMapLatest { id ->
        repository.getReportsForChild(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamic Observables based on Admin Selected Child (Clinical Dossier)
    val adminActiveChild: StateFlow<Child?> = combine(allChildren, _adminSelectedChildId) { children, adminId ->
        if (adminId != null) children.find { it.id == adminId } else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val adminActiveInterventions: StateFlow<List<Intervention>> = _adminSelectedChildId.flatMapLatest { adminId ->
        if (adminId != null) repository.getInterventionsForChild(adminId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminActiveEventLogs: StateFlow<List<EventLog>> = _adminSelectedChildId.flatMapLatest { adminId ->
        if (adminId != null) repository.getEventLogsForChild(adminId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminActiveMetricsLogs: StateFlow<List<EventMetricsLog>> = _adminSelectedChildId.flatMapLatest { adminId ->
        if (adminId != null) repository.getMetricsLogsForChild(adminId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminActiveReports: StateFlow<List<PatientReport>> = _adminSelectedChildId.flatMapLatest { adminId ->
        if (adminId != null) repository.getReportsForChild(adminId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // === Personalized Tips Eligibility Rules ===
    val eligibleTips: StateFlow<List<TipsLibraryItem>> = combine(activeChild, tipsLibrary) { child, tips ->
        if (child == null) return@combine emptyList<TipsLibraryItem>()
        val focusSet = child.focusAreas.split(",").map { it.trim().lowercase() }.toSet()
        val commProfile = child.communicationProfile.lowercase()

        tips.filter { tip ->
            val matchFocus = tip.eligibleFocus.lowercase() == "all" || focusSet.contains(tip.eligibleFocus.lowercase())
            val matchComm = tip.eligibleComm.lowercase() == "all" || 
                           tip.eligibleComm.lowercase() == commProfile || 
                           commProfile.contains(tip.eligibleComm.lowercase())
            matchFocus && matchComm
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // === Report Analysis State ===
    private val _isAnalyzingReport = MutableStateFlow(false)
    val isAnalyzingReport: StateFlow<Boolean> = _isAnalyzingReport.asStateFlow()

    // === Mutating Actions ===

    fun createChildProfile(
        name: String,
        age: Int,
        developmentalStage: String,
        communicationProfile: String,
        sensoryProfile: String,
        focusAreas: List<String>
    ) {
        viewModelScope.launch {
            val child = Child(
                name = name,
                age = age,
                developmentalStage = developmentalStage,
                communicationProfile = communicationProfile,
                sensoryProfile = sensoryProfile,
                focusAreas = focusAreas.joinToString(",")
            )
            val newId = repository.insertChild(child).toInt()
            // Initalize default pref
            repository.insertPreferences(
                ParentPreference(
                    childId = newId,
                    trackedMetrics = "Sleep Quality,Mood Regulation,Transition Ease,Sensory Sensitivities,Engagement",
                    notificationFrequency = "Daily"
                )
            )
            // Audit Log
            repository.insertAudit(
                userName = _currentUserName.value,
                userRole = _currentUserRole.value,
                actionType = "LOG_DATA",
                details = "Created new child profile: $name (Age $age, Focus Areas: ${focusAreas.joinToString(", ")})"
            )
            _selectedChildId.value = newId
            prefs.edit().putInt("selected_child_id", newId).apply()
        }
    }

    fun updateChildProfile(child: Child) {
        viewModelScope.launch {
            repository.updateChild(child)
            repository.insertAudit(
                userName = _currentUserName.value,
                userRole = _currentUserRole.value,
                actionType = "LOG_DATA",
                details = "Updated profile for: ${child.name}"
            )
        }
    }

    fun addDailyLogs(
        childId: Int,
        metrics: Map<String, Float>,
        noteText: String,
        category: String
    ) {
        viewModelScope.launch {
            val timestamp = System.currentTimeMillis()
            metrics.forEach { (metric, score) ->
                repository.insertMetricsLog(
                    EventMetricsLog(
                        childId = childId,
                        metricName = metric,
                        value = score,
                        timestamp = timestamp,
                        authorName = _currentUserName.value,
                        authorRole = _currentUserRole.value
                    )
                )
            }

            if (noteText.isNotBlank()) {
                repository.insertEventLog(
                    EventLog(
                        childId = childId,
                        timestamp = timestamp,
                        noteText = noteText,
                        category = category,
                        authorName = _currentUserName.value,
                        authorRole = _currentUserRole.value
                    )
                )
            } else {
                repository.insertAudit(
                    userName = _currentUserName.value,
                    userRole = _currentUserRole.value,
                    actionType = "LOG_DATA",
                    details = "Logged quantitative metric values for Child ID: $childId"
                )
            }
        }
    }

    fun addIntervention(childId: Int, name: String, category: String, description: String, source: String = "Parent-Added") {
        viewModelScope.launch {
            // If Parent-Added, reviewed = false (Pending Library)
            // If Therapist / Admin / Shared Curated, reviewed = true
            val isReviewed = (source != "Parent-Added") || (_currentUserRole.value == "Admin")
            val intervention = Intervention(
                childId = childId,
                name = name,
                category = category,
                description = description,
                source = source,
                status = "Active",
                isReviewed = isReviewed
            )
            repository.insertIntervention(intervention)
            repository.insertAudit(
                userName = _currentUserName.value,
                userRole = _currentUserRole.value,
                actionType = "LOG_DATA",
                details = "Added tracker intervention: $name ($category) for Child ID: $childId. Status: Active"
            )
        }
    }

    fun toggleInterventionStatus(intervention: Intervention) {
        viewModelScope.launch {
            val newStatus = if (intervention.status == "Active") "Inactive" else "Active"
            val updated = intervention.copy(status = newStatus)
            repository.updateIntervention(updated)
            repository.insertAudit(
                userName = _currentUserName.value,
                userRole = _currentUserRole.value,
                actionType = "LOG_DATA",
                details = "Toggled intervention '${intervention.name}' status to $newStatus for Child ID: ${intervention.childId}"
            )
        }
    }

    fun convertTipToIntervention(childId: Int, tip: TipsLibraryItem) {
        viewModelScope.launch {
            val intervention = Intervention(
                childId = childId,
                name = tip.title,
                category = "Sensory Support",
                description = tip.content,
                source = "Tip-Assigned",
                status = "Active",
                isReviewed = true
            )
            repository.insertIntervention(intervention)
            repository.insertAudit(
                userName = _currentUserName.value,
                userRole = _currentUserRole.value,
                actionType = "LOG_DATA",
                details = "Tip Convert: Saved tip '${tip.title}' as tracked active intervention for Child ID: $childId"
            )
        }
    }

    fun approvePendingIntervention(intervention: Intervention) {
        viewModelScope.launch {
            val approved = intervention.copy(isReviewed = true)
            repository.updateIntervention(approved)
            repository.insertAudit(
                userName = _currentUserName.value,
                userRole = _currentUserRole.value,
                actionType = "LOG_DATA",
                details = "Admin Approved pending local intervention: ${intervention.name} into Shared Master visibility"
            )
        }
    }

    fun uploadPatientReport(
        childId: Int,
        title: String,
        fileName: String,
        fileContent: String,
        childName: String,
        focusAreas: String
    ) {
        viewModelScope.launch {
            _isAnalyzingReport.value = true
            val logsFlow = repository.getEventLogsForChild(childId).firstOrNull() ?: emptyList()
            val recentLogs = logsFlow.take(5).map { "${it.category}: ${it.noteText}" }
            
            repository.uploadAndAnalyzeReport(
                childId = childId,
                title = title,
                fileName = fileName,
                fileContent = fileContent,
                childName = childName,
                focusAreas = focusAreas,
                recentLogs = recentLogs,
                uploadedBy = _currentUserName.value,
                uploaderRole = _currentUserRole.value
            )
            _isAnalyzingReport.value = false
        }
    }

    fun savePreferences(pref: ParentPreference) {
        viewModelScope.launch {
            repository.insertPreferences(pref)
            repository.insertAudit(
                userName = _currentUserName.value,
                userRole = _currentUserRole.value,
                actionType = "LOG_DATA",
                details = "Updated parent settings and log visibility preferences for Child ID: ${pref.childId}"
            )
        }
    }

    // === Invited Caregiver Sharing Roster ===
    // Simulated active invites & permissions linked in memory or easily audit logged
    private val _sharedCaregiers = MutableStateFlow(
        listOf(
            CaregiverInvite("Jane Doe", "caregiver_jane@email.com", "Caregiver", true),
            CaregiverInvite("Sarah Miller", "sarah_ot_speech@specialist.com", "Therapist", true)
        )
    )
    val sharedCaregivers: StateFlow<List<CaregiverInvite>> = _sharedCaregiers.asStateFlow()

    fun inviteCaregiver(name: String, email: String, role: String) {
        viewModelScope.launch {
            val current = _sharedCaregiers.value.toMutableList()
            current.add(CaregiverInvite(name, email, role, false))
            _sharedCaregiers.value = current
            repository.insertAudit(
                userName = _currentUserName.value,
                userRole = _currentUserRole.value,
                actionType = "UPDATE_ACCESS",
                details = "Sent email invitation to $name ($email) as role: $role"
            )
        }
    }

    fun acceptInviteSimulated(shareCode: String) {
        viewModelScope.launch {
            repository.insertAudit(
                userName = _currentUserName.value,
                userRole = _currentUserRole.value,
                actionType = "UPDATE_ACCESS",
                details = "Successfully linked dashboard session using cloud share code: $shareCode"
            )
        }
    }
}

data class CaregiverInvite(
    val name: String,
    val email: String,
    val role: String,
    val isAccepted: Boolean
)
