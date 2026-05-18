package com.kreedaankana.data.local.dao

import androidx.room.*
import com.kreedaankana.data.local.entity.InvoiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices ORDER BY createdAt DESC")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE bookingId = :bookingId")
    suspend fun getInvoiceByBookingId(bookingId: String): InvoiceEntity?

    @Query("SELECT * FROM invoices WHERE invoiceId = :invoiceId")
    suspend fun getInvoiceById(invoiceId: String): InvoiceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(invoice: InvoiceEntity)

    @Update
    suspend fun update(invoice: InvoiceEntity)

    @Query("SELECT COUNT(*) FROM invoices WHERE bookingId = :bookingId")
    suspend fun countInvoicesForBooking(bookingId: String): Int
}
