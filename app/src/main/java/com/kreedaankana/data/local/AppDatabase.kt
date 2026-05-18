package com.kreedaankana.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kreedaankana.data.local.dao.*
import com.kreedaankana.data.local.entity.*
import com.kreedaankana.data.local.util.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BookingEntity::class, UserEntity::class, GroundEntity::class, TeamEntity::class,
        OwnerGroundEntity::class, GroundImageEntity::class, TournamentEntity::class,
        MatchEntity::class, OwnerProfileEntity::class, OwnerVerificationEntity::class,
        RevenueEntity::class, SlotBlockEntity::class, AnnouncementEntity::class,
        PaymentEntity::class, OwnerUpiEntity::class, WaitlistEntity::class,
        FavoriteEntity::class, SyncQueueEntity::class, InvoiceEntity::class,
        VerificationLogEntity::class, ChallengeEntity::class, NotificationEntity::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookingDao(): BookingDao
    abstract fun userDao(): UserDao
    abstract fun groundDao(): GroundDao
    abstract fun teamDao(): TeamDao
    abstract fun ownerGroundDao(): OwnerGroundDao
    abstract fun groundImageDao(): GroundImageDao
    abstract fun tournamentDao(): TournamentDao
    abstract fun matchDao(): MatchDao
    abstract fun slotBlockDao(): SlotBlockDao
    abstract fun revenueDao(): RevenueDao
    abstract fun ownerVerificationDao(): OwnerVerificationDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun paymentDao(): PaymentDao
    abstract fun ownerUpiDao(): OwnerUpiDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun verificationDao(): VerificationDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kreeda_ankana_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    seedGrounds(database.groundDao())
                                    seedTeams(database.teamDao())
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedGrounds(groundDao: GroundDao) {
            val grounds = listOf(
                GroundEntity(1, "Sample Football Ground", "Local Village", "Your City", "Football", "\u26BD", 500.0, 4.0f, 10, "1 km", listOf("Basic"), true, "", "", "", "6 AM - 6 PM"),
                GroundEntity(2, "Sample Cricket Pitch", "Local Village", "Your City", "Cricket", "\uD83C\uDFCF", 800.0, 4.0f, 8, "1 km", listOf("Basic"), true, "", "", "", "6 AM - 6 PM")
            )
            groundDao.insertAll(grounds)
        }

        private suspend fun seedTeams(teamDao: TeamDao) {
            val teams = listOf(
                TeamEntity(1, "Thunderbolts FC", "Rahul Sharma", "9876543210", "Football", listOf("Rahul (C)", "Amit Kumar", "Vikram Singh", "Suresh Reddy", "Raj Mehta", "Priya Nair", "Neha Gupta", "Karan Patel", "Sanjay Iyer", "Deepak Verma", "Arjun Nair"), 45, 32),
                TeamEntity(2, "Royal Challengers", "Virat Kohli", "9876543211", "Cricket", listOf("Virat (C)", "AB de Villiers", "Yuvraj Singh", "Hardik Pandya", "Rohit Sharma", "KL Rahul", "Jasprit Bumrah", "Ravichandran Ashwin", "Bhuvneshwar Kumar", "Mohammed Shami", "Kuldeep Yadav"), 60, 48),
                TeamEntity(3, "Net Blazers", "Priyanka Das", "9876543212", "Badminton", listOf("Priyanka (C)", "Saina Nehwal", "PV Sindhu", "Kidambi Srikanth", "HS Prannoy"), 20, 15)
            )
            teams.forEach { teamDao.insert(it) }
        }
    }
}
