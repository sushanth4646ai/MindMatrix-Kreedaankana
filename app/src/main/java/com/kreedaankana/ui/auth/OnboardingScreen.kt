package com.kreedaankana.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kreedaankana.ui.theme.*

data class OnboardingPage(
    val icon: String,
    val title: String,
    val subtitle: String,
    val backgroundGradient: List<Color>
)

private val onboardingPages = listOf(
    OnboardingPage(
        icon = "🏟️",
        title = "Discover Premium Grounds",
        subtitle = "Find and book the best sports venues in your city. From football turfs to cricket grounds.",
        backgroundGradient = listOf(NeonGreen, Color(0xFF00C853))
    ),
    OnboardingPage(
        icon = "⚽",
        title = "Play With Your Team",
        subtitle = "Create your team, invite friends, and book slots together. Never play alone.",
        backgroundGradient = listOf(ElectricBlue, Color(0xFF0066FF))
    ),
    OnboardingPage(
        icon = "🏆",
        title = "Join Tournaments",
        subtitle = "Compete in weekend leagues and win exciting prizes. Show your skills.",
        backgroundGradient = listOf(OrangeAccent, DeepOrange)
    ),
    OnboardingPage(
        icon = "🎫",
        title = "Quick QR Entry",
        subtitle = "Get instant digital passes. No queues, no hassle. Just show and play.",
        backgroundGradient = listOf(Purple, Color(0xFF6366F1))
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToLogin: (String) -> Unit,
    onNavigateToGuest: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkCharcoal, SurfaceDark, DarkCharcoal)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            LogoHeader()
            
            Spacer(modifier = Modifier.height(48.dp))
            
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                OnboardingPageContent(page = onboardingPages[page])
            }
            
            PageIndicator(
                currentPage = pagerState.currentPage,
                totalPages = onboardingPages.size
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (pagerState.currentPage == onboardingPages.size - 1) {
                GetStartedButton(
                    onGetStarted = { onNavigateToLogin("customer") }
                )
            } else {
                SkipButton(
                    onSkip = { 
                        kotlinx.coroutines.runBlocking { 
                            pagerState.scrollToPage(onboardingPages.size - 1) 
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            RoleSelectionSection(
                onCustomerLogin = { onNavigateToLogin("customer") },
                onOwnerLogin = { onNavigateToLogin("owner") },
                onGuestExplore = onNavigateToGuest
            )
        }
    }
}

@Composable
private fun LogoHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "⚽", fontSize = 32.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "KREEDA ANKANA",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    val infiniteTransition = rememberInfiniteTransition(label = "onboarding")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_offset"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(y = offsetY.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(60.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(page.backgroundGradient)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = page.icon,
                        fontSize = 80.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = page.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun PageIndicator(
    currentPage: Int,
    totalPages: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(totalPages) { index ->
            val isSelected = index == currentPage
            val width by animateFloatAsState(
                targetValue = if (isSelected) 32f else 8f,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "indicator_width"
            )
            
            Box(
                modifier = Modifier
                    .width(width.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (isSelected) NeonGreen else TextMuted.copy(alpha = 0.5f)
                    )
            )
        }
    }
}

@Composable
private fun GetStartedButton(onGetStarted: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "btn")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btn_scale"
    )

    Button(
        onClick = onGetStarted,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Text(
            text = "Get Started →",
            style = MaterialTheme.typography.titleMedium,
            color = DarkCharcoal,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SkipButton(onSkip: () -> Unit) {
    TextButton(
        onClick = onSkip,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Skip to End",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )
    }
}

@Composable
private fun RoleSelectionSection(
    onCustomerLogin: () -> Unit,
    onOwnerLogin: () -> Unit,
    onGuestExplore: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = onCustomerLogin,
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceVariantDark
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "🏃", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Play",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = onOwnerLogin,
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceVariantDark
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "🏟️", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Manage",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onGuestExplore) {
            Text(
                text = "Continue as Guest →",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
    }
}