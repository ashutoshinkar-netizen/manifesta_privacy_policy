package com.example.data.auth

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID

object AuthSecurity {

    fun generateSalt(): String {
        val random = SecureRandom()
        val bytes = ByteArray(16)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun hashPassword(password: String, salt: String): String {
        val combined = "$password:$salt:manifesta_sanctuary_2026"
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(combined.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        val computed = hashPassword(password, salt)
        return computed.equals(expectedHash, ignoreCase = true)
    }

    fun generateResetCode(): String {
        val random = SecureRandom()
        val code = 100000 + random.nextInt(900000)
        return code.toString()
    }

    fun generateSessionToken(): String {
        return "sess_" + UUID.randomUUID().toString().replace("-", "")
    }

    fun generateUserId(): String {
        return "usr_" + UUID.randomUUID().toString().take(12)
    }

    fun generateSubscriptionId(): String {
        val random = SecureRandom()
        val code = 100000 + random.nextInt(900000)
        return "SUB_MFT_$code"
    }
}
