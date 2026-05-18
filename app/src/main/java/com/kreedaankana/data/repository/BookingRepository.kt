package com.kreedaankana.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kreedaankana.data.local.dao.BookingDao
import com.kreedaankana.data.local.entity.BookingEntity
import com.kreedaankana.util.Resource
import kotlinx.coroutines.tasks.await

class BookingRepository(
    private val bookingDao: BookingDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun createBookingLocally(booking: BookingEntity) {
        bookingDao.insert(booking)
    }

    suspend fun syncBooking(booking: BookingEntity): Resource<String> {
        return try {
            val bookingData = hashMapOf(
                "bookingId" to booking.bookingId,
                "groundId" to booking.groundId,
                "bookingDate" to booking.bookingDate,
                "slotTime" to booking.slotTime,
                "checkedIn" to booking.checkedIn,
                "expiryTime" to booking.expiryTime,
                "amount" to booking.amount,
                "customerName" to booking.customerName,
                "teamName" to booking.teamName,
                "groundName" to booking.groundName,
                "sport" to booking.sport,
                "players" to booking.players
            )
            firestore.collection("bookings").document(booking.bookingId).set(bookingData).await()
            Resource.Success(booking.bookingId)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Sync failed")
        }
    }

    suspend fun isSlotAvailable(groundId: Int, date: String, slotTime: String): Boolean {
        val localBooking = bookingDao.getActiveSlot(groundId, date, slotTime)
        if (localBooking != null) return false

        return try {
            val snapshot = firestore.collection("bookings")
                .whereEqualTo("groundId", groundId)
                .whereEqualTo("bookingDate", date)
                .whereEqualTo("slotTime", slotTime)
                .whereEqualTo("checkedIn", false)
                .get()
                .await()
            snapshot.isEmpty
        } catch (e: Exception) {
            true
        }
    }

    suspend fun createBooking(booking: BookingEntity): Result<String> {
        return try {
            if (!isSlotAvailable(booking.groundId, booking.bookingDate, booking.slotTime)) {
                return Result.failure(Exception("SLOT_ALREADY_BOOKED"))
            }
            bookingDao.insert(booking)

            val bookingData = hashMapOf(
                "bookingId" to booking.bookingId,
                "groundId" to booking.groundId,
                "bookingDate" to booking.bookingDate,
                "slotTime" to booking.slotTime,
                "checkedIn" to booking.checkedIn,
                "expiryTime" to booking.expiryTime,
                "amount" to booking.amount,
                "customerName" to booking.customerName,
                "teamName" to booking.teamName,
                "groundName" to booking.groundName
            )
            firestore.collection("bookings").document(booking.bookingId).set(bookingData).await()
            Result.success(booking.bookingId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun validateBooking(bookingId: String): QRValidationResult {
        return try {
            val snapshot = firestore.collection("bookings").document(bookingId).get().await()
            if (!snapshot.exists()) return QRValidationResult.INVALID_BOOKING

            val checkedIn = snapshot.getBoolean("checkedIn") ?: false
            if (checkedIn) return QRValidationResult.ALREADY_USED

            val expiryTime = snapshot.getLong("expiryTime") ?: 0L
            if (System.currentTimeMillis() > expiryTime) return QRValidationResult.EXPIRED

            firestore.collection("bookings").document(bookingId)
                .update("checkedIn", true).await()

            return QRValidationResult.VALID
        } catch (e: Exception) {
            QRValidationResult.INVALID_BOOKING
        }
    }
}

enum class QRValidationResult {
    VALID, INVALID_BOOKING, EXPIRED, ALREADY_USED
}