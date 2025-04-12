package com.rsplwe.esurfing.cipher.impl

open class ModXTEABase {

    private val NUM_ROUNDS = 32
    private val DELTA = 0x9E3779B9.toInt()

    internal fun encryptBlock(v0In: Int, v1In: Int, key: IntArray): Pair<Int, Int> {
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

    internal fun decryptBlock(v0In: Int, v1In: Int, key: IntArray): Pair<Int, Int> {
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

    internal fun ByteArray.getInt(offset: Int): Int =
        (this[offset].toInt() and 0xFF shl 24) or
                (this[offset + 1].toInt() and 0xFF shl 16) or
                (this[offset + 2].toInt() and 0xFF shl 8) or
                (this[offset + 3].toInt() and 0xFF)

    internal fun ByteArray.setInt(offset: Int, value: Int) {
        this[offset] = (value ushr 24).toByte()
        this[offset + 1] = (value ushr 16).toByte()
        this[offset + 2] = (value ushr 8).toByte()
        this[offset + 3] = value.toByte()
    }

    internal fun ByteArray.padToMultipleOf8(): ByteArray {
        val padding = (8 - this.size % 8) % 8
        return if (padding == 0) this else this + ByteArray(padding)
    }
}