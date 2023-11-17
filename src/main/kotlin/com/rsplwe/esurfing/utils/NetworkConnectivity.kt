package com.rsplwe.esurfing.utils

import cn.yescallop.fluenturi.Uri
import com.rsplwe.esurfing.Constants
import com.rsplwe.esurfing.network.sendCurlRequest

enum class ConnectivityStatus {
    SUCCESS,
    IS_REDIRECTS_NOT_FOUND_IP,
    IS_REDIRECTS_FOUND_IP,
    REQUEST_ERROR,
    DEFAULT
}

data class NetworkConnectivityResult(
    val status: ConnectivityStatus,
    val userIp: String? = "",
    val acIp: String? = "",
    val message: String = "ok"
)

fun checkConnectivity(): NetworkConnectivityResult {
    val params = ArrayList<String>()
    params.add("-H \"User-Agent: ${Constants.USER_AGENT}\"")
    params.add("-H \"Accept: ${Constants.REQUEST_ACCEPT}\"")
    params.add("url=\"${Constants.CAPTIVE_URL}\"")

    try {
        val response = sendCurlRequest(params, true).toString(Charsets.UTF_8)
        val responseCode = Regex("^HTTP/\\d\\.\\d (\\d+)", RegexOption.IGNORE_CASE).find(response)?.groupValues?.get(1)?.toInt() ?: 0
        val location = Regex("Location: (.+)", RegexOption.IGNORE_CASE).find(response)?.groupValues?.get(1)

        when (responseCode) {
            302 -> {
                return if (location != null && location.contains("ip=")) {
                    val uri = Uri.from(location)
                    val userIp = uri.queryParameters()["wlanuserip"]?.first()
                    val acIp = uri.queryParameters()["wlanacip"]?.first()

                    if (userIp.isNullOrEmpty() or acIp.isNullOrEmpty()) {
                        NetworkConnectivityResult(status = ConnectivityStatus.IS_REDIRECTS_NOT_FOUND_IP)
                    }else{
                        NetworkConnectivityResult(
                            status = ConnectivityStatus.IS_REDIRECTS_FOUND_IP,
                            userIp = userIp,
                            acIp = acIp
                        )
                    }
                } else {
                    NetworkConnectivityResult(status = ConnectivityStatus.IS_REDIRECTS_NOT_FOUND_IP)
                }
            }

            200, 204, 404 -> {
                return NetworkConnectivityResult(status = ConnectivityStatus.SUCCESS)
            }

            else -> {
                throw RuntimeException("Request Code: $responseCode")
            }
        }
    } catch (e: Throwable) {
        return NetworkConnectivityResult(ConnectivityStatus.REQUEST_ERROR, message = e.localizedMessage)
    }
}