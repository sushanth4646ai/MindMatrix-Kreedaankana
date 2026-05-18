package com.kreedaankana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "owner_verifications")
data class OwnerVerificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bookingId: String = "",
    val ownerId: String = "",
    val customerId: String = "",
    val teamName: String = "",
    val verificationTime: Long = System.currentTimeMillis(),
    val qrToken: String = "",
    val result: String = "SUCCESS"
)
