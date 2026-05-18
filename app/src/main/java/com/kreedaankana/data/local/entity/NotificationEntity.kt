package com.kreedaankana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val message: String = "",
    val type: String = "GENERAL", // BOOKING, TOURNAMENT, CHALLENGE, LIVE
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val deepLink: String = ""
)
