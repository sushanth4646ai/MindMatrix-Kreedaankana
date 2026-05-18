package com.kreedaankana.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kreedaankana.data.local.dao.GroundDao
import com.kreedaankana.data.local.entity.GroundEntity
import kotlinx.coroutines.tasks.await

class GroundSyncRepository(
    private val groundDao: GroundDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun syncFromFirestore() {
        try {
            val snapshot = firestore.collection("grounds").get().await()
            val grounds = snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                GroundEntity(
                    groundId = (data["groundId"] as? Number)?.toInt() ?: doc.id.hashCode(),
                    name = (data["name"] as? String) ?: "",
                    address = (data["address"] as? String) ?: "",
                    city = (data["city"] as? String) ?: "",
                    sport = (data["sport"] as? String) ?: "",
                    sportIcon = when ((data["sport"] as? String) ?: "") {
                        "Football" -> "⚽"; "Cricket" -> "🏏"; "Basketball" -> "🏀"
                        "Badminton" -> "🏸"; "Tennis" -> "🎾"; "Volleyball" -> "🏐"; else -> "⚽"
                    },
                    pricePerHour = (data["pricePerHour"] as? Number)?.toDouble() ?: (data["pricePerSlot"] as? Number)?.toDouble() ?: 0.0,
                    rating = (data["rating"] as? Number)?.toFloat() ?: 0f,
                    reviews = (data["reviews"] as? Number)?.toInt() ?: 0,
                    distance = (data["distance"] as? String) ?: "",
                    facilities = ((data["facilities"] as? List<*>)?.map { it.toString() }) ?: emptyList(),
                    isAvailable = (data["isAvailable"] as? Boolean) ?: true,
                    ownerName = (data["ownerName"] as? String) ?: "",
                    phone = (data["phone"] as? String) ?: "",
                    description = (data["description"] as? String) ?: (data["rules"] as? String) ?: "",
                    operatingHours = (data["operatingHours"] as? String) ?: "${data["openingTime"] ?: "06:00"} - ${data["closingTime"] ?: "22:00"}",
                    imageUrl = (data["imageUrl"] as? String)
                )
            }
            if (grounds.isNotEmpty()) {
                groundDao.insertAll(grounds)
            }
        } catch (_: Exception) { }
    }

    fun listenToFirestore() {
        firestore.collection("grounds")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { snap ->
                    snap.documents.forEach { doc ->
                        val data = doc.data ?: return@forEach
                        val existing = kotlinx.coroutines.runBlocking {
                            groundDao.getById((data["groundId"] as? Number)?.toInt() ?: doc.id.hashCode())
                        }
                        if (existing == null) {
                            kotlinx.coroutines.runBlocking {
                                groundDao.insert(GroundEntity(
                                    groundId = (data["groundId"] as? Number)?.toInt() ?: doc.id.hashCode(),
                                    name = (data["name"] as? String) ?: "",
                                    address = (data["address"] as? String) ?: "",
                                    city = (data["city"] as? String) ?: "",
                                    sport = (data["sport"] as? String) ?: "",
                                    sportIcon = "",
                                    pricePerHour = (data["pricePerHour"] as? Number)?.toDouble() ?: (data["pricePerSlot"] as? Number)?.toDouble() ?: 0.0,
                                    rating = (data["rating"] as? Number)?.toFloat() ?: 0f,
                                    description = (data["description"] as? String) ?: (data["rules"] as? String) ?: "",
                                    operatingHours = (data["operatingHours"] as? String) ?: "",
                                    isAvailable = (data["isAvailable"] as? Boolean) ?: true
                                ))
                            }
                        }
                    }
                }
            }
    }
}
