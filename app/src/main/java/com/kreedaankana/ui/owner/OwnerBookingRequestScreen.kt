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
import com.kreedaankana.data.local.entity.BookingEntity
import com.kreedaankana.ui.theme.*
import com.kreedaankana.viewmodel.OwnerBookingRequestViewModel

@Composable
fun OwnerBookingRequestScreen(
    viewModel: OwnerBookingRequestViewModel,
    onBack: () -> Unit
) {
    val bookings by viewModel.bookings.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DarkCharcoal, SurfaceDark, DarkCharcoal)))) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)) {
            item {
                Spacer(Modifier.statusBarsPadding())
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(SurfaceVariantDark)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Spacer(Modifier.width(16.dp))
                    Text("Booking Requests", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
            item { Spacer(Modifier.height(8.dp)) }

            if (bookings.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("\uD83C\uDFAB", fontSize = 48.sp)
                            Spacer(Modifier.height(16.dp))
                            Text("No booking requests", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            Text("New bookings will appear here", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                        }
                    }
                }
            }

            items(bookings) { booking -> BookingRequestCard(booking = booking, viewModel = viewModel) }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun BookingRequestCard(booking: BookingEntity, viewModel: OwnerBookingRequestViewModel) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(ElectricBlue.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Text("\uD83C\uDFAB", fontSize = 20.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(booking.customerName, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text(booking.teamName, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                Text("\u20B9${booking.amount.toInt()}", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Text("\uD83C\uDFDF\uFE0F ${booking.groundName}", color = TextSecondary, fontSize = 13.sp)
                Text("\uD83D\uDCC5 ${booking.bookingDate}", color = TextSecondary, fontSize = 13.sp)
                Text("\u23F0 ${booking.slotTime}", color = TextSecondary, fontSize = 13.sp)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { viewModel.rejectBooking(booking.bookingId) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)) { Text("Reject", color = ErrorRed) }
                Button(onClick = { viewModel.approveBooking(booking.bookingId) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)) { Text("Approve", color = DarkCharcoal) }
            }
        }
    }
}
