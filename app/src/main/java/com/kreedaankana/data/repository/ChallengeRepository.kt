package com.kreedaankana.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kreedaankana.data.local.dao.ChallengeDao
import com.kreedaankana.data.local.entity.ChallengeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChallengeRepository @Inject constructor(
    private val challengeDao: ChallengeDao,
    private val firestore: FirebaseFirestore
) {
    fun getAllChallenges(): Flow<List<ChallengeEntity>> = challengeDao.getAllChallenges()

    suspend fun createChallenge(challenge: ChallengeEntity) {
        challengeDao.insert(challenge)
        // Sync to Firestore
        try {
            firestore.collection("challenges").document(challenge.challengeId).set(challenge).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun syncChallenges() {
        try {
            val snapshot = firestore.collection("challenges").get().await()
            val challenges = snapshot.toObjects(ChallengeEntity::class.java)
            challenges.forEach { challengeDao.insert(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
