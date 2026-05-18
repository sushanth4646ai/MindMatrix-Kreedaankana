package com.kreedaankana.ui.customer

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.kreedaankana.data.local.entity.BookingEntity
import com.kreedaankana.data.blockchain.BlockchainService
import com.kreedaankana.ui.theme.*
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun QRScannerScreen(
    onBack: () -> Unit,
    onCheckIn: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    var hasCameraPermission by remember { mutableStateOf(false) }
    var scannedData by remember { mutableStateOf<BookingEntity?>(null) }
    var validationResult by remember { mutableStateOf<ValidationState?>(null) }
    var isScanning by remember { mutableStateOf(true) }
    var showManualInput by remember { mutableStateOf(false) }
    var manualInput by remember { mutableStateOf("") }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCharcoal)
    ) {
        if (hasCameraPermission && scannedData == null) {
            CameraPreview(
                onBarcodeDetected = { data ->
                    if (isScanning) {
                        isScanning = false
                        val booking = BookingEntity.fromQRString(data)
                        if (booking != null) {
                            scannedData = booking
                            coroutineScope.launch {
                                validationResult = validateBookingWithBlockchain(booking)
                            }
                        } else {
                            validationResult = ValidationState.INVALID
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else if (scannedData == null) {
            CameraPermissionUI(onRequestPermission = {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            })
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopBar(onBack = onBack, onManualInput = { showManualInput = true })

            if (showManualInput && scannedData == null) {
                ManualInputSection(
                    value = manualInput,
                    onValueChange = { manualInput = it },
                    onSubmit = {
                        val booking = BookingEntity.fromQRString(manualInput)
                        if (booking != null) {
                            scannedData = booking
                            coroutineScope.launch {
                                validationResult = validateBookingWithBlockchain(booking)
                            }
                            showManualInput = false
                        }
                    },
                    onDismiss = { showManualInput = false }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (scannedData == null && !showManualInput) {
                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp), contentAlignment = Alignment.BottomCenter) {
                    ScanGuideOverlay()
                }
            }
        }

        scannedData?.let { booking ->
            BookingResultOverlay(
                booking = booking,
                validation = validationResult!!,
                onRescan = {
                    scannedData = null
                    validationResult = null
                    isScanning = true
                    manualInput = ""
                },
                onCheckIn = {
                    onCheckIn(booking.bookingId)
                }
            )
        }
    }
}

@Composable
private fun CameraPreview(
    onBarcodeDetected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val barcodeScanner = BarcodeScanning.getClient()
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setTargetResolution(Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )
                            barcodeScanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    for (barcode in barcodes) {
                                        if (barcode.valueType == Barcode.TYPE_TEXT ||
                                            barcode.valueType == Barcode.TYPE_URL) {
                                            barcode.rawValue?.let { value ->
                                                onBarcodeDetected(value)
                                            }
                                        }
                                    }
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        } else {
                            imageProxy.close()
                        }
                    }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val boxSize = size.width * 0.65f
                    drawRect(
                        color = Color.Black.copy(alpha = 0.5f),
                        topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
                        size = androidx.compose.ui.geometry.Size(size.width, centerY - boxSize / 2)
                    )
                    drawRect(
                        color = Color.Black.copy(alpha = 0.5f),
                        topLeft = androidx.compose.ui.geometry.Offset(0f, centerY + boxSize / 2),
                        size = androidx.compose.ui.geometry.Size(size.width, centerY - boxSize / 2)
                    )
                    drawRect(
                        color = Color.Black.copy(alpha = 0.5f),
                        topLeft = androidx.compose.ui.geometry.Offset(0f, centerY - boxSize / 2),
                        size = androidx.compose.ui.geometry.Size(centerX - boxSize / 2, boxSize)
                    )
                    drawRect(
                        color = Color.Black.copy(alpha = 0.5f),
                        topLeft = androidx.compose.ui.geometry.Offset(centerX + boxSize / 2, centerY - boxSize / 2),
                        size = androidx.compose.ui.geometry.Size(centerX - boxSize / 2, boxSize)
                    )
                }
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(280.dp, 280.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawCircle(
                            color = NeonGreen.copy(alpha = glowAlpha * 0.3f),
                            radius = size.minDimension * 0.6f
                        )
                    }
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(3.dp, NeonGreen.copy(alpha = glowAlpha), RoundedCornerShape(24.dp))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(40.dp)
                    .border(3.dp, NeonGreen.copy(alpha = glowAlpha), RoundedCornerShape(topStart = 24.dp))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(40.dp)
                    .border(3.dp, NeonGreen.copy(alpha = glowAlpha), RoundedCornerShape(topEnd = 24.dp))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(40.dp)
                    .border(3.dp, NeonGreen.copy(alpha = glowAlpha), RoundedCornerShape(bottomStart = 24.dp))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(40.dp)
                    .border(3.dp, NeonGreen.copy(alpha = glowAlpha), RoundedCornerShape(bottomEnd = 24.dp))
            )
        }
    }
}

@Composable
private fun TopBar(onBack: () -> Unit, onManualInput: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SurfaceVariantDark.copy(alpha = 0.8f))
        ) {
            Text("←", fontSize = 20.sp, color = TextPrimary)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "QR Scanner",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Verify booking pass",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        IconButton(
            onClick = onManualInput,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SurfaceVariantDark.copy(alpha = 0.8f))
        ) {
            Text("⌨️", fontSize = 20.sp)
        }
    }
}

@Composable
private fun ScanGuideOverlay() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 48.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(DarkCharcoal.copy(alpha = 0.9f))
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Align QR code within the frame",
                style = MaterialTheme.typography.bodyMedium,
                color = NeonGreen,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ManualInputSection(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enter Booking Code",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Paste the booking code from your pass",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Paste booking code here...", color = TextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGreen,
                    unfocusedBorderColor = TextMuted
                ),
                shape = RoundedCornerShape(12.dp),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel", color = TextSecondary)
                }
                Button(
                    onClick = onSubmit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                ) {
                    Text("Validate", color = DarkCharcoal, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun BookingResultOverlay(
    booking: BookingEntity,
    validation: ValidationState,
    onRescan: () -> Unit,
    onCheckIn: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkCharcoal.copy(alpha = 0.95f))
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ValidationBadge(validation)

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    PassHeader(booking)

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = GlassWhite10
                    )

                    BookingInfoRow("Booking ID", booking.bookingId)
                    BookingInfoRow("Team", booking.teamName)
                    BookingInfoRow("Captain", booking.customerName)
                    BookingInfoRow("Ground", booking.groundName)
                    BookingInfoRow("Address", booking.groundAddress)
                    BookingInfoRow("Sport", booking.sport)
                    BookingInfoRow("Date", booking.bookingDate)
                    BookingInfoRow("Time", booking.slotTime)
                    BookingInfoRow("Slot", booking.slotNumber)

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = GlassWhite10
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Amount Paid", color = TextSecondary)
                        Text("₹${booking.amount.toInt()}", color = NeonGreen, fontWeight = FontWeight.Bold)
                    }

                    if (booking.players.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = GlassWhite10, modifier = Modifier.padding(vertical = 12.dp))
                        Text("Team Members (${booking.players.size})", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        booking.players.take(8).forEach { player ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("👤", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(player, color = TextPrimary, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onRescan,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Scan Again", color = TextPrimary)
                }

                if (validation == ValidationState.VALID) {
                    Button(
                        onClick = onCheckIn,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                    ) {
                        Text("Confirm Entry", color = DarkCharcoal, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

private data class ValidationDisplay(
    val bgColor: androidx.compose.ui.graphics.Color,
    val textColor: androidx.compose.ui.graphics.Color,
    val icon: String,
    val title: String,
    val subtitle: String
)

@Composable
private fun ValidationBadge(state: ValidationState) {
    val display = when (state) {
        ValidationState.VALID, ValidationState.VALID_BLOCKCHAIN_VERIFIED -> ValidationDisplay(
            SuccessGreen.copy(alpha = 0.15f), SuccessGreen, "✓", 
            if (state == ValidationState.VALID_BLOCKCHAIN_VERIFIED) "BLOCKCHAIN VERIFIED" else "BOOKING VERIFIED", 
            if (state == ValidationState.VALID_BLOCKCHAIN_VERIFIED) "Verified on Hyperledger Fabric" else "Entry approved - all details match"
        )
        ValidationState.EXPIRED -> ValidationDisplay(
            ErrorRed.copy(alpha = 0.15f), ErrorRed, "✗", "PASS EXPIRED", "This booking has passed its validity period"
        )
        ValidationState.INVALID -> ValidationDisplay(
            ErrorRed.copy(alpha = 0.15f), ErrorRed, "✗", "INVALID PASS", "This code could not be verified"
        )
        ValidationState.ALREADY_USED -> ValidationDisplay(
            WarningYellow.copy(alpha = 0.15f), WarningYellow, "⚠", "ALREADY CHECKED IN", "This pass has been used already"
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = display.bgColor)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = display.icon,
                fontSize = 56.sp,
                color = display.textColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = display.title,
                style = MaterialTheme.typography.headlineSmall,
                color = display.textColor,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = display.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PassHeader(booking: BookingEntity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "KREEDA ANKANA",
                style = MaterialTheme.typography.titleMedium,
                color = NeonGreen,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Entry Pass",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    when (booking.paymentStatus) {
                        "CONFIRMED" -> SuccessGreen.copy(alpha = 0.2f)
                        else -> WarningYellow.copy(alpha = 0.2f)
                    }
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "✓ ${booking.paymentStatus}",
                style = MaterialTheme.typography.labelSmall,
                color = if (booking.paymentStatus == "CONFIRMED") SuccessGreen else WarningYellow,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun BookingInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = TextPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun CameraPermissionUI(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📷", fontSize = 80.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Allow camera access to scan QR codes",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRequestPermission,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
        ) {
            Text("Grant Permission", color = DarkCharcoal, fontWeight = FontWeight.Bold)
        }
    }
}

private suspend fun validateBookingWithBlockchain(booking: BookingEntity): ValidationState {
    val now = System.currentTimeMillis()
    if (now > booking.expiryTime && booking.expiryTime > 0) {
        return ValidationState.EXPIRED
    }
    if (booking.checkedIn) {
        return ValidationState.ALREADY_USED
    }

    if (booking.blockchainTxId.isNotEmpty() && booking.blockchainTxId.startsWith("0x")) {
        val result = BlockchainService.getInstance().verifyBooking(booking.bookingId)
        if (result.isSuccess) {
            val response = result.getOrNull()
            if (response?.valid == true) {
                return ValidationState.VALID_BLOCKCHAIN_VERIFIED
            } else if (response?.checkedIn == true) {
                return ValidationState.ALREADY_USED
            }
        }
    }

    return ValidationState.VALID
}

enum class ValidationState {
    VALID, VALID_BLOCKCHAIN_VERIFIED, EXPIRED, INVALID, ALREADY_USED
}