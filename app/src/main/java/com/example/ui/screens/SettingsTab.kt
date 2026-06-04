package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import com.example.data.*
import com.example.ui.AppViewModel
import com.example.ui.CaregiverInvite
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTab(
    viewModel: AppViewModel,
    allChildren: List<Child>,
    sharedCaregivers: List<CaregiverInvite>,
    auditLogs: List<AuditLog>
) {
    // State Tracking
    var newChildName by remember { mutableStateOf("") }
    var newChildAge by remember { mutableStateOf("") }
    var newChildDevState by remember { mutableStateOf("Preschooler") }
    var newChildComm by remember { mutableStateOf("Emerging Verbal") }
    var newChildSensory by remember { mutableStateOf("Sensory Seeking") }

    // Focus areas tracking list
    val selectedFocus = remember { mutableStateListOf("Sleep", "Transitions", "Sensory") }
    val focusAreas = listOf("Sleep", "Transitions", "Sensory", "Speech", "Attention", "Eating")

    val developmentalStages = listOf("Toddler", "Preschooler", "School Age", "Adolescent")
    val communicationProfiles = listOf("Verbal", "Emerging Verbal", "Non-Verbal", "AAC User")
    val sensoryProfiles = listOf("Sensory Seeking", "Sensory Avoiding", "Mixed Profile", "Balanced")

    var editPrefShareCode by remember { mutableStateOf("") }
    var inviteName by remember { mutableStateOf("") }
    var inviteEmail by remember { mutableStateOf("") }
    var inviteRole by remember { mutableStateOf("Caregiver") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Tab Header
        item {
            Column {
                Text(
                    text = "Application Configuration Panel",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = Color(0xFF004D40)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Manage multi-role simulation states, link cloud codes, or register pediatric care candidates.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section A: Role selection (Simulation Engine)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.2.dp, LavenderMedium.copy(alpha = 0.25f)),
                colors = CardDefaults.cardColors(containerColor = LightLavender.copy(alpha = 0.3f))
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
                            Text("👤", fontSize = 12.sp)
                        }
                        Text(
                            text = "Clinician Board Simulator Engine",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = LavenderMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Verify specialized permission constraints, HIPAA access watermarks, and collaborative scopes instantly by switching actor roles.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.switchRole("Parent", "Jane Doe") },
                            colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Parent Owner", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.switchRole("Caregiver", "Emily (Teacher)") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Caregiver", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.switchRole("Therapist", "Sarah SLP") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Therapist", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.switchRole("Admin", "Dr. Robert Carter") },
                            colors = ButtonDefaults.buttonColors(containerColor = LavenderMedium),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Clinician Admin", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section B: Create Child Profile
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
                            Text("👶", fontSize = 12.sp)
                        }
                        Text(
                            text = "Register Pediatric Care Candidate",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = Color(0xFF004D40)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    OutlinedTextField(
                        value = newChildName,
                        onValueChange = { newChildName = it },
                        placeholder = { Text("Enter Child's Full Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_child_name"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftTeal,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    OutlinedTextField(
                        value = newChildAge,
                        onValueChange = { newChildAge = it },
                        placeholder = { Text("Age (Integer, e.g., 4)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_child_age"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftTeal,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    // Developmental category scrollable chips list
                    Text("Developmental Category", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        developmentalStages.forEach { s ->
                            FilterChip(
                                selected = newChildDevState == s,
                                onClick = { newChildDevState = s },
                                label = { Text(s, fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    // Communication Profile selections
                    Text("Communication Profile Type", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        communicationProfiles.forEach { cp ->
                            FilterChip(
                                selected = newChildComm == cp,
                                onClick = { newChildComm = cp },
                                label = { Text(cp, fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    // Sensory profiles selections
                    Text("Sensory Processing Designation", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        sensoryProfiles.forEach { sp ->
                            FilterChip(
                                selected = newChildSensory == sp,
                                onClick = { newChildSensory = sp },
                                label = { Text(sp, fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    // Multi-select tracked area categories
                    Text("Active Care Focus Indices (Multi-Select)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        focusAreas.forEach { area ->
                            val isChosen = selectedFocus.contains(area)
                            FilterChip(
                                selected = isChosen,
                                onClick = {
                                    if (isChosen) selectedFocus.remove(area) else selectedFocus.add(area)
                                },
                                label = { Text(area, fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            val ageInt = newChildAge.toIntOrNull() ?: 5
                            if (newChildName.isNotBlank()) {
                                viewModel.createChildProfile(
                                    name = newChildName,
                                    age = ageInt,
                                    developmentalStage = newChildDevState,
                                    communicationProfile = newChildComm,
                                    sensoryProfile = newChildSensory,
                                    focusAreas = selectedFocus.toList()
                                )
                                newChildName = ""
                                newChildAge = ""
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("add_child_submit"),
                        colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Add Centered Case Set", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section C: Collaboration & Shared Invites (Attrib/Watermarks)
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
                            Text("👥", fontSize = 12.sp)
                        }
                        Text(
                            text = "Collaborators & Specialist Invites",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = Color(0xFF004D40)
                        )
                    }

                    Text(
                        text = "Parent-authorized access bindings are logged securely under HIPAA protocols. Add speech pathologists, occupational therapists, or family caregivers.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    sharedCaregivers.forEach { member ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "${member.name} (${member.email})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Oversight Access Level: ${member.role}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (member.isAccepted) Color(0xFFE8F5E9) else Color(0xFFFFECEB),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (member.isAccepted) "Link Active" else "Pending Approval",
                                    color = if (member.isAccepted) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Generate New Collaboration Invitation", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)

                    OutlinedTextField(
                        value = inviteName,
                        onValueChange = { inviteName = it },
                        placeholder = { Text("Specialist / Caregiver Full Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("invite_name"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftTeal,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    OutlinedTextField(
                        value = inviteEmail,
                        onValueChange = { inviteEmail = it },
                        placeholder = { Text("Specialist Email Address") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("invite_email"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftTeal,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = inviteRole == "Caregiver", onClick = { inviteRole = "Caregiver" })
                            Text("Caregiver", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(10.dp))
                            RadioButton(selected = inviteRole == "Therapist", onClick = { inviteRole = "Therapist" })
                            Text("Therapist", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (inviteName.isNotBlank() && inviteEmail.isNotBlank()) {
                                    viewModel.inviteCaregiver(inviteName, inviteEmail, inviteRole)
                                    inviteName = ""
                                    inviteEmail = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
                            modifier = Modifier.testTag("submit_invite_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Send Invite", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section D: Connect with Share Code card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            Text("🔗", fontSize = 12.sp)
                        }
                        Text(
                            text = "Link Existing Case Profile",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = Color(0xFF004D40)
                        )
                    }

                    Text(
                        text = "Paste your 10-digit cloud share code linking credential here to import authorized records instantly.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    OutlinedTextField(
                        value = editPrefShareCode,
                        onValueChange = { editPrefShareCode = it },
                        placeholder = { Text("E.g. LEO-POV-992") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("enter_share_code"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SoftTeal,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Button(
                        onClick = {
                            if (editPrefShareCode.isNotBlank()) {
                                viewModel.acceptInviteSimulated(editPrefShareCode)
                                editPrefShareCode = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SoftTeal),
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("link_code_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Connect Cases", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
