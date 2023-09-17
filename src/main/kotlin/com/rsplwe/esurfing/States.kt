package com.rsplwe.esurfing

import com.rsplwe.esurfing.utils.ConnectivityStatus
import java.io.File
import java.util.*


object States {

    val rootDir = File("target")

    val clientId = UUID.randomUUID().toString().lowercase()
    var algoId = ""
    var macAddress = ""
    var userIp = ""
    var acIp = ""
    var ticket = ""

    @get:Synchronized
    var networkStatus: ConnectivityStatus = ConnectivityStatus.DEFAULT

    @Volatile
    var isRunning = true

    var useDynarmic = false

    val ticketUrl: String
        get() {
            return "${Constants.BASE_URL}/ticket.cgi?wlanuserip=${userIp}&wlanacip=${acIp}&portal_node=${Constants.PORTAL_NODE}"
        }

}