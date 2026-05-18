package com.kreedaankana.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreedaankana.ui.theme.*

data class BottomNavItem(
    val title: String,
    val icon: String,
    val route: String
)

@Composable
fun PremiumBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    items: List<BottomNavItem> = defaultNavItems,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = currentRoute == item.route
                    NavItem(item = item, isSelected = isSelected, onClick = { onNavigate(item.route) })
                }
            }
        }
    }
}

private val defaultNavItems = listOf(
    BottomNavItem("Home", "🏠", "home"),
    BottomNavItem("Grounds", "🏟️", "grounds"),
    BottomNavItem("Tournaments", "🏆", "tournaments"),
    BottomNavItem("Profile", "👤", "profile")
)

@Composable
private fun NavItem(item: BottomNavItem, isSelected: Boolean, onClick: () -> Unit) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "nav_scale"
    )

    Column(
        modifier = Modifier.clickable { onClick() }.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(if (isSelected) Brush.radialGradient(listOf(NeonGreen.copy(alpha = 0.3f), Color.Transparent)) else Brush.radialGradient(listOf(Color.Transparent, Color.Transparent))),
            contentAlignment = Alignment.Center
        ) {
            Text(text = item.icon, fontSize = if (isSelected) 26.sp else 24.sp)
            if (isSelected) {
                Box(modifier = Modifier.align(Alignment.BottomCenter).offset(y = 4.dp).size(6.dp).clip(CircleShape).background(NeonGreen))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        AnimatedVisibility(visible = isSelected, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            Text(text = item.title, style = MaterialTheme.typography.labelSmall, color = NeonGreen, fontWeight = FontWeight.Bold)
        }
    }
}