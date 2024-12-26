package com.rsplwe.esurfing

import com.rsplwe.esurfing.utils.randomMACAddress
import java.io.File
import java.util.UUID

object States {

    val rootDir = File("target")

    var clientId = ""
    var algoId = ""
    var macAddress = ""
    var ticket = ""
    var userIp = ""
    var acIp = ""

    @Volatile
    var isRunning = true

    var schoolId = ""
    var domain = ""
    var area = ""
    var ticketUrl = ""
    var authUrl = ""
    var extraCfgUrl = HashMap<String, String>()
    var isLogged = false

    fun refreshStates() {
        this.clientId = UUID.randomUUID().toString().lowercase()
        this.algoId = "00000000-0000-0000-0000-000000000000"
        this.macAddress = randomMACAddress()
    }
}