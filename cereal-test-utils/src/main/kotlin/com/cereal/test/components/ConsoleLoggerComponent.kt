package com.cereal.test.components

import com.cereal.sdk.component.logger.LoggerComponent
import com.cereal.test.util.Logger
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ConsoleLoggerComponent : LoggerComponent {
    private val logger = Logger("LoggerComponent")

    private fun formatLogMessage(
        level: String,
        message: String?,
        args: Array<out Any?>,
        throwable: Throwable? = null,
    ): String {
        val timestamp =
            Instant
                .now()
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
        val timestampFormatted = "ts=$timestamp"

        val messageFormatted = message?.let { "message=\"$it\"" } ?: ""

        val argsFormatted =
            if (args.isNotEmpty()) {
                args
                    .mapIndexed { index, arg ->
                        "args.arg$index=$arg"
                    }.joinToString(" ")
            } else {
                ""
            }

        val stacktraceFormatted =
            throwable?.let {
                val stackTrace = it.stackTraceToString().replace("\n", "\\n").replace("\t", "\\t")
                "stacktrace=\"$stackTrace\""
            } ?: ""

        return listOf(timestampFormatted, "level=$level", messageFormatted, argsFormatted, stacktraceFormatted)
            .filter { it.isNotEmpty() }
            .joinToString(" ")
    }

    override fun error(
        message: String?,
        throwable: Throwable?,
        vararg args: Any?,
    ) {
        val formattedMessage = formatLogMessage("ERROR", message, args, throwable)
        logger.logMessage(formattedMessage)
    }

    override fun warn(
        message: String?,
        vararg args: Any?,
    ) {
        val formattedMessage = formatLogMessage("WARN", message, args)
        logger.logMessage(formattedMessage)
    }

    override fun info(
        message: String?,
        vararg args: Any?,
    ) {
        val formattedMessage = formatLogMessage("INFO", message, args)
        logger.logMessage(formattedMessage)
    }

    override fun debug(
        message: String?,
        vararg args: Any?,
    ) {
        val formattedMessage = formatLogMessage("DEBUG", message, args)
        logger.logMessage(formattedMessage)
    }
}
