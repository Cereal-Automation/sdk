package com.cereal.sdk.component.llm

/**
 * The model's answer to one [LlmRequest]. Script code can't construct one (see [CerealPlatformApi]).
 *
 * @property text All text blocks of the answer, concatenated.
 * @property toolCalls Tool calls the model made, with validated arguments.
 * @property structuredJson The validated structured output. Set only when [LlmRequest.responseSchemaJson] was given
 *                          and [stopReason] is [StopReason.EndTurn], so it is never a half-finished answer.
 * @property stopReason Why the model stopped.
 * @property usage Token usage of this call.
 * @property model The model that actually answered.
 * @property assistantTurn This answer as a turn, to append to [LlmRequest.turns] as is.
 * @since 1.14.0
 */
class LlmResponse
    @CerealPlatformApi
    constructor(
        val text: String,
        val toolCalls: List<LlmToolCall>,
        val structuredJson: String?,
        val stopReason: StopReason,
        val usage: LlmUsage,
        val model: String,
        providerPayload: String?,
    ) {
        @OptIn(CerealPlatformApi::class)
        val assistantTurn: LlmTurn.Assistant = LlmTurn.Assistant(text, toolCalls, providerPayload)

        override fun equals(other: Any?): Boolean =
            other is LlmResponse &&
                structuredJson == other.structuredJson &&
                stopReason == other.stopReason &&
                usage == other.usage &&
                model == other.model &&
                assistantTurn == other.assistantTurn

        override fun hashCode(): Int = listOf(structuredJson, stopReason, usage, model, assistantTurn).hashCode()

        override fun toString(): String = "LlmResponse(text=$text, toolCalls=$toolCalls, structuredJson=$structuredJson, stopReason=$stopReason, usage=$usage, model=$model)"
    }

/**
 * A tool call made by the model.
 *
 * @property id Identifier to answer with [UserPart.ToolResult.toolCallId].
 * @property name The [LlmTool.name] called.
 * @property argumentsJson The arguments as a plain JSON object string, validated against [LlmTool.inputSchemaJson].
 * @since 1.14.0
 */
data class LlmToolCall(
    val id: String,
    val name: String,
    val argumentsJson: String,
)

/**
 * Why the model stopped, normalized across providers.
 *
 * @since 1.14.0
 */
enum class StopReason {
    /** The model finished its answer. */
    EndTurn,

    /** The model wants the script to run [LlmResponse.toolCalls]. */
    ToolUse,

    /** The answer hit [LlmRequest.maxTokens] and is cut off. */
    MaxTokens,

    /** The model or the provider's content filter declined to answer. This is an outcome, not an error. */
    Refusal,
}

/**
 * Token usage of one call, normalized across providers. Tokens only; the SDK never reports cost.
 *
 * @property inputUncached Input tokens not served from the prompt cache.
 * @property cacheRead Input tokens read from the prompt cache.
 * @property cacheWrite Input tokens written to the prompt cache.
 * @property output Output tokens, including any thinking.
 * @since 1.14.0
 */
data class LlmUsage(
    val inputUncached: Int,
    val cacheRead: Int,
    val cacheWrite: Int,
    val output: Int,
)
