package com.rsplwe.esurfing.utils

import cn.yescallop.fluenturi.Uri
import com.rsplwe.esurfing.Constants
import com.rsplwe.esurfing.network.createHttpClient
import okhttp3.Request

enum class ConnectivityStatus {
    SUCCESS,
    IS_REDIRECTS_NOT_FOUND_IP,
    IS_REDIRECTS_FOUND_IP,
    REQUEST_ERROR,
}

data class NetworkConnectivityResult(
    val status: ConnectivityStatus,
    val userIp: String? = "",
    val acIp: String? = "",
    val message: String = "ok"
)

fun checkConnectivity(): NetworkConnectivityResult {
    val client = createHttpClient(false)
    val request = Request.Builder()
        .removeHeader("User-Agent")
        .addHeader("User-Agent", Constants.USER_AGENT)
        .addHeader("Accept", Constants.REQUEST_ACCEPT)
        .url(Constants.CAPTIVE_URL)
        .build()

    try {
        val response = client.newCall(request).execute()
        val location = response.headers["Location"]
        val responseCode = response.code

        response.close()

        when (responseCode) {
            302 -> {
                return if (location != null && location.contains("ip=")) {
                    val uri = Uri.from(location)
                    val userIp = uri.queryParameters()["wlanuserip"]?.first()
                    val acIp = uri.queryParameters()["wlanacip"]?.first()

                    NetworkConnectivityResult(
                        status = ConnectivityStatus.IS_REDIRECTS_FOUND_IP,
                        userIp = userIp,
                        acIp = acIp
                    )
                } else {
                    NetworkConnectivityResult(status = ConnectivityStatus.IS_REDIRECTS_NOT_FOUND_IP)
                }
            }

            200, 204 -> {
                return NetworkConnectivityResult(status = ConnectivityStatus.SUCCESS)
            }

            else -> {
                throw RuntimeException("Request Code: ${response.code}")
            }
        }
    } catch (e: Throwable) {
        return NetworkConnectivityResult(ConnectivityStatus.REQUEST_ERROR, message = e.localizedMessage)
    }
}