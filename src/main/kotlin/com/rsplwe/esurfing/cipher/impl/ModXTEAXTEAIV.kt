package com.rsplwe.esurfing.cipher.impl

import com.rsplwe.esurfing.cipher.CipherInterface

@OptIn(ExperimentalStdlibApi::class)
class ModXTEAXTEAIV(
    private val key1: IntArray,
    private val key2: IntArray,
    private val key3: IntArray,
    private val iv: IntArray
) : CipherInterface, ModXTEABase() {

    private fun xorBlock(v0In: Int, v1In: Int, prev: IntArray): Pair<Int, Int> {
        return Pair((prev[0] xor v0In), (prev[1] xor v1In))
    }

    override fun encrypt(text: String): String {
        val bytes = text.toByteArray()
        val blocks = bytes.copyOf().padToMultipleOf8()
        var previous = iv
        for (i in blocks.indices step 8) {
            val v0 = blocks.getInt(i)
            val v1 = blocks.getInt(i + 4)
            val xored = xorBlock(v0, v1, previous)
            val round1 = encryptBlock(xored.first, xored.second, key3)
            val round2 = encryptBlock(round1.first, round1.second, key2)
            val round3 = encryptBlock(round2.first, round2.second, key1)
            blocks.setInt(i, round3.first)
            blocks.setInt(i + 4, round3.second)
            previous = intArrayOf(blocks.getInt(i), blocks.getInt(i + 4))
        }
        return blocks.toHexString(HexFormat.UpperCase)
    }

    override fun decrypt(hex: String): String {
        val blocks = hex.hexToByteArray()
        var previous = iv
        for (i in blocks.indices step 8) {
            val v0 = blocks.getInt(i)
            val v1 = blocks.getInt(i + 4)
            val round1 = decryptBlock(v0, v1, key1)
            val round2 = decryptBlock(round1.first, round1.second, key2)
            val round3 = decryptBlock(round2.first, round2.second, key3)
            val xored = xorBlock(round3.first, round3.second, previous)
            blocks.setInt(i, xored.first)
            blocks.setInt(i + 4, xored.second)
            previous = intArrayOf(v0, v1)
        }
        return blocks.dropLastWhile { it == 0.toByte() }.toByteArray().decodeToString()
    }
}
