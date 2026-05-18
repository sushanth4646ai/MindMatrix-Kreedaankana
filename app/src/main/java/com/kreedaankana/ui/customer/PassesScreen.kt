package com.kreedaankana.ui.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kreedaankana.data.local.entity.BookingEntity
import com.kreedaankana.ui.components.KreedaCard
import com.kreedaankana.ui.theme.OrangeAccent
import com.kreedaankana.viewmodel.BookingViewModel

@Composable
fun PassesScreen(
    onViewPass: (String) -> Unit,
    onBack: () -> Unit
) {
    // We can use a simplified BookingHistoryScreen logic here
    BookingHistoryScreen(
        onBack = onBack,
        onViewPass = onViewPass
    )
}
