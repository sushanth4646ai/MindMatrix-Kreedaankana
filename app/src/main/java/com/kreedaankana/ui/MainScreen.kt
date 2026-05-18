package com.kreedaankana.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kreedaankana.KreedaApplication
import com.kreedaankana.ui.components.KreedaBottomNav
import com.kreedaankana.ui.customer.*
import com.kreedaankana.ui.navigation.Screen
import com.kreedaankana.viewmodel.AuthViewModel

@Composable
fun MainScreen(
    parentNavController: NavHostController,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.HomeTab.route
    
    val context = LocalContext.current
    val application = context.applicationContext as KreedaApplication
    val factory = application.viewModelFactory

    Scaffold(
        bottomBar = {
            KreedaBottomNav(
                selectedRoute = currentRoute,
                onRouteSelected = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.HomeTab.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.HomeTab.route) {
                CustomerHomeScreen(
                    onNavigateToBooking = { navController.navigate(Screen.BookTab.route) },
                    onNavigateToQR = { navController.navigate(Screen.PassesTab.route) },
                    onNavigateToTournaments = { navController.navigate(Screen.BookTab.route) },
                    onNavigateToGrounds = { navController.navigate(Screen.BookTab.route) },
                    groundDao = application.database.groundDao()
                )
            }
            
            composable(Screen.BookTab.route) {
                GroundListingScreen(
                    sportType = "all",
                    onGroundClick = { id -> parentNavController.navigate(Screen.GroundDetail.createRoute(id)) },
                    onBack = { /* Handled by bottom nav */ }
                )
            }
            
            composable(Screen.PassesTab.route) {
                PassesScreen(
                    onBack = { /* Handled by bottom nav */ },
                    onViewPass = { bookingId -> parentNavController.navigate(Screen.QRGenerator.createRoute(bookingId)) }
                )
            }
            
            composable(Screen.VersusTab.route) {
                VersusScreen(
                    onBack = { /* Handled by bottom nav */ }
                )
            }
            
            composable(Screen.LiveTab.route) {
                LiveMatchesScreen(
                    onBack = { /* Handled by bottom nav */ }
                )
            }
            
            composable(Screen.YouTab.route) {
                CustomerProfileScreen(
                    onBack = { /* Handled by bottom nav */ },
                    onEditProfile = { /* Navigate to edit */ },
                    onLogout = { parentNavController.navigate(Screen.Login.route) }
                )
            }
        }
    }
}

@Composable
fun LiveScorePlaceholder(onBack: () -> Unit) {
    // Basic placeholder
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.Text(
            text = "LIVE SCORES COMING SOON",
            style = com.kreedaankana.ui.theme.Typography.headlineMedium,
            color = com.kreedaankana.ui.theme.OrangeAccent
        )
    }
}
