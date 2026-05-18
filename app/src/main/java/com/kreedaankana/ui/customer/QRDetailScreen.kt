package com.kreedaankana.ui.customer

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.kreedaankana.data.local.entity.BookingEntity
import com.kreedaankana.data.model.QRPayload
import com.kreedaankana.ui.components.*
import com.kreedaankana.ui.theme.*
import com.kreedaankana.util.SecureInvoiceGenerator
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun QRDetailScreen(
    booking: BookingEntity,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var timeLeft by remember { mutableStateOf("") }
    
    // Generate Secure QR Payload
    val qrPayload = remember(booking) {
        QRPayload(
            bookingId = booking.bookingId,
            timestamp = System.currentTimeMillis(),
            customerName = booking.customerName,
            customerPhone = booking.customerPhone,
            teamName = booking.teamName,
            groundName = booking.groundName,
            sportType = booking.sport,
            slotDate = booking.bookingDate,
            startTime = booking.slotTime,
            endTime = booking.slotTime, // Needs proper end time
            numPlayers = booking.players.size,
            paymentStatus = booking.paymentStatus,
            ownerId = "OWNER_${booking.groundId}", // Mocking owner ID
            customerId = "USER_123", // Mocking user ID
            expiryTime = booking.expiryTime
        )
    }

    val qrBitmap = remember(qrPayload) {
        generateQR(qrPayload.serialize())
    }

    LaunchedEffect(booking.expiryTime) {
        while (true) {
            val remaining = booking.expiryTime - System.currentTimeMillis()
            if (remaining <= 0) {
                timeLeft = "EXPIRED"
                break
            }
            val hours = (remaining / 3600000)
            val minutes = (remaining % 3600000) / 60000
            val seconds = (remaining % 60000) / 1000
            timeLeft = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            delay(1000)
        }
    }

    Scaffold(
        containerColor = DarkCharcoal,
        topBar = {
            KreedaSectionHeader(
                title = "SECURE PASS",
                modifier = Modifier.padding(16.dp).statusBarsPadding()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Secure Ticket Card
            KreedaCard(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = booking.groundName.uppercase(),
                        style = SportyHeader,
                        color = OrangeAccent
                    )
                    Text(
                        text = "${booking.bookingDate} | ${booking.slotTime}",
                        style = Typography.labelMedium,
                        color = TextMuted
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // QR Code Container
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        qrBitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Secure QR",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Expiry Timer
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(SurfaceVariantDark, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = if (timeLeft == "EXPIRED") ErrorRed else SuccessGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (timeLeft == "EXPIRED") "TICKET EXPIRED" else "EXPIRES IN: $timeLeft",
                            style = Typography.labelSmall,
                            color = if (timeLeft == "EXPIRED") ErrorRed else SuccessGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Details Section
            InfoRow("BOOKING ID", "#${booking.bookingId}")
            InfoRow("CAPTAIN", booking.customerName)
            InfoRow("TEAM", booking.teamName)
            InfoRow("PLAYERS", "${booking.players.size} MEMBERS")
            InfoRow("PAYMENT", booking.paymentStatus)

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { /* Share Logic */ },
                    modifier = Modifier.weight(1f).height(56.dp),
                    border = BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = TextPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SHARE", color = TextPrimary)
                }

                Button(
                    onClick = {
                        SecureInvoiceGenerator.generateBookingInvoice(context, booking, qrPayload)
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("INVOICE", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = SectionLabel, color = TextMuted)
        Text(text = value, style = Typography.bodyLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
    }
}

private fun generateQR(content: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
        for (x in 0 until 512) {
            for (y in 0 until 512) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}
