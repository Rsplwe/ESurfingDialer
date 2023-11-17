package com.rsplwe.esurfing

import com.rsplwe.esurfing.States.isRunning
import com.rsplwe.esurfing.States.ticket
import com.rsplwe.esurfing.hook.Session
import com.rsplwe.esurfing.network.NetResult
import com.rsplwe.esurfing.network.post
import com.rsplwe.esurfing.utils.ConnectivityStatus.*
import com.rsplwe.esurfing.utils.MacAddress
import com.rsplwe.esurfing.utils.getTime
import org.apache.log4j.Logger
import java.lang.Thread.sleep
import java.util.*

class Client(private val options: Options) : Runnable {

    private val logger: Logger = Logger.getLogger(Client::class.java)
    private var keepUrl = ""
    private var termUrl = ""
    private var keepRetry = ""

    var session: Session? = null

    @Volatile
    var tick: Long = 0

    override fun run() {
        while (isRunning) {
            if (States.networkStatus == DEFAULT) logger.info("wait network check...")
            if (States.networkStatus == SUCCESS) {
                if (session != null) {
                    if ((System.currentTimeMillis() - tick) >= (keepRetry.toLong() * 1000)) {
                        logger.info("Send Keep Packet")
                        heartbeat(ticket)
                        logger.info("Next Retry: $keepRetry")
                        tick = System.currentTimeMillis()
                    }
                }
            }
            if (States.networkStatus == IS_REDIRECTS_NOT_FOUND_IP) continue
            if (States.networkStatus == REQUEST_ERROR) {
                sleep(5000)
                continue
            }
            if (States.networkStatus == IS_REDIRECTS_FOUND_IP) {
                session?.free()
                // Reset Info
                States.algoId = "00000000-0000-0000-0000-000000000000"
                States.macAddress = MacAddress.random()
                States.clientId = UUID.randomUUID().toString().lowercase()

                initSession()
                if ((session?.getSessionId() ?: 0) == 0.toLong()) {
                    logger.error("Failed to initialize session.")
                    continue
                } else {
                    logger.info("Session ID: ${session?.getSessionId()}")
                }

                logger.info("Client IP: ${States.userIp}")
                logger.info("AC IP: ${States.acIp}")

                ticket = getTicket()
                logger.info("Ticket: $ticket")
                login()

                if (keepUrl.isEmpty()) {
                    logger.error("KeepUrl is empty.")
                    session?.free()
                    session = null

                    sleep(10 * 60 * 1000)
                    continue
                }
                States.networkStatus = SUCCESS
                tick = System.currentTimeMillis()
                logger.info("The network has been connected.")
            }
            sleep(200)
        }
    }

    private fun initSession() {
        when (val result = post(States.ticketUrl, States.algoId)) {
            is NetResult.Success -> {
                session = Session(result.data)
            }

            is NetResult.Error -> {
                error("Error: ${result.exception}")
            }
        }
    }

    private fun getTicket(): String {
        val payload = """
            <?xml version="1.0" encoding="utf-8"?>
            <request>
                <user-agent>${Constants.USER_AGENT}</user-agent>
                <client-id>${States.clientId}</client-id>
                <local-time>${getTime()}</local-time>
                <host-name>Xiaomi 6</host-name>
                <ipv4>${States.userIp}</ipv4>
                <ipv6></ipv6>
                <mac>${States.macAddress}</mac>
                <ostag>Xiaomi 6</ostag>
            </request>
        """.trimIndent()
        when (val result = post(States.ticketUrl, session!!.encrypt(payload))) {
            is NetResult.Success -> {
                val data = session!!.decrypt(result.data.toString(Charsets.UTF_8))
                logger.info(data)
                return data.substringAfter("<ticket>").substringBefore("</ticket>")
            }

            is NetResult.Error -> {
                error("Error: ${result.exception}")
            }
        }
    }

    private fun login() {
        val payload = """
            <?xml version="1.0" encoding="utf-8"?>
            <request>
                <user-agent>${Constants.USER_AGENT}</user-agent>
                <client-id>${States.clientId}</client-id>
                <local-time>${getTime()}</local-time>
                <ticket>${ticket}</ticket>
                <userid>${options.loginUser}</userid>
                <passwd>${options.loginPassword}</passwd>
            </request>
        """.trimIndent()
        when (val result = post(Constants.AUTH_URL, session!!.encrypt(payload))) {
            is NetResult.Success -> {
                val data = session!!.decrypt(result.data.toString(Charsets.UTF_8))
                logger.info(data)
                keepUrl = data.substringAfter("<keep-url><![CDATA[").substringBefore("]]></keep-url>")
                termUrl = data.substringAfter("<term-url><![CDATA[").substringBefore("]]></term-url>")
                keepRetry = data.substringAfter("<keep-retry>").substringBefore("</keep-retry>")

                logger.info("Keep Url: $keepUrl")
                logger.info("Term Url: $termUrl")
                logger.info("Keep Retry: $keepRetry")
            }

            is NetResult.Error -> {
                error("Error: ${result.exception}")
            }
        }
    }

    private fun heartbeat(ticket: String) {
        val payload = """
            <?xml version="1.0" encoding="utf-8"?>
            <request>
                <user-agent>${Constants.USER_AGENT}</user-agent>
                <client-id>${States.clientId}</client-id>
                <local-time>${getTime()}</local-time>
                <host-name>Xiaomi 6</host-name>
                <ipv4>${States.userIp}</ipv4>
                <ticket>${ticket}</ticket>
                <ipv6></ipv6>
                <mac>${States.macAddress}</mac>
                <ostag>Xiaomi 6</ostag>
            </request>
        """.trimIndent()
        when (val result = post(keepUrl, session!!.encrypt(payload))) {
            is NetResult.Success -> {
                val data = session!!.decrypt(result.data.toString(Charsets.UTF_8))
                keepRetry = data.substringAfter("<interval>").substringBefore("</interval>")
            }

            is NetResult.Error -> {
                error("Error: ${result.exception}")
            }
        }
    }

    fun term() {
        val payload = """
            <?xml version="1.0" encoding="utf-8"?>
            <request>
                <user-agent>${Constants.USER_AGENT}</user-agent>
                <client-id>${States.clientId}</client-id>
                <local-time>${getTime()}</local-time>
                <host-name>Xiaomi 6</host-name>
                <ipv4>${States.userIp}</ipv4>
                <ticket>${ticket}</ticket>
                <ipv6></ipv6>
                <mac>${States.macAddress}</mac>
                <ostag>Xiaomi 6</ostag>
            </request>
        """.trimIndent()
        when (val result = post(termUrl, session!!.encrypt(payload))) {
            is NetResult.Success -> {}
            is NetResult.Error -> {
                error("Error: ${result.exception}")
            }
        }
    }
}
