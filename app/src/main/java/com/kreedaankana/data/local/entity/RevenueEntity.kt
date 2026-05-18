package com.kreedaankana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "revenue")
data class RevenueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double = 0.0,
    val date: String = "",
    val bookingId: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
