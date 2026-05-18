package com.kreedaankana.util

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREFS_NAME = "kreeda_session"
    private const val KEY_GUEST_MODE = "guest_mode"
    private const val KEY_GUEST_NAME = "guest_name"
    private const val KEY_GUEST_TEAM = "guest_team"
    private const val KEY_ACTIVE_USER_ID = "active_user_id"
    private const val KEY_ACTIVE_USER_ROLE = "active_user_role"
    private const val KEY_ACTIVE_USER_EMAIL = "active_user_email"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_NOTIFICATIONS = "notifications"
    const val GUEST_USER_ID = "guest_local_user"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setGuestMode(context: Context, name: String, teamName: String) {
        prefs(context).edit()
            .putBoolean(KEY_GUEST_MODE, true)
            .putString(KEY_GUEST_NAME, name)
            .putString(KEY_GUEST_TEAM, teamName)
            .putString(KEY_ACTIVE_USER_ID, GUEST_USER_ID)
            .putString(KEY_ACTIVE_USER_ROLE, "customer")
            .apply()
    }

    fun clearGuestMode(context: Context) {
        prefs(context).edit()
            .putBoolean(KEY_GUEST_MODE, false)
            .remove(KEY_GUEST_NAME)
            .remove(KEY_GUEST_TEAM)
            .apply()
    }

    fun setActiveSession(context: Context, userId: String, role: String, email: String = "") {
        prefs(context).edit()
            .putBoolean(KEY_GUEST_MODE, false)
            .putString(KEY_ACTIVE_USER_ID, userId)
            .putString(KEY_ACTIVE_USER_ROLE, role)
            .putString(KEY_ACTIVE_USER_EMAIL, email)
            .apply()
    }

    fun clearActiveSession(context: Context) {
        prefs(context).edit()
            .putBoolean(KEY_GUEST_MODE, false)
            .remove(KEY_GUEST_NAME)
            .remove(KEY_GUEST_TEAM)
            .remove(KEY_ACTIVE_USER_ID)
            .remove(KEY_ACTIVE_USER_ROLE)
            .remove(KEY_ACTIVE_USER_EMAIL)
            .apply()
    }

    fun hasActiveSession(context: Context): Boolean =
        prefs(context).contains(KEY_ACTIVE_USER_ID)

    fun getActiveUserId(context: Context): String =
        prefs(context).getString(KEY_ACTIVE_USER_ID, "") ?: ""

    fun getActiveUserRole(context: Context): String =
        prefs(context).getString(KEY_ACTIVE_USER_ROLE, "customer") ?: "customer"

    fun getActiveUserEmail(context: Context): String =
        prefs(context).getString(KEY_ACTIVE_USER_EMAIL, "") ?: ""

    fun isGuestMode(context: Context): Boolean =
        prefs(context).getBoolean(KEY_GUEST_MODE, false)

    fun getGuestName(context: Context): String =
        prefs(context).getString(KEY_GUEST_NAME, "Guest") ?: "Guest"

    fun getGuestTeam(context: Context): String =
        prefs(context).getString(KEY_GUEST_TEAM, "My Team") ?: "My Team"

    fun setDarkMode(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun isDarkMode(context: Context): Boolean =
        prefs(context).getBoolean(KEY_DARK_MODE, false)

    fun setLanguage(context: Context, language: String) {
        prefs(context).edit().putString(KEY_LANGUAGE, language).apply()
    }

    fun getLanguage(context: Context): String =
        prefs(context).getString(KEY_LANGUAGE, "English") ?: "English"

    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
    }

    fun areNotificationsEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_NOTIFICATIONS, true)
}
