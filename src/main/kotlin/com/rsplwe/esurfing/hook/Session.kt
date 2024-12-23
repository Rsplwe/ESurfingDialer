package com.rsplwe.esurfing.hook

import com.github.unidbg.AndroidEmulator
import com.github.unidbg.linux.android.dvm.DvmClass
import com.github.unidbg.linux.android.dvm.DvmObject
import com.rsplwe.esurfing.States
import org.apache.log4j.Logger
import java.util.*

object Session {

    private val logger: Logger = Logger.getLogger(Session::class.java)
    private lateinit var mock: AndroidMock
    private lateinit var emulator: AndroidEmulator
    private lateinit var method: DvmClass
    private var sessionId: Long = 0
    private lateinit var clientId: String

    private var initialized = false

    fun initialize(zsm: ByteArray) {
        logger.info("Initializing Session...")
        mock = AndroidMock()
        emulator = mock.getEmulator()
        method = mock.getJniMethod()
        sessionId = load(zsm)
        clientId = UUID.randomUUID().toString().lowercase()
        States.algoId = getAlgoId()
        initialized = true
    }

    fun isInitialized(): Boolean {
        return initialized
    }

    private fun load(zsm: ByteArray): Long {
        return method.callStaticJniMethodLong(emulator, "load([B)J", zsm)
    }

    fun decrypt(hex: String): String {
        val r: DvmObject<*> =
            method.callStaticJniMethodObject(emulator, "dec(J[B)[B", sessionId, hex.toByteArray(Charsets.UTF_8))
        return String(r.value as ByteArray)
    }

    fun getAlgoId(): String {
        val r: DvmObject<*> = method.callStaticJniMethodObject(emulator, "aid(J)Ljava/lang/String;", sessionId)
        return r.value as String
    }

    fun getSessionId(): Long {
        return this.sessionId
    }

    fun getKey(): String {
        val r: DvmObject<*> = method.callStaticJniMethodObject(emulator, "key(J)Ljava/lang/String;", sessionId)
        return r.value as String
    }

    fun encrypt(hex: String): String {
        val r: DvmObject<*> =
            method.callStaticJniMethodObject(emulator, "enc(J[B)[B", sessionId, hex.toByteArray(Charsets.UTF_8))
        return String(r.value as ByteArray)
    }

    fun free() {
        method.callStaticJniMethodObject<DvmObject<*>>(emulator, "free(J)V", sessionId)
        emulator.close()
        initialized = false
    }
}