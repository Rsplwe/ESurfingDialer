package com.rsplwe.esurfing.utils

import java.util.Random

object MacAddress {
    fun random(): String {
        val rand = Random()
        val macAddress = ByteArray(6)
        rand.nextBytes(macAddress)
        macAddress[0] = (macAddress[0].toInt() and 254.toByte().toInt()).toByte()
        val sb = StringBuilder(18)
        for (b in macAddress) {
            if (sb.isNotEmpty()) sb.append(":")
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }
}