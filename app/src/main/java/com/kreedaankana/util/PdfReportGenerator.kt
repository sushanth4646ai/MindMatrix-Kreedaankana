package com.kreedaankana.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.kreedaankana.data.local.entity.BookingEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfReportGenerator {
    
    fun generateInvoice(
        context: android.content.Context,
        booking: BookingEntity
    ): File? {
        return try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            val canvas: Canvas = page.canvas
            
            val titlePaint = Paint().apply {
                color = Color.parseColor("#2E7D32")
                textSize = 24f
                isFakeBoldText = true
            }
            
            val bodyPaint = Paint().apply {
                color = Color.BLACK
                textSize = 12f
            }
            
            var y = 60f
            canvas.drawText("Kreeda-Ankana Invoice", 40f, y, titlePaint)
            y += 30f
            canvas.drawText("Booking ID: ${booking.bookingId}", 40f, y, bodyPaint)
            y += 20f
            canvas.drawText("Customer: ${booking.customerName}", 40f, y, bodyPaint)
            y += 20f
            canvas.drawText("Team: ${booking.teamName}", 40f, y, bodyPaint)
            y += 20f
            canvas.drawText("Ground: ${booking.groundName}", 40f, y, bodyPaint)
            y += 20f
            canvas.drawText("Date: ${booking.bookingDate}", 40f, y, bodyPaint)
            y += 20f
            canvas.drawText("Time: ${booking.slotTime}", 40f, y, bodyPaint)
            y += 20f
            canvas.drawText("Amount: ₹${booking.amount}", 40f, y, bodyPaint)
            
            document.finishPage(page)
            
            val file = File(context.cacheDir, "Invoice_${booking.bookingId}.pdf")
            document.writeTo(FileOutputStream(file))
            document.close()
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
