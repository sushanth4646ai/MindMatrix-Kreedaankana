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
import androidx.compose.material.icons.filled.Notifications
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kreedaankana.KreedaApplication
import com.kreedaankana.data.local.entity.NotificationEntity
import com.kreedaankana.ui.theme.OrangeAccent
import com.kreedaankana.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val application = context.applicationContext as KreedaApplication
    val viewModel: NotificationViewModel = viewModel(factory = application.viewModelFactory)
    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("NOTIFICATIONS", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OrangeAccent)
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.markAllAsRead() }) {
                        Text("MARK ALL READ", color = OrangeAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F0F0F),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0F0F0F)
    ) { padding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("NO NEW NOTIFICATIONS", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(notifications) { notification ->
                    NotificationItem(notification) {
                        viewModel.markAsRead(notification.id)
                    }
                    HorizontalDivider(color = Color(0xFF222222))
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(notification: NotificationEntity, onClick: () -> Unit) {
    val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
    val date = sdf.format(Date(notification.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (notification.isRead) Color.Transparent else Color(0xFF1A1A1A))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .offset(y = 6.dp)
                .clip(CircleShape)
                .background(if (notification.isRead) Color.Transparent else OrangeAccent)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.title.uppercase(),
                    fontSize = 14.sp,
                    color = if (notification.isRead) Color.Gray else Color.White,
                    fontWeight = FontWeight.Bold
                )
                // Type badge
                Box(
                    modifier = Modifier
                        .background(typeColor(notification.type), RoundedCornerShape(3.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(notification.type, fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Black)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.message,
                fontSize = 13.sp,
                color = if (notification.isRead) Color.DarkGray else Color.LightGray
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = date,
                fontSize = 10.sp,
                color = Color.DarkGray
            )
        }
    }
}

private fun typeColor(type: String) = when (type) {
    "BOOKING"    -> Color(0xFF1565C0)
    "TOURNAMENT" -> Color(0xFF6A1B9A)
    "CHALLENGE"  -> Color(0xFFD84315)
    "LIVE"       -> Color(0xFFC62828)
    else         -> Color(0xFF37474F)
}
