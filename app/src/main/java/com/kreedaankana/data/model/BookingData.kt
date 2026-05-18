package com.kreedaankana.data.model

import android.util.Base64
import org.json.JSONObject

data class BookingData(
    val bookingId: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val teamId: String = "",
    val teamName: String = "",
    val groundId: String = "",
    val groundName: String = "",
    val groundAddress: String = "",
    val sport: String = "",
    val bookingDate: String = "",
    val slotTime: String = "",
    val slotNumber: String = "",
    val checkedIn: Boolean = false,
    val expiryTime: Long = 0L,
    val paymentStatus: String = "PENDING",
    val amount: Double = 0.0,
    val invoicePath: String = "",
    val qrPdfPath: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val players: List<String> = emptyList()
) {
    fun toQRString(): String {
        val json = JSONObject().apply {
            put("bid", bookingId)
            put("cid", customerId)
            put("cn", customerName)
            put("cp", customerPhone)
            put("tid", teamId)
            put("tn", teamName)
            put("gid", groundId)
            put("gn", groundName)
            put("ga", groundAddress)
            put("sp", sport)
            put("bd", bookingDate)
            put("st", slotTime)
            put("sn", slotNumber)
            put("ci", checkedIn)
            put("ex", expiryTime)
            put("ps", paymentStatus)
            put("am", amount)
            put("ca", createdAt)
            put("pl", players.joinToString("|"))
        }
        return Base64.encodeToString(json.toString().toByteArray(), Base64.NO_WRAP)
    }

    companion object {
        fun fromQRString(qrData: String): BookingData? {
            return try {
                val jsonStr = String(Base64.decode(qrData.trim(), Base64.NO_WRAP))
                val json = JSONObject(jsonStr)
                BookingData(
                    bookingId = json.optString("bid"),
                    customerId = json.optString("cid"),
                    customerName = json.optString("cn"),
                    customerPhone = json.optString("cp"),
                    teamId = json.optString("tid"),
                    teamName = json.optString("tn"),
                    groundId = json.optString("gid"),
                    groundName = json.optString("gn"),
                    groundAddress = json.optString("ga"),
                    sport = json.optString("sp"),
                    bookingDate = json.optString("bd"),
                    slotTime = json.optString("st"),
                    slotNumber = json.optString("sn"),
                    checkedIn = json.optBoolean("ci"),
                    expiryTime = json.optLong("ex"),
                    paymentStatus = json.optString("ps"),
                    amount = json.optDouble("am"),
                    createdAt = json.optLong("ca"),
                    players = json.optString("pl").split("|").filter { it.isNotEmpty() }
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

data class TimeSlot(
    val time: String,
    val isAvailable: Boolean,
    val startTime: Long = 0L,
    val endTime: Long = 0L
)

data class GroundBookingInfo(
    val groundId: String,
    val groundName: String,
    val address: String,
    val sport: String,
    val pricePerHour: Double,
    val images: List<String> = emptyList(),
    val facilities: List<String> = emptyList()
)