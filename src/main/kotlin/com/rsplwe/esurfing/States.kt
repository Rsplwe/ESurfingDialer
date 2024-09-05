package com.rsplwe.esurfing

import com.rsplwe.esurfing.utils.randomMACAddress
import java.io.File
import java.util.UUID


object States {

    val rootDir = File("target")

    var clientId = UUID.randomUUID().toString().lowercase()
    var algoId = "00000000-0000-0000-0000-000000000000"
    var macAddress = randomMACAddress()
    var ticket = ""
    var userIp = ""
    var acIp = ""

    @Volatile
    var isRunning = true

    var useDynarmic = false

    var schoolId = ""
    var domain = ""
    var area = ""
    var ticketUrl = ""
    var authUrl = ""
    var extraCfgUrl = HashMap<String, String>()
}