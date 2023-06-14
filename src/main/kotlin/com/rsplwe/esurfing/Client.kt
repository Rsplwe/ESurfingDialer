package com.rsplwe.esurfing

import com.rsplwe.esurfing.hook.Session
import com.rsplwe.esurfing.network.NetResult
import com.rsplwe.esurfing.network.post
import com.rsplwe.esurfing.utils.ConnectivityStatus.*
import com.rsplwe.esurfing.utils.checkConnectivity
import com.rsplwe.esurfing.utils.getTime
import org.apache.log4j.Logger

class Client(private val options: Options) {

    private val logger: Logger = Logger.getLogger(Client::class.java)
    private lateinit var session: Session
    private var keepUrl = ""
    private var termUrl = ""
    private var keepRetry = ""

    @Volatile
    var tick: Long = 0

    @Volatile
    private var isRunning = false

    fun loop() {
        val networkStatus = checkConnectivity()
        when (networkStatus.status) {
            SUCCESS -> {
                logger.info("The network has been connected.")
                return
            }

            IS_REDIRECTS_NOT_FOUND_IP -> {
                logger.error("No parameter detected in url.")
                return
            }

            IS_REDIRECTS_FOUND_IP -> {
                logger.info("Client IP: ${networkStatus.userIp}")
                logger.info("AC IP: ${networkStatus.acIp}")

                if (networkStatus.userIp.isNullOrEmpty() or networkStatus.acIp.isNullOrEmpty()) {
                    logger.error("The necessary parameters are empty.")
                    return
                }
                States.userIp = networkStatus.userIp!!
                States.acIp = networkStatus.acIp!!
            }

            REQUEST_ERROR -> {
                logger.error("Request Error: ${networkStatus.message}")
                return
            }
        }

        initSession()
        if (session.getSessionId() == 0.toLong()) {
            logger.error("Failed to initialize session.")
            return
        } else {
            logger.info("Session ID: ${session.getSessionId()}")
        }

        logger.info("Algo ID: ${States.algoId}")
        logger.info("Key: ${session.getKey()}")

        val ticket = getTicket()
        logger.info("Ticket: $ticket")

        login(ticket)

        if (keepUrl.isEmpty()) {
            logger.error("KeepUrl is empty.")
            return
        }

        tick = System.currentTimeMillis()
        isRunning = true

        val loop = object : Thread() {
            override fun run() {
                while (true) {
                    if (!isRunning) {
                        break
                    } else {
                        if ((System.currentTimeMillis() - tick) >= (keepRetry.toLong() * 1000)) {
                            logger.info("Send Keep Packet")
                            heartbeat(ticket)
                            logger.info("Next Retry: $keepRetry")
                            tick = System.currentTimeMillis()
                        }
                    }
                    sleep(200)
                }
            }
        }

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                try {
                    if (isRunning) {
                        isRunning = false
                        term(ticket)
                        session.free()
                    }
                    println("Shutting down...")
                } catch (e: InterruptedException) {
                    currentThread().interrupt()
                    e.printStackTrace()
                }
            }
        })

        loop.start()
    }

    private fun initSession() {
        when (val result = post(States.ticketUrl, States.algoId)) {
            is NetResult.Success -> {
                session = Session(result.data.bytes())
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
                <gwip></gwip>
                <sysinfo>
                    <sysname>Linux</sysname>
                    <ifname>wlan0,${States.userIp}</ifname>
                </sysinfo>
            </request>
        """.trimIndent()
        when (val result = post(States.ticketUrl, session.encrypt(payload))) {
            is NetResult.Success -> {
                val data = session.decrypt(result.data.string())
                return data.substringAfter("<ticket>").substringBefore("</ticket>")
            }

            is NetResult.Error -> {
                error("Error: ${result.exception}")
            }
        }
    }

    private fun login(ticket: String) {
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
        when (val result = post(Constants.AUTH_URL, session.encrypt(payload))) {
            is NetResult.Success -> {
                val data = session.decrypt(result.data.string())
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
                <gwip></gwip>
                <sysinfo>
                    <sysname>Linux</sysname>
                    <ifname>wlan0,${States.userIp}</ifname>
                </sysinfo>
            </request>
        """.trimIndent()
        when (val result = post(keepUrl, session.encrypt(payload))) {
            is NetResult.Success -> {
                val data = session.decrypt(result.data.string())
                keepRetry = data.substringAfter("<interval>").substringBefore("</interval>")
            }

            is NetResult.Error -> {
                error("Error: ${result.exception}")
            }
        }
    }

    private fun term(ticket: String) {
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
                <gwip></gwip>
                <sysinfo>
                    <sysname>Linux</sysname>
                    <ifname>wlan0,${States.userIp}</ifname>
                </sysinfo>
            </request>
        """.trimIndent()
        when (val result = post(termUrl, session.encrypt(payload))) {
            is NetResult.Success -> {}
            is NetResult.Error -> {
                error("Error: ${result.exception}")
            }
        }
    }

}
