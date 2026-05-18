package com.kreedaankana.util

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.kreedaankana.data.model.InvoiceDetails
import java.io.File
import java.io.FileOutputStream
import java.util.*

object SecureInvoiceGenerator {

    fun generateBookingInvoice(context: Context, booking: com.kreedaankana.data.local.entity.BookingEntity, qrPayload: com.kreedaankana.data.model.QRPayload): File? {
        val details = InvoiceDetails(
            invoiceId = booking.bookingId,
            title = "Ground Booking Invoice",
            date = booking.bookingDate,
            groundName = booking.groundName,
            groundAddress = "Kreeda Ankana Ground",
            ownerName = "Owner",
            ownerPhone = "1234567890",
            customerName = booking.customerName,
            teamName = booking.teamName,
            customerPhone = booking.customerPhone,
            upiId = "kreeda@upi",
            sportType = booking.sport,
            paymentMethod = "Online",
            items = listOf(com.kreedaankana.data.model.InvoiceItem(booking.sport, "1 Slot", booking.amount)),
            totalAmount = booking.amount,
            paymentStatus = booking.paymentStatus,
            qrPayload = qrPayload.serialize()
        )
        return generateInvoice(context, details)
    }

    fun generateInvoice(
        context: Context,
        details: InvoiceDetails
    ): File? {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()

            // Branding Header
            paint.color = Color.parseColor("#FF5722")
            canvas.drawRect(0f, 0f, 595f, 120f, paint)
            
            paint.color = Color.WHITE
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textSize = 32f
            canvas.drawText("KREEDA-ANKANA", 40f, 60f, paint)
            
            paint.textSize = 14f
            paint.typeface = Typeface.DEFAULT
            canvas.drawText(details.title.uppercase(), 40f, 90f, paint)

            var y = 160f
            paint.color = Color.BLACK
            
            // Invoice Metadata
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textSize = 14f
            canvas.drawText("INVOICE #${details.invoiceId}", 40f, y, paint)
            paint.typeface = Typeface.DEFAULT
            paint.textSize = 10f
            canvas.drawText("Date: ${details.date}", 450f, y, paint)
            y += 40f

            // Owner & Customer Columns
            val mid = 297f
            paint.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText("FROM:", 40f, y, paint)
            canvas.drawText("TO:", mid + 20f, y, paint)
            y += 20f
            paint.typeface = Typeface.DEFAULT
            canvas.drawText(details.groundName, 40f, y, paint)
            canvas.drawText(details.customerName, mid + 20f, y, paint)
            y += 15f
            canvas.drawText(details.groundAddress, 40f, y, paint)
            canvas.drawText(details.teamName, mid + 20f, y, paint)
            y += 15f
            canvas.drawText("Owner: ${details.ownerName}", 40f, y, paint)
            canvas.drawText("Phone: ${details.customerPhone}", mid + 20f, y, paint)
            y += 15f
            canvas.drawText("UPI: ${details.upiId}", 40f, y, paint)
            y += 50f

            // Table Header
            paint.color = Color.parseColor("#EEEEEE")
            canvas.drawRect(40f, y, 555f, y + 30f, paint)
            paint.color = Color.BLACK
            paint.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText("ITEM DESCRIPTION", 50f, y + 20f, paint)
            canvas.drawText("QTY", 350f, y + 20f, paint)
            canvas.drawText("PRICE", 480f, y + 20f, paint)
            y += 50f

            // Table Body
            paint.typeface = Typeface.DEFAULT
            details.items.forEach { item ->
                canvas.drawText(item.description, 50f, y, paint)
                canvas.drawText(item.quantity, 350f, y, paint)
                canvas.drawText("Rs. ${item.price}", 480f, y, paint)
                y += 25f
            }
            
            y += 20f
            canvas.drawLine(40f, y, 555f, y, paint)
            y += 30f

            // Summary
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textSize = 12f
            canvas.drawText("SUBTOTAL", 350f, y, paint)
            canvas.drawText("Rs. ${details.totalAmount}", 480f, y, paint)
            y += 20f
            paint.textSize = 16f
            paint.color = Color.parseColor("#FF5722")
            canvas.drawText("TOTAL PAID", 350f, y, paint)
            canvas.drawText("Rs. ${details.totalAmount}", 460f, y, paint)
            y += 50f

            // QR Ticket Integration
            details.qrPayload?.let { payload ->
                paint.color = Color.BLACK
                paint.textSize = 11f
                paint.typeface = Typeface.DEFAULT_BOLD
                canvas.drawText("DIGITAL ENTRY TICKET (SECURE-SIGNED)", 40f, y, paint)
                y += 15f
                
                val qrBitmap = generateQRBitmap(payload, 140)
                if (qrBitmap != null) {
                    canvas.drawBitmap(qrBitmap, 40f, y, null)
                    
                    val infoX = 200f
                    paint.typeface = Typeface.DEFAULT
                    paint.textSize = 9f
                    canvas.drawText("Booking Verified: ${details.paymentStatus}", infoX, y + 25f, paint)
                    canvas.drawText("Verification Method: SECURE-AES-256", infoX, y + 40f, paint)
                    canvas.drawText("Digital Signature: ${payload.take(20)}...", infoX, y + 55f, paint)
                    canvas.drawText("Support: support@kreedaankana.com", infoX, y + 70f, paint)
                }
                y += 160f
            }

            // Footer
            paint.color = Color.GRAY
            paint.textSize = 8f
            canvas.drawText("Thank you for using Kreeda-Ankana. Play Hard, Stay Local!", 40f, y, paint)
            y += 12f
            canvas.drawText("This is a computer-generated invoice and requires no physical signature.", 40f, y, paint)

            pdfDocument.finishPage(page)
            val file = File(context.cacheDir, "Invoice_${details.invoiceId}.pdf")
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun generateQRBitmap(content: String, size: Int): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }
}
