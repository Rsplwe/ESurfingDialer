package com.rsplwe.esurfing.cipher.impl

import com.rsplwe.esurfing.cipher.CipherInterface
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@OptIn(ExperimentalStdlibApi::class)
class AESECB(
    private val key1: ByteArray,
    private val key2: ByteArray,
) : CipherInterface {

    private fun aesEncrypt(bytes: ByteArray, key: ByteArray): ByteArray {
        val paddedPlaintext = if (bytes.size % 16 == 0) {
            bytes
        } else {
            bytes.copyOf((bytes.size / 16 + 1) * 16)
        }
        val cipher = Cipher.getInstance("AES/ECB/NoPadding")
        val secretKey = SecretKeySpec(key, "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encrypt = cipher.doFinal(paddedPlaintext)
        return encrypt
    }

    private fun aesDecrypt(bytes: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/ECB/NoPadding")
        val secretKey = SecretKeySpec(key, "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decrypt = cipher.doFinal(bytes)
        return decrypt
    }

    override fun encrypt(text: String): String {
        val r1 = aesEncrypt(text.toByteArray(), key1)
        val r2 = aesEncrypt(r1, key2)
        return r2.toHexString(format = HexFormat.UpperCase)
    }

    override fun decrypt(hex: String): String {
        val bytes = hex.hexToByteArray()
        val r1 = aesDecrypt(bytes, key2)
        val r2 = aesDecrypt(r1, key1).dropLastWhile { it == 0.toByte() }.toByteArray()
        return r2.decodeToString()
    }
}