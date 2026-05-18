package com.kreedaankana.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.kreedaankana.data.local.AppDatabase
import com.kreedaankana.data.repository.QRRepository
import kotlinx.coroutines.tasks.await

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val firestore = FirebaseFirestore.getInstance()

            // 1. Sync unsynced verification logs
            val unsyncedLogs = database.verificationDao().getUnsyncedLogs()
            unsyncedLogs.forEach { log ->
                try {
                    firestore.collection("verifications")
                        .document(log.logId.toString())
                        .set(log)
                        .await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // 2. Sync new challenges from Firestore to Room
            try {
                val challengeSnapshot = firestore.collection("challenges").get().await()
                challengeSnapshot.documents.forEach { doc ->
                    val challenge = doc.toObject(com.kreedaankana.data.local.entity.ChallengeEntity::class.java)
                    challenge?.let { database.challengeDao().insert(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 3. Sync pending bookings
            try {
                val pendingBookings = database.bookingDao().getPendingBookings()
                pendingBookings.forEach { booking ->
                    firestore.collection("bookings")
                        .document(booking.bookingId)
                        .set(booking)
                        .await()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
