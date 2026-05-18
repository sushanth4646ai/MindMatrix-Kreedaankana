package com.kreedaankana.ui.customer

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.kreedaankana.data.local.dao.BookingDao
import com.kreedaankana.data.local.entity.BookingEntity
import com.kreedaankana.ui.theme.*
import java.io.File
import com.kreedaankana.util.PdfReportGenerator
import java.io.FileOutputStream

@Composable
fun QRGeneratorScreen(
    bookingId: String = "BK${System.currentTimeMillis()}",
    onNavigateBack: () -> Unit,
    bookingDao: BookingDao? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val infiniteTransition = rememberInfiniteTransition(label = "qr")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    var loadedBooking by remember { mutableStateOf<BookingEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(bookingId) {
        bookingDao?.getByBookingId(bookingId)?.let { booking ->
            loadedBooking = booking
        }
        isLoading = false
    }

    val booking = loadedBooking
    val qrString = remember(bookingId) {
        booking?.toQRString() ?: "NO_DATA"
    }
    val qrBitmap = remember(qrString) { generateQRCode(qrString) }

    var showShareDialog by remember { mutableStateOf(false) }
    var showDownloadDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkCharcoal, SurfaceDark, Purple.copy(alpha = 0.08f), SurfaceDark, DarkCharcoal)))
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = NeonGreen)
        } else if (booking == null) {
            Column(modifier = Modifier.align(Alignment.Center).padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("\u26A0\uFE0F", fontSize = 48.sp)
                Spacer(Modifier.height(16.dp))
                Text("No booking found", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(Modifier.height(8.dp))
                Text("Booking ID: $bookingId", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                Spacer(Modifier.height(24.dp))
                Button(onClick = onNavigateBack, colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)) {
                    Text("Go Back", color = DarkCharcoal)
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(bottom = 100.dp)) {
                QRTopBar(onBack = onNavigateBack, onShare = { showShareDialog = true })
                Spacer(Modifier.height(16.dp))
                QRCard(qrBitmap = qrBitmap, glowAlpha = glowAlpha, booking = booking)
                Spacer(Modifier.height(24.dp))
                BookingDetailCard(booking = booking)
                Spacer(Modifier.height(16.dp))
                if (booking.players.isNotEmpty()) {
                    TeamCard(players = booking.players)
                    Spacer(Modifier.height(24.dp))
                }
                QRActionButtons(onShare = { showShareDialog = true }, onDownload = { showDownloadDialog = true })
                Spacer(Modifier.height(16.dp))
            }
        }

        if (showShareDialog && booking != null) {
            QRShareDialog(context = context, booking = booking, qrString = qrString, onDismiss = { showShareDialog = false })
        }
        if (showDownloadDialog && booking != null) {
            QRDownloadDialog(context = context, booking = booking, qrString = qrString, onDismiss = { showDownloadDialog = false })
        }
    }
}

@Composable
private fun QRTopBar(onBack: () -> Unit, onShare: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp).statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(48.dp).clip(CircleShape).background(SurfaceVariantDark)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Entry Pass", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            Text("Show at venue", style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }
        IconButton(onClick = onShare, modifier = Modifier.size(48.dp).clip(CircleShape).background(SurfaceVariantDark)) {
            Icon(Icons.Default.Share, contentDescription = "Share", tint = TextPrimary)
        }
    }
}

@Composable
private fun QRCard(qrBitmap: Bitmap?, glowAlpha: Float, booking: BookingEntity) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(booking.groundName.ifEmpty { "Kreeda Ankana" }, style = MaterialTheme.typography.headlineSmall, color = NeonGreen, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(4.dp))
                Text(booking.sport.ifEmpty { "Sports" }, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
                Spacer(Modifier.height(20.dp))
                Box(modifier = Modifier.size(240.dp), contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.size(280.dp).clip(RoundedCornerShape(32.dp)).background(Brush.radialGradient(listOf(NeonGreen.copy(alpha = glowAlpha * 0.3f), Color.Transparent))))
                    Card(modifier = Modifier.size(220.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Box(modifier = Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.Center) {
                            qrBitmap?.let { Image(bitmap = it.asImageBitmap(), contentDescription = "QR Code", modifier = Modifier.fillMaxSize()) }
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                Text(booking.bookingDate.ifEmpty { "---" }, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Text(booking.slotTime.ifEmpty { "---" }, style = MaterialTheme.typography.bodyMedium, color = ElectricBlue)
            }
        }
    }
}

@Composable
private fun BookingDetailCard(booking: BookingEntity) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Booking Details", style = MaterialTheme.typography.titleSmall, color = NeonGreen, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            if (booking.teamName.isNotEmpty()) { DetailRow("Team", booking.teamName); Spacer(Modifier.height(8.dp)) }
            DetailRow("Captain", booking.customerName); Spacer(Modifier.height(8.dp))
            if (booking.groundAddress.isNotEmpty()) { DetailRow("Venue", booking.groundAddress); Spacer(Modifier.height(8.dp)) }
            DetailRow("Date", booking.bookingDate); Spacer(Modifier.height(8.dp))
            DetailRow("Time", booking.slotTime); Spacer(Modifier.height(8.dp))
            DetailRow("Slot", booking.slotNumber); Spacer(Modifier.height(8.dp))
            DetailRow("Amount", "\u20B9${booking.amount.toInt()}")
        }
    }
}

@Composable
private fun TeamCard(players: List<String>) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Team Members (${players.size})", style = MaterialTheme.typography.titleSmall, color = NeonGreen, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            players.forEach { Text("\u2022 $it", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, modifier = Modifier.padding(vertical = 2.dp)) }
        }
    }
}

@Composable
private fun QRActionButtons(onShare: () -> Unit, onDownload: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = onShare, modifier = Modifier.weight(1f).height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlue)) { Text("Share") }
            Button(onClick = onDownload, modifier = Modifier.weight(1f).height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)) { Text("Download PDF", color = DarkCharcoal, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Text(value, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun QRShareDialog(context: Context, booking: BookingEntity, qrString: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } },
        containerColor = SurfaceDark,
        title = { Text("Share Booking Pass", color = TextPrimary) },
        text = {
            Column {
                Text("Share via:", color = TextSecondary)
                Spacer(Modifier.height(12.dp))
                ShareOption("\uD83D\uDCAC", "WhatsApp", onClick = { shareViaWhatsApp(context, booking, qrString); onDismiss() })
                ShareOption("\uD83D\uDCE7", "Email", onClick = {
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Booking Pass - ${booking.bookingId}")
                        putExtra(Intent.EXTRA_TEXT, buildPassText(booking))
                    }.let { context.startActivity(Intent.createChooser(it, "Share via")) }; onDismiss()
                })
                ShareOption("\uD83D\uDCF1", "SMS", onClick = {
                    Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:")).apply {
                        putExtra("sms_body", buildPassText(booking))
                    }.let { context.startActivity(it) }; onDismiss()
                })
            }
        }
    )
}

@Composable
private fun ShareOption(icon: String, title: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark), onClick = onClick) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 24.sp)
            Spacer(Modifier.width(16.dp))
            Text(title, color = TextPrimary, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Text("\u2192", color = NeonGreen, fontSize = 20.sp)
        }
    }
}

@Composable
private fun QRDownloadDialog(context: Context, booking: BookingEntity, qrString: String, onDismiss: () -> Unit) {
    var isGeneratingEntry by remember { mutableStateOf(false) }
    var isGeneratingInvoice by remember { mutableStateOf(false) }
    var entryPdf by remember { mutableStateOf<File?>(null) }
    var invoicePdf by remember { mutableStateOf<File?>(null) }
    var selectedType by remember { mutableStateOf<String?>(null) }

    fun viewPdf(file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
            }.let { context.startActivity(it) }
        } catch (_: Exception) {
            Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "${context.packageName}.provider", file))
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }.let { context.startActivity(Intent.createChooser(it, "Open PDF with")) }
        }
    }

    if (selectedType == null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = SurfaceDark,
            title = { Text("Download", color = TextPrimary) },
            text = {
                Column {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
                        onClick = {
                            selectedType = "entry"
                            isGeneratingEntry = true
                            entryPdf = generatePassPDF(context, booking, qrString)
                            isGeneratingEntry = false
                        }
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("\uD83C\uDFAB", fontSize = 24.sp)
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Entry Pass", color = TextPrimary, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                Text("Show at venue for entry", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                            }
                            Text("\u2192", color = NeonGreen, fontSize = 20.sp)
                        }
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
                        onClick = {
                            selectedType = "invoice"
                            isGeneratingInvoice = true
                            invoicePdf = PdfReportGenerator.generateInvoice(context, booking)
                            isGeneratingInvoice = false
                        }
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("\uD83D\uDCCB", fontSize = 24.sp)
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Invoice", color = TextPrimary, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                Text("Payment receipt \u20B9${booking.amount.toInt()}", color = TextMuted, style = MaterialTheme.typography.bodySmall)
                            }
                            Text("\u2192", color = NeonGreen, fontSize = 20.sp)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } }
        )
    } else {
        val isLoading = if (selectedType == "entry") isGeneratingEntry else isGeneratingInvoice
        val pdfFile = if (selectedType == "entry") entryPdf else invoicePdf

        AlertDialog(
            onDismissRequest = { if (!isLoading) onDismiss() },
            containerColor = SurfaceDark,
            title = { Text(if (isLoading) "Generating..." else if (selectedType == "entry") "Pass Ready!" else "Invoice Ready!", color = TextPrimary) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    if (isLoading) {
                        CircularProgressIndicator(color = NeonGreen)
                        Spacer(Modifier.height(16.dp))
                        Text("Please wait...", color = TextSecondary)
                    } else {
                        Text(if (selectedType == "entry") "\uD83C\uDFAB" else "\uD83D\uDCCB", fontSize = 64.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            if (selectedType == "entry") "Entry pass PDF generated!"
                            else "Invoice PDF generated!",
                            color = SuccessGreen, style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Saved to: ${pdfFile?.name ?: "---"}", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                if (!isLoading && pdfFile != null) {
                    Button(onClick = { viewPdf(pdfFile!!); onDismiss() }, colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)) {
                        Text("View PDF", color = DarkCharcoal)
                    }
                }
            },
            dismissButton = { TextButton(onClick = { selectedType = null }) { Text("Back", color = TextMuted) } }
        )
    }
}

private fun generateQRCode(content: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width; val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) for (y in 0 until height)
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        bitmap
    } catch (_: Exception) { null }
}

private fun generatePassPDF(context: Context, booking: BookingEntity, qrString: String): File? {
    return try {
        val pdfDocument = PdfDocument()
        val page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
        val c = page.canvas
        val titlePaint = Paint().apply { color = android.graphics.Color.BLACK; textSize = 28f; typeface = Typeface.DEFAULT_BOLD }
        val headerPaint = Paint().apply { color = android.graphics.Color.DKGRAY; textSize = 16f; typeface = Typeface.DEFAULT_BOLD }
        val valuePaint = Paint().apply { color = android.graphics.Color.BLACK; textSize = 18f }
        val smallPaint = Paint().apply { color = android.graphics.Color.GRAY; textSize = 14f }
        var y = 50f
        c.drawText("KREEDA ANKANA", 50f, y, titlePaint); c.drawText("Entry Pass", 50f, y + 30, smallPaint); y += 80f
        c.drawText("BOOKING CONFIRMED", 50f, y, headerPaint); y += 50f
        c.drawLine(50f, y, 545f, y, Paint().apply { color = android.graphics.Color.LTGRAY }); y += 30f
        listOf(
            "Booking ID" to "#${booking.bookingId}", "Team" to booking.teamName, "Captain" to booking.customerName,
            "Phone" to booking.customerPhone, "Ground" to booking.groundName, "Address" to booking.groundAddress,
            "Sport" to booking.sport, "Date" to booking.bookingDate, "Time" to booking.slotTime,
            "Slot" to booking.slotNumber, "Amount" to "Rs${booking.amount.toInt()}", "Status" to "CONFIRMED"
        ).forEach { (l, v) -> c.drawText("$l:", 50f, y, smallPaint); c.drawText(v, 200f, y, valuePaint); y += 28f }
        y += 20f; c.drawLine(50f, y, 545f, y, Paint().apply { color = android.graphics.Color.LTGRAY }); y += 30f
        if (booking.players.isNotEmpty()) {
            c.drawText("Team Members (${booking.players.size}):", 50f, y, headerPaint); y += 25f
            booking.players.take(10).forEachIndexed { i, p -> c.drawText("${i + 1}. $p", 60f, y, smallPaint); y += 22f }
        }
        y += 40f
        val qrSize = 160
        val qrX = (595 - qrSize) / 2f
        generateQRCode(qrString)?.let { qr ->
            val scaled = Bitmap.createScaledBitmap(qr, qrSize, qrSize, true)
            c.drawBitmap(scaled, qrX, y, null)
            y += qrSize + 15f
            c.drawText("Scan QR for instant entry", qrX, y, smallPaint)
        }
        pdfDocument.finishPage(page)
        val file = File(context.cacheDir, "EntryPass_${booking.bookingId}.pdf")
        pdfDocument.writeTo(FileOutputStream(file)); pdfDocument.close(); file
    } catch (_: Exception) { null }
}

private fun buildPassText(booking: BookingEntity): String = """
\uD83C\uDFDF\uFE0F KREEDA ANKANA - Booking Pass
ID: ${booking.bookingId}
Team: ${booking.teamName}
Captain: ${booking.customerName}
Ground: ${booking.groundName}
Date: ${booking.bookingDate}
Time: ${booking.slotTime}
Amount: \u20B9${booking.amount.toInt()}
Show this pass at the venue for entry.
""".trimIndent()

private fun shareViaWhatsApp(context: Context, booking: BookingEntity, qrString: String) {
    try {
        val msg = buildPassText(booking) + "\n\nBooking Code:\n$qrString"
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/?text=${Uri.encode(msg)}")))
    } catch (_: Exception) {}
}
