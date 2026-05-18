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
import com.kreedaankana.viewmodel.OwnerGroundViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OwnerGroundFormScreen(
    groundId: String? = null,
    viewModel: OwnerGroundViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val isSaving by viewModel.isSaving.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()

    var name by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var sports by remember { mutableStateOf("Football") }
    var openTime by remember { mutableStateOf("06:00") }
    var closeTime by remember { mutableStateOf("22:00") }
    var slotDuration by remember { mutableStateOf("60") }
    var price by remember { mutableStateOf("") }
    var amenities by remember { mutableStateOf("") }
    var hasParking by remember { mutableStateOf(false) }
    var hasLighting by remember { mutableStateOf(false) }
    var hasWashroom by remember { mutableStateOf(false) }
    var hasWater by remember { mutableStateOf(false) }
    var seating by remember { mutableStateOf("") }
    var rules by remember { mutableStateOf("") }

    LaunchedEffect(success) { if (success) { viewModel.clearStates(); onSaved() } }

    val sportsList = listOf("Football", "Cricket", "Basketball", "Badminton", "Tennis", "Volleyball")
    val fieldColors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue, focusedLabelColor = ElectricBlue)

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkCharcoal, SurfaceDark, DarkCharcoal)))) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 100.dp)) {
            Spacer(Modifier.statusBarsPadding())
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(SurfaceVariantDark)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(Modifier.width(16.dp))
                Text(if (groundId == null) "Add Ground" else "Edit Ground", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))

            Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Basic Info", style = MaterialTheme.typography.titleSmall, color = ElectricBlue, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Ground Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors, singleLine = true)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = village, onValueChange = { village = it }, label = { Text("Village / Location") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors, singleLine = true)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Full Address") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors)
                }
            }
            Spacer(Modifier.height(16.dp))

            Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Sports & Pricing", style = MaterialTheme.typography.titleSmall, color = ElectricBlue, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                        OutlinedTextField(value = sports, onValueChange = {}, readOnly = true, label = { Text("Sport") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp), colors = fieldColors)
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            sportsList.forEach {                         DropdownMenuItem(text = { Text(it) }, onClick = { sports = it; expanded = false }) }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = openTime, onValueChange = { openTime = it }, label = { Text("Opens") }, placeholder = { Text("06:00") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = fieldColors, singleLine = true)
                        OutlinedTextField(value = closeTime, onValueChange = { closeTime = it }, label = { Text("Closes") }, placeholder = { Text("22:00") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = fieldColors, singleLine = true)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = slotDuration, onValueChange = { slotDuration = it }, label = { Text("Slot (min)") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = fieldColors, singleLine = true)
                        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price (\u20B9)") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = fieldColors, singleLine = true)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Amenities", style = MaterialTheme.typography.titleSmall, color = ElectricBlue, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = hasParking, onClick = { hasParking = !hasParking }, label = { Text("Parking") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricBlue.copy(alpha = 0.3f)))
                        FilterChip(selected = hasLighting, onClick = { hasLighting = !hasLighting }, label = { Text("Lighting") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricBlue.copy(alpha = 0.3f)))
                        FilterChip(selected = hasWashroom, onClick = { hasWashroom = !hasWashroom }, label = { Text("Washroom") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricBlue.copy(alpha = 0.3f)))
                        FilterChip(selected = hasWater, onClick = { hasWater = !hasWater }, label = { Text("Drinking Water") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricBlue.copy(alpha = 0.3f)))
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = amenities, onValueChange = { amenities = it }, label = { Text("Other Amenities") }, placeholder = { Text("e.g. WiFi, Cafeteria, Changing Room") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = seating, onValueChange = { seating = it }, label = { Text("Seating Capacity") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors, singleLine = true)
                }
            }
            Spacer(Modifier.height(16.dp))

            Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Rules & Instructions", style = MaterialTheme.typography.titleSmall, color = ElectricBlue, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(value = rules, onValueChange = { rules = it }, label = { Text("Rules") }, placeholder = { Text("e.g. No food inside, wear proper shoes...") }, modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(12.dp), colors = fieldColors)
                }
            }
            Spacer(Modifier.height(24.dp))

            error?.let {
                Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 12.dp), colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.2f))) {
                    Text(it, color = ErrorRed, modifier = Modifier.padding(16.dp))
                }
            }

            Button(
                onClick = { viewModel.saveGround(name, village, address, 0.0, 0.0, sports, openTime, closeTime, slotDuration.toIntOrNull() ?: 60, price.toDoubleOrNull() ?: 0.0, amenities, hasParking, hasLighting, hasWashroom, hasWater, seating.toIntOrNull() ?: 0, rules) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                enabled = !isSaving
            ) {
                if (isSaving) CircularProgressIndicator(color = DarkCharcoal, modifier = Modifier.size(24.dp))
                else Text("Save Ground", color = DarkCharcoal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
