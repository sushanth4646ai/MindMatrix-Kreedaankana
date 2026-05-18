package com.kreedaankana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String = "",
    val itemId: String = "",
    val itemType: String = "",
    val title: String = "",
    val subtitle: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
