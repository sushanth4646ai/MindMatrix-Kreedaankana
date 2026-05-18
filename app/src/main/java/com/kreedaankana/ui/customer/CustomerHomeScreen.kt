package com.kreedaankana.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
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
import com.google.firebase.firestore.FirebaseFirestore
import com.kreedaankana.data.local.dao.GroundDao
import com.kreedaankana.data.local.entity.GroundEntity
import com.kreedaankana.data.repository.GroundSyncRepository
import com.kreedaankana.ui.components.*
import com.kreedaankana.ui.theme.*

@Composable
fun CustomerHomeScreen(
    onNavigateToBooking: () -> Unit,
    onNavigateToQR: () -> Unit,
    onNavigateToTournaments: () -> Unit,
    onNavigateToGrounds: () -> Unit,
    groundDao: GroundDao? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var grounds by remember { mutableStateOf<List<GroundEntity>>(emptyList()) }
    val syncRepo = remember { groundDao?.let { GroundSyncRepository(it, FirebaseFirestore.getInstance()) } }

    LaunchedEffect(groundDao) {
        syncRepo?.syncFromFirestore()
        groundDao?.getAllGrounds()?.collect { groundList ->
            grounds = groundList
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCharcoal),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Header (Village Hub / Kreeda-Ankana)
        item {
            HomeHeader()
        }

        // Search Bar
        item {
            PaddingValues(horizontal = 20.dp).let {
                KreedaSearchBar(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }
        }

        // Active Hub & Rank Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoCard(
                    title = "ACTIVE HUB",
                    subtitle = "MY PASSES",
                    value = "04",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToQR
                )
                InfoCard(
                    title = "RANK",
                    subtitle = "#12 HUB",
                    value = "01",
                    modifier = Modifier.weight(1f),
                    color = ElectricBlue
                )
            }
        }

        // Live Broadcasting Section
        item {
            KreedaSectionHeader(
                title = "Live Broadcasting",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sampleLiveMatches) { match ->
                    LiveBroadcastingCard(match)
                }
            }
        }

        // Sports Categories
        item {
            KreedaSectionHeader(
                title = "Sports Hub",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val sports = listOf("CRICKET", "FOOTBALL", "BADMINTON", "VOLLEYBALL", "TENNIS")
                items(sports) { sport ->
                    SportCategoryChip(sport)
                }
            }
        }

        // Popular Grounds
        item {
            KreedaSectionHeader(
                title = "Popular Grounds",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
        
        items(grounds.take(5)) { ground ->
            GroundListItem(ground, onNavigateToBooking)
        }
    }
}

@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "VILLAGE HUB",
                style = SectionLabel,
                color = TextSecondary
            )
            Text(
                text = "KREEDA-ANKANA",
                style = SportyHeader,
                color = TextPrimary
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, CardBorder, CircleShape)
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            KreedaAvatar(imageUrl = null, size = 48)
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    subtitle: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color = OrangeAccent,
    onClick: () -> Unit = {}
) {
    KreedaCard(
        modifier = modifier.height(120.dp),
        onClick = onClick
    ) {
        Text(text = title, style = SectionLabel, color = TextSecondary)
        Text(text = subtitle, style = Typography.labelMedium, color = TextMuted)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = Typography.displaySmall,
            color = color,
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun LiveBroadcastingCard(match: LiveMatch) {
    KreedaCard(
        modifier = Modifier.width(280.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ErrorRed)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "LIVE", style = Typography.labelSmall, color = ErrorRed, fontWeight = FontWeight.Bold)
            }
            Text(text = match.time, style = Typography.labelSmall, color = TextMuted)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamScore(name = match.teamA, score = match.scoreA)
            Text(text = "VS", style = Typography.titleMedium, color = TextMuted, fontWeight = FontWeight.Bold)
            TeamScore(name = match.teamB, score = match.scoreB)
        }
    }
}

@Composable
fun TeamScore(name: String, score: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = score.toString(),
            style = Typography.displaySmall,
            color = TextPrimary,
            fontWeight = FontWeight.Black
        )
        Text(
            text = name.take(3).uppercase(),
            style = Typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
fun SportCategoryChip(name: String) {
    Box(
        modifier = Modifier
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .background(SurfaceDark, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = Typography.labelMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GroundListItem(ground: GroundEntity, onClick: () -> Unit) {
    KreedaCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceVariantDark)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = ground.name.uppercase(), style = Typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SportTag(ground.sport)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "⭐ ${ground.rating}", style = Typography.labelSmall, color = WarningYellow)
                }
            }
            Text(text = "₹${ground.pricePerHour}", style = Typography.titleMedium, color = OrangeAccent, fontWeight = FontWeight.Black)
        }
    }
}

private val sampleLiveMatches = listOf(
    LiveMatch("1", "DRAGONS", "WARRIORS", "", "", 42, 38, "15:20", "LIVE"),
    LiveMatch("2", "STRIKERS", "TITANS", "", "", 2, 1, "08:45", "LIVE")
)

data class LiveMatch(
    val id: String,
    val teamA: String,
    val teamB: String,
    val teamALogo: String,
    val teamBLogo: String,
    val scoreA: Int,
    val scoreB: Int,
    val time: String,
    val status: String
)

