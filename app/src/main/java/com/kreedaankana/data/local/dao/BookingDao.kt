package com.kreedaankana.data.local.dao

import androidx.room.*
import com.kreedaankana.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: BookingEntity)

    @Query("SELECT * FROM bookings ORDER BY createdAt DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE bookingId = :id")
    suspend fun getByBookingId(id: String): BookingEntity?

    @Query("SELECT * FROM bookings WHERE bookingId = :id")
    fun getByBookingIdFlow(id: String): Flow<BookingEntity?>

    @Query("SELECT * FROM bookings WHERE groundId = :groundId AND bookingDate = :date AND slotTime = :slotTime AND checkedIn = 0")
    suspend fun getActiveSlot(groundId: Int, date: String, slotTime: String): BookingEntity?

    @Query("UPDATE bookings SET checkedIn = 1 WHERE bookingId = :bookingId")
    suspend fun markCheckedIn(bookingId: String)

    @Query("DELETE FROM bookings WHERE bookingId = :id")
    suspend fun delete(id: String)

    @Query("SELECT COUNT(*) FROM bookings WHERE groundId = :groundId AND bookingDate = :date AND slotTime = :slotTime")
    suspend fun countBookedSlots(groundId: Int, date: String, slotTime: String): Int

    @Query("SELECT * FROM bookings WHERE paymentStatus = 'PENDING_SYNC'")
    suspend fun getPendingBookings(): List<BookingEntity>
}