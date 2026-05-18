package com.kreedaankana.ui.owner

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreedaankana.ui.theme.*
import com.kreedaankana.viewmodel.OwnerDashboardState
import com.kreedaankana.viewmodel.OwnerDashboardViewModel

@Composable
fun OwnerDashboardScreen(
    viewModel: OwnerDashboardViewModel,
    onNavigateToQRScanner: () -> Unit,
    onNavigateToGrounds: () -> Unit,
    onNavigateToSlots: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToTournaments: () -> Unit,
    onNavigateToLiveScores: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToPayments: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkCharcoal, SurfaceDark, DarkCharcoal)))) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(bottom = 100.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
            item { OwnerHeader(onQR = onNavigateToQRScanner) }
            item { Spacer(Modifier.height(16.dp)) }
            item { StatsRow(state = state) }
            item { Spacer(Modifier.height(24.dp)) }
            item { QuickActions(onNavigateToGrounds, onNavigateToSlots, onNavigateToBookings, onNavigateToTournaments, onNavigateToLiveScores, onNavigateToAnalytics, onNavigateToPayments, onNavigateToQRScanner) }
            item { Spacer(Modifier.height(24.dp)) }
            item { RecentActivitySection(state = state) }
        }
    }
}

@Composable
private fun OwnerHeader(onQR: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp).statusBarsPadding(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text("Owner Dashboard", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
            Text("Ground Manager", style = MaterialTheme.typography.bodySmall, color = TextMuted)
        }
        Box(Modifier.size(56.dp).clip(CircleShape).background(Brush.linearGradient(listOf(NeonGreen, ElectricBlue))), contentAlignment = Alignment.Center) {
            Text("KA", color = DarkCharcoal, fontWeight = FontWeight.Black, fontSize = 18.sp)
        }
    }
}

@Composable
private fun StatsRow(state: OwnerDashboardState) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard("\uD83D\uDCB0", "\u20B9${String.format("%.0f", state.todayRevenue)}", "Today", NeonGreen, Modifier.weight(1f))
        StatCard("\uD83C\uDFAB", "${state.todayBookings}", "Bookings", ElectricBlue, Modifier.weight(1f))
        StatCard("\uD83C\uDFDF\uFE0F", "${state.activeGrounds}", "Grounds", OrangeAccent, Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(icon: String, value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 20.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(label, color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun QuickActions(
    onGrounds: () -> Unit, onSlots: () -> Unit, onBookings: () -> Unit,
    onTournaments: () -> Unit, onLive: () -> Unit, onAnalytics: () -> Unit, onPayments: () -> Unit,
    onQRScanner: () -> Unit = {}
) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text("Quick Actions", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionCard("\uD83C\uDFDF\uFE0F", "Grounds", onGrounds, GradientBlue, Modifier.weight(1f))
            ActionCard("\uD83D\uDCC5", "Slots", onSlots, GradientGreen, Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionCard("\uD83C\uDFAB", "Bookings", onBookings, GradientOrange, Modifier.weight(1f))
            ActionCard("\uD83C\uDFC6", "Tournaments", onTournaments, GradientPurple, Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionCard("\uD83C\uDFC0", "Live Scores", onLive, GradientGreen, Modifier.weight(1f))
            ActionCard("\uD83D\uDCCA", "Analytics", onAnalytics, GradientBlue, Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionCard("\uD83D\uDCB3", "Payments", onPayments, GradientOrange, Modifier.weight(1f))
            ActionCard("\uD83D\uDD0D", "QR Scan", onQRScanner, GradientPurple, Modifier.weight(1f))
        }
    }
}

@Composable
private fun ActionCard(icon: String, title: String, onClick: () -> Unit, gradient: Brush, modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "action")
    val scale by infinite.animateFloat(initialValue = 1f, targetValue = 1.03f, animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse), label = "scale")
    Card(modifier = modifier.scale(scale).clickable(onClick = onClick), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
        Box(Modifier.fillMaxWidth().background(SurfaceVariantDark, RoundedCornerShape(20.dp)).padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 22.sp); Spacer(Modifier.width(10.dp)); Text(title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun RecentActivitySection(state: OwnerDashboardState) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        Text("Recent Activity", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        if (state.recentBookings.isEmpty()) {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { Text("No recent activity", color = TextMuted) }
            }
        } else {
            state.recentBookings.take(3).forEach { booking ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("\uD83C\uDFAB", fontSize = 20.sp); Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(booking.customerName, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("${booking.groundName} - ${booking.slotTime}", color = TextMuted, fontSize = 12.sp)
                        }
                        Text("\u20B9${booking.amount.toInt()}", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}


