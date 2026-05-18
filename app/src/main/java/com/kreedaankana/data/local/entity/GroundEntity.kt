package com.kreedaankana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grounds")
data class GroundEntity(
    @PrimaryKey
    val groundId: Int = 0,
    val name: String = "",
    val address: String = "",
    val city: String = "",
    val sport: String = "",
    val sportIcon: String = "",
    val pricePerHour: Double = 0.0,
    val rating: Float = 0f,
    val reviews: Int = 0,
    val distance: String = "",
    val facilities: List<String> = emptyList(),
    val isAvailable: Boolean = true,
    val ownerName: String = "",
    val phone: String = "",
    val description: String = "",
    val operatingHours: String = "",
    val imageUrl: String? = null
)