package com.kreedaankana.data.local.dao

import androidx.room.*
import com.kreedaankana.data.local.entity.GroundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ground: GroundEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(grounds: List<GroundEntity>)

    @Query("SELECT * FROM grounds ORDER BY rating DESC")
    fun getAllGrounds(): Flow<List<GroundEntity>>

    @Query("SELECT * FROM grounds WHERE groundId = :id")
    suspend fun getById(id: Int): GroundEntity?

    @Query("SELECT * FROM grounds WHERE sport = :sport ORDER BY rating DESC")
    fun getBySport(sport: String): Flow<List<GroundEntity>>

    @Query("SELECT * FROM grounds WHERE name LIKE '%' || :query || '%' ORDER BY rating DESC")
    fun searchGrounds(query: String): Flow<List<GroundEntity>>
}