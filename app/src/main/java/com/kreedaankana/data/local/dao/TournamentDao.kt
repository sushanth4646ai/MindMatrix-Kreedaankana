package com.kreedaankana.data.local.dao

import androidx.room.*
import com.kreedaankana.data.local.entity.TournamentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TournamentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tournament: TournamentEntity)

    @Update
    suspend fun update(tournament: TournamentEntity)

    @Delete
    suspend fun delete(tournament: TournamentEntity)

    @Query("SELECT * FROM tournaments ORDER BY createdAt DESC")
    fun getAll(): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE tournamentId = :id")
    suspend fun getByTournamentId(id: String): TournamentEntity?

    @Query("SELECT * FROM tournaments WHERE createdBy = :ownerId ORDER BY createdAt DESC")
    fun getByOwnerId(ownerId: String): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE status = :status ORDER BY createdAt DESC")
    fun getByStatus(status: String): Flow<List<TournamentEntity>>

    @Query("UPDATE tournaments SET status = :status WHERE tournamentId = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("UPDATE tournaments SET teamsJson = :teamsJson WHERE tournamentId = :id")
    suspend fun updateTeams(id: String, teamsJson: String)

    @Query("UPDATE tournaments SET roundsJson = :roundsJson WHERE tournamentId = :id")
    suspend fun updateRounds(id: String, roundsJson: String)

    @Query("UPDATE tournaments SET winner = :winner WHERE tournamentId = :id")
    suspend fun declareWinner(id: String, winner: String)

    @Query("DELETE FROM tournaments WHERE tournamentId = :id")
    suspend fun deleteByTournamentId(id: String)
}
