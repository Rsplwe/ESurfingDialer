package com.rsplwe.esurfing

import com.rsplwe.esurfing.States.isRunning
import com.rsplwe.esurfing.utils.ConnectivityStatus
import com.rsplwe.esurfing.utils.checkConnectivity
import org.apache.commons.cli.*
import org.apache.commons.cli.Options
import org.apache.log4j.Logger
import kotlin.system.exitProcess

object DialerApp {

    private val logger: Logger = Logger.getLogger(DialerApp::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        // root directory
        if (!States.rootDir.exists()) States.rootDir.mkdirs()
        if (States.rootDir.isFile) throw IllegalArgumentException("rootDir must be directory: " + States.rootDir)

        val options = Options()
        val loginUser = Option.builder("u").longOpt("user")
            .argName("user")
            .hasArg()
            .required(true)
            .desc("Login User (Phone Number or Other)").build()
        val loginPassword = Option.builder("p").longOpt("password")
            .argName("password")
            .hasArg()
            .required(true)
            .desc("Login User Password").build()
        val useDynarmicBackend = Option.builder("d").longOpt("dynarmic")
            .argName("dynarmic")
            .hasArg(false)
            .required(false)
            .desc("Use Dynarmic Backend").build()
        val networkInterface = Option.builder("i").longOpt("interface")
            .argName("interface")
            .hasArg()
            .required(false)
            .desc("Special an interface").build()

        options.addOption(loginUser)
        options.addOption(loginPassword)
        options.addOption(useDynarmicBackend)
        options.addOption(networkInterface)

        val cmd: CommandLine
        val parser: CommandLineParser = DefaultParser()
        val helper = HelpFormatter()

        try {
            cmd = parser.parse(options, args)
        } catch (e: ParseException) {
            logger.error(e.message)
            helper.printHelp("ESurfingDialer", options)
            exitProcess(1)
        }

        States.useDynarmic = cmd.hasOption("dynarmic")
        if (cmd.hasOption("interface")) {
            logger.info("You have specialized an interface named ${cmd.getOptionValue("interface")}.")
            States.networkInterface = cmd.getOptionValue("interface")
        }

        val networkCheck = object : Thread() {
            override fun run() {
                while (isRunning) {
                    val networkStatus = checkConnectivity()
                    States.networkStatus = networkStatus.status

                    when (networkStatus.status) {
                        ConnectivityStatus.SUCCESS -> {
                            // logger.info("The network has been connected.")
                        }

                        ConnectivityStatus.IS_REDIRECTS_NOT_FOUND_IP -> {
                            logger.error("No parameter detected in url.")
                        }

                        ConnectivityStatus.IS_REDIRECTS_FOUND_IP -> {
                            States.userIp = networkStatus.userIp!!
                            States.acIp = networkStatus.acIp!!
                        }

                        ConnectivityStatus.REQUEST_ERROR -> {
                            logger.error("Request Error: ${networkStatus.message}")
                        }

                        ConnectivityStatus.DEFAULT -> {}
                    }
                    sleep(1000)
                }
            }
        }

        val client = Client(Options(cmd.getOptionValue("user"), cmd.getOptionValue("password")))

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                try {
                    if (isRunning) {
                        isRunning = false
                    }
                    if (client.session != null) {
                        client.term()
                    }
                    println("Shutting down...")
                } catch (e: InterruptedException) {
                    currentThread().interrupt()
                    e.printStackTrace()
                }
            }
        })

        Thread(client).start()
        networkCheck.start()
    }

}