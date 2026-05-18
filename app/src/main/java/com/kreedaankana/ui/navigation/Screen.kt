package com.kreedaankana.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login/{role}") {
        fun createRoute(role: String) = "login/$role"
    }
    object Signup : Screen("signup/{role}") {
        fun createRoute(role: String) = "signup/$role"
    }
    
    // Bottom Tabs
    object HomeTab : Screen("home_tab")
    object BookTab : Screen("book_tab")
    object PassesTab : Screen("passes_tab")
    object VersusTab : Screen("versus_tab")
    object LiveTab : Screen("live_tab")
    object YouTab : Screen("you_tab")

    // Customer
    object CustomerHome : Screen("customer_home") // Legacy or specific view
    object GroundListing : Screen("ground_listing/{sportType}") {
        fun createRoute(sportType: String = "all") = "ground_listing/$sportType"
    }
    object GroundDetail : Screen("ground_detail/{groundId}") {
        fun createRoute(groundId: String) = "ground_detail/$groundId"
    }
    object Booking : Screen("booking/{groundId}") {
        fun createRoute(groundId: String) = "booking/$groundId"
    }
    object BookingHistory : Screen("booking_history")
    object QRGenerator : Screen("qr_generator/{bookingId}") {
        fun createRoute(bookingId: String) = "qr_generator/$bookingId"
    }
    object TeamManagement : Screen("team_management")
    object Tournament : Screen("tournament/{tournamentId}") {
        fun createRoute(tournamentId: String) = "tournament/$tournamentId"
    }
    object Notifications : Screen("notifications")
    object CustomerProfile : Screen("customer_profile")
    object EditProfile : Screen("edit_profile")
    object MyStats : Screen("my_stats")
    object LiveScoreDetail : Screen("live_score/{matchId}") {
        fun createRoute(matchId: String) = "live_score/$matchId"
    }
    object ChallengeDetail : Screen("challenge_detail/{challengeId}") {
        fun createRoute(challengeId: String) = "challenge_detail/$challengeId"
    }
    
    // Owner
    object OwnerDashboard : Screen("owner_dashboard")
    object MyGrounds : Screen("my_grounds")
    object GroundManagement : Screen("ground_management/{groundId}") {
        fun createRoute(groundId: String) = "ground_management/$groundId"
    }
    object SlotManagement : Screen("slot_management/{groundId}") {
        fun createRoute(groundId: String) = "slot_management/$groundId"
    }
    object BookingRequests : Screen("booking_requests")
    object QRScanner : Screen("qr_scanner")
    object OwnerQRScanner : Screen("owner_qr_scanner")
    object OwnerAnalytics : Screen("owner_analytics")
    object TournamentManagement : Screen("tournament_management")
    object OwnerProfile : Screen("owner_profile")
}