package com.rsplwe.esurfing.hook

import com.github.unidbg.AndroidEmulator
import com.github.unidbg.arm.backend.BackendFactory
import com.github.unidbg.arm.backend.DynarmicFactory
import com.github.unidbg.linux.android.AndroidEmulatorBuilder
import com.github.unidbg.linux.android.AndroidResolver
import com.github.unidbg.linux.android.dvm.DvmClass
import com.rsplwe.esurfing.Constants
import com.rsplwe.esurfing.States
import org.apache.log4j.Logger

class AndroidMock {

    private val logger: Logger = Logger.getLogger(AndroidMock::class.java)
    private val jniMethod: DvmClass
    private val emulator: AndroidEmulator = AndroidEmulatorBuilder
        .for64Bit()
        .setRootDir(States.rootDir)
        .addBackendFactory(getBackend())
        .setProcessName(Constants.PACKAGE_ID)
        .build()

    init {
        logger.info("Initializing Android Mock...")
        emulator.memory.setLibraryResolver(AndroidResolver(23))

        val vm = emulator.createDalvikVM()
        vm.setJni(ESurfingJni())
        vm.setVerbose(Constants.DEBUG)

        val libRaw = this::class.java.classLoader.getResource("libdaproxy.so")!!.readBytes()
        val library = vm.loadLibrary("daproxy", libRaw, true)

        library.callJNI_OnLoad(emulator)

        jniMethod = vm.resolveClass("com/cndatacom/campus/netcore/DaMod")
    }

    private fun getBackend(): BackendFactory {
        logger.info("Use Dynarmic Backend")
        return DynarmicFactory(false)
    }

    fun getJniMethod(): DvmClass {
        return this.jniMethod
    }

    fun getEmulator(): AndroidEmulator {
        return this.emulator
    }
}