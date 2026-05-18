package com.kreedaankana.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreedaankana.data.local.entity.*
import com.kreedaankana.data.repository.OwnerRepository
import com.kreedaankana.util.Resource

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


data class OwnerDashboardState(
    val isLoading: Boolean = false,
    val totalRevenue: Double = 0.0,
    val todayRevenue: Double = 0.0,
    val totalBookings: Int = 0,
    val todayBookings: Int = 0,
    val activeGrounds: Int = 0,
    val pendingBookings: Int = 0,
    val activeTournaments: Int = 0,
    val liveMatches: Int = 0,
    val grounds: List<OwnerGroundEntity> = emptyList(),
    val recentBookings: List<BookingEntity> = emptyList(),
    val error: String? = null
)

class OwnerDashboardViewModel(
    private val ownerRepository: OwnerRepository
) : ViewModel() {
    private val _state = MutableStateFlow(OwnerDashboardState())
    val state: StateFlow<OwnerDashboardState> = _state.asStateFlow()

    init { loadDashboard() }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            _state.value = _state.value.copy(
                todayRevenue = ownerRepository.getDailyRevenue(today),
                todayBookings = ownerRepository.getBookingCount(today),
                isLoading = false
            )
        }
        viewModelScope.launch {
            ownerRepository.getOwnerGrounds().collect { grounds ->
                _state.value = _state.value.copy(grounds = grounds, activeGrounds = grounds.size)
            }
        }
    }
}

class OwnerGroundViewModel(
    private val ownerRepository: OwnerRepository
) : ViewModel() {
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success.asStateFlow()

    val grounds: StateFlow<List<OwnerGroundEntity>> = ownerRepository.getOwnerGrounds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveGround(
        name: String, village: String, address: String, lat: Double, lng: Double,
        sports: String, openTime: String, closeTime: String, slotDuration: Int,
        price: Double, amenities: String, hasParking: Boolean, hasLighting: Boolean,
        hasWashroom: Boolean, hasWater: Boolean, seating: Int, rules: String
    ) {
        if (name.isBlank()) { _error.value = "Ground name is required"; return }
        viewModelScope.launch {
            _isSaving.value = true; _error.value = null
            val result = ownerRepository.saveGround(OwnerGroundEntity(
                name = name, village = village, address = address, latitude = lat, longitude = lng,
                sports = sports, openingTime = openTime, closingTime = closeTime,
                slotDuration = slotDuration, pricePerSlot = price, amenities = amenities,
                hasParking = hasParking, hasLighting = hasLighting, hasWashroom = hasWashroom,
                hasDrinkingWater = hasWater, seatingCapacity = seating, rules = rules
            ))
            _isSaving.value = false
            when (result) {
                is Resource.Success -> _success.value = true
                is Resource.Error -> _error.value = result.message
                else -> {}
            }
        }
    }

    fun deleteGround(groundId: String) {
        viewModelScope.launch { ownerRepository.deleteGround(groundId) }
    }

    fun clearStates() { _error.value = null; _success.value = false }
}

class OwnerSlotViewModel(
    private val ownerRepository: OwnerRepository
) : ViewModel() {
    fun getBlockedSlots(date: String) = ownerRepository.getBlockedSlots(date)

    fun blockSlot(date: String, timeSlot: String, reason: String, isMaintenance: Boolean) {
        viewModelScope.launch { ownerRepository.blockSlot(date, timeSlot, reason, isMaintenance) }
    }

    fun unblockSlot(id: Int) {
        viewModelScope.launch { ownerRepository.unblockSlot(id) }
    }
}

class OwnerBookingRequestViewModel(
    private val ownerRepository: OwnerRepository
) : ViewModel() {
    val bookings: StateFlow<List<BookingEntity>> = ownerRepository.getPendingBookings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun approveBooking(bookingId: String) {
        viewModelScope.launch { ownerRepository.approveBooking(bookingId) }
    }

    fun rejectBooking(bookingId: String) {
        viewModelScope.launch { ownerRepository.rejectBooking(bookingId) }
    }
}

class OwnerTournamentViewModel(
    private val ownerRepository: OwnerRepository
) : ViewModel() {
    val tournaments: StateFlow<List<TournamentEntity>> = ownerRepository.getTournaments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success.asStateFlow()

    fun saveTournament(name: String, sportType: String, entryFee: Double, maxTeams: Int,
                       format: String, prize: String, rules: String, matchDuration: Int,
                       startDate: String, endDate: String, deadline: String) {
        if (name.isBlank()) { _error.value = "Tournament name is required"; return }
        viewModelScope.launch {
            _isSaving.value = true
            val result = ownerRepository.saveTournament(TournamentEntity(
                tournamentId = "", name = name, sportType = sportType, startDate = startDate,
                endDate = endDate, status = "upcoming"
            ))
            _isSaving.value = false
            when (result) {
                is Resource.Success -> _success.value = true
                is Resource.Error -> _error.value = result.message
                else -> {}
            }
        }
    }

    fun deleteTournament(id: String) {
        viewModelScope.launch { ownerRepository.deleteTournament(id) }
    }

    fun approveTeam(tournamentId: String, teamsJson: String) {
        viewModelScope.launch { ownerRepository.approveTeam(tournamentId, teamsJson) }
    }

    fun generateFixtures(tournamentId: String, teamsJson: String) {
        viewModelScope.launch { ownerRepository.generateFixtures(tournamentId, teamsJson) }
    }

    fun clearStates() { _error.value = null; _success.value = false }
}

class OwnerLiveScoreViewModel(
    private val ownerRepository: OwnerRepository
) : ViewModel() {
    val matches: StateFlow<List<MatchEntity>> = ownerRepository.getMatches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateScore(matchId: String, scoreA: Int, scoreB: Int) {
        viewModelScope.launch { ownerRepository.updateScore(matchId, scoreA, scoreB, "$scoreA-$scoreB") }
    }

    fun startMatch(matchId: String) {
        viewModelScope.launch { ownerRepository.updateMatchStatus(matchId, "live") }
    }

    fun endMatch(matchId: String, winner: String) {
        viewModelScope.launch {
            ownerRepository.declareWinner(matchId, winner)
        }
    }

    fun pauseMatch(matchId: String) {
        viewModelScope.launch { ownerRepository.updateMatchStatus(matchId, "paused") }
    }
}

class OwnerAnalyticsViewModel(
    private val ownerRepository: OwnerRepository
) : ViewModel() {
    private val _revenue = MutableStateFlow(0.0)
    val revenue: StateFlow<Double> = _revenue.asStateFlow()
    private val _weeklyRevenue = MutableStateFlow(0.0)
    val weeklyRevenue: StateFlow<Double> = _weeklyRevenue.asStateFlow()
    private val _monthlyRevenue = MutableStateFlow(0.0)
    val monthlyRevenue: StateFlow<Double> = _monthlyRevenue.asStateFlow()

    init { loadAnalytics() }

    private fun loadAnalytics() {
        viewModelScope.launch {
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            _revenue.value = ownerRepository.getDailyRevenue(today)
            val cal = java.util.Calendar.getInstance()
            cal.add(java.util.Calendar.DAY_OF_YEAR, -7)
            _weeklyRevenue.value = ownerRepository.getRangeRevenue(
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(cal.time), today
            )
            cal.add(java.util.Calendar.DAY_OF_YEAR, -23)
            _monthlyRevenue.value = ownerRepository.getRangeRevenue(
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(cal.time), today
            )
        }
    }
}

class OwnerPaymentViewModel(
    private val ownerRepository: OwnerRepository
) : ViewModel() {
    val payments: StateFlow<List<PaymentEntity>> = ownerRepository.getPayments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun approvePayment(paymentId: String) {
        viewModelScope.launch { ownerRepository.approvePayment(paymentId) }
    }

    fun rejectPayment(paymentId: String) {
        viewModelScope.launch { ownerRepository.rejectPayment(paymentId) }
    }

    fun saveUpi(upiId: String) {
        viewModelScope.launch { ownerRepository.saveUpi(OwnerUpiEntity(ownerId = "", upiId = upiId)) }
    }
}
