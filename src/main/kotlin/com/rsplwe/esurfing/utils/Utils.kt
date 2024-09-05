package com.rsplwe.esurfing.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun getTime(): String {
    val now = LocalDateTime.now(ZoneId.of("+8"))
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return formatter.format(now)
}

fun extractBetweenTags(input: String, startTag: String, endTag: String): String {
    val startIndex = input.indexOf(startTag)
    if (startIndex != -1) {
        val endIndex = input.indexOf(endTag, startIndex + startTag.length)
        if (endIndex != -1) {
            return input.substring(startIndex + startTag.length, endIndex)
        }
    }
    return ""
}