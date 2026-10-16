package com.cereal.sdk.component.llm

import kotlin.time.Duration

/**
 * Thrown by [LlmComponent.complete]. Sealed, so a script can handle every failure kind exhaustively. An uncaught
 * `LlmException` fails the task with its message.
 *
 * @since 1.14.0
 */
sealed class LlmException(
    message: String?,
    cause: Throwable?,
) : Exception(message, cause) {
    /** The user has no AI provider set up. A script can fall back to non-LLM behaviour here at no cost. */
    class NotConfigured(
        message: String? = "No AI provider is set up. Choose one in Settings → AI.",
    ) : LlmException(message, null)

    /** The provider rejected the API key. */
    class Authentication(
        message: String? = null,
        cause: Throwable? = null,
    ) : LlmException(message, cause)

    /**
     * The provider still rate-limits after the client's retries.
     *
     * @property retryAfter How long the provider asked to wait, when it said so.
     */
    class RateLimited(
        val retryAfter: Duration? = null,
        message: String? = null,
        cause: Throwable? = null,
    ) : LlmException(message, cause)

    /**
     * The provider rejected the request, for example an unsupported schema, an oversized image, a too-long context or
     * a model that lacks a needed capability. [message] carries the provider's explanation.
     */
    class InvalidRequest(
        message: String?,
        cause: Throwable? = null,
    ) : LlmException(message, cause)

    /**
     * The model's structured output or tool arguments don't match their schema. Append [assistantTurn] and a
     * correcting user turn to retry. Script code can't construct one (see [CerealPlatformApi]).
     *
     * @property raw The output that failed validation.
     * @property assistantTurn The model's turn, to append to [LlmRequest.turns] as is.
     */
    class InvalidOutput
        @CerealPlatformApi
        constructor(
            val raw: String,
            val assistantTurn: LlmTurn.Assistant,
            message: String? = "The model's output doesn't match the requested schema.",
        ) : LlmException(message, null)

    /** Network errors, timeouts and provider server errors, after the client's retries. */
    class Unavailable(
        message: String? = null,
        cause: Throwable? = null,
    ) : LlmException(message, cause)
}
