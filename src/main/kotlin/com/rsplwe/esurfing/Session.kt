package com.rsplwe.esurfing

import com.rsplwe.esurfing.cipher.CipherFactory
import com.rsplwe.esurfing.cipher.CipherInterface
import org.apache.log4j.Logger
import java.lang.IllegalArgumentException
import java.util.UUID

object Session {

    private val logger: Logger = Logger.getLogger(Session::class.java)
    private lateinit var clientId: String
    private var initialized = false
    private lateinit var cipher: CipherInterface

    fun initialize(zsm: ByteArray) {
        logger.info("Initializing Session...")
        clientId = UUID.randomUUID().toString().lowercase()
        initialized = load(zsm)
    }

    fun isInitialized(): Boolean {
        return initialized
    }

    private fun load(zsm: ByteArray): Boolean {
        if (zsm.size < 4) {
            return false
        }
        // val header = zsm.sliceArray(0 until 3)
        val keyLen = zsm[3]
        var pos = 4
        if (pos + keyLen > zsm.size) {
            return false
        }
        // val key = zsm.sliceArray(pos until pos + keyLen)
        pos += keyLen

        if (pos >= zsm.size) {
            return false
        }
        val algoIdLen = zsm[pos]
        pos += 1
        if (pos + algoIdLen > zsm.size) {
            return false
        }
        val algoId = zsm.sliceArray(pos until pos + algoIdLen).decodeToString()
        pos += algoIdLen

        try {
            cipher = CipherFactory.getInstance(algoId)
        } catch (e: IllegalArgumentException) {
            logger.error(e.message)
            return false
        }
        States.algoId = algoId
        return true
    }

    fun decrypt(hex: String): String {
        return cipher.decrypt(hex)
    }

    fun getAlgoId(): String {
        return States.algoId
    }

    fun encrypt(text: String): String {
        return cipher.encrypt(text)
    }

    fun free() {
        initialized = false
    }
}