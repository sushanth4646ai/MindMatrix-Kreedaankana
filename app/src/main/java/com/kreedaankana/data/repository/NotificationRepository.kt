package com.kreedaankana.data.repository

import com.kreedaankana.data.local.dao.NotificationDao
import com.kreedaankana.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao
) {
    fun getAllNotifications(): Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()

    suspend fun markAsRead(id: Int) = notificationDao.markAsRead(id)
    
    suspend fun markAllAsRead() = notificationDao.markAllAsRead()

    suspend fun addNotification(notification: NotificationEntity) {
        notificationDao.insert(notification)
    }
}
