package com.rsplwe.esurfing.cipher.impl

import com.rsplwe.crypto.SM4
import com.rsplwe.crypto.SM4.BLOCK_SIZE
import com.rsplwe.esurfing.cipher.CipherInterface

@OptIn(ExperimentalStdlibApi::class)
class SM4CBC(
    private val key: ByteArray,
    private val iv: ByteArray
) : CipherInterface, SM4Base() {

    override fun encrypt(text: String): String {
        val bytes = text.toByteArray()
        val encrypt = encryptCBC(key, iv, bytes)
        return encrypt.toHexString(HexFormat.UpperCase)
    }

    override fun decrypt(hex: String): String {
        val bytes = hex.hexToByteArray()
        val decrypt = decryptCBC(key, iv, bytes)
        return decrypt.dropLastWhile { it == 0.toByte() }.toByteArray().decodeToString()
    }

    fun encryptCBC(key: ByteArray, iv: ByteArray, plaintext: ByteArray): ByteArray {
        require(key.size == BLOCK_SIZE) { "Key must be 16 bytes" }
        require(iv.size == BLOCK_SIZE) { "IV must be 16 bytes" }

        val padded = pkcs7Padding(plaintext)
        val blocks = padded.size / BLOCK_SIZE

        val output = ByteArray(padded.size)
        var prevBlock = iv.copyOf()

        val core = SM4()
        core.init(true, key)

        for (i in 0 until blocks) {
            val block = padded.copyOfRange(i * BLOCK_SIZE, (i + 1) * BLOCK_SIZE)
            val xored = block.zip(prevBlock) { a, b -> (a.toInt() xor b.toInt()).toByte() }.toByteArray()
            val encrypted = core.processBlock(xored)
            System.arraycopy(encrypted, 0, output, i * BLOCK_SIZE, BLOCK_SIZE)
            prevBlock = encrypted
        }
        return output
    }

    fun decryptCBC(key: ByteArray, iv: ByteArray, ciphertext: ByteArray): ByteArray {
        require(key.size == BLOCK_SIZE) { "Key must be 16 bytes" }
        require(iv.size == BLOCK_SIZE) { "IV must be 16 bytes" }
        require(ciphertext.size % BLOCK_SIZE == 0) { "Ciphertext length must be multiple of 16" }

        val blocks = ciphertext.size / BLOCK_SIZE

        val output = ByteArray(ciphertext.size)
        var prevBlock = iv.copyOf()

        val core = SM4()
        core.init(false, key)

        for (i in 0 until blocks) {
            val block = ciphertext.copyOfRange(i * BLOCK_SIZE, (i + 1) * BLOCK_SIZE)
            val decrypted = core.processBlock(block)
            val xored = decrypted.zip(prevBlock) { a, b -> (a.toInt() xor b.toInt()).toByte() }.toByteArray()
            System.arraycopy(xored, 0, output, i * BLOCK_SIZE, BLOCK_SIZE)
            prevBlock = block
        }
        return pkcs7UnPadding(output)
    }
}