package com.kreedaankana.ui.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.firestore.FirebaseFirestore
import com.kreedaankana.data.local.AppDatabase
import com.kreedaankana.data.local.entity.GroundEntity
import com.kreedaankana.data.repository.GroundSyncRepository
import com.kreedaankana.ui.components.*
import com.kreedaankana.ui.theme.*

@Composable
fun GroundListingScreen(
    sportType: String = "all",
    onGroundClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val syncRepo = remember { GroundSyncRepository(database.groundDao(), FirebaseFirestore.getInstance()) }
    val allGrounds by database.groundDao().getAllGrounds().collectAsStateWithLifecycle(initialValue = emptyList())

    LaunchedEffect(Unit) {
        syncRepo.syncFromFirestore()
    }
    
    var selectedCategory by remember { mutableStateOf(if (sportType == "all") "ALL" else sportType.uppercase()) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredGrounds = remember(allGrounds, selectedCategory, searchQuery) {
        allGrounds.filter { ground ->
            val matchesSport = selectedCategory == "ALL" || ground.sport.uppercase() == selectedCategory
            val matchesSearch = searchQuery.isEmpty() || ground.name.contains(searchQuery, ignoreCase = true)
            matchesSport && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            ListingHeader(onBack = onBack)
        },
        containerColor = DarkCharcoal
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            KreedaSearchBar(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                placeholder = "Search grounds..."
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                val categories = listOf("ALL", "CRICKET", "FOOTBALL", "BADMINTON", "VOLLEYBALL")
                items(categories) { category ->
                    FilterChip(
                        label = category,
                        isSelected = selectedCategory == category,
                        onClick = { selectedCategory = category }
                    )
                }
            }

            Text(
                text = "${filteredGrounds.size} GROUNDS FOUND",
                style = Typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredGrounds) { ground ->
                    GroundListItem(
                        ground = ground,
                        onClick = { onGroundClick(ground.groundId.toString()) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ListingHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "BOOK",
                    style = SectionLabel,
                    color = TextSecondary
                )
                Text(
                    text = "EXPLORE GROUNDS",
                    style = Typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Black
                )
            }
        }
        IconButton(onClick = { }) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = TextPrimary)
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = if (isSelected) OrangeAccent else SurfaceDark,
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) null else BorderStroke(1.dp, CardBorder)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            style = Typography.labelMedium,
            color = if (isSelected) Color.Black else TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}