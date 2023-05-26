package com.rsplwe.esurfing.network

sealed class NetResult<out T : Any> {

    data class Success<out T : Any>(val data: T) : NetResult<T>()
    data class Error(val exception: String) : NetResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}