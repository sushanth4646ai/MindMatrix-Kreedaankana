package com.kreedaankana.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.kreedaankana.data.local.entity.PaymentEntity
import com.kreedaankana.ui.theme.*
import com.kreedaankana.viewmodel.OwnerPaymentViewModel

@Composable
fun OwnerPaymentScreen(
    viewModel: OwnerPaymentViewModel,
    onBack: () -> Unit
) {
    val payments by viewModel.payments.collectAsState()
    var showUpiDialog by remember { mutableStateOf(false) }
    var upiId by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkCharcoal, SurfaceDark, DarkCharcoal)))) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)) {
            item {
                Spacer(Modifier.statusBarsPadding())
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(SurfaceVariantDark)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Spacer(Modifier.width(16.dp)); Text("Payments", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    TextButton(onClick = { showUpiDialog = true }) { Text("+ Add UPI", color = ElectricBlue) }
                }
            }
            item { Spacer(Modifier.height(8.dp)) }

            item {
                Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Quick Info", style = MaterialTheme.typography.titleSmall, color = NeonGreen, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("Total Payments: ${payments.size}", color = TextSecondary, fontSize = 14.sp)
                        Text("Approved: ${payments.count { it.status == "APPROVED" }}", color = SuccessGreen, fontSize = 14.sp)
                        Text("Pending: ${payments.count { it.status == "PENDING" }}", color = WarningYellow, fontSize = 14.sp)
                    }
                }
            }

            if (payments.isEmpty()) {
                item { Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("\uD83D\uDCB3", fontSize = 48.sp); Spacer(Modifier.height(16.dp)); Text("No payments yet", style = MaterialTheme.typography.titleMedium, color = TextSecondary) } } }
            }

            items(payments) { payment ->
                Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(
                            when (payment.status) { "APPROVED" -> SuccessGreen.copy(alpha = 0.2f); "REJECTED" -> ErrorRed.copy(alpha = 0.2f); else -> WarningYellow.copy(alpha = 0.2f) }
                        ), contentAlignment = Alignment.Center) {
                            Text(when (payment.status) { "APPROVED" -> "\u2705"; "REJECTED" -> "\u274C"; else -> "\u23F3" }, fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(payment.customerName, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            Text("${payment.method} \u2022 ${payment.description}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                        Text("\u20B9${payment.amount.toInt()}", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    if (payment.status == "PENDING") {
                        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = { viewModel.rejectPayment(payment.paymentId) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Reject", color = ErrorRed) }
                            Button(onClick = { viewModel.approvePayment(payment.paymentId) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)) { Text("Approve", color = DarkCharcoal) }
                        }
                    }
                }
            }
        }

        if (showUpiDialog) {
            AlertDialog(onDismissRequest = { showUpiDialog = false }, containerColor = SurfaceDark,
                title = { Text("Add UPI ID", color = TextPrimary) },
                text = {
                    OutlinedTextField(value = upiId, onValueChange = { upiId = it }, label = { Text("UPI ID") }, placeholder = { Text("owner@upi") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue))
                },
                confirmButton = { Button(onClick = { viewModel.saveUpi(upiId); showUpiDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)) { Text("Save", color = DarkCharcoal) } },
                dismissButton = { TextButton(onClick = { showUpiDialog = false }) { Text("Cancel", color = TextMuted) } }
            )
        }
    }
}
