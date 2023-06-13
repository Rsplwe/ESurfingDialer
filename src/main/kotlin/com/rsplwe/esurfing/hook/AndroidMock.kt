package com.rsplwe.esurfing.hook

import com.github.unidbg.AndroidEmulator
import com.github.unidbg.Emulator
import com.github.unidbg.arm.HookStatus
import com.github.unidbg.arm.backend.Unicorn2Factory
import com.github.unidbg.hook.HookContext
import com.github.unidbg.hook.ReplaceCallback
import com.github.unidbg.hook.hookzz.HookZz
import com.github.unidbg.linux.android.AndroidEmulatorBuilder
import com.github.unidbg.linux.android.AndroidResolver
import com.github.unidbg.linux.android.dvm.DvmClass
import com.github.unidbg.memory.MemoryBlock
import com.rsplwe.esurfing.Constants
import org.apache.log4j.Logger
import unicorn.ArmConst
import java.nio.charset.StandardCharsets

class AndroidMock {

    private val logger: Logger = Logger.getLogger(AndroidMock::class.java)
    private val cache: HashMap<String, MemoryBlock> = HashMap()
    private val jniMethod: DvmClass
    private val emulator: AndroidEmulator = AndroidEmulatorBuilder
        .for32Bit()
        .addBackendFactory(Unicorn2Factory(false))
        .setProcessName(Constants.PACKAGE_ID)
        .build()

    init {
        logger.info("Initializing Android Mock...")
        emulator.memory.setLibraryResolver(AndroidResolver(23))

        val vm = emulator.createDalvikVM()
        vm.setJni(ESurfingJni())
        vm.setVerbose(false)

        val libRaw = this::class.java.classLoader.getResource("libdaproxy.so")!!.readBytes()
        val library = vm.loadLibrary("daproxy", libRaw, true)

        val hook = HookZz.getInstance(emulator)
        hook.replace(library.module.findSymbolByName("strcmp").address, object : ReplaceCallback() {
            override fun onCall(emulator: Emulator<*>?, context: HookContext?, originFunction: Long): HookStatus {
                val arg2 = context!!.getPointerArg(1).getString(0)
                val key = "ipv4"

                if (arg2.indexOf(key) == 0){
                    if (!cache.containsKey(key)) {
                        val fakeInputBlock = emulator!!.memory.malloc(key.length, true)
                        fakeInputBlock.pointer.write(key.toByteArray(StandardCharsets.UTF_8))
                        cache[key] = fakeInputBlock
                    }
                    emulator!!.backend.reg_write(ArmConst.UC_ARM_REG_R0, cache[key]!!.pointer.peer)
                }
                return HookStatus.RET(emulator, originFunction)
            }
        })
        library.callJNI_OnLoad(emulator)

        jniMethod = vm.resolveClass("com/cndatacom/campus/netcore/DaMod")
    }


    companion object {
        private var instance: AndroidMock? = null

        fun getInstance(): AndroidMock {
            if (instance == null) {
                instance = AndroidMock()
            }
            return instance!!
        }
    }

    fun getJniMethod(): DvmClass {
        return this.jniMethod
    }

    fun getEmulator(): AndroidEmulator {
        return this.emulator
    }
}