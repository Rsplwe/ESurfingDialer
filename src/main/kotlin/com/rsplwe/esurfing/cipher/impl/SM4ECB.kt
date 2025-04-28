package com.rsplwe.esurfing.cipher.impl

import com.rsplwe.crypto.SM4
import com.rsplwe.crypto.SM4.BLOCK_SIZE
import com.rsplwe.esurfing.cipher.CipherInterface

@OptIn(ExperimentalStdlibApi::class)
class SM4ECB(
    private val key: ByteArray
) : CipherInterface, SM4Base() {

    override fun encrypt(text: String): String {
        val bytes = text.toByteArray()
        val encrypt = encryptECB(key, bytes)
        return encrypt.toHexString(HexFormat.UpperCase)
    }

    override fun decrypt(hex: String): String {
        val bytes = hex.hexToByteArray()
        val decrypt = decryptECB(key, bytes)
        return decrypt.dropLastWhile { it == 0.toByte() }.toByteArray().decodeToString()
    }

    fun encryptECB(key: ByteArray, plaintext: ByteArray): ByteArray {
        require(key.size == BLOCK_SIZE) { "Key must be 16 bytes" }

        val padded = pkcs7Padding(plaintext)
        val blocks = padded.size / BLOCK_SIZE
        val output = ByteArray(padded.size)
        val core = SM4()
        core.init(true, key)

        for (i in 0 until blocks) {
            val block = padded.copyOfRange(i * BLOCK_SIZE, (i + 1) * BLOCK_SIZE)
            val encrypted = core.processBlock(block)
            System.arraycopy(encrypted, 0, output, i * BLOCK_SIZE, BLOCK_SIZE)
        }
        return output
    }

    fun decryptECB(key: ByteArray, ciphertext: ByteArray): ByteArray {
        require(key.size == BLOCK_SIZE) { "Key must be 16 bytes" }
        require(ciphertext.size % BLOCK_SIZE == 0) { "Ciphertext length must be multiple of 16" }

        val blocks = ciphertext.size / BLOCK_SIZE
        val output = ByteArray(ciphertext.size)
        val core = SM4()
        core.init(false, key)

        for (i in 0 until blocks) {
            val block = ciphertext.copyOfRange(i * BLOCK_SIZE, (i + 1) * BLOCK_SIZE)
            val decrypted = core.processBlock(block)
            System.arraycopy(decrypted, 0, output, i * BLOCK_SIZE, BLOCK_SIZE)
        }
        return pkcs7UnPadding(output)
    }
}