@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.kreedaankana.ui.customer

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kreedaankana.data.local.AppDatabase
import com.kreedaankana.data.local.entity.BookingEntity
import com.kreedaankana.data.local.entity.GroundEntity
import com.kreedaankana.data.local.entity.TeamEntity
import com.kreedaankana.ui.components.*
import com.kreedaankana.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class TimeSlotData(val time: String, val isAvailable: Boolean, val isBooked: Boolean = false)

@Composable
fun BookingScreen(
    groundId: String,
    onBookingSuccess: (String) -> Unit,
    onBack: () -> Unit,
    onCreateTeam: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDatabase(context) }

    var ground by remember { mutableStateOf<GroundEntity?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedSlot by remember { mutableStateOf<String?>(null) }
    var selectedSport by remember { mutableStateOf("FOOTBALL") }
    var step by remember { mutableIntStateOf(1) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showTeamDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedTeam by remember { mutableStateOf<TeamEntity?>(null) }
    var timeSlots by remember { mutableStateOf<List<TimeSlotData>>(emptyList()) }

    LaunchedEffect(groundId, selectedDate) {
        ground = database.groundDao().getById(groundId.toIntOrNull() ?: 1)
        val teams = database.teamDao().getAllTeams().first()
        if (selectedTeam == null && teams.isNotEmpty()) {
            selectedTeam = teams.first()
        }
        selectedSport = ground?.sport?.uppercase() ?: "FOOTBALL"
        generateSlotsForDate(groundId.toIntOrNull() ?: 1, selectedDate.toString(), database).let { (slots, _) ->
            timeSlots = slots
        }
    }

    val price = ground?.pricePerHour?.toInt() ?: 1200

    Scaffold(
        topBar = {
            BookingHeader(
                step = step,
                groundName = ground?.name ?: "LOADING...",
                onBack = { if (step > 1) step-- else onBack() }
            )
        },
        bottomBar = {
            if (step < 5) {
                BottomContinueBar(
                    canContinue = when (step) {
                        1 -> true
                        2 -> true
                        3 -> selectedSlot != null
                        4 -> selectedTeam != null
                        else -> false
                    },
                    onContinue = { step++ }
                )
            }
        },
        containerColor = DarkCharcoal
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (step) {
                1 -> SelectSportStep(selectedSport = selectedSport, onSportSelected = { selectedSport = it })
                2 -> SelectDateStep(selectedDate = selectedDate, onDateSelected = {
                    selectedDate = it
                    coroutineScope.launch {
                        val (slots, _) = generateSlotsForDate(groundId.toIntOrNull() ?: 1, it.toString(), database)
                        timeSlots = slots
                    }
                })
                3 -> SelectTimeStep(timeSlots = timeSlots, selectedSlot = selectedSlot, onSlotSelected = { selectedSlot = it })
                4 -> TeamDetailsStep(
                    profile = selectedTeam,
                    selectedSport = selectedSport,
                    onSelectTeam = { showTeamDialog = true }
                )
                5 -> PaymentStep(
                    groundName = ground?.name ?: "",
                    sport = selectedSport,
                    date = selectedDate,
                    slot = selectedSlot ?: "",
                    price = "₹$price",
                    onPayNow = { showPaymentDialog = true }
                )
            }
        }
    }

    if (showPaymentDialog) {
        PaymentDialog(
            onDismiss = { showPaymentDialog = false },
            onPaymentSuccess = {
                showPaymentDialog = false
                isLoading = true
                coroutineScope.launch {
                    val bookingId = "BK${System.currentTimeMillis()}"
                    val expiryTime = System.currentTimeMillis() + (3 * 60 * 60 * 1000)
                    val booking = BookingEntity(
                        bookingId = bookingId,
                        groundId = groundId.toIntOrNull() ?: 0,
                        groundName = ground?.name ?: "",
                        groundAddress = ground?.address ?: "",
                        sport = selectedSport,
                        bookingDate = selectedDate.format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")),
                        slotTime = selectedSlot ?: "",
                        slotNumber = "Slot",
                        expiryTime = expiryTime,
                        paymentStatus = "CONFIRMED",
                        amount = price.toDouble(),
                        customerName = selectedTeam?.captainName ?: "Customer",
                        customerPhone = selectedTeam?.captainPhone ?: "",
                        teamName = selectedTeam?.teamName ?: "",
                        players = selectedTeam?.players ?: emptyList(),
                        createdAt = System.currentTimeMillis()
                    )
                    database.bookingDao().insert(booking)
                    isLoading = false
                    onBookingSuccess(bookingId)
                }
            }
        )
    }

    if (showTeamDialog) {
        TeamSelectionDialog(
            context = context,
            selectedSport = selectedSport,
            onDismiss = { showTeamDialog = false },
            onSave = { team ->
                selectedTeam = team
                showTeamDialog = false
            },
            onCreateTeam = onCreateTeam
        )
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = OrangeAccent)
        }
    }
}

private suspend fun generateSlotsForDate(groundId: Int, date: String, database: AppDatabase): Pair<List<TimeSlotData>, Set<String>> {
    val bookedSlots = mutableSetOf<String>()
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    val slots = mutableListOf<TimeSlotData>()

    repeat(8) { i ->
        val slotTime = LocalTime.of(6 + i * 2, 0)
        val timeStr = slotTime.format(formatter)
        val isBooked = if (i % 4 == 0) {
            bookedSlots.add(timeStr)
            true
        } else {
            val count = database.bookingDao().countBookedSlots(groundId, date, timeStr)
            if (count > 0) bookedSlots.add(timeStr)
            count > 0
        }
        val now = LocalTime.now()
        val slotDateTime = LocalTime.of(slotTime.hour, slotTime.minute)
        val isPast = slotDateTime.isBefore(now) || slotDateTime.isBefore(now.plusHours(1))
        slots.add(TimeSlotData(time = timeStr, isAvailable = !isBooked && !isPast, isBooked = isBooked))
    }
    return slots to bookedSlots
}

@Composable
private fun BookingHeader(step: Int, groundName: String, onBack: () -> Unit) {
    val steps = listOf("SPORT", "DATE", "TIME", "TEAM", "PAY")
    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .border(1.dp, CardBorder, CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("STEP $step OF 5", style = SectionLabel, color = OrangeAccent)
                Text(groundName.uppercase(), style = Typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Black)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, label ->
                val stepNum = index + 1
                val isActive = stepNum == step
                val isCompleted = stepNum < step
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCompleted -> OrangeAccent
                                    isActive -> OrangeAccent.copy(alpha = 0.2f)
                                    else -> SurfaceDark
                                }
                            )
                            .border(
                                1.dp,
                                if (isActive || isCompleted) OrangeAccent else CardBorder,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        } else {
                            Text(
                                text = stepNum.toString(),
                                style = Typography.labelSmall,
                                color = if (isActive) OrangeAccent else TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        style = Typography.labelSmall,
                        fontSize = 8.sp,
                        color = if (isActive) OrangeAccent else TextMuted
                    )
                }
                
                if (index < steps.size - 1) {
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .background(if (isCompleted) OrangeAccent else CardBorder)
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectSportStep(selectedSport: String, onSportSelected: (String) -> Unit) {
    val sports = listOf("FOOTBALL", "CRICKET", "BASKETBALL", "TENNIS", "BADMINTON", "VOLLEYBALL")
    Column(modifier = Modifier.padding(20.dp)) {
        KreedaSectionHeader(title = "Select Sport")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            sports.chunked(2).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { sport ->
                        val isSelected = selectedSport == sport
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSportSelected(sport) },
                            color = if (isSelected) OrangeAccent.copy(alpha = 0.1f) else SurfaceDark,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, if (isSelected) OrangeAccent else CardBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = sport,
                                    style = Typography.labelLarge,
                                    color = if (isSelected) OrangeAccent else TextPrimary,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectDateStep(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val today = LocalDate.now()
    val dates = remember(today) { (0..13).map { today.plusDays(it.toLong()) } }
    
    Column(modifier = Modifier.padding(20.dp)) {
        KreedaSectionHeader(title = "Select Date")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(dates) { date ->
                val isSelected = date == selectedDate
                Surface(
                    modifier = Modifier
                        .width(70.dp)
                        .height(100.dp)
                        .clickable { onDateSelected(date) },
                    color = if (isSelected) OrangeAccent else SurfaceDark,
                    shape = RoundedCornerShape(16.dp),
                    border = if (isSelected) null else BorderStroke(1.dp, CardBorder)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("EEE")).uppercase(),
                            style = Typography.labelSmall,
                            color = if (isSelected) Color.Black else TextMuted
                        )
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("d")),
                            style = Typography.titleLarge,
                            color = if (isSelected) Color.Black else TextPrimary,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("MMM")).uppercase(),
                            style = Typography.labelSmall,
                            color = if (isSelected) Color.Black else TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectTimeStep(timeSlots: List<TimeSlotData>, selectedSlot: String?, onSlotSelected: (String) -> Unit) {
    Column(modifier = Modifier.padding(20.dp)) {
        KreedaSectionHeader(title = "Select Time Slot")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            timeSlots.chunked(2).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { slot ->
                        val isSelected = selectedSlot == slot.time
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = slot.isAvailable) { onSlotSelected(slot.time) },
                            color = when {
                                isSelected -> OrangeAccent
                                !slot.isAvailable -> SurfaceDark.copy(alpha = 0.5f)
                                else -> SurfaceDark
                            },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, if (isSelected) OrangeAccent else CardBorder)
                        ) {
                            Box(
                                modifier = Modifier.padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = slot.time,
                                    style = Typography.labelLarge,
                                    color = when {
                                        isSelected -> Color.Black
                                        !slot.isAvailable -> TextMuted
                                        else -> TextPrimary
                                    },
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamDetailsStep(profile: TeamEntity?, selectedSport: String, onSelectTeam: () -> Unit) {
    Column(modifier = Modifier.padding(20.dp)) {
        KreedaSectionHeader(title = "Team Selection")
        if (profile != null) {
            KreedaCard(onClick = onSelectTeam) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    KreedaAvatar(imageUrl = null, size = 56)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(profile.teamName.uppercase(), style = Typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Black)
                        Text("CAPTAIN: ${profile.captainName.uppercase()}", style = Typography.labelSmall, color = TextSecondary)
                    }
                    Text(text = "CHANGE", style = Typography.labelSmall, color = OrangeAccent, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SportTag(selectedSport)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "${profile.players.size} PLAYERS", style = Typography.labelSmall, color = TextMuted)
                }
            }
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clickable { onSelectTeam() },
                color = SurfaceDark,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("+ SELECT YOUR TEAM", style = Typography.labelLarge, color = OrangeAccent, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun PaymentStep(groundName: String, sport: String, date: LocalDate, slot: String, price: String, onPayNow: () -> Unit) {
    Column(modifier = Modifier.padding(20.dp)) {
        KreedaSectionHeader(title = "Booking Summary")
        KreedaCard {
            SummaryRow(label = "GROUND", value = groundName.uppercase())
            SummaryRow(label = "SPORT", value = sport)
            SummaryRow(label = "DATE", value = date.format(DateTimeFormatter.ofPattern("EEE, MMM d")).uppercase())
            SummaryRow(label = "TIME", value = slot)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("TOTAL AMOUNT", style = Typography.labelLarge, color = TextPrimary, fontWeight = FontWeight.Black)
                Text(price, style = Typography.titleLarge, color = OrangeAccent, fontWeight = FontWeight.Black)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onPayNow,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
        ) {
            Text("CONFIRM & PAY", color = Color.Black, style = Typography.labelLarge, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = SectionLabel, color = TextMuted)
        Text(value, style = Typography.labelLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun BottomContinueBar(canContinue: Boolean, onContinue: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceDark,
        tonalElevation = 8.dp,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(56.dp)
                .navigationBarsPadding(),
            shape = RoundedCornerShape(16.dp),
            enabled = canContinue,
            colors = ButtonDefaults.buttonColors(
                containerColor = OrangeAccent,
                disabledContainerColor = SurfaceVariantDark
            )
        ) {
            Text(
                text = "CONTINUE",
                color = if (canContinue) Color.Black else TextMuted,
                style = Typography.labelLarge,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamSelectionDialog(context: Context, selectedSport: String, onDismiss: () -> Unit, onSave: (TeamEntity) -> Unit, onCreateTeam: () -> Unit = {}) {
    val database = remember { AppDatabase.getDatabase(context) }
    val teams by database.teamDao().getAllTeams().collectAsStateWithLifecycle(initialValue = emptyList())
    val filteredTeams = teams.filter { it.sport.uppercase() == selectedSport }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            border = BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("SELECT TEAM", style = Typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(20.dp))
                
                if (filteredTeams.isEmpty()) {
                    Text("NO TEAMS FOUND FOR $selectedSport", style = Typography.bodyMedium, color = TextMuted)
                } else {
                    filteredTeams.forEach { team ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { onSave(team) },
                            color = SurfaceVariantDark,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(team.teamName.uppercase(), style = Typography.labelLarge, color = TextPrimary, fontWeight = FontWeight.Black)
                                    Text("CAPTAIN: ${team.captainName.uppercase()}", style = Typography.labelSmall, color = TextMuted)
                                }
                                Icon(Icons.Default.Check, contentDescription = null, tint = OrangeAccent)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = { onDismiss(); onCreateTeam() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+ CREATE NEW TEAM", color = OrangeAccent, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun PaymentDialog(onDismiss: () -> Unit, onPaymentSuccess: () -> Unit) {
    var isProcessing by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { kotlinx.coroutines.delay(2000); isProcessing = false }
    
    BasicAlertDialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceDark,
            border = BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = OrangeAccent)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("PROCESSING PAYMENT", style = SectionLabel, color = TextPrimary)
                } else {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("BOOKING CONFIRMED", style = Typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = onPaymentSuccess,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
                    ) {
                        Text("VIEW PASS", color = Color.Black, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}