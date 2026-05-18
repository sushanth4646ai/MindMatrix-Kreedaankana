package com.kreedaankana.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kreedaankana.data.local.dao.VerificationDao
import com.kreedaankana.data.local.entity.VerificationLogEntity
import com.kreedaankana.data.model.QRPayload
import kotlinx.coroutines.tasks.await

class QRRepository(
    private val verificationDao: VerificationDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun validateTicket(payload: QRPayload): ValidationResult {
        // 1. Check Expiry
        if (System.currentTimeMillis() > payload.expiryTime) {
            return ValidationResult.Error("Ticket Expired")
        }

        // 2. Check for Duplicate Usage (Offline first)
        if (verificationDao.isAlreadyVerified(payload.bookingId)) {
            return ValidationResult.Error("Ticket Already Used")
        }

        // 3. Online Check (if available)
        try {
            val snapshot = firestore.collection("verifications").document(payload.bookingId).get().await()
            if (snapshot.exists()) {
                return ValidationResult.Error("Ticket Already Used (Synced)")
            }
        } catch (e: Exception) {
            // If offline, continue with local verification
        }

        return ValidationResult.Success(payload)
    }

    suspend fun logVerification(log: VerificationLogEntity) {
        verificationDao.insertLog(log)
        if (log.result == "SUCCESS") {
            syncLogToFirebase(log)
        }
    }

    private suspend fun syncLogToFirebase(log: VerificationLogEntity) {
        try {
            firestore.collection("verifications").document(log.bookingId)
                .set(log).await()
            verificationDao.insertLog(log.copy(isSynced = true))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

sealed class ValidationResult {
    data class Success(val payload: QRPayload) : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}
