package com.rsplwe.esurfing

import com.rsplwe.esurfing.States.isRunning
import com.rsplwe.esurfing.States.ticket
import com.rsplwe.esurfing.hook.Session
import com.rsplwe.esurfing.network.NetResult
import com.rsplwe.esurfing.network.post
import com.rsplwe.esurfing.utils.ConnectivityStatus.*
import com.rsplwe.esurfing.utils.checkVerifyCodeStatus
import com.rsplwe.esurfing.utils.detectConfig
import com.rsplwe.esurfing.utils.getTime
import com.rsplwe.esurfing.utils.getVerifyCode
import org.apache.log4j.Logger
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.lang.Thread.sleep

class Client(private val options: Options) {

    private val logger: Logger = Logger.getLogger(Client::class.java)
    private var keepUrl = ""
    private var termUrl = ""
    private var keepRetry = ""

    var session: Session? = null

    @Volatile
    var tick: Long = 0

    fun run() {
        while (isRunning) {
            val networkStatus = detectConfig()
            when (networkStatus) {
                SUCCESS -> {
                    if (session != null) {
                        if ((System.currentTimeMillis() - tick) >= (keepRetry.toLong() * 1000)) {
                            logger.info("Send Keep Packet")
                            heartbeat(ticket)
                            logger.info("Next Retry: $keepRetry")
                            tick = System.currentTimeMillis()
                        }
                    } else {
                        logger.info("The network has been connected.")
                        sleep(5000)
                    }
                }

                REQUIRE_AUTHORIZATION -> authorization()
                else -> {
                    sleep(5000)
                }
            }
            sleep(1000)
        }
    }

    private fun authorization() {
        var code = checkSMSVerify()
        session?.free()
        initSession()
        if ((session?.getSessionId() ?: 0) == 0.toLong()) {
            logger.error("Failed to initialize session.")
            isRunning = false
        }

        logger.info("Session ID: ${session?.getSessionId()}")
        logger.info("Client IP: ${States.userIp}")
        logger.info("AC IP: ${States.acIp}")

        ticket = getTicket()
        logger.info("Ticket: $ticket")

        login(code)
        if (keepUrl.isEmpty()) {
            logger.error("KeepUrl is empty.")
            session?.free()
            isRunning = false
        }
        tick = System.currentTimeMillis()
        logger.info("The login has been authorized.")
    }

    private fun checkSMSVerify(): String {
        if (checkVerifyCodeStatus(options.loginUser) && getVerifyCode(options.loginUser)) {
            logger.info("This login requires a SMS verification code.")
            while (true) {
                print("Input Code: ")
                val input = readLine()
                if (input != null) {
                    val code = input.trim()
                    if (code.isNotBlank()) {
                        println("Code is: $code")
                        return code
                    }
                }
            }
        }
        return ""
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
                <host-name>${Constants.HOST_NAME}</host-name>
                <ipv4>${States.userIp}</ipv4>
                <ipv6></ipv6>
                <mac>${States.macAddress}</mac>
                <ostag>${Constants.HOST_NAME}</ostag>
                <gwip>${States.acIp}</gwip>
            </request>
        """.trimIndent()
        when (val result = post(States.ticketUrl, session!!.encrypt(payload))) {
            is NetResult.Success -> {
                val data = session!!.decrypt(result.data.string())
                val doc = Jsoup.parse(data, Parser.xmlParser())
                return doc.getElementsByTag("ticket").first()?.text() ?: ""
            }

            is NetResult.Error -> {
                error("Error: ${result.exception}")
            }
        }
    }

    private fun login(code: String = "") {
        val verify = if (code.isBlank()) "" else "<verify>${code}</verify>"
        val payload = """
            <?xml version="1.0" encoding="utf-8"?>
            <request>
                <user-agent>${Constants.USER_AGENT}</user-agent>
                <client-id>${States.clientId}</client-id>
                <ticket>${ticket}</ticket>
                <local-time>${getTime()}</local-time>
                <userid>${options.loginUser}</userid>
                <passwd>${options.loginPassword}</passwd>
                $verify
            </request>
        """.trimIndent()
        when (val result = post(States.authUrl, session!!.encrypt(payload))) {
            is NetResult.Success -> {
                val data = session!!.decrypt(result.data.string())
                val doc = Jsoup.parse(data, Parser.xmlParser())

                keepUrl = doc.getElementsByTag("keep-url").first()?.text() ?: ""
                termUrl = doc.getElementsByTag("term-url").first()?.text() ?: ""
                keepRetry = doc.getElementsByTag("keep-retry").first()?.text() ?: ""

                // 检查keepRetry值是否正确，不正确则设置为默认值60
                if (keepRetry.isBlank() || keepRetry.toInt() <= 0) {
                    keepRetry = "60"
                    logger.warn("KeepRetry value is incorrect, setting to default: $keepRetry")
                    logger.warn("If you are using proxy software such as Clash, please close it and try again.")
                }

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
                <host-name>${Constants.HOST_NAME}</host-name>
                <ipv4>${States.userIp}</ipv4>
                <ticket>${ticket}</ticket>
                <ipv6></ipv6>
                <mac>${States.macAddress}</mac>
                <ostag>${Constants.HOST_NAME}</ostag>
            </request>
        """.trimIndent()
        when (val result = post(keepUrl, session!!.encrypt(payload))) {
            is NetResult.Success -> {
                val data = session!!.decrypt(result.data.string())
                val doc = Jsoup.parse(data, Parser.xmlParser())
                keepRetry = doc.getElementsByTag("interval").first()?.text() ?: ""

                // 再次检查keepRetry值是否正确，不正确则设置为默认值60
                if (keepRetry.isBlank() || keepRetry.toInt() <= 0) {
                    keepRetry = "60"
                    logger.warn("KeepRetry value is incorrect after heartbeat, setting to default: $keepRetry")
                    logger.warn("If you are using proxy software such as Clash, please close it and try again.")
                }
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
                <host-name>${Constants.HOST_NAME}</host-name>
                <ipv4>${States.userIp}</ipv4>
                <ticket>${ticket}</ticket>
                <ipv6></ipv6>
                <mac>${States.macAddress}</mac>
                <ostag>${Constants.HOST_NAME}</ostag>
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
