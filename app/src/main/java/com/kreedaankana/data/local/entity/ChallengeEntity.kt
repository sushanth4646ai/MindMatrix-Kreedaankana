package com.kreedaankana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey
    val challengeId: String = "",
    val teamName: String = "",
    val sportType: String = "",
    val location: String = "",
    val description: String = "",
    val challengeDate: String = "",
    val status: String = "OPEN", // OPEN, ACCEPTED, COMPLETED
    val createdBy: String = "", // User ID
    val repliesJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis()
)
