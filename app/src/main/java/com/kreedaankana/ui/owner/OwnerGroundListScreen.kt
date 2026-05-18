package com.kreedaankana.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreedaankana.data.local.entity.OwnerGroundEntity
import com.kreedaankana.ui.theme.*
import com.kreedaankana.viewmodel.OwnerGroundViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OwnerGroundListScreen(
    viewModel: OwnerGroundViewModel,
    onBack: () -> Unit,
    onAddGround: () -> Unit,
    onEditGround: (String) -> Unit
) {
    val grounds by viewModel.grounds.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkCharcoal, SurfaceDark, DarkCharcoal)))) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)) {
            item {
                Spacer(Modifier.statusBarsPadding())
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp).clip(CircleShape).background(SurfaceVariantDark)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Spacer(Modifier.width(16.dp))
                    Text("My Grounds", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    IconButton(onClick = onAddGround, modifier = Modifier.size(48.dp).clip(CircleShape).background(ElectricBlue.copy(alpha = 0.2f))) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = ElectricBlue)
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }

            if (grounds.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("\uD83C\uDFDF\uFE0F", fontSize = 48.sp)
                            Spacer(Modifier.height(16.dp))
                            Text("No grounds yet", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Text("Tap + to add your first ground", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                        }
                    }
                }
            }

            items(grounds) { ground ->
                GroundCard(ground = ground, onClick = { onEditGround(ground.groundId) })
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun GroundCard(ground: OwnerGroundEntity, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(ElectricBlue.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                Text("\uD83C\uDFDF\uFE0F", fontSize = 24.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(ground.name, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(ground.village, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                Spacer(Modifier.height(4.dp))
                Text("\u20B9${ground.pricePerSlot}/slot \u2022 ${ground.sports}", style = MaterialTheme.typography.labelSmall, color = ElectricBlue)
            }
            Text(if (ground.isActive) "Active" else "Inactive", color = if (ground.isActive) SuccessGreen else TextMuted, fontSize = 12.sp)
        }
    }
}
