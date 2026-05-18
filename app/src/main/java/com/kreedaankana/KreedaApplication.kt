package com.kreedaankana

import android.app.Application
import androidx.work.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kreedaankana.data.local.AppDatabase
import com.kreedaankana.data.repository.*
import com.kreedaankana.viewmodel.ViewModelFactory
import com.kreedaankana.worker.SyncWorker
import java.util.concurrent.TimeUnit

class KreedaApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var viewModelFactory: ViewModelFactory
        private set

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        database = AppDatabase.getDatabase(this)
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val storage = FirebaseStorage.getInstance()

        val bookingRepository = BookingRepository(database.bookingDao(), firestore)
        val authRepository = AuthRepository(auth, database.userDao(), firestore)
        val groundSyncRepository = GroundSyncRepository(database.groundDao(), firestore)
        val challengeRepository = ChallengeRepository(database.challengeDao(), firestore)
        val notificationRepository = NotificationRepository(database.notificationDao())
        val ownerRepository = OwnerRepository(
            ownerGroundDao = database.ownerGroundDao(),
            groundImageDao = database.groundImageDao(),
            tournamentDao = database.tournamentDao(),
            matchDao = database.matchDao(),
            slotBlockDao = database.slotBlockDao(),
            revenueDao = database.revenueDao(),
            ownerVerificationDao = database.ownerVerificationDao(),
            announcementDao = database.announcementDao(),
            paymentDao = database.paymentDao(),
            ownerUpiDao = database.ownerUpiDao(),
            bookingDao = database.bookingDao(),
            firestore = firestore,
            storage = storage
        )

        viewModelFactory = ViewModelFactory(
            authRepository = authRepository,
            bookingRepository = bookingRepository,
            ownerRepository = ownerRepository,
            challengeRepository = challengeRepository,
            notificationRepository = notificationRepository
        )

        schedulePeriodicSync()
    }

    private fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "kreeda_full_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
