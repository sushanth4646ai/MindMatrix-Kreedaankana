package com.kreedaankana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey
    val bookingId: String = "",
    val id: Int = 0,
    val teamId: Int = 0,
    val groundId: Int = 0,
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
    val customerName: String = "",
    val customerPhone: String = "",
    val teamName: String = "",
    val players: List<String> = emptyList(),
    val invoicePath: String = "",
    val qrPdfPath: String = "",
    val blockchainTxId: String = "",
    val blockchainVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toQRString(): String {
        val json = JSONObject().apply {
            put("bid", bookingId)
            put("cn", customerName)
            put("cp", customerPhone)
            put("tn", teamName)
            put("gn", groundName)
            put("ga", groundAddress)
            put("sp", sport)
            put("bd", bookingDate)
            put("st", slotTime)
            put("sn", slotNumber)
            put("ex", expiryTime)
            put("am", amount)
            put("ci", checkedIn)
            put("pl", players.joinToString("|"))
            put("tx", blockchainTxId)
            put("bv", blockchainVerified)
        }
        return android.util.Base64.encodeToString(
            json.toString().toByteArray(),
            android.util.Base64.NO_WRAP
        )
    }

    companion object {
        fun fromQRString(qrData: String): BookingEntity? {
            return try {
                val jsonStr = String(android.util.Base64.decode(qrData.trim(), android.util.Base64.NO_WRAP))
                val json = JSONObject(jsonStr)
                BookingEntity(
                    bookingId = json.optString("bid"),
                    customerName = json.optString("cn"),
                    customerPhone = json.optString("cp"),
                    teamName = json.optString("tn"),
                    groundName = json.optString("gn"),
                    groundAddress = json.optString("ga"),
                    sport = json.optString("sp"),
                    bookingDate = json.optString("bd"),
                    slotTime = json.optString("st"),
                    slotNumber = json.optString("sn"),
                    expiryTime = json.optLong("ex"),
                    amount = json.optDouble("am"),
                    checkedIn = json.optBoolean("ci"),
                    players = json.optString("pl").split("|").filter { it.isNotEmpty() },
                    blockchainTxId = json.optString("tx"),
                    blockchainVerified = json.optBoolean("bv")
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}