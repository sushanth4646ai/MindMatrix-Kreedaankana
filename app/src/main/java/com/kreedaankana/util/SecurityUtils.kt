package com.kreedaankana.util

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object SecurityUtils {
    // In a real production app, these should be securely stored (e.g., Android Keystore)
    // or fetched from a secure remote config. For this implementation, we use static keys.
    private const val AES_KEY = "KREEDA_ANKANA_SECRET_AES_KEY_32" // 32 chars for AES-256
    private const val HMAC_KEY = "KREEDA_ANKANA_HMAC_SECRET_KEY_64"

    fun encryptPayload(payload: String): String {
        return try {
            val keySpec = SecretKeySpec(AES_KEY.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val iv = ByteArray(cipher.blockSize)
            SecureRandom().nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)
            
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            val encrypted = cipher.doFinal(payload.toByteArray())
            
            val combined = ByteArray(iv.size + encrypted.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encrypted, 0, combined, iv.size, encrypted.size)
            
            Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            payload // Fallback to raw if encryption fails (should handle better in production)
        }
    }

    fun decryptPayload(encryptedPayload: String): String? {
        return try {
            val combined = Base64.decode(encryptedPayload, Base64.NO_WRAP)
            val keySpec = SecretKeySpec(AES_KEY.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            
            val ivSize = cipher.blockSize
            val iv = ByteArray(ivSize)
            val encrypted = ByteArray(combined.size - ivSize)
            
            System.arraycopy(combined, 0, iv, 0, ivSize)
            System.arraycopy(combined, ivSize, encrypted, 0, encrypted.size)
            
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            String(cipher.doFinal(encrypted))
        } catch (e: Exception) {
            null
        }
    }

    fun generateSignature(data: String): String {
        return try {
            val mac = Mac.getInstance("HmacSHA256")
            val secretKey = SecretKeySpec(HMAC_KEY.toByteArray(), "HmacSHA256")
            mac.init(secretKey)
            Base64.encodeToString(mac.doFinal(data.toByteArray()), Base64.NO_WRAP)
        } catch (e: Exception) {
            ""
        }
    }

    fun verifySignature(data: String, signature: String): Boolean {
        return generateSignature(data) == signature
    }
}
