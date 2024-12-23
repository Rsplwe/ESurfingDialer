package com.rsplwe.esurfing.utils

import cn.yescallop.fluenturi.Uri
import com.google.gson.Gson
import com.rsplwe.esurfing.Constants
import com.rsplwe.esurfing.States
import com.rsplwe.esurfing.model.RequireVerificate
import com.rsplwe.esurfing.model.ResponseRequireVerificate
import com.rsplwe.esurfing.network.apiClient
import com.rsplwe.esurfing.network.createHttpClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.commons.codec.digest.DigestUtils
import org.apache.log4j.Logger
import org.jsoup.Jsoup
import org.jsoup.parser.Parser

enum class ConnectivityStatus {
    SUCCESS,
    REQUIRE_AUTHORIZATION,
    REQUEST_ERROR,
}

val captiveClient = createHttpClient()
val logger: Logger = Logger.getLogger("ConnectivityStatus")

fun detectConfig(): ConnectivityStatus {
    val request = Request.Builder()
        .removeHeader("User-Agent")
        .addHeader("User-Agent", Constants.USER_AGENT)
        .addHeader("Accept", Constants.REQUEST_ACCEPT)
        .addHeader("Client-ID", States.clientId)
        .url(Constants.CAPTIVE_URL)
        .build()
    try {
        val response = captiveClient.newCall(request).execute()

        if (!response.isSuccessful) {
            throw RuntimeException("Request Code: ${response.code}")
        }
        val content = response.body!!.string()
        response.close()

        val portalConfig = content.extractBetweenTags(Constants.PORTAL_START_TAG, Constants.PORTAL_END_TAG)
        if (portalConfig.isNotEmpty()) {

            val doc = Jsoup.parse(portalConfig, Parser.xmlParser())
            States.authUrl = doc.getElementsByTag("auth-url").first()?.text() ?: ""
            States.ticketUrl = doc.getElementsByTag("ticket-url").first()?.text() ?: ""

            doc.getElementsByTag("funcfg").first()?.children()?.forEach {
                if ((it.attribute("enable")?.value ?: "") == "1"
                    && (it.attribute("url")?.value ?: "").isNotEmpty()
                ) {
                    States.extraCfgUrl[it.tagName()] = it.attribute("url")!!.value
                }
            }
            if (States.authUrl.isEmpty() || States.ticketUrl.isEmpty()) {
                throw RuntimeException("Missing auth-url or ticket-url")
            }
            val uri = Uri.from(States.ticketUrl)
            val userIp = uri.queryParameters()["wlanuserip"]?.first()
            val acIp = uri.queryParameters()["wlanacip"]?.first()
            if (userIp.isNullOrEmpty() or acIp.isNullOrEmpty()) {
                throw RuntimeException("Missing userIp or acIp")
            } else {
                States.userIp = userIp!!
                States.acIp = acIp!!
            }
            return ConnectivityStatus.REQUIRE_AUTHORIZATION
        } else {
            return ConnectivityStatus.SUCCESS
        }
    } catch (e: Throwable) {
        logger.error(e.stackTraceToString())
        return ConnectivityStatus.REQUEST_ERROR
    }
}

fun checkVerifyCodeStatus(username: String): Boolean {
    return requestVerifyCode(username, "QueryVerificateCodeStatus", "11062000")
}

fun getVerifyCode(username: String): Boolean {
    return requestVerifyCode(username, "QueryAuthCode", "0")
}

fun requestVerifyCode(username: String, type: String, success: String): Boolean {
    val url = States.extraCfgUrl[type]
    if (url.isNullOrEmpty()) return false
    val currentTimeMillis = System.currentTimeMillis().toString()
    val body = Gson().toJson(
        RequireVerificate(
            schoolId = States.schoolId,
            username = username,
            timestamp = currentTimeMillis,
            authenticator = DigestUtils.md5Hex(States.schoolId + currentTimeMillis + Constants.AUTH_KEY).uppercase()
        )
    ).toRequestBody("application/json".toMediaTypeOrNull())
    val request = Request.Builder()
        .removeHeader("User-Agent")
        .addHeader("User-Agent", Constants.USER_AGENT)
        .addHeader("Accept", "okhttp/3.4.1")
        .url(url)
        .post(body)

    try {
        val response = apiClient.newCall(request.build()).execute()
        if (!response.isSuccessful) return false
        val model = Gson().fromJson(response.body?.string(), ResponseRequireVerificate::class.java)
        if (model.resCode == success) return true
    } catch (e: Throwable) {
        logger.error(e.stackTraceToString())
        return false
    }
    return false
}
