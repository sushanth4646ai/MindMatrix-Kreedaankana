package com.kreedaankana.data.local.dao

import androidx.room.*
import com.kreedaankana.data.local.entity.GroundImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroundImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: GroundImageEntity)

    @Query("SELECT * FROM ground_images WHERE groundId = :groundId ORDER BY createdAt DESC")
    fun getByGroundId(groundId: String): Flow<List<GroundImageEntity>>

    @Query("DELETE FROM ground_images WHERE groundId = :groundId")
    suspend fun deleteByGroundId(groundId: String)

    @Delete
    suspend fun delete(image: GroundImageEntity)
}
