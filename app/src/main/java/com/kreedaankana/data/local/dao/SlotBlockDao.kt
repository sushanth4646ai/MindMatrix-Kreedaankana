package com.kreedaankana.data.local.dao

import androidx.room.*
import com.kreedaankana.data.local.entity.SlotBlockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SlotBlockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(block: SlotBlockEntity)

    @Query("SELECT * FROM slot_blocks WHERE date = :date ORDER BY timeSlot ASC")
    fun getByDate(date: String): Flow<List<SlotBlockEntity>>

    @Query("SELECT * FROM slot_blocks WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getByDateRange(startDate: String, endDate: String): Flow<List<SlotBlockEntity>>

    @Query("SELECT * FROM slot_blocks WHERE syncStatus = 'PENDING'")
    suspend fun getPendingSync(): List<SlotBlockEntity>

    @Query("SELECT * FROM slot_blocks WHERE date = :date AND timeSlot = :timeSlot")
    suspend fun getBlock(date: String, timeSlot: String): SlotBlockEntity?

    @Query("UPDATE slot_blocks SET syncStatus = 'SYNCED' WHERE id = :id")
    suspend fun markSynced(id: Int)

    @Query("DELETE FROM slot_blocks WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM slot_blocks WHERE date < :beforeDate")
    suspend fun deleteOlderThan(beforeDate: String)
}
