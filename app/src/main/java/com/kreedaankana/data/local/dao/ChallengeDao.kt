package com.kreedaankana.data.local.dao

import androidx.room.*
import com.kreedaankana.data.local.entity.ChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges ORDER BY createdAt DESC")
    fun getAllChallenges(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE sportType = :sportType")
    fun getChallengesBySport(sportType: String): Flow<List<ChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(challenge: ChallengeEntity)

    @Update
    suspend fun update(challenge: ChallengeEntity)

    @Delete
    suspend fun delete(challenge: ChallengeEntity)
}
