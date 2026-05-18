package com.kreedaankana.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kreedaankana.KreedaApplication
import com.kreedaankana.data.local.entity.ChallengeEntity
import com.kreedaankana.ui.components.KreedaCard
import com.kreedaankana.ui.components.SportTag
import com.kreedaankana.ui.theme.OrangeAccent
import com.kreedaankana.viewmodel.ChallengeViewModel

@Composable
fun VersusScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val application = context.applicationContext as KreedaApplication
    val viewModel: ChallengeViewModel = viewModel(factory = application.viewModelFactory)
    val challenges by viewModel.challenges.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFF0F0F0F),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = OrangeAccent,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Challenge")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "VERSUS",
                fontSize = 12.sp,
                color = OrangeAccent,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "CHALLENGE BOARD",
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (challenges.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("NO ACTIVE CHALLENGES\nBe the first to post one!", color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(challenges) { challenge ->
                        ChallengeItem(challenge)
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateChallengeDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { team, sport, loc, desc ->
                viewModel.createChallenge(
                    ChallengeEntity(
                        challengeId = "CH-${System.currentTimeMillis()}",
                        teamName = team,
                        sportType = sport,
                        location = loc,
                        description = desc,
                        challengeDate = "TBD"
                    )
                )
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun ChallengeItem(challenge: ChallengeEntity) {
    KreedaCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SportTag(name = challenge.sportType.uppercase())
                Box(
                    modifier = Modifier
                        .background(
                            if (challenge.status == "OPEN") Color(0xFF1B5E20) else Color(0xFF33691E),
                            RoundedCornerShape(3.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = challenge.status,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = challenge.teamName.uppercase(),
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            if (challenge.description.isNotBlank()) {
                Text(
                    text = challenge.description,
                    fontSize = 13.sp,
                    color = Color.LightGray,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = OrangeAccent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = challenge.location.uppercase(),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("REPLY / ACCEPT", fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun CreateChallengeDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String) -> Unit
) {
    var teamName by remember { mutableStateOf("") }
    var sport by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        title = { Text("CREATE CHALLENGE", color = Color.White, fontWeight = FontWeight.Black) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple(teamName, "TEAM NAME") { v: String -> teamName = v },
                    Triple(sport, "SPORT (e.g. CRICKET)") { v: String -> sport = v },
                    Triple(location, "LOCATION") { v: String -> location = v },
                    Triple(desc, "DESCRIPTION") { v: String -> desc = v }
                ).forEach { (value, label, setter) ->
                    OutlinedTextField(
                        value = value,
                        onValueChange = setter,
                        label = { Text(label, fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangeAccent,
                            focusedLabelColor = OrangeAccent,
                            cursorColor = OrangeAccent
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (teamName.isNotBlank()) onCreate(teamName, sport, location, desc) }
            ) {
                Text("POST CHALLENGE", color = OrangeAccent, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = Color.Gray)
            }
        }
    )
}
