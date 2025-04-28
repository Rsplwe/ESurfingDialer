package com.rsplwe.esurfing.cipher.impl

import com.rsplwe.esurfing.cipher.CipherInterface

@OptIn(ExperimentalStdlibApi::class)
class ModXTEA(
    private val key1: IntArray,
    private val key2: IntArray,
    private val key3: IntArray
) : CipherInterface, ModXTEABase() {

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
}
