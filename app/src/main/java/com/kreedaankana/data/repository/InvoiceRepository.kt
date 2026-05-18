package com.kreedaankana.data.repository

import com.google.firebase.storage.FirebaseStorage
import com.kreedaankana.data.local.dao.InvoiceDao
import com.kreedaankana.data.local.entity.InvoiceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

class InvoiceRepository @Inject constructor(
    private val invoiceDao: InvoiceDao,
    private val firebaseStorage: FirebaseStorage
) {
    fun getAllInvoices(): Flow<List<InvoiceEntity>> = invoiceDao.getAllInvoices()

    suspend fun getInvoiceByBookingId(bookingId: String) = invoiceDao.getInvoiceByBookingId(bookingId)

    suspend fun saveInvoice(invoice: InvoiceEntity) {
        invoiceDao.insert(invoice)
        if (invoice.pdfPath.isNotEmpty()) {
            backupToFirebase(invoice)
        }
    }

    private suspend fun backupToFirebase(invoice: InvoiceEntity) {
        try {
            val file = File(invoice.pdfPath)
            if (file.exists()) {
                val ref = firebaseStorage.reference.child("invoices/${invoice.customerId}/${file.name}")
                ref.putFile(android.net.Uri.fromFile(file)).await()
                invoiceDao.update(invoice.copy(isSynced = true))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun downloadFromFirebase(customerId: String, fileName: String, targetFile: File): Boolean {
        return try {
            val ref = firebaseStorage.reference.child("invoices/$customerId/$fileName")
            ref.getFile(targetFile).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
