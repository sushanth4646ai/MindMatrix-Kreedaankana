package com.kreedaankana.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreedaankana.ui.theme.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle

data class CommunityPost(
    val id: String,
    val author: String,
    val authorAvatar: String,
    val sport: String,
    val title: String,
    val content: String,
    val likes: Int,
    val comments: Int,
    val time: String,
    val tags: List<String>
)

data class CommunityEvent(
    val id: String,
    val title: String,
    val sport: String,
    val location: String,
    val date: String,
    val participants: Int,
    val maxParticipants: Int,
    val prize: String,
    val isJoined: Boolean
)

@Composable
fun CommunityScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val posts = remember {
        listOf(
            CommunityPost("1", "Rahul Sharma", "RS", "Football", "Weekend League Looking for Players", " organizing a 7-a-side football match at Decathlon Arena this Sunday. Looking for 2 more players. All skill levels welcome!", 24, 8, "2h ago", listOf("Football", "Beginner Friendly", "HSR")),
            CommunityPost("2", "Priya Patel", "PP", "Badminton", "Coaching Session - Beginners Welcome", " hosting a free badminton coaching session for beginners this Saturday at Champions Arena. Learn basic techniques!", 45, 12, "4h ago", listOf("Badminton", "Coaching", "Free")),
            CommunityPost("3", "Amit Singh", "AS", "Cricket", "Tournament Team Formation", " forming a team for the Inter-City Cricket Championship. Need 5 more players. Contact if interested!", 18, 22, "1d ago", listOf("Cricket", "Tournament", "Competitive")),
            CommunityPost("4", "Sneha Gupta", "SG", "Tennis", "Tennis Partner Wanted", " Looking for a tennis partner to practice doubles. Available mornings on weekends.", 12, 5, "2d ago", listOf("Tennis", "Partner Wanted")),
            CommunityPost("5", "Vikram Rao", "VR", "Football", "Sunday Morning Game - Jayanagar", "Friendly 5-a-side football every Sunday at Jayanagar turf. Join us if you're free!", 31, 9, "3d ago", listOf("Football", "Sunday", "Jayanagar"))
        )
    }

    val events = remember {
        listOf(
            CommunityEvent("1", "Weekend Premier League", "Football", "Decathlon Arena, HSR", "Dec 20, 2024", 12, 16, "₹50,000", false),
            CommunityEvent("2", "Badminton Open Championship", "Badminton", "Champions Arena, MG Road", "Dec 22, 2024", 28, 32, "₹25,000", false),
            CommunityEvent("3", "Inter-City Cricket Cup", "Cricket", "Anubhava Ground", "Jan 5, 2025", 6, 8, "₹1,00,000", true),
            CommunityEvent("4", "Futsal Championship", "Football", "Strikers Arena", "Jan 10, 2025", 18, 20, "₹30,000", false),
            CommunityEvent("5", "Tennis Doubles League", "Tennis", "Royal Tennis Center", "Jan 15, 2025", 10, 16, "₹15,000", false)
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkCharcoal, SurfaceDark, DarkCharcoal)))) {
        Column(modifier = Modifier.fillMaxSize()) {
            CommunityHeader(onBack = onBack)
            
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceDark,
                contentColor = NeonGreen,
                indicator = { }
            ) {
                TabItem("Feed", selectedTab == 0) { selectedTab = 0 }
                TabItem("Events", selectedTab == 1) { selectedTab = 1 }
                TabItem("Find Players", selectedTab == 2) { selectedTab = 2 }
            }

            when (selectedTab) {
                0 -> FeedTab(posts = posts)
                1 -> EventsTab(events = events)
                2 -> FindPlayersTab()
            }
        }
    }
}

@Composable
private fun TabItem(title: String, isSelected: Boolean, onSelect: () -> Unit) {
    TextButton(onClick = onSelect) {
        Text(
            text = title,
            color = if (isSelected) NeonGreen else TextMuted,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun CommunityHeader(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp).clip(CircleShape).background(SurfaceVariantDark)
            ) {
                Text("←", color = TextPrimary, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Community", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text("Connect with players near you", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun FeedTab(posts: List<CommunityPost>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CreatePostCard()
        }
        items(posts) { post ->
            PostCard(post = post)
        }
    }
}

@Composable
private fun CreatePostCard() {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(NeonGreen.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                Text("RS", style = MaterialTheme.typography.labelMedium, color = NeonGreen, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(SurfaceDark).padding(16.dp)) {
                Text("Share what's on your mind...", color = TextMuted, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun PostCard(post: CommunityPost) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Brush.linearGradient(listOf(ElectricBlue, NeonGreen))), contentAlignment = Alignment.Center) {
                    Text(post.authorAvatar, style = MaterialTheme.typography.labelLarge, color = DarkCharcoal, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(post.author, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(NeonGreen.copy(alpha = 0.2f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text(when (post.sport) { "Football" -> "⚽"; "Cricket" -> "🏏"; "Badminton" -> "🏸"; "Tennis" -> "🎾"; else -> "🏐" }, fontSize = 12.sp)
                        }
                    }
                    Text(post.time, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(post.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(post.content, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(post.tags) { tag ->
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(GlassWhite10).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("#$tag", style = MaterialTheme.typography.labelSmall, color = NeonGreen)
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = GlassWhite10)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("❤️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${post.likes}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💬", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${post.comments} comments", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun EventsTab(events: List<CommunityEvent>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(events) { event ->
            EventCard(event = event)
        }
    }
}

@Composable
private fun EventCard(event: CommunityEvent) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(NeonGreen.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Text(when (event.sport) { "Football" -> "⚽"; "Cricket" -> "🏏"; "Badminton" -> "🏸"; "Tennis" -> "🎾"; else -> "🏐" }, fontSize = 28.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(event.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row { Text("📍", fontSize = 14.sp); Spacer(modifier = Modifier.width(4.dp)); Text(event.location, style = MaterialTheme.typography.bodySmall, color = TextSecondary) }
                    Row { Text("📅", fontSize = 14.sp); Spacer(modifier = Modifier.width(4.dp)); Text(event.date, style = MaterialTheme.typography.bodySmall, color = TextSecondary) }
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(WarningYellow.copy(alpha = 0.2f)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("🏆 ${event.prize}", style = MaterialTheme.typography.labelSmall, color = WarningYellow, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Prize Pool: ${event.prize}", style = MaterialTheme.typography.bodyMedium, color = WarningYellow, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { event.participants.toFloat() / event.maxParticipants.toFloat() },
                        modifier = Modifier.width(120.dp).height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = NeonGreen,
                        trackColor = GlassWhite10
                    )
                    Text("${event.participants}/${event.maxParticipants} joined", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
                if (event.isJoined) {
                    OutlinedButton(shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = SuccessGreen), onClick = {}) {
                        Text("✓ Joined")
                    }
                } else {
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                    ) {
                        Text("Join Now", color = DarkCharcoal, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun FindPlayersTab() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = com.kreedaankana.data.local.AppDatabase.getDatabase(context)
    val teams by database.teamDao().getAllTeams().collectAsStateWithLifecycle(initialValue = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Teams Looking for Players", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
        }
        items(teams) { team ->
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(NeonGreen.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                        Text(when (team.sport) { "Football" -> "⚽"; "Cricket" -> "🏏"; "Badminton" -> "🏸"; else -> "🏐" }, fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(team.teamName, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("${team.players.size} players • ${team.wins} wins", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text("Captain: ${team.captainName}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                    Button(onClick = { }, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)) {
                        Text("Request", color = DarkCharcoal, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Open Slots by Sport", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            SportPlayerCard("Football", "5-a-side", 8, 3, "⚽")
            SportPlayerCard("Cricket", "11-a-side", 11, 5, "🏏")
            SportPlayerCard("Badminton", "Doubles", 2, 0, "🏸")
            SportPlayerCard("Basketball", "5v5", 5, 2, "🏀")
        }
    }
}

@Composable
private fun SportPlayerCard(sport: String, format: String, total: Int, open: Int, icon: String) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark), modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("$sport ($format)", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Text("$open spots open out of $total", style = MaterialTheme.typography.bodySmall, color = if (open > 0) SuccessGreen else TextMuted)
            }
            if (open > 0) {
                Button(onClick = { }, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = NeonGreen), modifier = Modifier.height(36.dp)) {
                    Text("Find", color = DarkCharcoal, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
            } else {
                OutlinedButton(shape = RoundedCornerShape(12.dp), modifier = Modifier.height(36.dp), onClick = {}) {
                    Text("Full", color = TextMuted, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}