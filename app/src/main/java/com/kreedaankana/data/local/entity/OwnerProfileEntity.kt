package com.kreedaankana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "owner_profiles")
data class OwnerProfileEntity(
    @PrimaryKey
    val ownerId: String,
    val groundName: String = "",
    val groundLocation: String = "",
    val sportsSupported: String = "", // Comma-separated or serialized list
    val contactNumber: String = "",
    val operatingHours: String = "",
    val revenue: Double = 0.0,
    val profileImageUrl: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)
