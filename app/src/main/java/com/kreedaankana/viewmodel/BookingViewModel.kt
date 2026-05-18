package com.kreedaankana.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreedaankana.data.local.entity.BookingEntity
import com.kreedaankana.data.repository.BookingRepository
import com.kreedaankana.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookingState(
    val isLoading: Boolean = false,
    val bookings: List<BookingEntity> = emptyList(),
    val currentBooking: BookingEntity? = null,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _bookingState = MutableStateFlow(BookingState())
    val bookingState: StateFlow<BookingState> = _bookingState

    fun loadBookings() {
        viewModelScope.launch {
            _bookingState.value = _bookingState.value.copy(isLoading = true)
            // Bookings are loaded via Flow from RoomDB in the UI
            _bookingState.value = _bookingState.value.copy(isLoading = false)
        }
    }

    fun createBooking(booking: BookingEntity) {
        viewModelScope.launch {
            _bookingState.value = _bookingState.value.copy(isLoading = true, error = null)
            try {
                // Insert into local RoomDB first
                bookingRepository.createBookingLocally(booking)
                // Then try to sync to Firebase
                val result = bookingRepository.syncBooking(booking)
                when (result) {
                    is com.kreedaankana.util.Resource.Success -> {
                        _bookingState.value = _bookingState.value.copy(
                            isLoading = false,
                            currentBooking = booking,
                            isSuccess = true
                        )
                    }
                    is com.kreedaankana.util.Resource.Error -> {
                        _bookingState.value = _bookingState.value.copy(
                            isLoading = false,
                            currentBooking = booking,
                            isSuccess = true,
                            error = "Saved offline - will sync when online"
                        )
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _bookingState.value = _bookingState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun clearError() {
        _bookingState.value = _bookingState.value.copy(error = null)
    }

    fun resetSuccess() {
        _bookingState.value = _bookingState.value.copy(isSuccess = false)
    }
}