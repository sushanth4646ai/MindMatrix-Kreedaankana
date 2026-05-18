package com.kreedaankana.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreedaankana.ui.components.KreedaCard
import com.kreedaankana.ui.theme.OrangeAccent

@Composable
fun CustomerProfileScreen(
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFF0F0F0F)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item { ProfileHeader() }
            item { StatsRow() }
            item { MenuSection(onEditProfile, onLogout) }
        }
    }
}

@Composable
private fun ProfileHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(2.dp, OrangeAccent, CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color(0xFF1A1A1A)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "S",
                    fontSize = 40.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
            }
            
            // Verified Badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 10.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF2E7D32))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("VERIFIED", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Black)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "SUSHANTH R",
            fontSize = 24.sp,
            color = Color.White,
            fontWeight = FontWeight.Black
        )
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .background(OrangeAccent, RoundedCornerShape(2.dp))
                    .padding(horizontal = 6.dp, vertical = 1.dp)
            ) {
                Text("PLAYER", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Black)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("SATTUR, DHARWAD", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StatsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatBox(modifier = Modifier.weight(1f), count = "24", label = "GROUND ENTRIES")
        StatBox(modifier = Modifier.weight(1f), count = "12", label = "MATCH WINS")
    }
}

@Composable
private fun StatBox(modifier: Modifier, count: String, label: String) {
    KreedaCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count, fontSize = 24.sp, color = OrangeAccent, fontWeight = FontWeight.Black)
            Text(text = label, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MenuSection(onEditProfile: () -> Unit, onLogout: () -> Unit) {
    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MenuItem(icon = Icons.Default.Person, title = "EDIT PROFILE", subtitle = "Update Name, Village & Photo", onClick = onEditProfile)
        MenuItem(icon = Icons.Default.List, title = "BOOKING HISTORY", subtitle = "Manage Past & Upcoming Slots") { }
        MenuItem(icon = Icons.Default.CheckCircle, title = "VERIFICATION", subtitle = "Identity & Team Verification") { }
        MenuItem(icon = Icons.Default.Star, title = "MY STATS", subtitle = "Match History & Rankings") { }
        MenuItem(icon = Icons.Default.Lock, title = "PRIVACY", subtitle = "Data & Permission Settings") { }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("LOGOUT", color = Color.Red, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun MenuItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    KreedaCard(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF1A1A1A), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = OrangeAccent, modifier = Modifier.size(20.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Black)
                Text(text = subtitle, fontSize = 11.sp, color = Color.Gray)
            }
            
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.DarkGray)
        }
    }
}