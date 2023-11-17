package com.rsplwe.esurfing.network

import com.rsplwe.esurfing.Constants
import com.rsplwe.esurfing.States
import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.util.UUID
import java.util.concurrent.TimeUnit

fun sendCurlRequest(params: ArrayList<String>, onlyHeaders: Boolean = false): ByteArray {
    val cmd = ArrayList<String>()
    val curl = ArrayList<String>()
    val id = UUID.randomUUID().toString().lowercase()
    val conf = File("/tmp/esurfing-$id.conf")
    val data = File("/tmp/esurfing-$id.dat")
    var result: ByteArray

    if (onlyHeaders) {
        curl.add("-I")
    }
    curl.add("-o /tmp/esurfing-$id.dat")
    curl.addAll(params)
    curl.forEach {
        conf.appendText("$it\n")
    }
    if (States.networkInterface != "") {
        cmd.add("/usr/sbin/mwan3")
        cmd.add("use")
        cmd.add(States.networkInterface)
        cmd.add("/usr/bin/curl -K /tmp/esurfing-$id.conf")
    }
    else {
        cmd.add("/usr/bin/curl")
        cmd.add("-K")
        cmd.add("/tmp/esurfing-$id.conf")
    }

    val builder = ProcessBuilder(cmd)
    val exec = builder.start()
    exec.waitFor(30, TimeUnit.SECONDS)
    result = data.readBytes()
    conf.delete()
    data.delete()
    if (onlyHeaders) {
        result = result.toString(Charsets.UTF_8).replace("\r\n", "\n").toByteArray()
    }
    return result
}

fun post(url: String, data: String): NetResult<ByteArray> {
    val params = ArrayList<String>()
    params.add("-d \"$data\"")
    params.add("-H \"User-Agent: ${Constants.USER_AGENT}\"")
    params.add("-H \"Accept: ${Constants.REQUEST_ACCEPT}\"")
    params.add("-H \"CDC-Checksum: ${DigestUtils.md5Hex(data)}\"")
    params.add("-H \"Client-ID: ${States.clientId}\"")
    params.add("-H \"Algo-ID: ${States.algoId}\"")
    params.add("url=\"$url\"")

    return try {
        val result = sendCurlRequest(params)
        return NetResult.Success(result)
    } catch (e: Throwable) {
        NetResult.Error(e.stackTraceToString())
    }
}