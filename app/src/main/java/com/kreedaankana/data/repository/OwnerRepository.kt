package com.kreedaankana.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kreedaankana.data.local.dao.*
import com.kreedaankana.data.local.entity.*
import com.kreedaankana.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class OwnerRepository(
    private val ownerGroundDao: OwnerGroundDao,
    private val groundImageDao: GroundImageDao,
    private val tournamentDao: TournamentDao,
    private val matchDao: MatchDao,
    private val slotBlockDao: SlotBlockDao,
    private val revenueDao: RevenueDao,
    private val ownerVerificationDao: OwnerVerificationDao,
    private val announcementDao: AnnouncementDao,
    private val paymentDao: PaymentDao,
    private val ownerUpiDao: OwnerUpiDao,
    private val bookingDao: BookingDao,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    private val auth get() = FirebaseAuth.getInstance()
    private fun ownerId(): String = auth.currentUser?.uid ?: "owner_default"

    fun getOwnerGrounds(): Flow<List<OwnerGroundEntity>> = ownerGroundDao.getByOwnerId(ownerId())

    suspend fun getGroundById(groundId: String): OwnerGroundEntity? = ownerGroundDao.getByGroundId(groundId)

    fun getGroundImages(groundId: String): Flow<List<GroundImageEntity>> = groundImageDao.getByGroundId(groundId)

    suspend fun saveGround(ground: OwnerGroundEntity): Resource<String> {
        return try {
            val gId = if (ground.groundId.isBlank()) UUID.randomUUID().toString() else ground.groundId
            val entity = ground.copy(groundId = gId, ownerId = ownerId(), syncStatus = "PENDING")
            ownerGroundDao.insert(entity)
            firestore.collection("owner_grounds").document(gId).set(groundToMap(entity)).await()
            ownerGroundDao.insert(entity.copy(syncStatus = "SYNCED"))
            val marketRef = firestore.collection("grounds").document(gId)
            marketRef.set(mapOf(
                "groundId" to gId.hashCode(), "name" to ground.name, "address" to ground.address,
                "city" to ground.village, "sport" to ground.sports, "pricePerHour" to ground.pricePerSlot,
                "rating" to 0, "isAvailable" to ground.isActive, "description" to ground.rules,
                "operatingHours" to "${ground.openingTime} - ${ground.closingTime}"
            )).await()
            Resource.Success(gId)
        } catch (e: Exception) { Resource.Error(e.message ?: "Save failed") }
    }

    suspend fun uploadGroundImage(groundId: String, imageBytes: ByteArray): Resource<String> {
        return try {
            val ref = storage.reference.child("grounds/$groundId/${UUID.randomUUID()}.jpg")
            ref.putBytes(imageBytes).await()
            val url = ref.downloadUrl.await().toString()
            groundImageDao.insert(GroundImageEntity(groundId = groundId, imageUrl = url, isLocal = false))
            Resource.Success(url)
        } catch (e: Exception) { Resource.Error(e.message ?: "Upload failed") }
    }

    suspend fun deleteGround(groundId: String) {
        ownerGroundDao.deleteByGroundId(groundId)
        firestore.collection("owner_grounds").document(groundId).delete()
        firestore.collection("grounds").document(groundId).delete()
    }

    fun getTournaments(): Flow<List<TournamentEntity>> = tournamentDao.getByOwnerId(ownerId())

    suspend fun saveTournament(tournament: TournamentEntity): Resource<String> {
        return try {
            val tId = if (tournament.tournamentId.isBlank()) UUID.randomUUID().toString() else tournament.tournamentId
            val entity = tournament.copy(tournamentId = tId, createdBy = ownerId())
            tournamentDao.insert(entity)
            firestore.collection("tournaments").document(tId).set(tournamentToMap(entity)).await()
            Resource.Success(tId)
        } catch (e: Exception) { Resource.Error(e.message ?: "Operation failed") }
    }

    suspend fun deleteTournament(id: String) { tournamentDao.deleteByTournamentId(id); firestore.collection("tournaments").document(id).delete() }

    suspend fun approveTeam(tournamentId: String, teamsJson: String) {
        tournamentDao.updateTeams(tournamentId, teamsJson); firestore.collection("tournaments").document(tournamentId).update("teamsJson", teamsJson).await()
    }

    suspend fun generateFixtures(tournamentId: String, teamsJson: String): Resource<List<MatchEntity>> {
        return try {
            val teams = org.json.JSONArray(teamsJson).let { arr -> (0 until arr.length()).map { arr.getString(it) } }
            val matches = mutableListOf<MatchEntity>()
            for (i in teams.indices step 2) if (i + 1 < teams.size) {
                val match = MatchEntity(matchId = "${tournamentId}_M$i", teamA = teams[i], teamB = teams[i + 1],
                    sportType = tournamentDao.getByTournamentId(tournamentId)?.sportType ?: "", date = tournamentDao.getByTournamentId(tournamentId)?.startDate ?: "", status = "scheduled")
                matchDao.insert(match); matches.add(match)
            }
            val roundsJson = org.json.JSONArray().apply {
                put(org.json.JSONObject().apply { put("round", 1); put("matches", org.json.JSONArray(matches.map { it.matchId })) })
            }.toString()
            tournamentDao.updateRounds(tournamentId, roundsJson)
            firestore.collection("tournaments").document(tournamentId).update("roundsJson", roundsJson).await()
            Resource.Success(matches)
        } catch (e: Exception) { Resource.Error(e.message ?: "Operation failed") }
    }

    fun getMatches(): Flow<List<MatchEntity>> = matchDao.getAll()
    fun getMatchesByStatus(status: String): Flow<List<MatchEntity>> = matchDao.getByStatus(status)

    suspend fun saveMatch(match: MatchEntity): Resource<String> {
        return try { val mId = if (match.matchId.isBlank()) UUID.randomUUID().toString() else match.matchId; matchDao.insert(match.copy(matchId = mId)); Resource.Success(mId) }
        catch (e: Exception) { Resource.Error(e.message ?: "Operation failed") }
    }

    suspend fun updateScore(matchId: String, scoreA: Int, scoreB: Int, displayScore: String) {
        matchDao.updateScore(matchId, scoreA, scoreB, displayScore)
        firestore.collection("matches").document(matchId).update(mapOf("teamAScore" to scoreA, "teamBScore" to scoreB, "score" to displayScore)).await()
    }

    suspend fun declareWinner(matchId: String, winner: String) {
        matchDao.declareWinner(matchId, winner)
        firestore.collection("matches").document(matchId).update(mapOf("winner" to winner, "status" to "completed")).await()
    }

    suspend fun updateMatchStatus(matchId: String, status: String) {
        matchDao.updateStatus(matchId, status)
        firestore.collection("matches").document(matchId).update("status", status).await()
    }

    fun getBookingsByGround(groundName: String): Flow<List<BookingEntity>> = bookingDao.getAllBookings()

    fun getPendingBookings(): Flow<List<BookingEntity>> = bookingDao.getAllBookings()

    suspend fun approveBooking(bookingId: String) {
        bookingDao.markCheckedIn(bookingId)
        firestore.collection("bookings").document(bookingId).update("checkedIn", true).await()
    }

    suspend fun rejectBooking(bookingId: String) {
        bookingDao.delete(bookingId)
        firestore.collection("bookings").document(bookingId).delete()
    }

    fun getBlockedSlots(date: String): Flow<List<SlotBlockEntity>> = slotBlockDao.getByDate(date)

    suspend fun blockSlot(date: String, timeSlot: String, reason: String, isMaintenance: Boolean) {
        slotBlockDao.insert(SlotBlockEntity(date = date, timeSlot = timeSlot, reason = reason, isMaintenance = isMaintenance, createdBy = ownerId()))
        firestore.collection("slot_blocks").add(mapOf("date" to date, "timeSlot" to timeSlot, "reason" to reason, "isMaintenance" to isMaintenance))
    }

    suspend fun unblockSlot(id: Int) { slotBlockDao.delete(id) }

    fun getRevenueByDate(date: String): Flow<List<RevenueEntity>> = revenueDao.getByDate(date)
    suspend fun getDailyRevenue(date: String): Double = revenueDao.getDailyTotal(date) ?: 0.0
    suspend fun getRangeRevenue(start: String, end: String): Double = revenueDao.getRangeTotal(start, end) ?: 0.0
    suspend fun getBookingCount(date: String): Int = revenueDao.getBookingCount(date)
    suspend fun addRevenue(amount: Double, bookingId: String, description: String) {
        val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        revenueDao.insert(RevenueEntity(amount = amount, date = date, bookingId = bookingId, description = description))
    }

    fun getVerifications(): Flow<List<OwnerVerificationEntity>> = ownerVerificationDao.getByOwnerId(ownerId())
    fun getAnnouncements(): Flow<List<AnnouncementEntity>> = announcementDao.getAll()
    suspend fun markAnnouncementRead(id: Int) = announcementDao.markRead(id)
    fun getPayments(): Flow<List<PaymentEntity>> = paymentDao.getByOwnerId(ownerId())
    suspend fun approvePayment(paymentId: String) = paymentDao.updateStatus(paymentId, PaymentEntity.PaymentStatus.APPROVED)
    suspend fun rejectPayment(paymentId: String) = paymentDao.updateStatus(paymentId, PaymentEntity.PaymentStatus.REJECTED)

    suspend fun saveUpi(upi: OwnerUpiEntity) {
        ownerUpiDao.clearDefault(ownerId()); ownerUpiDao.insert(upi.copy(ownerId = ownerId(), isDefault = true))
        firestore.collection("owner_upi").document(ownerId()).set(mapOf("upiId" to upi.upiId)).await()
    }

    suspend fun getDefaultUpi(): OwnerUpiEntity? = ownerUpiDao.getDefault(ownerId())

    private fun groundToMap(g: OwnerGroundEntity) = mapOf(
        "groundId" to g.groundId, "ownerId" to g.ownerId, "name" to g.name, "village" to g.village,
        "address" to g.address, "latitude" to g.latitude, "longitude" to g.longitude, "sports" to g.sports,
        "openingTime" to g.openingTime, "closingTime" to g.closingTime, "slotDuration" to g.slotDuration,
        "pricePerSlot" to g.pricePerSlot, "amenities" to g.amenities, "hasParking" to g.hasParking,
        "hasLighting" to g.hasLighting, "hasWashroom" to g.hasWashroom, "hasDrinkingWater" to g.hasDrinkingWater,
        "seatingCapacity" to g.seatingCapacity, "rules" to g.rules, "images" to g.images, "isActive" to g.isActive
    )

    private fun tournamentToMap(t: TournamentEntity) = mapOf(
        "tournamentId" to t.tournamentId, "name" to t.name, "sportType" to t.sportType,
        "startDate" to t.startDate, "endDate" to t.endDate, "teamsJson" to t.teamsJson,
        "roundsJson" to t.roundsJson, "status" to t.status, "winner" to t.winner,
        "createdBy" to t.createdBy, "createdAt" to t.createdAt
    )
}
