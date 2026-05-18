package com.kreedaankana.data.blockchain

import android.content.Context
import com.kreedaankana.data.local.entity.BookingEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BlockchainService private constructor(private val api: FabricBlockchainApi) {

    companion object {
        private const val DEFAULT_BASE_URL = "http://10.0.2.2:3000/"
        private var instance: BlockchainService? = null
        private var baseUrl: String = DEFAULT_BASE_URL

        fun initialize(baseUrl: String = DEFAULT_BASE_URL) {
            this.baseUrl = baseUrl
            instance = null
        }

        fun getInstance(): BlockchainService {
            return instance ?: synchronized(this) {
                instance ?: createInstance().also { instance = it }
            }
        }

        private fun createInstance(): BlockchainService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return BlockchainService(retrofit.create(FabricBlockchainApi::class.java))
        }
    }

    suspend fun registerBooking(booking: BookingEntity): Result<BlockchainBookingResponse> = withContext(Dispatchers.IO) {
        try {
            val request = BlockchainBookingRequest(
                bookingId = booking.bookingId,
                customerName = booking.customerName,
                customerPhone = booking.customerPhone,
                teamName = booking.teamName,
                groundName = booking.groundName,
                groundAddress = booking.groundAddress,
                sport = booking.sport,
                bookingDate = booking.bookingDate,
                slotTime = booking.slotTime,
                amount = booking.amount,
                players = booking.players,
                timestamp = System.currentTimeMillis()
            )

            val response = api.registerBooking(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.txId != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception(body.message ?: "Blockchain registration failed"))
                }
            } else {
                Result.failure(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyBooking(bookingId: String): Result<BlockchainVerificationResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.verifyBooking(bookingId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Verification failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBookingDetails(bookingId: String): Result<BlockchainVerificationResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getBooking(bookingId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get booking: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkInBooking(bookingId: String): Result<BlockchainVerificationResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.checkInBooking(bookingId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Check-in failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNetworkStatus(): Result<BlockchainStatusResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getNetworkStatus()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get network status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isNetworkAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val result = getNetworkStatus()
            result.isSuccess && result.getOrNull()?.connected == true
        } catch (e: Exception) {
            false
        }
    }
}