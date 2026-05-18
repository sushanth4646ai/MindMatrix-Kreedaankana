package com.kreedaankana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val matchId: String = "",
    val teamA: String = "",
    val teamB: String = "",
    val teamAScore: Int = 0,
    val teamBScore: Int = 0,
    val score: String = "0-0",
    val status: String = "scheduled",
    val sportType: String = "",
    val date: String = "",
    val startTime: Long = 0,
    val endTime: Long = 0,
    val winner: String = "",
    val syncStatus: String = "pending",
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)
