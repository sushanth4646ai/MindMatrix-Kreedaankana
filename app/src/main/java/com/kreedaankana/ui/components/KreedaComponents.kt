package com.kreedaankana.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kreedaankana.ui.theme.*

@Composable
fun KreedaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Surface(
        modifier = cardModifier
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp),
        content = {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    )
}

@Composable
fun KreedaAvatar(
    imageUrl: String?,
    size: Int = 40,
    borderWidth: Int = 2,
    placeholder: String = "U"
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .border(borderWidth.dp, OrangeAccent, CircleShape)
            .padding(2.dp)
            .clip(CircleShape)
            .background(SurfaceVariantDark),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Profile Avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = placeholder,
                style = Typography.titleLarge,
                color = OrangeAccent,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun KreedaSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search for grounds, tournaments..."
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
        placeholder = { Text(placeholder, color = TextMuted) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OrangeAccent) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SurfaceDark,
            unfocusedContainerColor = SurfaceDark,
            disabledContainerColor = SurfaceDark,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = OrangeAccent
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
fun KreedaSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String = "VIEW ALL",
    onActionClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title.uppercase(),
            style = SectionLabel,
            color = TextSecondary
        )
        Text(
            text = actionText,
            style = Typography.labelMedium,
            color = OrangeAccent,
            modifier = Modifier.clickable { onActionClick() }
        )
    }
}

@Composable
fun SportTag(
    name: String,
    color: Color = OrangeAccent
) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            text = name.uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = Typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun KreedaStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    color: Color = OrangeAccent
) {
    Column(
        modifier = modifier
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .background(SurfaceDark, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = Typography.headlineMedium,
            color = color,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label.uppercase(),
            style = Typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
fun KreedaBottomNav(
    selectedRoute: String,
    onRouteSelected: (String) -> Unit
) {
    val items = listOf(
        KreedaBottomNavItem("HOME", "home_tab", Icons.Default.Search), // Using Search as placeholder
        KreedaBottomNavItem("BOOK", "book_tab", Icons.Default.Search),
        KreedaBottomNavItem("PASSES", "passes_tab", Icons.Default.Search),
        KreedaBottomNavItem("VERSUS", "versus_tab", Icons.Default.Search),
        KreedaBottomNavItem("LIVE", "live_tab", Icons.Default.Search),
        KreedaBottomNavItem("YOU", "you_tab", Icons.Default.Search)
    )

    NavigationBar(
        containerColor = DarkCharcoal,
        tonalElevation = 8.dp,
        modifier = Modifier.border(BorderStroke(0.5.dp, CardBorder), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        items.forEach { item ->
            val isSelected = selectedRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onRouteSelected(item.route) },
                icon = { 
                    Icon(
                        imageVector = item.icon, 
                        contentDescription = item.title,
                        tint = if (isSelected) OrangeAccent else TextMuted
                    )
                },
                label = { 
                    Text(
                        text = item.title,
                        style = Typography.labelSmall,
                        color = if (isSelected) OrangeAccent else TextMuted,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class KreedaBottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)
