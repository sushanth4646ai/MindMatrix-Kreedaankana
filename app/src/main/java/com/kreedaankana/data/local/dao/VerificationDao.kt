package com.kreedaankana.data.local.dao

import androidx.room.*
import com.kreedaankana.data.local.entity.VerificationLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VerificationDao {
    @Query("SELECT * FROM verification_logs ORDER BY scanTimestamp DESC")
    fun getAllLogs(): Flow<List<VerificationLogEntity>>

    @Query("SELECT * FROM verification_logs WHERE bookingId = :bookingId AND result = 'SUCCESS'")
    suspend fun getSuccessfulScanForBooking(bookingId: String): VerificationLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: VerificationLogEntity)

    @Query("SELECT COUNT(*) FROM verification_logs WHERE bookingId = :bookingId AND result = 'SUCCESS'")
    suspend fun isAlreadyVerified(bookingId: String): Boolean

    @Query("SELECT * FROM verification_logs WHERE isSynced = 0")
    suspend fun getUnsyncedLogs(): List<VerificationLogEntity>
}
