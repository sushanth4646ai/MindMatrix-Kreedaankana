package com.kreedaankana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "owner_grounds")
data class OwnerGroundEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val groundId: String = "",
    val ownerId: String = "",
    val name: String = "",
    val village: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val sports: String = "",
    val openingTime: String = "06:00",
    val closingTime: String = "22:00",
    val slotDuration: Int = 120,
    val pricePerSlot: Double = 0.0,
    val amenities: String = "",
    val hasParking: Boolean = false,
    val hasLighting: Boolean = false,
    val hasWashroom: Boolean = false,
    val hasDrinkingWater: Boolean = false,
    val seatingCapacity: Int = 0,
    val rules: String = "",
    val images: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "SYNCED"
)
