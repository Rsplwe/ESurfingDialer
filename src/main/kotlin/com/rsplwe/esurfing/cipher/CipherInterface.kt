package com.rsplwe.esurfing.cipher

interface CipherInterface {
    fun encrypt(text: String): String
    fun decrypt(hex: String): String
}