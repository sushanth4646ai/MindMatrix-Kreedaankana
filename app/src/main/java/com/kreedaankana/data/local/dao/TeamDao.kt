package com.kreedaankana.data.local.dao

import androidx.room.*
import com.kreedaankana.data.local.entity.TeamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(team: TeamEntity)

    @Query("SELECT * FROM my_teams ORDER BY teamId DESC")
    fun getAllTeams(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM my_teams WHERE sport = :sport")
    fun getBySport(sport: String): Flow<List<TeamEntity>>

    @Query("SELECT * FROM my_teams WHERE teamId = :id")
    suspend fun getById(id: Int): TeamEntity?

    @Query("DELETE FROM my_teams WHERE teamId = :id")
    suspend fun delete(id: Int)
}