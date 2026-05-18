package com.kreedaankana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verification_logs")
data class VerificationLogEntity(
    @PrimaryKey(autoGenerate = true)
    val logId: Long = 0L,
    val bookingId: String = "",
    val scannerId: String = "",
    val scanTimestamp: Long = System.currentTimeMillis(),
    val result: String = "SUCCESS", // SUCCESS, EXPIRED, ALREADY_USED, INVALID
    val message: String = "",
    val offlineVerified: Boolean = true,
    val deviceId: String = "",
    val isSynced: Boolean = false
)
