package com.cereal.sdk.component.logger

/**
 * The logger can be used to log output to the Cereal application log files.
 */
interface LoggerComponent {
    /**
     *	Designates error events that might still allow the application to continue running.
     */
    fun error(
        message: String? = null,
        throwable: Throwable? = null,
        vararg args: Any?,
    )

    /**
     * Designates potentially harmful situations.
     */
    fun warn(
        message: String? = null,
        vararg args: Any?,
    )

    /**
     * Designates informational messages that highlight the progress of the application at coarse-grained level.
     */
    fun info(
        message: String? = null,
        vararg args: Any?,
    )

    /**
     * Designates fine-grained informational events that are most useful to debug an application.
     */
    fun debug(
        message: String? = null,
        vararg args: Any?,
    )
}
