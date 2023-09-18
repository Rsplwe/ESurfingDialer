package com.rsplwe.esurfing

import com.rsplwe.esurfing.utils.ConnectivityStatus
import java.io.File


object States {

    val rootDir = File("target")

    var clientId = ""
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