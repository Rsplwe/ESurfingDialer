package com.rsplwe.esurfing.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Random

fun getTime(): String {
    val now = LocalDateTime.now(ZoneId.of("+8"))
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return formatter.format(now)
}

fun String.extractBetweenTags(startTag: String, endTag: String): String {
    val startIndex = this.indexOf(startTag)
    if (startIndex != -1) {
        val endIndex = this.indexOf(endTag, startIndex + startTag.length)
        if (endIndex != -1) {
            return this.substring(startIndex + startTag.length, endIndex)
        }
    }
    return ""
}

fun randomMACAddress(): String {
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
fun randomString(length: Int): String {
    val charset = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return List(length) { charset.random() }
        .joinToString("")
}