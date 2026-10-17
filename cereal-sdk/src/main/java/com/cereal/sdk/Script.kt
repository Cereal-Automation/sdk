package com.cereal.sdk

import com.cereal.sdk.component.ComponentProvider

/**
 * Core contract implemented by all scripts. A script follows a simple lifecycle:
 * 1. [onStart] is invoked exactly once to allow initialization. Return false to abort before execution begins.
 * 2. [execute] is invoked repeatedly while the script is running until it returns [ExecutionResult.Success] or
 *    [ExecutionResult.Error]. Returning [ExecutionResult.Loop] schedules another invocation after the provided delay.
 *    If the script is externally paused no calls to [execute] occur until resumed.
 * 3. [onFinish] is invoked once after a terminal result (success or error) to release resources.
 *
 * Implementations should be cancellation cooperative (respect coroutine cancellation) and avoid long blocking calls.
 */
interface Script<T : ScriptConfiguration> {
    /**
     * Called before the loop function when starting a script.
     *
     * @param configuration Script configuration object with properties set by the user.
     * @param provider Unique component provider per script providing several utilities like a logger and a notification component.
     *
     * @return true if script was able to start or else false.
     */
    suspend fun onStart(
        configuration: T,
        provider: ComponentProvider,
    ): Boolean

    /**
     * The main loop. Called if you return true from onStart, then runs continuously until either [ExecutionResult.Success]
     * or [ExecutionResult.Error] is returned or the script is stopped externally. When this script is paused this method
     * will not be called until the script is resumed.
     *
     * @param configuration Script configuration object with properties set by the user.
     * @param provider Unique component provider per script providing several utilities like a logger and a notification component.
     * @param statusUpdate A handle used to provide feedback to the user during script execution.
     *
     * @return The loop result which can be:
     * - [ExecutionResult.Loop] to reschedule the task with a delay in milliseconds and a message which is shown to the user. The minimum delay is 50ms.
     * - [ExecutionResult.Success] to end the task in a successful state with a message which is shown to the user.
     * - [ExecutionResult.Error] to end the task with an error with a message which is shown to the user.
     */
    suspend fun execute(
        configuration: T,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult

    /**
     * Called after the loop function returned either [ExecutionResult.Error] or [ExecutionResult.Success]. Useful for cleaning up resources.
     *
     * @param configuration Script configuration object with properties set by the user.
     * @param provider Unique component provider per script providing several utilities like a logger and a notification component.
     */
    suspend fun onFinish(
        configuration: T,
        provider: ComponentProvider,
    )
}
