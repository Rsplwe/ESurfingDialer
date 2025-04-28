package com.rsplwe.esurfing.cipher.impl

import com.rsplwe.crypto.Zuc128
import com.rsplwe.esurfing.cipher.CipherInterface

@OptIn(ExperimentalStdlibApi::class)
class ZUC(
    private val key: ByteArray,
    private val iv: ByteArray
) : CipherInterface {

    override fun encrypt(text: String): String {
        val bytes = text.toByteArray()
        val paddedPlaintext = if (bytes.size % 4 == 0) {
            bytes
        } else {
            bytes.copyOf((bytes.size / 4 + 1) * 4)
        }
        return processZUC(paddedPlaintext).toHexString(HexFormat.UpperCase)
    }


    override fun decrypt(hex: String): String {
        val bytes = hex.hexToByteArray()
        return processZUC(bytes).dropLastWhile { it == 0.toByte() }.toByteArray().decodeToString()
    }

    fun processZUC(input: ByteArray): ByteArray {
        val zuc = Zuc128()
        val output = ByteArray(input.size)
        zuc.init(key, iv)
        zuc.processBytes(input, 0, input.size, output, 0)
        return output
    }

}