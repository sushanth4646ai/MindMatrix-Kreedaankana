package com.kreedaankana.data.model

data class LiveMatch(
    val matchId: String = "",
    val teamA: String = "",
    val teamB: String = "",
    val scoreA: Int = 0,
    val scoreB: Int = 0,
    val sportType: String = "",
    val status: String = "live",
    val currentInning: Int = 1,
    val timeElapsed: Long = 0,
    val events: List<MatchEvent> = emptyList(),
    val spectators: Int = 0,
    val startTime: Long = System.currentTimeMillis()
)

data class MatchEvent(
    val type: String = "",
    val team: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
