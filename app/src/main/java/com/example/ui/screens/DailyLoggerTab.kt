package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import com.example.data.Child
import com.example.ui.AppViewModel
import com.example.ui.components.SliderLogItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLoggerTab(viewModel: AppViewModel, child: Child?) {
    if (child == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(LightTeal, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 42.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "No Active Child Profile Selected",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Log Day features require selecting an active child context. Please navigate to the Settings panel.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        return
    }

    var sleepQuality by remember { mutableFloatStateOf(3f) }
    var moodRegulation by remember { mutableFloatStateOf(3f) }
    var transitionEase by remember { mutableFloatStateOf(3f) }
    var sensorySensitivity by remember { mutableFloatStateOf(3f) }
    var engagementLevel by remember { mutableFloatStateOf(3f) }

    var qualNote by remember { mutableStateOf("") }
    var incidentCategory by remember { mutableStateOf("Transition Success") }
    val categories = listOf("Meltdown", "Routine Deviation", "Sleep Accent", "Transition Success", "Feeding Issues")

    var expandedCatDropdown by remember { mutableStateOf(false) }
    var submitSuccessMessage by remember { mutableStateOf<String?>(null) }

    val focusAreasList = child.focusAreas.split(",").map { it.trim() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Log Day Header Box
        Column {
            Text(
                text = "Log Observations • ${child.name}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = Color(0xFF004D40)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Single-tap logging scale for daily therapeutic and behavioral measurements. Under 30 seconds daily.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Action Success Toast banner
        if (submitSuccessMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFC8E6C9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color(0xFF81C784), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = submitSuccessMessage!!,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section A: Numerical Metric Sliders
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(LightTeal, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📊", fontSize = 12.sp)
                    }
                    Text(
                        text = "Quantitative Care Metrics",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color(0xFF004D40)
                    )
                }

                // Sleep Metric (Always show sleep prep)
                if (focusAreasList.contains("Sleep") || true) {
                    SliderLogItem(
                        title = "Sleep Quality & Restfulness",
                        description = "1 (Awake frequently, restless transitions) to 5 (Slept full night, woke refreshed)",
                        value = sleepQuality,
                        onValueChange = { sleepQuality = it }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }

                // Mood/Regulation Metric
                SliderLogItem(
                    title = "Mood & Self-Regulation",
                    description = "1 (Intense sensory distress / dysregulation) to 5 (Soothed, flexible transitions, calm, content)",
                    value = moodRegulation,
                    onValueChange = { moodRegulation = it }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Transitions
                if (focusAreasList.contains("Transitions")) {
                    SliderLogItem(
                        title = "Task Transitions Ease",
                        description = "1 (Extreme avoidance, vocal resistance) to 5 (Swift, effortless visual countdown transitions)",
                        value = transitionEase,
                        onValueChange = { transitionEase = it }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }

                // Sensory Sensitivities
                if (focusAreasList.contains("Sensory")) {
                    SliderLogItem(
                        title = "Sensory Integration & Resilience",
                        description = "1 (Highly overstimulated to noise, tags, lights) to 5 (Resilient and sensory balanced)",
                        value = sensorySensitivity,
                        onValueChange = { sensorySensitivity = it }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }

                // Engagement
                SliderLogItem(
                    title = "Interactive Attention & Engagement",
                    description = "1 (Prefers isolation, avoids interaction cues) to 5 (Collaborative sharing and interactive loops)",
                    value = engagementLevel,
                    onValueChange = { engagementLevel = it }
                )
            }
        }

        // Section B: Qualitative Notes Logging Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(LightTeal, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📝", fontSize = 12.sp)
                    }
                    Text(
                        text = "Behavioral Context Entries",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color(0xFF004D40)
                    )
                }

                Text(
                    text = "Describe events, unexpected deviations, or contextual triggers to expand clinical pattern recognition.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Incident Category Selector Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    ExposedDropdownMenuBox(
                        expanded = expandedCatDropdown,
                        onExpandedChange = { expandedCatDropdown = !expandedCatDropdown }
                    ) {
                        OutlinedTextField(
                            value = "Category: $incidentCategory",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCatDropdown) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("category_select_button"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SoftTeal,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCatDropdown,
                            onDismissRequest = { expandedCatDropdown = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        incidentCategory = cat
                                        expandedCatDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = qualNote,
                    onValueChange = { qualNote = it },
                    placeholder = {
                        Text(
                            text = "Log food intake, visual routines used, or specific meltdown contexts here...",
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("qualitative_incident_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SoftTeal,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }

        // Section C: Submit Button
        Button(
            onClick = {
                val registeredMetrics = mutableMapOf(
                    "Sleep Quality" to sleepQuality,
                    "Mood Regulation" to moodRegulation,
                    "Engagement" to engagementLevel
                )
                if (focusAreasList.contains("Transitions")) {
                    registeredMetrics["Transition Ease"] = transitionEase
                }
                if (focusAreasList.contains("Sensory")) {
                    registeredMetrics["Sensory Sensitivities"] = sensorySensitivity
                }

                viewModel.addDailyLogs(
                    childId = child.id,
                    metrics = registeredMetrics,
                    noteText = qualNote,
                    category = incidentCategory
                )

                submitSuccessMessage = "Daily logs successfully committed to database. Historical patterns updated!"
                qualNote = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("submit_daily_logs"),
            colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
            shape = RoundedCornerShape(14.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = "Save Daily Log",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
