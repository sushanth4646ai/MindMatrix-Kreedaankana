package com.kreedaankana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey
    val invoiceId: String = "",
    val bookingId: String = "",
    val transactionId: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val customerEmail: String = "",
    val groundId: String = "",
    val groundName: String = "",
    val groundOwnerId: String = "",
    val groundAddress: String = "",
    val sportType: String = "",
    val slotDate: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val amount: Double = 0.0,
    val paymentStatus: String = "PENDING", // PENDING, COMPLETED, FAILED
    val paymentMethod: String = "", // UPI, CASH, CARD
    val upiReference: String = "",
    val qrToken: String = "",
    val expiryTimestamp: Long = 0L,
    val pdfPath: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
