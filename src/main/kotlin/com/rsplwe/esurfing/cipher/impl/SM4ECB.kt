package com.rsplwe.esurfing.cipher.impl

import com.rsplwe.esurfing.cipher.CipherInterface
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@OptIn(ExperimentalStdlibApi::class)
class SM4ECB(
    private val key: ByteArray
): CipherInterface {

    override fun encrypt(text: String): String {
        val bytes = text.toByteArray()
        val paddedPlaintext = if (bytes.size % 16 == 0) {
            bytes
        } else {
            bytes.copyOf((bytes.size / 16 + 1) * 16)
        }
        val cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", "BC")
        val secretKey = SecretKeySpec(key, "SM4")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encrypt = cipher.doFinal(paddedPlaintext)
        return encrypt.toHexString(HexFormat.UpperCase)
    }

    override fun decrypt(hex: String): String {
        val bytes = hex.hexToByteArray()
        val cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", "BC")
        val secretKey = SecretKeySpec(key, "SM4")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decrypt = cipher.doFinal(bytes)
        return decrypt.dropLastWhile { it == 0.toByte() }.toByteArray().decodeToString()
    }
}