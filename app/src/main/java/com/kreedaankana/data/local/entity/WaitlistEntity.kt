package com.kreedaankana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "waitlist")
data class WaitlistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bookingId: String = "",
    val teamId: String = "",
    val teamName: String = "",
    val customerId: String = "",
    val userId: String = "",
    val date: String = "",
    val timeSlot: String = "",
    val sportType: String = "",
    val position: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)
