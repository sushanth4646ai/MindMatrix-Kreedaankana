package com.kreedaankana.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.kreedaankana.data.local.AppDatabase
import com.kreedaankana.data.local.dao.BookingDao
import com.kreedaankana.data.repository.BookingRepository

object ServiceLocator {
    private var database: AppDatabase? = null
    private var firestore: FirebaseFirestore? = null
    private var bookingRepository: BookingRepository? = null
    
    fun initialize(context: Context) {
        database = AppDatabase.getDatabase(context)
        firestore = FirebaseFirestore.getInstance()
    }
    
    fun provideBookingDao(): BookingDao {
        return database?.bookingDao() ?: throw IllegalStateException("Database not initialized")
    }
    
    fun provideBookingRepository(): BookingRepository {
        return bookingRepository ?: BookingRepository(
            provideBookingDao(),
            firestore ?: FirebaseFirestore.getInstance()
        ).also { bookingRepository = it }
    }
}
