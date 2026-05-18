package com.kreedaankana.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreedaankana.data.local.entity.BookingEntity
import com.kreedaankana.data.local.entity.InvoiceEntity
import com.kreedaankana.data.model.QRPayload
import com.kreedaankana.data.repository.InvoiceRepository
import com.kreedaankana.util.SecureInvoiceGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class InvoiceViewModel(
    private val invoiceRepository: InvoiceRepository
) : ViewModel() {

    private val _invoiceState = MutableStateFlow<InvoiceState>(InvoiceState.Idle)
    val invoiceState = _invoiceState.asStateFlow()

    fun generateAndSaveInvoice(context: Context, booking: BookingEntity, qrPayload: QRPayload) {
        viewModelScope.launch {
            _invoiceState.value = InvoiceState.Loading
            
            val pdfFile = SecureInvoiceGenerator.generateBookingInvoice(context, booking, qrPayload)
            
            if (pdfFile != null) {
                val invoice = InvoiceEntity(
                    invoiceId = "INV-${booking.bookingId}",
                    bookingId = booking.bookingId,
                    customerId = "USER_123", // Replace with real user ID
                    customerName = booking.customerName,
                    amount = booking.amount,
                    pdfPath = pdfFile.absolutePath,
                    paymentStatus = booking.paymentStatus,
                    createdAt = System.currentTimeMillis()
                )
                invoiceRepository.saveInvoice(invoice)
                _invoiceState.value = InvoiceState.Success(pdfFile)
            } else {
                _invoiceState.value = InvoiceState.Error("Failed to generate PDF")
            }
        }
    }
}

sealed class InvoiceState {
    object Idle : InvoiceState()
    object Loading : InvoiceState()
    data class Success(val file: File) : InvoiceState()
    data class Error(val message: String) : InvoiceState()
}
