package com.kreedaankana.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreedaankana.data.local.entity.TournamentEntity
import com.kreedaankana.ui.theme.*
import com.kreedaankana.viewmodel.OwnerTournamentViewModel

@Composable
fun OwnerTournamentScreen(
    viewModel: OwnerTournamentViewModel,
    onBack: () -> Unit,
    onCreateTournament: () -> Unit
) {
    val tournaments by viewModel.tournaments.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkCharcoal, SurfaceDark, DarkCharcoal)))) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)) {
            item {
                Spacer(Modifier.statusBarsPadding())
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(SurfaceVariantDark)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Spacer(Modifier.width(16.dp))
                    Text("Tournaments", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    IconButton(onClick = onCreateTournament, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(ElectricBlue.copy(alpha = 0.2f))) {
                        Icon(Icons.Default.Add, contentDescription = "Create", tint = ElectricBlue)
                    }
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
            if (tournaments.isEmpty()) {
                item { Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("\uD83C\uDFC6", fontSize = 48.sp); Spacer(Modifier.height(16.dp)); Text("No tournaments", style = MaterialTheme.typography.titleMedium, color = TextSecondary) } } }
            }
            items(tournaments) { TournamentCard(it, viewModel) }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun TournamentCard(tournament: TournamentEntity, viewModel: OwnerTournamentViewModel) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(NeonGreen.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) { Text("\uD83C\uDFC6", fontSize = 22.sp) }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(tournament.name, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("${tournament.sportType} \u2022 ${tournament.startDate}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                Text(tournament.status.uppercase(), color = when (tournament.status) { "live" -> NeonGreen; "completed" -> TextMuted; else -> ElectricBlue }, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerTournamentFormScreen(
    viewModel: OwnerTournamentViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val isSaving by viewModel.isSaving.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()
    LaunchedEffect(success) { if (success) { viewModel.clearStates(); onSaved() } }

    var name by remember { mutableStateOf("") }; var sportType by remember { mutableStateOf("Football") }
    var entryFee by remember { mutableStateOf("") }; var maxTeams by remember { mutableStateOf("") }
    var format by remember { mutableStateOf("Knockout") }; var prize by remember { mutableStateOf("") }
    var rules by remember { mutableStateOf("") }; var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    val sports = listOf("Football", "Cricket", "Basketball", "Badminton", "Tennis", "Volleyball")
    val formats = listOf("Knockout", "League", "Group + Knockout")

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkCharcoal, SurfaceDark, DarkCharcoal)))) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(bottom = 100.dp)) {
            item {
                Spacer(Modifier.statusBarsPadding())
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(SurfaceVariantDark)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Spacer(Modifier.width(16.dp)); Text("Create Tournament", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
            item { Spacer(Modifier.height(16.dp)) }

            item {
                Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
                    Column(Modifier.padding(20.dp)) {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tournament Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonGreen))
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(value = entryFee, onValueChange = { entryFee = it }, label = { Text("Entry Fee (\u20B9)") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonGreen))
                            OutlinedTextField(value = maxTeams, onValueChange = { maxTeams = it }, label = { Text("Max Teams") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonGreen))
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(value = prize, onValueChange = { prize = it }, label = { Text("Prize Pool") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonGreen))
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Start Date") }, placeholder = { Text("2025-01-01") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonGreen))
                            OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("End Date") }, placeholder = { Text("2025-01-07") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonGreen))
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(value = rules, onValueChange = { rules = it }, label = { Text("Rules") }, modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonGreen))
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }

            error?.let {
                item { Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.2f))) { Text(it, color = ErrorRed, modifier = Modifier.padding(16.dp)) } }
                item { Spacer(Modifier.height(8.dp)) }
            }

            item {
                Button(onClick = { viewModel.saveTournament(name, sportType, entryFee.toDoubleOrNull() ?: 0.0, maxTeams.toIntOrNull() ?: 8, format, prize, rules, 60, startDate, endDate, startDate) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = NeonGreen), enabled = !isSaving) {
                    if (isSaving) CircularProgressIndicator(color = DarkCharcoal, modifier = Modifier.size(24.dp)) else Text("Create Tournament", color = DarkCharcoal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}
