package com.cereal.sdk.component.llm

/**
 * Component through which a script calls the large language model the user configured in Settings → AI.
 *
 * The client makes the HTTP call itself, so the user's API key never reaches script code, and the script needs no
 * HTTP client or JSON library of its own. The user picks the provider and model; a request can't override them.
 *
 * Scripts that use this component must set `"sdk_version": "1.14.0"` (or higher) in `manifest.json`, so older
 * clients refuse the script with "update your client" instead of failing at runtime.
 * They must also declare the `llm` Script feature with `"features": ["llm"]` in `manifest.json`; without it,
 * `provider.llm()` throws and the task fails with an error telling the author to add it. `features` doesn't
 * replace `sdk_version`, because older clients ignore `features`.
 *
 * @since 1.14.0
 */
interface LlmComponent {
    /**
     * Sends [request] to the configured model and returns its answer.
     *
     * Stateless: every call makes exactly one model call. To continue a conversation, append
     * [LlmResponse.assistantTurn] and your next [LlmTurn.User] to the turns and call again; a tool loop is driven
     * the same way by the script.
     *
     * The client retries rate limits (HTTP 429) and server errors a few times with backoff before throwing. It never
     * retries [LlmException.InvalidOutput], so a retry never spends tokens without the script's say.
     * Cancelling the calling coroutine cancels the call.
     *
     * @throws LlmException.NotConfigured when the user has no AI provider set up.
     * @throws LlmException.Authentication when the provider rejects the API key.
     * @throws LlmException.RateLimited when the provider still rate-limits after the client's retries.
     * @throws LlmException.InvalidRequest when the provider rejects the request.
     * @throws LlmException.InvalidOutput when structured output or tool arguments don't match their schema.
     * @throws LlmException.Unavailable on network errors, timeouts and server errors after the client's retries.
     */
    suspend fun complete(request: LlmRequest): LlmResponse
}
