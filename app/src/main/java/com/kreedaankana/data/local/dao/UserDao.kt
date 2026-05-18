package com.kreedaankana.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kreedaankana.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getByUserId(userId: String): UserEntity?
}
