package com.kreedaankana.data.model

data class InvoiceDetails(
    val title: String,
    val invoiceId: String,
    val date: String,
    val ownerName: String,
    val ownerPhone: String,
    val groundName: String,
    val groundAddress: String,
    val customerName: String,
    val customerPhone: String,
    val teamName: String,
    val sportType: String,
    val items: List<InvoiceItem>,
    val totalAmount: Double,
    val paymentStatus: String,
    val paymentMethod: String,
    val upiId: String = "kreeda@upi",
    val qrPayload: String? = null
)

data class InvoiceItem(
    val description: String,
    val quantity: String,
    val price: Double
)
