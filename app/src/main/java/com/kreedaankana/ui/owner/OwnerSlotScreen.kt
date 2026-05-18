package com.kreedaankana.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.kreedaankana.data.local.entity.SlotBlockEntity
import com.kreedaankana.ui.theme.*
import com.kreedaankana.viewmodel.OwnerSlotViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerSlotScreen(
    viewModel: OwnerSlotViewModel,
    onBack: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
    var selectedDate by remember { mutableStateOf(dateFormat.format(Date())) }
    var showBlockDialog by remember { mutableStateOf(false) }
    var blockTimeSlot by remember { mutableStateOf("") }
    var blockReason by remember { mutableStateOf("") }
    var isMaintenance by remember { mutableStateOf(false) }
    val blockedSlots by viewModel.getBlockedSlots(selectedDate).collectAsState(initial = emptyList())

    val timeSlots = listOf("6 AM-8 AM", "8 AM-10 AM", "10 AM-12 PM", "12 PM-2 PM", "2 PM-4 PM", "4 PM-6 PM", "6 PM-8 PM", "8 PM-10 PM")

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkCharcoal, SurfaceDark, DarkCharcoal)))) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 100.dp)) {
            Spacer(Modifier.statusBarsPadding())
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(SurfaceVariantDark)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(Modifier.width(16.dp))
                Text("Slot Management", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))

            Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Select Date", style = MaterialTheme.typography.titleSmall, color = ElectricBlue, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    var showDatePicker by remember { mutableStateOf(false) }
                    OutlinedTextField(value = try { displayFormat.format(dateFormat.parse(selectedDate)!!) } catch (_: Exception) { selectedDate },
                        onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue), trailingIcon = { Text("\uD83D\uDCC5", fontSize = 20.sp) },
                        label = { Text("Date") })
                }
            }
            Spacer(Modifier.height(16.dp))

            Text("Time Slots", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(8.dp))

            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                timeSlots.forEach { slot ->
                    val isBlocked = blockedSlots.any { it.timeSlot == slot }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isBlocked) ErrorRed.copy(alpha = 0.15f) else SurfaceDark),
                        onClick = {
                            if (isBlocked) {
                                blockedSlots.find { it.timeSlot == slot }?.let { viewModel.unblockSlot(it.id) }
                            } else {
                                blockTimeSlot = slot; blockReason = ""; isMaintenance = false; showBlockDialog = true
                            }
                        }
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(if (isBlocked) ErrorRed.copy(alpha = 0.2f) else SuccessGreen.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                                Text(if (isBlocked) "\u26D4" else "\u2705", fontSize = 18.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(slot, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                Text(if (isBlocked) "Blocked" else "Available", color = if (isBlocked) ErrorRed else SuccessGreen, style = MaterialTheme.typography.bodySmall)
                            }
                            Text(if (isBlocked) "Tap to unblock" else "Tap to block", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }

        if (showBlockDialog) {
            AlertDialog(
                onDismissRequest = { showBlockDialog = false },
                containerColor = SurfaceDark,
                title = { Text("Block Slot: $blockTimeSlot", color = TextPrimary) },
                text = {
                    Column {
                        OutlinedTextField(value = blockReason, onValueChange = { blockReason = it }, label = { Text("Reason") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue))
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isMaintenance, onCheckedChange = { isMaintenance = it }, colors = CheckboxDefaults.colors(checkedColor = ElectricBlue))
                            Text("Maintenance mode", color = TextSecondary)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.blockSlot(selectedDate, blockTimeSlot, blockReason, isMaintenance); showBlockDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)) {
                        Text("Block Slot", color = DarkCharcoal)
                    }
                },
                dismissButton = { TextButton(onClick = { showBlockDialog = false }) { Text("Cancel", color = TextMuted) } }
            )
        }
    }
}
