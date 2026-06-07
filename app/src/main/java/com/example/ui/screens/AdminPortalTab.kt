package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.AppViewModel
import com.example.ui.components.CustomTrendLineChart
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPortalTab(
    viewModel: AppViewModel,
    allChildren: List<Child>,
    adminSelectedId: Int?,
    adminActiveChild: Child?,
    adminActiveInterventions: List<Intervention>,
    adminActiveEventLogs: List<EventLog>,
    adminActiveMetricsLogs: List<EventMetricsLog>,
    adminActiveReports: List<PatientReport>,
    pendingInterventions: List<Intervention>,
    auditLogs: List<AuditLog>
) {
    var inputDocTitle by remember { mutableStateOf("") }
    var inputDocText by remember { mutableStateOf("") }
    var selectedUploadChildId by remember { mutableIntStateOf(1) }

    val isAnalyzing by viewModel.isAnalyzingReport.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Clinical Portal Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(LightLavender, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔒", fontSize = 14.sp)
                    }
                    Text(
                        text = "Clinical Oversight Portal",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = LavenderMedium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Lead Pediatric Clinician and OT Administrator Hub. All activities registered under HIPAA BAA standards.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section A: Patient Registry List
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Patient Registry Dossiers",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = LavenderMedium,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    allChildren.forEachIndexed { idx, p ->
                        if (idx > 0) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                        val isSelected = adminSelectedId == p.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isSelected) viewModel.selectAdminPatient(null) else viewModel.selectAdminPatient(p.id)
                                }
                                .background(
                                    color = if (isSelected) LightLavender.copy(alpha = 0.4f) else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(LavenderMedium, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = p.name.take(1).uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp
                                    )
                                }
                                Column {
                                    Text(
                                        text = p.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Age ${p.age} • ${p.communicationProfile}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                text = if (isSelected) "Dismiss Case ✕" else "Inspect Dossier →",
                                fontSize = 12.sp,
                                color = LavenderMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section B: Dossier Details Panel
        if (adminSelectedId != null && adminActiveChild != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.2.dp, LavenderMedium.copy(alpha = 0.35f)),
                    colors = CardDefaults.cardColors(containerColor = LightLavender.copy(alpha = 0.25f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Oversight Dossier: ${adminActiveChild.name}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = LavenderMedium
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Clinical Strategy Mapping",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text("• Focus Areas: ${adminActiveChild.focusAreas}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("• Sensory Profile: ${adminActiveChild.sensoryProfile}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // Clinical Interventions List
                        Text("Configured Therapeutic Trackers", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        if (adminActiveInterventions.isEmpty()) {
                            Text("No trackings initialized yet.", fontSize = 12.sp, color = Color.Gray)
                        } else {
                            adminActiveInterventions.forEach { tracker ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "• ${tracker.name} [${tracker.category}]",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = if (tracker.status == "Active") Color(0xFFE0F2F1) else Color.LightGray.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = tracker.status,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (tracker.status == "Active") SoftTeal else Color.Gray
                                        )
                                    }
                                }
                            }
                        }

                        // Trend Lines (Oversight View)
                        Text("Symptom & Metric Progress Mapping (7-Day)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        if (adminActiveMetricsLogs.isEmpty()) {
                            Text("No measurements reported recently by caregivers.", fontSize = 12.sp, color = Color.Gray)
                        } else {
                            val distinctMetrics = adminActiveMetricsLogs.map { it.metricName }.distinct().take(2)
                            distinctMetrics.forEach { metric ->
                                Text(
                                    text = metric,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                val points = adminActiveMetricsLogs
                                    .filter { it.metricName == metric }
                                    .sortedBy { it.timestamp }
                                    .takeLast(7)
                                CustomTrendLineChart(points = points)
                            }
                        }

                        // Clinical evaluations PDF analyzer
                        Text("Integrated Documents & AI Correlation Logs", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        if (adminActiveReports.isEmpty()) {
                            Text("Awaiting clinical PDF evaluations upload.", fontSize = 12.sp, color = Color.Gray)
                        } else {
                            adminActiveReports.forEach { report ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(report.title, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = LavenderMedium)
                                        Text("Correlated Doc: ${report.fileName}", fontSize = 10.sp, color = Color.Gray, fontStyle = FontStyle.Italic)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(report.aiFindingsSummary, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section C: Upload & Analytics parser
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(LightTeal, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📄", fontSize = 12.sp)
                        }
                        Text(
                            text = "AI-Drive: Parse Medical Evaluation PDF",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = Color(0xFF004D40)
                        )
                    }

                    Text(
                        text = "Paste speech-language reviews, pediatric clinic write-ups, or OT sensory feedback logs. Our HIPAA-safe offline correlation module instantly parses raw evaluations and generates insights under the child's profile.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    // Profile selector chips for document matching
                    Text("Coordinate Patient Case", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        allChildren.forEach { p ->
                            FilterChip(
                                selected = selectedUploadChildId == p.id,
                                onClick = { selectedUploadChildId = p.id },
                                label = { Text(p.name, fontWeight = FontWeight.Bold) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = inputDocTitle,
                        onValueChange = { inputDocTitle = it },
                        placeholder = { Text("Report Title (e.g. SLP Speech Therapy Assessment)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upload_report_title"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftTeal,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    OutlinedTextField(
                        value = inputDocText,
                        onValueChange = { inputDocText = it },
                        placeholder = { Text("Paste Clinical Report Text Content...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp)
                            .testTag("upload_report_text"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftTeal,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    // Presets
                    Text("Preload Validation Mock Trials", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SuggestionChip(
                            onClick = {
                                inputDocTitle = "OT Sensory Tactile Review"
                                inputDocText = "Patient exhibits tactile sensory defensiveness, speech barries, high motor stimming. Extremely sensitive to vacuum cleaners. Recommends implementing sensory diet, deep message brushing, and First-Then cards."
                                viewModel.showToast("Loaded Sensory/OT Plan preset")
                            },
                            label = { Text("OT Brushing / Sensory Plan") },
                            shape = RoundedCornerShape(8.dp)
                        )
                        SuggestionChip(
                            onClick = {
                                inputDocTitle = "SLP Speech Assessment"
                                inputDocText = "Maya exhibits expressive language emerging. Highly motivated by AAC visual device routines which should contain core board selections on eating table for request tracking."
                                viewModel.showToast("Loaded SLP/AAC Device preset")
                            },
                            label = { Text("AAC Speech Device Board") },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    if (isAnalyzing) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .background(LightLavender, shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Secured Clinical engine is parsing & correlating pattern observations...",
                                color = LavenderMedium,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                if (inputDocTitle.isNotBlank() && inputDocText.isNotBlank()) {
                                    val matchedChild = allChildren.find { it.id == selectedUploadChildId }
                                    viewModel.uploadPatientReport(
                                        childId = selectedUploadChildId,
                                        title = inputDocTitle,
                                        fileName = inputDocTitle.replace(" ", "_").lowercase() + ".pdf",
                                        fileContent = inputDocText,
                                        childName = matchedChild?.name ?: "Patient",
                                        focusAreas = matchedChild?.focusAreas ?: ""
                                    )
                                    inputDocTitle = ""
                                    inputDocText = ""
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.End)
                                .testTag("upload_analyze_report_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = LavenderMedium),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Simulate Report Sync & Correlation", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section D: Library Review Approvals
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Strategies Pending Board Review (${pendingInterventions.size})",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color(0xFF004D40),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    if (pendingInterventions.isEmpty()) {
                        Text(
                            text = "No custom entries require board certification details.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        pendingInterventions.forEachIndexed { index, pending ->
                            if (index > 0) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(pending.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Category: ${pending.category}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(pending.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { viewModel.approvePendingIntervention(pending) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text("Approve", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section E: HIPAA Trail Log
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(LightLavender, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📋", fontSize = 11.sp)
                        }
                        Text(
                            text = "HIPAA Activity Audit Logs",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = LavenderMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Encrypted access trace recording database linkages, clinician lookups, and specialist approvals.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    auditLogs.take(8).forEach { log ->
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${log.actionType} • ${log.userName} (${log.userRole})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = LavenderMedium
                                )
                                Text(
                                    text = SimpleDateFormat("MM/dd HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp)),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = log.details,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 15.sp
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(top = 8.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }
    }
}
