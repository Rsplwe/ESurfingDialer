package com.rsplwe.esurfing.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun getTime(): String {
    val now = LocalDateTime.now(ZoneId.of("+8"))
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return formatter.format(now)
}