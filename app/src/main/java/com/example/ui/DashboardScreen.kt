package com.example.ui

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.screens.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: AppViewModel) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val currentRole by viewModel.currentUserRole.collectAsStateWithLifecycle()
    val userName by viewModel.currentUserName.collectAsStateWithLifecycle()
    val allChildren by viewModel.allChildren.collectAsStateWithLifecycle()
    val activeChild by viewModel.activeChild.collectAsStateWithLifecycle()
    val activeInterventions by viewModel.activeInterventions.collectAsStateWithLifecycle()
    val activeEventLogs by viewModel.activeEventLogs.collectAsStateWithLifecycle()
    val activeMetricsLogs by viewModel.activeMetricsLogs.collectAsStateWithLifecycle()
    val activePreferences by viewModel.activePreferences.collectAsStateWithLifecycle()
    val eligibleTips by viewModel.eligibleTips.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val allReports by viewModel.allReports.collectAsStateWithLifecycle()
    val activeReports by viewModel.activeReports.collectAsStateWithLifecycle()
    val pendingInterventions by viewModel.pendingInterventions.collectAsStateWithLifecycle()
    val masterLibrary by viewModel.masterLibrary.collectAsStateWithLifecycle()
    val sharedCaregivers by viewModel.sharedCaregivers.collectAsStateWithLifecycle()

    // Admin State Access
    val adminSelectedId by viewModel.adminSelectedChildId.collectAsStateWithLifecycle()
    val adminActiveChild by viewModel.adminActiveChild.collectAsStateWithLifecycle()
    val adminActiveInterventions by viewModel.adminActiveInterventions.collectAsStateWithLifecycle()
    val adminActiveEventLogs by viewModel.adminActiveEventLogs.collectAsStateWithLifecycle()
    val adminActiveMetricsLogs by viewModel.adminActiveMetricsLogs.collectAsStateWithLifecycle()
    val adminActiveReports by viewModel.adminActiveReports.collectAsStateWithLifecycle()

    // Screen navigation layout: 0=Dashboard, 1=Daily Logger, 2=Interventions Library, 3=Tips, 4=Admin Portal, 5=Settings & Members
    var currentTab by remember { mutableStateOf(0) }

    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Autism Care Tracker",
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = SoftTeal
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (currentRole == "Admin") LavenderMedium else SoftTeal,
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$userName ($currentRole)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Quick profile selector
                    if (currentRole == "Parent" || currentRole == "Caregiver" || currentRole == "Therapist") {
                        var expandedProfiles by remember { mutableStateOf(false) }
                        Button(
                            onClick = { expandedProfiles = true },
                            colors = ButtonDefaults.buttonColors(containerColor = LightTeal, contentColor = SoftTeal),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("profile_selector_btn")
                        ) {
                            Text(activeChild?.name ?: "No Profile", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(" ▼", fontSize = 11.sp)
                        }
                        DropdownMenu(
                            expanded = expandedProfiles,
                            onDismissRequest = { expandedProfiles = false }
                        ) {
                            allChildren.forEach { child ->
                                DropdownMenuItem(
                                    text = { Text(child.name, fontWeight = FontWeight.SemiBold) },
                                    onClick = {
                                        viewModel.selectChild(child.id)
                                        expandedProfiles = false
                                    }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("+ Add Child Profile", color = SoftTeal) },
                                onClick = {
                                    expandedProfiles = false
                                    currentTab = 5 // Go to configuration settings to add child
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (!isTablet) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        alwaysShowLabel = false,
                        icon = { 
                            Icon(
                                imageVector = if (currentTab == 0) Icons.Rounded.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", maxLines = 1, fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_home")
                    )
                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        alwaysShowLabel = false,
                        icon = { 
                            Icon(
                                imageVector = if (currentTab == 1) Icons.Rounded.EditCalendar else Icons.Outlined.EditCalendar,
                                contentDescription = "Log"
                            )
                        },
                        label = { Text("Log", maxLines = 1, fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_log")
                    )
                    NavigationBarItem(
                        selected = currentTab == 2,
                        onClick = { currentTab = 2 },
                        alwaysShowLabel = false,
                        icon = { 
                            Icon(
                                imageVector = if (currentTab == 2) Icons.Rounded.Star else Icons.Outlined.Star,
                                contentDescription = "Trackers"
                            )
                        },
                        label = { Text("Trackers", maxLines = 1, fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_trackers")
                    )
                    NavigationBarItem(
                        selected = currentTab == 3,
                        onClick = { currentTab = 3 },
                        alwaysShowLabel = false,
                        icon = { 
                            Icon(
                                imageVector = if (currentTab == 3) Icons.Rounded.Lightbulb else Icons.Outlined.Lightbulb,
                                contentDescription = "Tips"
                            )
                        },
                        label = { Text("Tips", maxLines = 1, fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_tips")
                    )
                    if (currentRole == "Admin") {
                        NavigationBarItem(
                            selected = currentTab == 4,
                            onClick = { currentTab = 4 },
                            alwaysShowLabel = false,
                            icon = { 
                                Icon(
                                    imageVector = if (currentTab == 4) Icons.Rounded.Lock else Icons.Outlined.Lock,
                                    contentDescription = "Clinical"
                                )
                            },
                            label = { Text("Clinical", maxLines = 1, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = LavenderMedium,
                                selectedTextColor = LavenderMedium,
                                indicatorColor = LightLavender
                            ),
                            modifier = Modifier.testTag("nav_admin")
                        )
                    }
                    NavigationBarItem(
                        selected = currentTab == 5,
                        onClick = { currentTab = 5 },
                        alwaysShowLabel = false,
                        icon = { 
                            Icon(
                                imageVector = if (currentTab == 5) Icons.Rounded.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Settings", maxLines = 1, fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_settings")
                    )
                }
            }
        }
    ) { innerPadding ->
        if (isTablet) {
            // Adaptive horizontal layout for wide screen
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface,
                    header = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(SoftTeal.copy(alpha = 0.12f), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.MedicalServices,
                                    contentDescription = "Care Portal",
                                    tint = SoftTeal,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier.testTag("side_nav_rail")
                ) {
                    NavigationRailItem(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == 0) Icons.Rounded.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    NavigationRailItem(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == 1) Icons.Rounded.EditCalendar else Icons.Outlined.EditCalendar,
                                contentDescription = "Log Day"
                            )
                        },
                        label = { Text("Log Day", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    NavigationRailItem(
                        selected = currentTab == 2,
                        onClick = { currentTab = 2 },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == 2) Icons.Rounded.Star else Icons.Outlined.Star,
                                contentDescription = "Trackers"
                            )
                        },
                        label = { Text("Trackers", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    NavigationRailItem(
                        selected = currentTab == 3,
                        onClick = { currentTab = 3 },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == 3) Icons.Rounded.Lightbulb else Icons.Outlined.Lightbulb,
                                contentDescription = "Tips"
                            )
                        },
                        label = { Text("Tips", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    if (currentRole == "Admin") {
                        NavigationRailItem(
                            selected = currentTab == 4,
                            onClick = { currentTab = 4 },
                            icon = {
                                Icon(
                                    imageVector = if (currentTab == 4) Icons.Rounded.Lock else Icons.Outlined.Lock,
                                    contentDescription = "Clinical Portal",
                                    tint = if (currentTab == 4) LavenderMedium else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            label = { Text("Clinical Portal", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                    NavigationRailItem(
                        selected = currentTab == 5,
                        onClick = { currentTab = 5 },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == 5) Icons.Rounded.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Settings", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    TabContent(
                        tabIndex = currentTab,
                        viewModel = viewModel,
                        activeChild = activeChild,
                        allChildren = allChildren,
                        interventions = activeInterventions,
                        eventLogs = activeEventLogs,
                        metricsLogs = activeMetricsLogs,
                        prefs = activePreferences,
                        eligibleTips = eligibleTips,
                        auditLogs = auditLogs,
                        allReports = allReports,
                        activeReports = activeReports,
                        pendingInterventions = pendingInterventions,
                        masterLibrary = masterLibrary,
                        sharedCaregivers = sharedCaregivers,
                        currentRole = currentRole,
                        adminSelectedId = adminSelectedId,
                        adminActiveChild = adminActiveChild,
                        adminActiveInterventions = adminActiveInterventions,
                        adminActiveEventLogs = adminActiveEventLogs,
                        adminActiveMetricsLogs = adminActiveMetricsLogs,
                        adminActiveReports = adminActiveReports,
                        onTabChange = { currentTab = it }
                    )
                }
            }
        } else {
            // Mobile layout
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                TabContent(
                    tabIndex = currentTab,
                    viewModel = viewModel,
                    activeChild = activeChild,
                    allChildren = allChildren,
                    interventions = activeInterventions,
                    eventLogs = activeEventLogs,
                    metricsLogs = activeMetricsLogs,
                    prefs = activePreferences,
                    eligibleTips = eligibleTips,
                    auditLogs = auditLogs,
                    allReports = allReports,
                    activeReports = activeReports,
                    pendingInterventions = pendingInterventions,
                    masterLibrary = masterLibrary,
                    sharedCaregivers = sharedCaregivers,
                    currentRole = currentRole,
                    adminSelectedId = adminSelectedId,
                    adminActiveChild = adminActiveChild,
                    adminActiveInterventions = adminActiveInterventions,
                    adminActiveEventLogs = adminActiveEventLogs,
                    adminActiveMetricsLogs = adminActiveMetricsLogs,
                    adminActiveReports = adminActiveReports,
                    onTabChange = { currentTab = it }
                )
            }
        }
    }
}

@Composable
fun TabContent(
    tabIndex: Int,
    viewModel: AppViewModel,
    activeChild: Child?,
    allChildren: List<Child>,
    interventions: List<Intervention>,
    eventLogs: List<EventLog>,
    metricsLogs: List<EventMetricsLog>,
    prefs: ParentPreference?,
    eligibleTips: List<TipsLibraryItem>,
    auditLogs: List<AuditLog>,
    allReports: List<PatientReport>,
    activeReports: List<PatientReport>,
    pendingInterventions: List<Intervention>,
    masterLibrary: List<MasterLibraryItem>,
    sharedCaregivers: List<CaregiverInvite>,
    currentRole: String,
    adminSelectedId: Int?,
    adminActiveChild: Child?,
    adminActiveInterventions: List<Intervention>,
    adminActiveEventLogs: List<EventLog>,
    adminActiveMetricsLogs: List<EventMetricsLog>,
    adminActiveReports: List<PatientReport>,
    onTabChange: (Int) -> Unit
) {
    AnimatedContent(
        targetState = tabIndex,
        transitionSpec = {
            fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
        },
        label = "tab_transition"
    ) { targetTab ->
        when (targetTab) {
            0 -> ChildDashboardTab(
                viewModel = viewModel,
                child = activeChild,
                interventions = interventions,
                eventLogs = eventLogs,
                metricsLogs = metricsLogs,
                eligibleTips = eligibleTips,
                reports = activeReports,
                currentRole = currentRole,
                onNavigateToLogger = { onTabChange(1) }
            )
            1 -> DailyLoggerTab(viewModel = viewModel, child = activeChild)
            2 -> InterventionsTab(
                viewModel = viewModel,
                child = activeChild,
                interventions = interventions,
                masterLibrary = masterLibrary
            )
            3 -> TipsTab(
                viewModel = viewModel,
                child = activeChild,
                eligibleTips = eligibleTips
            )
            4 -> if (currentRole == "Admin") {
                AdminPortalTab(
                    viewModel = viewModel,
                    allChildren = allChildren,
                    adminSelectedId = adminSelectedId,
                    adminActiveChild = adminActiveChild,
                    adminActiveInterventions = adminActiveInterventions,
                    adminActiveEventLogs = adminActiveEventLogs,
                    adminActiveMetricsLogs = adminActiveMetricsLogs,
                    adminActiveReports = adminActiveReports,
                    pendingInterventions = pendingInterventions,
                    auditLogs = auditLogs
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Access strictly limited. Requires lead clinician credentials.")
                }
            }
            5 -> SettingsTab(
                viewModel = viewModel,
                allChildren = allChildren,
                sharedCaregivers = sharedCaregivers,
                auditLogs = auditLogs
            )
        }
    }
}
