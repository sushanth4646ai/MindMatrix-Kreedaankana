package com.kreedaankana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kreedaankana.data.local.entity.PaymentEntity.PaymentMethod
import com.kreedaankana.data.local.entity.PaymentEntity.PaymentStatus

@Entity(tableName = "owner_payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val paymentId: String = "",
    val bookingId: String = "",
    val ownerId: String = "",
    val amount: Double = 0.0,
    val method: String = PaymentMethod.CASH,
    val status: String = PaymentStatus.PENDING,
    val upiId: String = "",
    val screenshotUrl: String = "",
    val customerName: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "PENDING"
) {
    object PaymentMethod { const val CASH = "CASH"; const val UPI = "UPI"; const val CARD = "CARD"; const val ONLINE = "ONLINE" }
    object PaymentStatus { const val PENDING = "PENDING"; const val APPROVED = "APPROVED"; const val REJECTED = "REJECTED"; const val REFUNDED = "REFUNDED" }
}

@Entity(tableName = "owner_upi")
data class OwnerUpiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ownerId: String = "",
    val upiId: String = "",
    val upiQrUrl: String = "",
    val isDefault: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
