package com.kreedaankana.util

object Constants {
    const val PREFS_NAME = "kreeda_ankana_prefs"
    const val KEY_USER_ID = "user_id"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val GUEST_USER_ID = "guest_local_user"
    const val MAX_SLOTS_PER_TIME = 2
    const val MAX_BOOKINGS_PER_DAY = 3
    const val BOOKING_EXPIRY_MINUTES = 30L

    val SPORT_TYPES = listOf("Cricket", "Football", "Basketball", "Badminton", "Tennis", "Volleyball")
    val SKILL_LEVELS = listOf("Beginner", "Intermediate", "Advanced", "Professional")
    val TIME_SLOTS = listOf(
        "06:00 AM - 07:00 AM",
        "07:00 AM - 08:00 AM",
        "08:00 AM - 09:00 AM",
        "09:00 AM - 10:00 AM",
        "10:00 AM - 11:00 AM",
        "11:00 AM - 12:00 PM",
        "12:00 PM - 01:00 PM",
        "01:00 PM - 02:00 PM",
        "02:00 PM - 03:00 PM",
        "03:00 PM - 04:00 PM",
        "04:00 PM - 05:00 PM",
        "05:00 PM - 06:00 PM",
        "06:00 PM - 07:00 PM",
        "07:00 PM - 08:00 PM",
        "08:00 PM - 09:00 PM"
    )
}
