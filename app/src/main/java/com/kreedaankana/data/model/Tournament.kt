package com.kreedaankana.data.model

data class Tournament(
    val id: String = "",
    val name: String = "",
    val sportType: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val teams: List<String> = emptyList(),
    val rounds: List<Round> = emptyList(),
    val status: String = "upcoming",
    val winner: String = "",
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class Round(
    val roundNumber: Int = 0,
    val matches: List<TournamentMatch> = emptyList()
)

data class TournamentMatch(
    val matchId: String = "",
    val teamA: String = "",
    val teamB: String = "",
    val winner: String = "",
    val scoreA: Int = 0,
    val scoreB: Int = 0,
    val status: String = "scheduled"
)
