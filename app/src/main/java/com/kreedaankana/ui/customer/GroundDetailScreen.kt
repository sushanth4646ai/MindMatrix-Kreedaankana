package com.kreedaankana.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kreedaankana.ui.components.*
import com.kreedaankana.ui.theme.*

data class GroundDetailData(
    val id: String,
    val name: String,
    val ownerName: String,
    val address: String,
    val city: String,
    val rating: Float,
    val reviews: Int,
    val pricePerHour: String,
    val images: List<String>,
    val sports: List<Pair<String, String>>,
    val facilities: List<Pair<String, String>>,
    val description: String,
    val operatingHours: String,
    val isAvailable: Boolean
)

private val sampleGroundDetail = GroundDetailData(
    id = "1",
    name = "DECATHLON ARENA",
    ownerName = "Decathlon Sports India",
    address = "HSR Layout, Sector 2",
    city = "Bangalore",
    rating = 4.8f,
    reviews = 234,
    pricePerHour = "₹1,200",
    images = listOf(
        "https://picsum.photos/seed/detail1/1200/800",
        "https://picsum.photos/seed/detail2/1200/800",
        "https://picsum.photos/seed/detail3/1200/800"
    ),
    sports = listOf("FOOTBALL" to "⚽", "CRICKET" to "🏏", "BASKETBALL" to "🏀"),
    facilities = listOf("PARKING" to "🅿️", "WIFI" to "📶", "CAFETERIA" to "🍔", "CHANGING ROOM" to "👕", "FIRST AID" to "🏥"),
    description = "Premium sports arena with FIFA-standard turf. Features state-of-the-art floodlights, professional changing rooms, and a fully-stocked cafeteria.",
    operatingHours = "6:00 AM - 10:00 PM",
    isAvailable = true
)

@Composable
fun GroundDetailScreen(
    groundId: String,
    onBack: () -> Unit,
    onBookNow: (String) -> Unit,
    onViewMap: () -> Unit
) {
    var selectedSport by remember { mutableStateOf("FOOTBALL") }

    Scaffold(
        bottomBar = {
            BottomBookingBar(
                price = sampleGroundDetail.pricePerHour,
                onBookNow = { onBookNow(groundId) }
            )
        },
        containerColor = DarkCharcoal
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item { GroundImageHeader(ground = sampleGroundDetail, onBack = onBack, onShare = { }) }
            item { GroundInfoSection(ground = sampleGroundDetail, onViewMap = onViewMap) }
            item { SportsSelectionSection(sports = sampleGroundDetail.sports, selectedSport = selectedSport, onSportSelected = { selectedSport = it }) }
            item { FacilitiesSection(facilities = sampleGroundDetail.facilities) }
            item { DescriptionSection(description = sampleGroundDetail.description) }
            item { ReviewsSection() }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun GroundImageHeader(ground: GroundDetailData, onBack: () -> Unit, onShare: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
        AsyncImage(
            model = ground.images.firstOrNull() ?: "",
            contentDescription = ground.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            IconButton(
                onClick = onShare,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
            }
        }
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SuccessGreen))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "AVAILABLE TODAY", style = SectionLabel, color = SuccessGreen)
            }
            Text(
                text = ground.name,
                style = SportyHeader,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun GroundInfoSection(ground: GroundDetailData, onViewMap: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "⭐ ${ground.rating}", style = Typography.titleMedium, color = WarningYellow, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "(${ground.reviews} Reviews)", style = Typography.labelSmall, color = TextMuted)
            }
            Text(text = ground.operatingHours, style = Typography.labelSmall, color = TextSecondary)
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        KreedaCard(onClick = onViewMap) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceVariantDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = OrangeAccent)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = ground.address, style = Typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text(text = ground.city, style = Typography.labelSmall, color = TextMuted)
                }
                Text(text = "MAP", style = Typography.labelSmall, color = OrangeAccent, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SportsSelectionSection(sports: List<Pair<String, String>>, selectedSport: String, onSportSelected: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        KreedaSectionHeader(title = "Select Sport")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(sports.size) { index ->
                val (sportName, _) = sports[index]
                SportCategoryChip(
                    name = sportName,
                    isSelected = selectedSport == sportName,
                    onClick = { onSportSelected(sportName) }
                )
            }
        }
    }
}

@Composable
fun SportCategoryChip(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = if (isSelected) OrangeAccent else SurfaceDark,
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) null else BorderStroke(1.dp, CardBorder)
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            style = Typography.labelMedium,
            color = if (isSelected) Color.Black else TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FacilitiesSection(facilities: List<Pair<String, String>>) {
    Column(modifier = Modifier.padding(20.dp)) {
        KreedaSectionHeader(title = "Facilities")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(facilities.size) { index ->
                val (name, _) = facilities[index]
                Box(
                    modifier = Modifier
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .background(SurfaceDark, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(text = name, style = Typography.labelSmall, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun DescriptionSection(description: String) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        KreedaSectionHeader(title = "About")
        Text(
            text = description,
            style = Typography.bodyMedium,
            color = TextSecondary,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun ReviewsSection() {
    Column(modifier = Modifier.padding(20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            KreedaSectionHeader(title = "Reviews", modifier = Modifier.weight(1f))
            Text(text = "SEE ALL", style = SectionLabel, color = OrangeAccent, modifier = Modifier.clickable { })
        }
        
        KreedaCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                KreedaAvatar(imageUrl = null, size = 40)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("RAHUL SHARMA", style = Typography.labelMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("⭐⭐⭐⭐⭐", fontSize = 10.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Amazing facility! The turf quality is excellent and staff is very helpful.",
                style = Typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun BottomBookingBar(price: String, onBookNow: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceDark,
        tonalElevation = 8.dp,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = price, style = Typography.titleLarge, color = OrangeAccent, fontWeight = FontWeight.Black)
                Text(text = "PER HOUR", style = SectionLabel, color = TextMuted)
            }
            Button(
                onClick = onBookNow,
                modifier = Modifier
                    .height(56.dp)
                    .width(160.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent)
            ) {
                Text(text = "BOOK NOW", color = Color.Black, style = Typography.labelLarge, fontWeight = FontWeight.Black)
            }
        }
    }
}