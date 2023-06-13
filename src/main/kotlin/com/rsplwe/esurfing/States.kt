package com.rsplwe.esurfing

import com.rsplwe.esurfing.utils.MacAddress
import java.io.File
import java.util.*

object States {

    val rootDir = File("target")

    val clientId = UUID.randomUUID().toString().lowercase()
    var algoId = "00000000-0000-0000-0000-000000000000"
    val macAddress = MacAddress.random()
    var userIp = ""
    var acIp = ""
    val ticketUrl: String
        get() {
            return "${Constants.BASE_URL}/ticket.cgi?wlanuserip=${userIp}&wlanacip=${acIp}&portal_node=${Constants.PORTAL_NODE}"
        }

}