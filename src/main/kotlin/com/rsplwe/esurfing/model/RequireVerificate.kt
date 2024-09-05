package com.rsplwe.esurfing.model

import com.google.gson.annotations.SerializedName

data class RequireVerificate(
    @SerializedName("schoolid")
    val schoolId: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("timestamp")
    val timestamp: String,

    @SerializedName("authenticator")
    val authenticator: String
)

data class ResponseRequireVerificate(
    @SerializedName("phone")
    val phone: String,

    @SerializedName("resinfo")
    val resInfo: String,

    @SerializedName("rescode")
    val resCode: String,
)
