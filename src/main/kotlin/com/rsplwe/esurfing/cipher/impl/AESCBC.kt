package com.rsplwe.esurfing.cipher.impl

import com.rsplwe.esurfing.cipher.CipherInterface
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@OptIn(ExperimentalStdlibApi::class)
class AESCBC(
    private val key1: ByteArray,
    private val key2: ByteArray,
    private val iv: ByteArray
) : CipherInterface {

    private fun aesEncrypt(bytes: ByteArray, key: ByteArray): ByteArray {
        val paddedPlaintext = if (bytes.size % 16 == 0) {
            bytes
        } else {
            bytes.copyOf((bytes.size / 16 + 1) * 16)
        }
        val cipher = Cipher.getInstance("AES/CBC/NoPadding", "BC")
        val secretKey = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        val encrypt = cipher.doFinal(paddedPlaintext)
        return iv + encrypt
    }

    private fun aesDecrypt(bytes: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/NoPadding", "BC")
        val secretKey = SecretKeySpec(key, "AES")
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
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
        val r1 = aesDecrypt(bytes.copyOfRange(16, bytes.size), key2)
        val r2 = aesDecrypt(r1.copyOfRange(16, r1.size), key1).dropLastWhile { it == 0.toByte() }.toByteArray()
        return r2.decodeToString()
    }
}