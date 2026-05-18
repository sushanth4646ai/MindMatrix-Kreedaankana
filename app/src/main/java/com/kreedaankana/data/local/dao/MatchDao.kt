package com.kreedaankana.data.local.dao

import androidx.room.*
import com.kreedaankana.data.local.entity.MatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(match: MatchEntity)

    @Update
    suspend fun update(match: MatchEntity)

    @Query("SELECT * FROM matches ORDER BY startTime DESC")
    fun getAll(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE matchId = :id")
    suspend fun getByMatchId(id: String): MatchEntity?

    @Query("SELECT * FROM matches WHERE status = :status ORDER BY startTime DESC")
    fun getByStatus(status: String): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE sportType = :sport ORDER BY startTime DESC")
    fun getBySport(sport: String): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE teamA = :team OR teamB = :team ORDER BY startTime DESC")
    fun getByTeam(team: String): Flow<List<MatchEntity>>

    @Query("UPDATE matches SET teamAScore = :scoreA, teamBScore = :scoreB, score = :displayScore WHERE matchId = :id")
    suspend fun updateScore(id: String, scoreA: Int, scoreB: Int, displayScore: String)

    @Query("UPDATE matches SET status = :status WHERE matchId = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("UPDATE matches SET winner = :winner, status = 'completed' WHERE matchId = :id")
    suspend fun declareWinner(id: String, winner: String)

    @Query("UPDATE matches SET syncStatus = :sync WHERE matchId = :id")
    suspend fun updateSyncStatus(id: String, sync: String)

    @Query("DELETE FROM matches WHERE matchId = :id")
    suspend fun deleteByMatchId(id: String)

    @Query("SELECT * FROM matches WHERE syncStatus = 'pending'")
    suspend fun getPendingSync(): List<MatchEntity>
}

@Dao
interface RevenueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(revenue: com.kreedaankana.data.local.entity.RevenueEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<com.kreedaankana.data.local.entity.RevenueEntity>)

    @Query("SELECT * FROM revenue WHERE date = :date ORDER BY createdAt DESC")
    fun getByDate(date: String): Flow<List<com.kreedaankana.data.local.entity.RevenueEntity>>

    @Query("SELECT * FROM revenue WHERE date BETWEEN :start AND :end ORDER BY date ASC")
    fun getByDateRange(start: String, end: String): Flow<List<com.kreedaankana.data.local.entity.RevenueEntity>>

    @Query("SELECT SUM(amount) FROM revenue WHERE date = :date")
    suspend fun getDailyTotal(date: String): Double?

    @Query("SELECT SUM(amount) FROM revenue WHERE date BETWEEN :start AND :end")
    suspend fun getRangeTotal(start: String, end: String): Double?

    @Query("SELECT COUNT(*) FROM revenue WHERE date = :date")
    suspend fun getBookingCount(date: String): Int
}

@Dao
interface OwnerVerificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(verification: com.kreedaankana.data.local.entity.OwnerVerificationEntity)

    @Query("SELECT * FROM owner_verifications WHERE ownerId = :ownerId ORDER BY verificationTime DESC")
    fun getByOwnerId(ownerId: String): Flow<List<com.kreedaankana.data.local.entity.OwnerVerificationEntity>>

    @Query("SELECT * FROM owner_verifications WHERE verificationTime >= :since ORDER BY verificationTime DESC")
    fun getRecent(since: Long): Flow<List<com.kreedaankana.data.local.entity.OwnerVerificationEntity>>

    @Query("SELECT COUNT(*) FROM owner_verifications WHERE result = :result")
    suspend fun countByResult(result: String): Int
}

@Dao
interface AnnouncementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(announcement: com.kreedaankana.data.local.entity.AnnouncementEntity)

    @Query("SELECT * FROM announcements ORDER BY timestamp DESC")
    fun getAll(): Flow<List<com.kreedaankana.data.local.entity.AnnouncementEntity>>

    @Query("UPDATE announcements SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: Int)

    @Query("UPDATE announcements SET isRead = 1")
    suspend fun markAllRead()

    @Query("DELETE FROM announcements")
    suspend fun clearAll()
}

@Dao
interface PaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: com.kreedaankana.data.local.entity.PaymentEntity)

    @Update
    suspend fun update(payment: com.kreedaankana.data.local.entity.PaymentEntity)

    @Query("SELECT * FROM owner_payments WHERE ownerId = :ownerId ORDER BY createdAt DESC")
    fun getByOwnerId(ownerId: String): Flow<List<com.kreedaankana.data.local.entity.PaymentEntity>>

    @Query("SELECT * FROM owner_payments WHERE status = :status ORDER BY createdAt DESC")
    fun getByStatus(status: String): Flow<List<com.kreedaankana.data.local.entity.PaymentEntity>>

    @Query("UPDATE owner_payments SET status = :status WHERE paymentId = :paymentId")
    suspend fun updateStatus(paymentId: String, status: String)

    @Query("SELECT * FROM owner_payments WHERE syncStatus = 'PENDING'")
    suspend fun getPendingSync(): List<com.kreedaankana.data.local.entity.PaymentEntity>
}

@Dao
interface OwnerUpiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(upi: com.kreedaankana.data.local.entity.OwnerUpiEntity)

    @Query("SELECT * FROM owner_upi WHERE ownerId = :ownerId AND isDefault = 1 LIMIT 1")
    suspend fun getDefault(ownerId: String): com.kreedaankana.data.local.entity.OwnerUpiEntity?

    @Query("SELECT * FROM owner_upi WHERE ownerId = :ownerId")
    fun getByOwnerId(ownerId: String): Flow<List<com.kreedaankana.data.local.entity.OwnerUpiEntity>>

    @Query("UPDATE owner_upi SET isDefault = 0 WHERE ownerId = :ownerId")
    suspend fun clearDefault(ownerId: String)

    @Delete
    suspend fun delete(upi: com.kreedaankana.data.local.entity.OwnerUpiEntity)
}
