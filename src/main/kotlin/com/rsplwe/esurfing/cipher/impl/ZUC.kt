package com.rsplwe.esurfing.cipher.impl

import com.rsplwe.esurfing.cipher.CipherInterface
import org.bouncycastle.crypto.CipherParameters
import org.bouncycastle.crypto.StreamCipher
import org.bouncycastle.crypto.engines.Zuc128Engine
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV

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
        return processZUC(true, paddedPlaintext).toHexString(HexFormat.UpperCase)
    }


    override fun decrypt(hex: String): String {
        val bytes = hex.hexToByteArray()
        return processZUC(false, bytes).dropLastWhile { it == 0.toByte() }.toByteArray().decodeToString()
    }

    fun processZUC(forEncryption: Boolean, input: ByteArray): ByteArray {
        val zuc: StreamCipher = Zuc128Engine()
        val params: CipherParameters = ParametersWithIV(KeyParameter(key), iv)
        val output = ByteArray(input.size)
        zuc.init(forEncryption, params)
        zuc.processBytes(input, 0, input.size, output, 0)
        return output
    }

}