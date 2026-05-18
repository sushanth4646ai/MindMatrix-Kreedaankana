package com.kreedaankana.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.kreedaankana.data.local.entity.BookingEntity
import com.kreedaankana.ui.theme.*
import com.kreedaankana.ui.components.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BookingHistoryScreen(
    onBack: () -> Unit,
    onViewPass: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as com.kreedaankana.KreedaApplication
    val database = application.database
    val bookings by database.bookingDao().getAllBookings().collectAsStateWithLifecycle(initialValue = emptyList())
    
    // We can use the ViewModel here if needed, but for simplicity we'll just use the Repository
    val invoiceRepository = remember { 
        com.kreedaankana.data.repository.InvoiceRepository(
            database.invoiceDao(), 
            com.google.firebase.storage.FirebaseStorage.getInstance()
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F0F))) {
        Column(modifier = Modifier.fillMaxSize()) {
            BookingHistoryHeader(onBack = onBack)

            if (bookings.isEmpty()) {
                EmptyBookingsState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(bookings) { booking ->
                        BookingHistoryCard(
                            booking = booking,
                            onViewPass = { onViewPass(booking.bookingId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingHistoryHeader(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "ACTIVE HUB",
            fontSize = 12.sp,
            color = OrangeAccent,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Text(
            text = "MY PASSES",
            fontSize = 28.sp,
            color = Color.White,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun EmptyBookingsState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎫", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("NO PASSES YET", color = Color.White, fontWeight = FontWeight.Black)
            Text("Book a ground to see your entry passes", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
private fun BookingHistoryCard(booking: BookingEntity, onViewPass: () -> Unit) {
    KreedaCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(booking.groundName.uppercase(), fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(booking.sport.uppercase(), fontSize = 12.sp, color = OrangeAccent, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .background(if (booking.checkedIn) Color.DarkGray else OrangeAccent, RoundedCornerShape(4.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (booking.checkedIn) "USED" else "ACTIVE",
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("DATE", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(booking.bookingDate, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("SLOT", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(booking.slotTime, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("AMOUNT", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("₹${booking.amount.toInt()}", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { /* TODO: Trigger Invoice generation */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
                ) {
                    Text("INVOICE", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onViewPass,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
                ) {
                    Text("VIEW PASS", color = Color.White, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun BookingDetailColumn(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
    }
}