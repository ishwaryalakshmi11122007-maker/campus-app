package com.example.util

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CampusCrypto {

    // 256-bit institutional encryption key derivation seed
    private const val MASTER_SEED = "CAMPUS-SECURE-DB-KEY-2026#AES256"
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val CIPHER_KEY_SPEC = "AES"

    private val secretKey: SecretKeySpec by lazy {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(MASTER_SEED.toByteArray(StandardCharsets.UTF_8))
        SecretKeySpec(keyBytes, CIPHER_KEY_SPEC)
    }

    // Fixed institutional IV for reproducible encrypted field storage in database
    private val fixedIv: IvParameterSpec by lazy {
        val digest = MessageDigest.getInstance("MD5")
        val ivBytes = digest.digest("CAMPUS-IV-SEED-2026".toByteArray(StandardCharsets.UTF_8))
        IvParameterSpec(ivBytes)
    }

    /**
     * Encrypts plain text string using AES-256-CBC.
     * Returns Base64-encoded ciphertext prefixed with "ENC:"
     */
    fun encrypt(plainText: String): String {
        if (plainText.isBlank()) return ""
        if (plainText.startsWith("ENC:")) return plainText // already encrypted
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, fixedIv)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
            "ENC:" + Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            plainText
        }
    }

    /**
     * Decrypts ciphertext prefixed with "ENC:" back to plain text.
     * If not prefixed, returns string as is.
     */
    fun decrypt(cipherText: String): String {
        if (!cipherText.startsWith("ENC:")) return cipherText
        return try {
            val rawBase64 = cipherText.removePrefix("ENC:")
            val decodedBytes = Base64.decode(rawBase64, Base64.NO_WRAP)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, fixedIv)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            cipherText
        }
    }

    /**
     * Generates SHA-256 checksum fingerprint of text/data.
     */
    fun sha256Fingerprint(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(input.toByteArray(StandardCharsets.UTF_8))
            hash.joinToString("") { "%02x".format(it) }.take(16).uppercase()
        } catch (e: Exception) {
            "HASH-ERR"
        }
    }

    /**
     * Masks sensitive values (e.g. Aadhaar/Govt ID: ••••-••••-4920)
     */
    fun maskSensitive(value: String): String {
        val decrypted = decrypt(value)
        if (decrypted.length <= 4) return "••••"
        val visiblePart = decrypted.takeLast(4)
        return "••••-••••-$visiblePart"
    }

    /**
     * Hashes password using SHA-256 for secure comparison.
     */
    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.trim().toByteArray(StandardCharsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
}
