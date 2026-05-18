package com.kreedaankana.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.kreedaankana.viewmodel.OwnerAnalyticsViewModel

@Composable
fun OwnerAnalyticsScreen(
    viewModel: OwnerAnalyticsViewModel,
    onBack: () -> Unit
) {
    val revenue by viewModel.revenue.collectAsState()
    val weeklyRevenue by viewModel.weeklyRevenue.collectAsState()
    val monthlyRevenue by viewModel.monthlyRevenue.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkCharcoal, SurfaceDark, DarkCharcoal)))) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 80.dp)) {
            Spacer(Modifier.statusBarsPadding())
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(SurfaceVariantDark)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(Modifier.width(16.dp)); Text("Analytics", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("\uD83D\uDCB0", "Today", "\u20B9${String.format("%.0f", revenue)}", NeonGreen, Modifier.weight(1f))
                StatCard("\uD83D\uDCC5", "This Week", "\u20B9${String.format("%.0f", weeklyRevenue)}", ElectricBlue, Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("\uD83D\uDCC8", "This Month", "\u20B9${String.format("%.0f", monthlyRevenue)}", OrangeAccent, Modifier.weight(1f))
                StatCard("\uD83C\uDFC6", "Avg Daily", "\u20B9${String.format("%.0f", monthlyRevenue / 30)}", Purple, Modifier.weight(1f))
            }
            Spacer(Modifier.height(24.dp))

            Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Revenue Overview", style = MaterialTheme.typography.titleSmall, color = NeonGreen, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        RevenueBar("Mon", 0.4f); RevenueBar("Tue", 0.6f); RevenueBar("Wed", 0.5f); RevenueBar("Thu", 0.7f); RevenueBar("Fri", 0.8f); RevenueBar("Sat", 1.0f); RevenueBar("Sun", 0.3f)
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatCard(icon: String, label: String, value: String, accent: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
        Column(Modifier.padding(16.dp)) {
            Text(icon, fontSize = 24.sp)
            Spacer(Modifier.height(8.dp))
            Text(label, color = TextMuted, fontSize = 12.sp)
            Text(value, color = accent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RevenueBar(day: String, heightFraction: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.width(28.dp).height((heightFraction * 80).dp).clip(RoundedCornerShape(8.dp)).background(NeonGreen.copy(alpha = 0.3f + heightFraction * 0.7f)))
        Spacer(Modifier.height(4.dp)); Text(day, color = TextMuted, fontSize = 10.sp)
    }
}
