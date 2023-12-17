package com.rsplwe.esurfing.hook

import com.github.unidbg.linux.android.dvm.*
import com.rsplwe.esurfing.Constants
import com.rsplwe.esurfing.States
import java.io.File

class ESurfingJni : AbstractJni() {

    override fun callObjectMethodV(
        vm: BaseVM?,
        dvmObject: DvmObject<*>?,
        signature: String?,
        vaList: VaList?
    ): DvmObject<*> {
        when (signature) {
            "android/content/Context->getFilesDir()Ljava/io/File;" -> {
                return vm!!.resolveClass("java/io/File").newObject(States.rootDir)
            }

            "java/io/File->getAbsolutePath()Ljava/lang/String;" -> {
                val file = dvmObject!!.value as File
                return StringObject(vm, file.absolutePath)
            }
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList)
    }

    override fun callStaticObjectMethodV(
        vm: BaseVM?,
        dvmClass: DvmClass?,
        signature: String?,
        vaList: VaList?
    ): DvmObject<*> {
        when (signature) {
            "android/app/ActivityThread->currentPackageName()Ljava/lang/String;" -> {
                return StringObject(vm, Constants.PACKAGE_ID)
            }

            "android/app/ActivityThread->currentApplication()Landroid/app/Application;" -> {
                return vm!!.resolveClass(
                    "android/app/Application",
                    vm.resolveClass("android/content/ContextWrapper", vm.resolveClass("android/content/Context"))
                ).newObject(signature)
            }
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList)
    }

}