package com.kreedaankana.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreedaankana.data.local.entity.MatchEntity
import com.kreedaankana.ui.theme.*
import com.kreedaankana.viewmodel.OwnerLiveScoreViewModel

@Composable
fun OwnerLiveScoreScreen(
    viewModel: OwnerLiveScoreViewModel,
    onBack: () -> Unit
) {
    val matches by viewModel.matches.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkCharcoal, SurfaceDark, DarkCharcoal)))) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)) {
            item {
                Spacer(Modifier.statusBarsPadding())
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(SurfaceVariantDark)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Spacer(Modifier.width(16.dp)); Text("Live Scores", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
            if (matches.isEmpty()) {
                item { Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("\uD83C\uDFC0", fontSize = 48.sp); Spacer(Modifier.height(16.dp)); Text("No matches", style = MaterialTheme.typography.titleMedium, color = TextSecondary) } } }
            }
            items(matches) { match -> LiveMatchCard(match, viewModel) }
        }
    }
}

@Composable
private fun LiveMatchCard(match: MatchEntity, viewModel: OwnerLiveScoreViewModel) {
    var scoreA by remember(match.teamAScore) { mutableStateOf(match.teamAScore.toString()) }
    var scoreB by remember(match.teamBScore) { mutableStateOf(match.teamBScore.toString()) }
    var showWinnerDialog by remember { mutableStateOf(false) }
    var winner by remember { mutableStateOf("") }

    val isLive = match.status == "live"
    val isScheduled = match.status == "scheduled"
    val isCompleted = match.status == "completed"

    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(match.sportType, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                when {
                    isLive -> Box(Modifier.clip(RoundedCornerShape(4.dp)).background(ErrorRed).padding(horizontal = 8.dp, vertical = 2.dp)) { Text("LIVE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    isCompleted -> Text("COMPLETED", color = TextMuted, fontSize = 12.sp)
                    else -> Text("SCHEDULED", color = ElectricBlue, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(match.teamA, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                    if (isLive || isCompleted) {
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(value = scoreA, onValueChange = { scoreA = it }, modifier = Modifier.width(80.dp), shape = RoundedCornerShape(8.dp), singleLine = true, textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonGreen, unfocusedBorderColor = SurfaceDark))
                    }
                }
                Text("VS", color = TextMuted, modifier = Modifier.padding(horizontal = 16.dp))
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(match.teamB, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                    if (isLive || isCompleted) {
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(value = scoreB, onValueChange = { scoreB = it }, modifier = Modifier.width(80.dp), shape = RoundedCornerShape(8.dp), singleLine = true, textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonGreen, unfocusedBorderColor = SurfaceDark))
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isScheduled) {
                    Button(onClick = { viewModel.startMatch(match.matchId) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)) { Text("Start Match", color = DarkCharcoal) }
                }
                if (isLive) {
                    Button(onClick = { viewModel.updateScore(match.matchId, scoreA.toIntOrNull() ?: 0, scoreB.toIntOrNull() ?: 0) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)) { Text("Update Score", color = DarkCharcoal) }
                    Button(onClick = { winner = match.teamA; showWinnerDialog = true }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)) { Text("End Match", color = Color.White) }
                }
            }
        }
    }

    if (showWinnerDialog) {
        AlertDialog(onDismissRequest = { showWinnerDialog = false }, containerColor = SurfaceDark,
            title = { Text("Declare Winner", color = TextPrimary) },
            text = { Column {
                Button(onClick = { winner = match.teamA }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = if (winner == match.teamA) NeonGreen else SurfaceVariantDark)) { Text(match.teamA, color = if (winner == match.teamA) DarkCharcoal else TextPrimary) }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { winner = match.teamB }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = if (winner == match.teamB) NeonGreen else SurfaceVariantDark)) { Text(match.teamB, color = if (winner == match.teamB) DarkCharcoal else TextPrimary) }
            } },
            confirmButton = { Button(onClick = { viewModel.endMatch(match.matchId, winner); showWinnerDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)) { Text("Confirm", color = DarkCharcoal) } },
            dismissButton = { TextButton(onClick = { showWinnerDialog = false }) { Text("Cancel", color = TextMuted) } }
        )
    }
}
