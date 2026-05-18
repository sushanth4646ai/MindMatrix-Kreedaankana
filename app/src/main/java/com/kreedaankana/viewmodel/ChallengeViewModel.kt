package com.kreedaankana.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreedaankana.data.local.entity.ChallengeEntity
import com.kreedaankana.data.repository.ChallengeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChallengeViewModel(
    private val challengeRepository: ChallengeRepository
) : ViewModel() {

    val challenges = challengeRepository.getAllChallenges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            challengeRepository.syncChallenges()
        }
    }

    fun createChallenge(challenge: ChallengeEntity) {
        viewModelScope.launch {
            challengeRepository.createChallenge(challenge)
        }
    }
}
