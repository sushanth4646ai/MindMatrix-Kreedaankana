package com.kreedaankana.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kreedaankana.data.repository.AuthRepository
import com.kreedaankana.data.repository.BookingRepository
import com.kreedaankana.data.repository.ChallengeRepository
import com.kreedaankana.data.repository.NotificationRepository
import com.kreedaankana.data.repository.OwnerRepository

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository,
    private val ownerRepository: OwnerRepository? = null,
    private val challengeRepository: ChallengeRepository? = null,
    private val notificationRepository: NotificationRepository? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(authRepository) as T
            modelClass.isAssignableFrom(BookingViewModel::class.java) ->
                BookingViewModel(bookingRepository) as T
            modelClass.isAssignableFrom(OwnerDashboardViewModel::class.java) ->
                OwnerDashboardViewModel(ownerRepository!!) as T
            modelClass.isAssignableFrom(OwnerGroundViewModel::class.java) ->
                OwnerGroundViewModel(ownerRepository!!) as T
            modelClass.isAssignableFrom(OwnerSlotViewModel::class.java) ->
                OwnerSlotViewModel(ownerRepository!!) as T
            modelClass.isAssignableFrom(OwnerBookingRequestViewModel::class.java) ->
                OwnerBookingRequestViewModel(ownerRepository!!) as T
            modelClass.isAssignableFrom(OwnerTournamentViewModel::class.java) ->
                OwnerTournamentViewModel(ownerRepository!!) as T
            modelClass.isAssignableFrom(OwnerLiveScoreViewModel::class.java) ->
                OwnerLiveScoreViewModel(ownerRepository!!) as T
            modelClass.isAssignableFrom(OwnerAnalyticsViewModel::class.java) ->
                OwnerAnalyticsViewModel(ownerRepository!!) as T
            modelClass.isAssignableFrom(OwnerPaymentViewModel::class.java) ->
                OwnerPaymentViewModel(ownerRepository!!) as T
            modelClass.isAssignableFrom(ChallengeViewModel::class.java) ->
                ChallengeViewModel(challengeRepository!!) as T
            modelClass.isAssignableFrom(NotificationViewModel::class.java) ->
                NotificationViewModel(notificationRepository!!) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
