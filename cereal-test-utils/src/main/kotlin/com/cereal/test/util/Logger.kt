package com.cereal.test.util

class Logger(
    private val name: String,
) {
    fun logMessage(message: String) {
        println("[$name] $message")
    }
}
