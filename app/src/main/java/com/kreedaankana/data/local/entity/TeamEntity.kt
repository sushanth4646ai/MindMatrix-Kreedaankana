package com.kreedaankana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_teams")
data class TeamEntity(
    @PrimaryKey
    val teamId: Int = 0,
    val teamName: String = "",
    val captainName: String = "",
    val captainPhone: String = "",
    val sport: String = "",
    val players: List<String> = emptyList(),
    val totalMatches: Int = 0,
    val wins: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)