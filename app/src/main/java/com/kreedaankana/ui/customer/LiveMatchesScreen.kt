package com.kreedaankana.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreedaankana.ui.components.KreedaCard
import com.kreedaankana.ui.theme.OrangeAccent

@Composable
fun LiveMatchesScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = Color(0xFF0F0F0F)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.Red, androidx.compose.foundation.shape.CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LIVE SCORE BROADCASTING",
                    fontSize = 12.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            
            Text(
                text = "KREEDA LIVE",
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.Black
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Active Match Card (Design from Screenshot 3)
            LiveMatchCard(
                tournament = "CRICKET CHAMPIONSHIP",
                team1 = "DR",
                team1Name = "DRAGONS",
                score1 = "42",
                team2 = "WA",
                team2Name = "WARRIORS",
                score2 = "38",
                time = "14:20"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "UPCOMING MATCHES",
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(listOf("ST" to "STORM", "HU" to "HUNTERS")) { (abbr, name) ->
                    UpcomingMatchItem(abbr, name)
                }
            }
        }
    }
}

@Composable
fun LiveMatchCard(
    tournament: String,
    team1: String,
    team1Name: String,
    score1: String,
    team2: String,
    team2Name: String,
    score2: String,
    time: String
) {
    KreedaCard {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = tournament,
                fontSize = 10.sp,
                color = OrangeAccent,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team 1
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .border(1.dp, Color(0xFF333333), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(team1, fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Black)
                    }
                    Text(team1Name, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                }
                
                // Score
                Text(score1, fontSize = 48.sp, color = Color.White, fontWeight = FontWeight.Black)
                Text(":", fontSize = 48.sp, color = OrangeAccent, fontWeight = FontWeight.Black)
                Text(score2, fontSize = 48.sp, color = Color.White, fontWeight = FontWeight.Black)
                
                // Team 2
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .border(1.dp, Color(0xFF333333), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(team2, fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Black)
                    }
                    Text(team2Name, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(time, fontSize = 14.sp, color = OrangeAccent, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SQUAD", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { })
            }
        }
    }
}

@Composable
fun UpcomingMatchItem(abbr: String, name: String) {
    KreedaCard {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(Color(0xFF1A1A1A), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(abbr, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(name, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Text("06:00 PM", color = OrangeAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
