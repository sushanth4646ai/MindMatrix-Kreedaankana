package com.kreedaankana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ground_images")
data class GroundImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val groundId: String = "",
    val imageUrl: String = "",
    val isLocal: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
