package com.kreedaankana.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreedaankana.data.local.entity.VerificationLogEntity
import com.kreedaankana.data.model.QRPayload
import com.kreedaankana.data.repository.QRRepository
import com.kreedaankana.data.repository.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OwnerQRScannerViewModel(
    private val qrRepository: QRRepository
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState = _scanState.asStateFlow()

    fun processScannedData(encryptedData: String, ownerId: String) {
        viewModelScope.launch {
            _scanState.value = ScanState.Processing
            
            val payload = QRPayload.deserialize(encryptedData)
            if (payload == null) {
                _scanState.value = ScanState.Error("Invalid or Tampered QR Code")
                return@launch
            }

            // Verify if this ground belongs to the scanning owner
            if (payload.ownerId != ownerId) {
                _scanState.value = ScanState.Error("Unauthorized Venue")
                logResult(payload.bookingId, "INVALID_VENUE", "Scanned at wrong venue")
                return@launch
            }

            when (val result = qrRepository.validateTicket(payload)) {
                is ValidationResult.Success -> {
                    _scanState.value = ScanState.Success(result.payload)
                    logResult(payload.bookingId, "SUCCESS", "Entry Allowed")
                }
                is ValidationResult.Error -> {
                    _scanState.value = ScanState.Error(result.message)
                    logResult(payload.bookingId, "REJECTED", result.message)
                }
            }
        }
    }

    private suspend fun logResult(bookingId: String, result: String, message: String) {
        val log = VerificationLogEntity(
            bookingId = bookingId,
            result = result,
            message = message,
            offlineVerified = true
        )
        qrRepository.logVerification(log)
    }

    fun resetScanner() {
        _scanState.value = ScanState.Idle
    }
}

sealed class ScanState {
    object Idle : ScanState()
    object Processing : ScanState()
    data class Success(val payload: QRPayload) : ScanState()
    data class Error(val message: String) : ScanState()
}
