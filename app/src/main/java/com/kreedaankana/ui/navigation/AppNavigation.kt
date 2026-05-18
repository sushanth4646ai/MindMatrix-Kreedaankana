package com.kreedaankana.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import com.kreedaankana.data.local.entity.BookingEntity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kreedaankana.KreedaApplication
import com.kreedaankana.ui.auth.OnboardingScreen
import com.kreedaankana.ui.auth.LoginScreen
import com.kreedaankana.ui.auth.SignupScreen
import com.kreedaankana.ui.customer.*
import com.kreedaankana.ui.owner.*
import com.kreedaankana.ui.splash.SplashScreen
import com.kreedaankana.viewmodel.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val application = context.applicationContext as KreedaApplication
    val factory = application.viewModelFactory

    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val bookingViewModel: BookingViewModel = viewModel(factory = factory)
    val ownerDashboardVm: OwnerDashboardViewModel = viewModel(factory = factory)
    val ownerGroundVm: OwnerGroundViewModel = viewModel(factory = factory)
    val ownerSlotVm: OwnerSlotViewModel = viewModel(factory = factory)
    val ownerBookingVm: OwnerBookingRequestViewModel = viewModel(factory = factory)
    val ownerTournamentVm: OwnerTournamentViewModel = viewModel(factory = factory)
    val ownerLiveScoreVm: OwnerLiveScoreViewModel = viewModel(factory = factory)
    val ownerAnalyticsVm: OwnerAnalyticsViewModel = viewModel(factory = factory)
    val ownerPaymentVm: OwnerPaymentViewModel = viewModel(factory = factory)

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) { popUpTo(Screen.Splash.route) { inclusive = true } } })
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToLogin = { role -> navController.navigate(Screen.Login.createRoute(role)) },
                onNavigateToGuest = { navController.navigate(Screen.GroundListing.createRoute()) }
            )
        }

        // Auth Routes
        composable(Screen.Login.route, arguments = listOf(navArgument("role") { type = NavType.StringType })) {
            val role = it.arguments?.getString("role") ?: "customer"
            LoginScreen(role = role,
                onLoginSuccess = { navController.navigate(if (role == "owner") Screen.OwnerDashboard.route else Screen.CustomerHome.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                onNavigateToSignup = { r -> navController.navigate(Screen.Signup.createRoute(r)) }, authViewModel = authViewModel)
        }

        composable(Screen.Signup.route, arguments = listOf(navArgument("role") { type = NavType.StringType })) {
            val role = it.arguments?.getString("role") ?: "customer"
            SignupScreen(role = role,
                onSignupSuccess = { navController.navigate(if (role == "owner") Screen.OwnerDashboard.route else Screen.CustomerHome.route) { popUpTo(Screen.Signup.route) { inclusive = true } } },
                onNavigateToLogin = { r -> navController.navigate(Screen.Login.createRoute(r)) }, authViewModel = authViewModel)
        }

        // Customer Routes
        composable(Screen.CustomerHome.route) {
            com.kreedaankana.ui.MainScreen(
                parentNavController = navController,
                authViewModel = authViewModel
            )
        }


        composable(Screen.GroundListing.route, arguments = listOf(navArgument("sportType") { type = NavType.StringType; defaultValue = "all" })) {
            GroundListingScreen(sportType = it.arguments?.getString("sportType") ?: "all",
                onGroundClick = { id -> navController.navigate(Screen.GroundDetail.createRoute(id)) }, onBack = { navController.popBackStack() })
        }

        composable(Screen.GroundDetail.route, arguments = listOf(navArgument("groundId") { type = NavType.StringType })) {
            GroundDetailScreen(groundId = it.arguments?.getString("groundId") ?: "", onBack = { navController.popBackStack() },
                onBookNow = { id -> navController.navigate(Screen.Booking.createRoute(id)) }, onViewMap = { })
        }

        composable(Screen.Booking.route, arguments = listOf(navArgument("groundId") { type = NavType.StringType })) {
            BookingScreen(groundId = it.arguments?.getString("groundId") ?: "",
                onBookingSuccess = { bookingId -> navController.navigate(Screen.QRGenerator.createRoute(bookingId)) },
                onBack = { navController.popBackStack() }, onCreateTeam = { navController.navigate(Screen.TeamManagement.route) })
        }

        composable(Screen.BookingHistory.route) {
            BookingHistoryScreen(onBack = { navController.popBackStack() },
                onViewPass = { bookingId -> navController.navigate(Screen.QRGenerator.createRoute(bookingId)) })
        }

        composable(Screen.QRGenerator.route, arguments = listOf(navArgument("bookingId") { type = NavType.StringType })) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            var booking by remember { mutableStateOf<BookingEntity?>(null) }
            
            LaunchedEffect(bookingId) {
                booking = application.database.bookingDao().getByBookingId(bookingId)
            }
            
            booking?.let {
                QRDetailScreen(
                    booking = it,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.TeamManagement.route) {
            TeamManagementScreen(onBack = { navController.popBackStack() }, onTeamSaved = { navController.popBackStack() })
        }

        composable(Screen.CustomerProfile.route) {
            CustomerProfileScreen(
                onBack = { navController.popBackStack() },
                onEditProfile = { navController.navigate(Screen.TeamManagement.route) },
                onLogout = { 
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.createRoute("customer")) { popUpTo(0) }
                }
            )
        }

        // Owner Routes
        composable(Screen.OwnerDashboard.route) {
            OwnerDashboardScreen(
                viewModel = ownerDashboardVm,
                onNavigateToQRScanner = { navController.navigate(Screen.OwnerQRScanner.route) },
                onNavigateToGrounds = { navController.navigate(Screen.MyGrounds.route) },
                onNavigateToSlots = { navController.navigate(Screen.SlotManagement.createRoute("all")) },
                onNavigateToBookings = { navController.navigate(Screen.BookingRequests.route) },
                onNavigateToTournaments = { navController.navigate(Screen.TournamentManagement.route) },
                onNavigateToLiveScores = { navController.navigate(Screen.OwnerAnalytics.route) },
                onNavigateToAnalytics = { navController.navigate(Screen.OwnerAnalytics.route) },
                onNavigateToPayments = { navController.navigate(Screen.OwnerProfile.route) }
            )
        }

        composable(Screen.MyGrounds.route) {
            OwnerGroundListScreen(viewModel = ownerGroundVm, onBack = { navController.popBackStack() },
                onAddGround = { navController.navigate(Screen.GroundManagement.createRoute("new")) },
                onEditGround = { id -> navController.navigate(Screen.GroundManagement.createRoute(id)) })
        }

        composable(Screen.GroundManagement.route, arguments = listOf(navArgument("groundId") { type = NavType.StringType })) {
            OwnerGroundFormScreen(groundId = it.arguments?.getString("groundId"), viewModel = ownerGroundVm,
                onBack = { navController.popBackStack() }, onSaved = { navController.popBackStack() })
        }

        composable(Screen.SlotManagement.route, arguments = listOf(navArgument("groundId") { type = NavType.StringType })) {
            OwnerSlotScreen(viewModel = ownerSlotVm, onBack = { navController.popBackStack() })
        }

        composable(Screen.BookingRequests.route) {
            OwnerBookingRequestScreen(viewModel = ownerBookingVm, onBack = { navController.popBackStack() })
        }

        composable(Screen.TournamentManagement.route) {
            OwnerTournamentScreen(viewModel = ownerTournamentVm, onBack = { navController.popBackStack() },
                onCreateTournament = { navController.navigate(Screen.Tournament.createRoute("new")) })
        }

        composable(Screen.Tournament.route, arguments = listOf(navArgument("tournamentId") { type = NavType.StringType })) {
            OwnerTournamentFormScreen(viewModel = ownerTournamentVm, onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() })
        }

        composable(Screen.OwnerAnalytics.route) {
            OwnerAnalyticsScreen(viewModel = ownerAnalyticsVm, onBack = { navController.popBackStack() })
        }

        composable(Screen.OwnerProfile.route) {
            OwnerPaymentScreen(viewModel = ownerPaymentVm, onBack = { navController.popBackStack() })
        }

        composable(Screen.OwnerQRScanner.route) {
            val ownerQRScannerVm: OwnerQRScannerViewModel = viewModel(factory = factory)
            OwnerQRScannerScreen(onNavigateBack = { navController.popBackStack() }, viewModel = ownerQRScannerVm)
        }

        composable(Screen.QRScanner.route) {
            com.kreedaankana.ui.customer.QRScannerScreen(
                onBack = { navController.popBackStack() },
                onCheckIn = { navController.popBackStack() })
        }

        composable("notifications") {
            NotificationScreen(onBack = { navController.popBackStack() })
        }
    }
}
