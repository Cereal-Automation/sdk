package com.cereal.sdk.component.script

import com.cereal.sdk.Script

/**
 * Component used by a running parent script to start child scripts that are annotated with
 * [com.cereal.sdk.ChildScript]. Implementations manage lifecycle, scheduling and isolation of the child.
 *
 * The API is intentionally minimal: callers can only start a script and poll for coarse-grained progress via the
 * returned [ScriptHandle]. Cancellation / stopping (if supported) would be exposed through the handle in the future.
 */
interface ScriptLauncherComponent {
    /**
     * Starts a child script identified by its class type. This will automatically start the script asynchronous.
     *
     * @param scriptCls the [Class] of the script you want to start.
     * @param parameters parameters that are provided to the child scripts' constructor.
     *
     * @throws StartScriptException when script couldn't be started.
     */
    suspend fun start(
        scriptCls: Class<out Script<*>>,
        parameters: ScriptParameters? = null,
    ): ScriptHandle
}

/** Thrown when a child script fails to start due to configuration, permission or internal platform errors. */
class StartScriptException(
    override val message: String?,
) : Exception(message)

/**
 * Represents a lightweight handle to a running child script. Currently only exposes aggregated task counts; future
 * iterations may add cancellation or richer progress metrics.
 */
interface ScriptHandle {
    /** Returns the current status snapshot; values may be eventually consistent. */
    suspend fun getStatus(): Status

    data class Status(
        /**
         * Total number of unique tasks that are executed for this script.
         */
        val totalTasks: Int,
        /**
         * Number of tasks that are currently executing.
         */
        val runningTasks: Int,
        /**
         * Number of tasks that succeeded.
         */
        val succeededTasks: Int,
        /**
         * Number of tasks that errorred.
         */
        val erroredTasks: Int,
    )
}
