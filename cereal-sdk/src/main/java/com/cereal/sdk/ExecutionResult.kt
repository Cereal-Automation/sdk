package com.cereal.sdk

/**
 * Represents the outcome of a single invocation of [Script.execute].
 *
 * Implementations describe whether the script loop should continue (and when) or terminate with success or error.
 * The host uses the concrete subtype to drive scheduling and UI state.
 *
 * Guidelines:
 * - Prefer short, user‑friendly [message] values (they are surfaced in the UI / history logs).
 * - Keep delays small but reasonable; the host enforces a minimum delay of 50 ms for [Loop].
 */
sealed class ExecutionResult(
    /** Message surfaced to the user describing the outcome or next action. */
    val message: String,
) {
    /**
     * Indicates the script should be scheduled again after [delay] milliseconds.
     *
     * @param delay The requested delay in milliseconds before the next call to [Script.execute]. The platform may
     *              clamp very small values (minimum 50 ms). Use modest delays to avoid unnecessarily busy looping.
     */
    class Loop(
        message: String,
        val delay: Long,
    ) : ExecutionResult(message = message)

    /**
     * Indicates the script has finished successfully. The host will call [Script.onFinish] next.
     */
    class Success(
        message: String,
    ) : ExecutionResult(message = message)

    /**
     * Indicates the script terminated with an unrecoverable issue. The host will call [Script.onFinish] next
     * and may surface an error state to the user.
     */
    class Error(
        message: String,
    ) : ExecutionResult(message = message)
}
