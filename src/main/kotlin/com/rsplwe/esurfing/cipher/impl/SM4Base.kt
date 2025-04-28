package com.rsplwe.esurfing.cipher.impl

import com.rsplwe.crypto.SM4.BLOCK_SIZE

open class SM4Base {

    fun pkcs7Padding(data: ByteArray): ByteArray {
        val padLen = BLOCK_SIZE - (data.size % BLOCK_SIZE)
        return data + ByteArray(padLen) { padLen.toByte() }
    }

    fun pkcs7UnPadding(data: ByteArray): ByteArray {
        if (data.isEmpty()) throw IllegalArgumentException("Invalid data for unPadding")
        val padLen = data.last().toInt() and 0xFF
        if (padLen < 1 || padLen > BLOCK_SIZE) {
            throw IllegalArgumentException("Invalid padding length")
        }
        for (i in 1..padLen) {
            if (data[data.size - i] != padLen.toByte()) {
                throw IllegalArgumentException("Invalid padding content")
            }
        }
        return data.copyOfRange(0, data.size - padLen)
    }

}