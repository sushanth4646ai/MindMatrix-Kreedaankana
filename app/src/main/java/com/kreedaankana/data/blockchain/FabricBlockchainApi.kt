package com.kreedaankana.data.blockchain

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class BlockchainBookingRequest(
    val bookingId: String,
    val customerName: String,
    val customerPhone: String,
    val teamName: String,
    val groundName: String,
    val groundAddress: String,
    val sport: String,
    val bookingDate: String,
    val slotTime: String,
    val amount: Double,
    val players: List<String>,
    val timestamp: Long
)

data class BlockchainBookingResponse(
    val success: Boolean,
    val txId: String?,
    val message: String?,
    val blockNumber: Long?,
    val timestamp: Long?
)

data class BlockchainVerificationResponse(
    val valid: Boolean,
    val bookingId: String?,
    val customerName: String?,
    val groundName: String?,
    val sport: String?,
    val bookingDate: String?,
    val slotTime: String?,
    val checkedIn: Boolean?,
    val expiryTime: Long?,
    val blockNumber: Long?,
    val txId: String?,
    val message: String?
)

data class BlockchainStatusResponse(
    val connected: Boolean,
    val channelName: String,
    val chaincodeName: String,
    val blockHeight: Long,
    val networkId: String
)

interface FabricBlockchainApi {
    @POST("api/booking/register")
    suspend fun registerBooking(@Body request: BlockchainBookingRequest): Response<BlockchainBookingResponse>

    @GET("api/booking/verify/{bookingId}")
    suspend fun verifyBooking(@Path("bookingId") bookingId: String): Response<BlockchainVerificationResponse>

    @GET("api/booking/{bookingId}")
    suspend fun getBooking(@Path("bookingId") bookingId: String): Response<BlockchainVerificationResponse>

    @POST("api/booking/checkin/{bookingId}")
    suspend fun checkInBooking(@Path("bookingId") bookingId: String): Response<BlockchainVerificationResponse>

    @GET("api/network/status")
    suspend fun getNetworkStatus(): Response<BlockchainStatusResponse>
}