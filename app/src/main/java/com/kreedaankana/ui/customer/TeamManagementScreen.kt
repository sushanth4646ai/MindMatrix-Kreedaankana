package com.kreedaankana.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreedaankana.data.local.AppDatabase
import com.kreedaankana.data.local.entity.TeamEntity
import com.kreedaankana.ui.theme.*
import kotlinx.coroutines.launch

data class TeamMember(val id: Int = 0, val name: String = "", val phone: String = "")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamManagementScreen(
    onBack: () -> Unit,
    onTeamSaved: (teamId: Int) -> Unit = {}
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val coroutineScope = rememberCoroutineScope()

    var teamName by remember { mutableStateOf("") }
    var sport by remember { mutableStateOf("Football") }
    var captainName by remember { mutableStateOf("") }
    var captainPhone by remember { mutableStateOf("") }
    var members by remember { mutableStateOf(listOf<TeamMember>()) }
    var memberName by remember { mutableStateOf("") }
    var memberPhone by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    val sports = listOf("Football", "Cricket", "Basketball", "Badminton", "Tennis", "Volleyball")

    fun sportMinPlayers(s: String) = when (s) { "Badminton", "Tennis" -> 1; else -> 2 }
    fun sportMaxPlayers(s: String) = when (s) { "Football" -> 11; "Cricket" -> 11; "Basketball" -> 5; "Volleyball" -> 6; "Badminton" -> 2; "Tennis" -> 2; else -> 11 }
    fun sportFormat(s: String) = when (s) { "Badminton", "Tennis" -> "1v1 (singles) / 2v2 (doubles)"; else -> "${sportMaxPlayers(s)}v${sportMaxPlayers(s)}" }

    val totalPlayers = (if (captainName.isNotBlank()) 1 else 0) + members.count { it.name.isNotBlank() }
    val minPlayers = sportMinPlayers(sport)
    val maxPlayers = sportMaxPlayers(sport)

    val isFormValid = teamName.isNotBlank() && captainName.isNotBlank() && captainPhone.isNotBlank()
    val canSave = isFormValid && totalPlayers >= minPlayers

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(DarkCharcoal, SurfaceDark, DarkCharcoal))
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 100.dp)
        ) {
            Spacer(Modifier.statusBarsPadding())

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp).clip(CircleShape).background(SurfaceVariantDark)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(Modifier.width(16.dp))
                Text("Create Team", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Team Info", style = MaterialTheme.typography.titleSmall, color = NeonGreen, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = teamName,
                        onValueChange = { teamName = it; error = null },
                        label = { Text("Team Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonGreen, focusedLabelColor = NeonGreen)
                    )
                    Spacer(Modifier.height(12.dp))

                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                        OutlinedTextField(
                            value = sport,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sport") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonGreen, focusedLabelColor = NeonGreen)
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            sports.forEach { s ->
                                DropdownMenuItem(text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(s, modifier = Modifier.weight(1f))
                                        Text(sportFormat(s), color = TextMuted, fontSize = 12.sp)
                                    }
                                }, onClick = { sport = s; expanded = false })
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Format: ${sportFormat(sport)}  •  Need $minPlayers-$maxPlayers per side", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Captain", style = MaterialTheme.typography.titleSmall, color = NeonGreen, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = captainName,
                        onValueChange = { captainName = it; error = null },
                        label = { Text("Captain Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonGreen, focusedLabelColor = NeonGreen)
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = captainPhone,
                        onValueChange = { captainPhone = it; error = null },
                        label = { Text("Captain Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonGreen, focusedLabelColor = NeonGreen)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Team Members", style = MaterialTheme.typography.titleSmall, color = NeonGreen, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("$totalPlayers / $maxPlayers  (min $minPlayers)", color = if (totalPlayers >= minPlayers) SuccessGreen else TextMuted, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(16.dp))

                    rows@ for ((index, member) in members.withIndex()) {
                        if (member.name.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(ElectricBlue.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                                    Text(member.name.take(1).uppercase(), color = ElectricBlue, fontWeight = FontWeight.Bold)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(member.name, color = TextPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    if (member.phone.isNotBlank()) Text(member.phone, color = TextMuted, style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { members = members.toMutableList().also { it.removeAt(index) } }, modifier = Modifier.size(36.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = ErrorRed, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }

                    if (members.isEmpty()) {
                        Text("No members added yet", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                    }

                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = memberName,
                        onValueChange = { memberName = it },
                        label = { Text("Member Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue, focusedLabelColor = ElectricBlue)
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = memberPhone,
                            onValueChange = { memberPhone = it },
                            label = { Text("Phone (optional)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue, focusedLabelColor = ElectricBlue)
                        )
                        Spacer(Modifier.width(8.dp))
                        FilledTonalButton(
                            onClick = {
                                if (memberName.isNotBlank()) {
                                    members = members + TeamMember(id = members.size + 1, name = memberName, phone = memberPhone)
                                    memberName = ""; memberPhone = ""
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = ElectricBlue.copy(alpha = 0.2f))
                        ) { Icon(Icons.Default.Add, contentDescription = "Add", tint = ElectricBlue) }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            error?.let {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.2f))
                ) { Text(it, color = ErrorRed, modifier = Modifier.padding(16.dp)) }
            }

            if (success) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.2f))
                ) { Text("Team created successfully!", color = SuccessGreen, modifier = Modifier.padding(16.dp)) }
            }

            Button(
                onClick = {
                    when {
                        !isFormValid -> error = "Fill in team name, captain name, and phone"
                        totalPlayers < minPlayers -> error = "$sport needs at least $minPlayers player${if (minPlayers > 1) "s" else ""} per team (you have $totalPlayers)"
                        else -> {
                            isSaving = true; error = null
                            coroutineScope.launch {
                                try {
                                    val playerNames = listOf("$captainName (C)") + members.filter { it.name.isNotBlank() }.map { it.name }
                                    val team = TeamEntity(
                                        teamId = System.currentTimeMillis().toInt(),
                                        teamName = teamName,
                                        captainName = captainName,
                                        captainPhone = captainPhone,
                                        sport = sport,
                                        players = playerNames,
                                        createdAt = System.currentTimeMillis()
                                    )
                                    database.teamDao().insert(team)
                                    success = true; isSaving = false
                                } catch (e: Exception) {
                                    error = e.message; isSaving = false
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                enabled = !isSaving && canSave
            ) { Text("Save Team ($totalPlayers/$minPlayers players)", color = DarkCharcoal, fontWeight = FontWeight.Bold, fontSize = 16.sp) }

            Spacer(Modifier.height(32.dp))
        }
    }
}
