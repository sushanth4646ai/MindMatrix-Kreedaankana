package com.kreedaankana.data.model

import com.kreedaankana.util.SecurityUtils
import org.json.JSONObject

data class QRPayload(
    val bookingId: String,
    val timestamp: Long,
    val customerName: String,
    val customerPhone: String,
    val teamName: String,
    val groundName: String,
    val sportType: String,
    val slotDate: String,
    val startTime: String,
    val endTime: String,
    val numPlayers: Int,
    val paymentStatus: String,
    val ownerId: String,
    val customerId: String,
    val expiryTime: Long,
    val verificationToken: String = ""
) {
    fun serialize(): String {
        val json = JSONObject().apply {
            put("bid", bookingId)
            put("ts", timestamp)
            put("cn", customerName)
            put("cp", customerPhone)
            put("tn", teamName)
            put("gn", groundName)
            put("st", sportType)
            put("sd", slotDate)
            put("start", startTime)
            put("end", endTime)
            put("np", numPlayers)
            put("ps", paymentStatus)
            put("oid", ownerId)
            put("cid", customerId)
            put("ex", expiryTime)
        }
        val rawData = json.toString()
        val signature = SecurityUtils.generateSignature(rawData)
        json.put("sig", signature)
        
        return SecurityUtils.encryptPayload(json.toString())
    }

    companion object {
        fun deserialize(encryptedData: String): QRPayload? {
            val decrypted = SecurityUtils.decryptPayload(encryptedData) ?: return null
            return try {
                val json = JSONObject(decrypted)
                val signature = json.remove("sig") as String
                val rawData = json.toString()
                
                if (!SecurityUtils.verifySignature(rawData, signature)) return null
                
                QRPayload(
                    bookingId = json.getString("bid"),
                    timestamp = json.getLong("ts"),
                    customerName = json.getString("cn"),
                    customerPhone = json.getString("cp"),
                    teamName = json.getString("tn"),
                    groundName = json.getString("gn"),
                    sportType = json.getString("st"),
                    slotDate = json.getString("sd"),
                    startTime = json.getString("start"),
                    endTime = json.getString("end"),
                    numPlayers = json.getInt("np"),
                    paymentStatus = json.getString("ps"),
                    ownerId = json.getString("oid"),
                    customerId = json.getString("cid"),
                    expiryTime = json.getLong("ex"),
                    verificationToken = signature
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
