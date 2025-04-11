package com.rsplwe.esurfing.cipher.impl

import com.rsplwe.esurfing.cipher.CipherInterface

@OptIn(ExperimentalStdlibApi::class)
class ModXTEA(
    private val key1: IntArray,
    private val key2: IntArray,
    private val key3: IntArray
) : CipherInterface {

    private val NUM_ROUNDS = 32
    private val DELTA = 0x9E3779B9.toInt()

    override fun encrypt(text: String): String {
        val bytes = text.toByteArray()
        val blocks = bytes.copyOf().padToMultipleOf8()
        for (i in blocks.indices step 8) {
            val v0 = blocks.getInt(i)
            val v1 = blocks.getInt(i + 4)
            val round1 = encryptBlock(v0, v1, key1)
            val round2 = encryptBlock(round1.first, round1.second, key2)
            val round3 = encryptBlock(round2.first, round2.second, key3)
            blocks.setInt(i, round3.first)
            blocks.setInt(i + 4, round3.second)
        }
        return blocks.toHexString(HexFormat.UpperCase)
    }

    override fun decrypt(hex: String): String {
        val bytes = hex.hexToByteArray()
        val blocks = bytes.copyOf()
        for (i in blocks.indices step 8) {
            val v0 = blocks.getInt(i)
            val v1 = blocks.getInt(i + 4)
            val round1 = decryptBlock(v0, v1, key3)
            val round2 = decryptBlock(round1.first, round1.second, key2)
            val round3 = decryptBlock(round2.first, round2.second, key1)
            blocks.setInt(i, round3.first)
            blocks.setInt(i + 4, round3.second)
        }
        return blocks.dropLastWhile { it == 0.toByte() }.toByteArray().decodeToString()
    }

    private fun encryptBlock(v0In: Int, v1In: Int, key: IntArray): Pair<Int, Int> {
        var v0 = v0In
        var v1 = v1In
        var sum = 0
        for (i in 0 until NUM_ROUNDS) {
            v0 += (v1 xor sum) + key[sum and 3] + ((v1 shl 4) xor (v1 ushr 5))
            sum += DELTA
            v1 += key[(sum ushr 11) and 3] + (v0 xor sum) + ((v0 shl 4) xor (v0 ushr 5))
        }
        return Pair(v0, v1)
    }

    private fun decryptBlock(v0In: Int, v1In: Int, key: IntArray): Pair<Int, Int> {
        var v0 = v0In
        var v1 = v1In
        var sum = DELTA * NUM_ROUNDS
        for (i in 0 until NUM_ROUNDS) {
            v1 -= key[(sum ushr 11) and 3] + (v0 xor sum) + ((v0 shl 4) xor (v0 ushr 5))
            sum -= DELTA
            v0 -= (v1 xor sum) + key[sum and 3] + ((v1 shl 4) xor (v1 ushr 5))
        }
        return Pair(v0, v1)
    }

    private fun ByteArray.getInt(offset: Int): Int =
        (this[offset].toInt() and 0xFF shl 24) or
                (this[offset + 1].toInt() and 0xFF shl 16) or
                (this[offset + 2].toInt() and 0xFF shl 8) or
                (this[offset + 3].toInt() and 0xFF)

    private fun ByteArray.setInt(offset: Int, value: Int) {
        this[offset] = (value ushr 24).toByte()
        this[offset + 1] = (value ushr 16).toByte()
        this[offset + 2] = (value ushr 8).toByte()
        this[offset + 3] = value.toByte()
    }

    private fun ByteArray.padToMultipleOf8(): ByteArray {
        val padding = (8 - this.size % 8) % 8
        return if (padding == 0) this else this + ByteArray(padding)
    }
}
