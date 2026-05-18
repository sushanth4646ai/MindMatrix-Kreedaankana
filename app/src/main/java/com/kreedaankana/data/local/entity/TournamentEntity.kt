package com.kreedaankana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tournaments")
data class TournamentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tournamentId: String = "",
    val name: String = "",
    val groundId: String = "",
    val sportType: String = "",
    val entryFee: Double = 0.0,
    val startDate: String = "",
    val endDate: String = "",
    val teamsJson: String = "[]",
    val roundsJson: String = "[]",
    val status: String = "upcoming",
    val winner: String = "",
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
