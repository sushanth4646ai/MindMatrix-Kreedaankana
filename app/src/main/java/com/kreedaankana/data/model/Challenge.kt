package com.kreedaankana.data.model

data class Challenge(
    val id: String = "",
    val teamId: Int = 0,
    val teamName: String = "",
    val userId: String = "",
    val sportType: String = "",
    val skillLevel: String = "",
    val dateTime: String = "",
    val message: String = "",
    val status: String = "open",
    val acceptedBy: String = "",
    val acceptedTeamName: String = "",
    val replies: List<ChallengeReply> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class ChallengeReply(
    val userId: String = "",
    val teamName: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
