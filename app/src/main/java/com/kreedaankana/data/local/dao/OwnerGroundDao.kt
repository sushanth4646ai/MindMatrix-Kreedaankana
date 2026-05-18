package com.kreedaankana.data.local.dao

import androidx.room.*
import com.kreedaankana.data.local.entity.OwnerGroundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OwnerGroundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ground: OwnerGroundEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(grounds: List<OwnerGroundEntity>)

    @Update
    suspend fun update(ground: OwnerGroundEntity)

    @Delete
    suspend fun delete(ground: OwnerGroundEntity)

    @Query("SELECT * FROM owner_grounds WHERE ownerId = :ownerId ORDER BY createdAt DESC")
    fun getByOwnerId(ownerId: String): Flow<List<OwnerGroundEntity>>

    @Query("SELECT * FROM owner_grounds WHERE groundId = :groundId")
    suspend fun getByGroundId(groundId: String): OwnerGroundEntity?

    @Query("SELECT * FROM owner_grounds WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllActive(): Flow<List<OwnerGroundEntity>>

    @Query("SELECT * FROM owner_grounds WHERE name LIKE '%' || :query || '%' OR village LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<OwnerGroundEntity>>

    @Query("UPDATE owner_grounds SET isActive = :active WHERE groundId = :groundId")
    suspend fun setActive(groundId: String, active: Boolean)

    @Query("DELETE FROM owner_grounds WHERE groundId = :groundId")
    suspend fun deleteByGroundId(groundId: String)
}
