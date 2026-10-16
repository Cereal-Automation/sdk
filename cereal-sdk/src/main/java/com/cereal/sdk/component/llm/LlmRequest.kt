package com.cereal.sdk.component.llm

/**
 * One call to the model. The user's configuration decides the provider and model; there is deliberately no model
 * field, no sampling parameters and no stop sequences.
 *
 * JSON crosses the API as `String`s, so scripts can use any JSON library they like.
 *
 * **Schemas** ([responseSchemaJson], [LlmTool.inputSchemaJson]) must stay within the JSON Schema subset Anthropic
 * supports: every object sets `"additionalProperties": false`, and there are no numeric (`minimum`, `maximum`, …) or
 * length (`minLength`, `maxLength`, …) constraints. The client validates the returned JSON against the schema on
 * every provider, including those that don't enforce schemas, and throws [LlmException.InvalidOutput] when it
 * doesn't match.
 *
 * @property system System prompt, kept separate from the conversation. The client places it where the provider
 *                  expects it.
 * @property turns The conversation in order. Append each [LlmResponse.assistantTurn] as is.
 * @property tools Tools the model may call.
 * @property toolChoice Whether the model may call [tools] on this turn.
 * @property responseSchemaJson JSON Schema for structured output (see the subset above). When set, the validated
 *                              JSON is returned in [LlmResponse.structuredJson]. Can be combined with [tools].
 * @property maxTokens Upper bound on the number of output tokens.
 * @since 1.14.0
 */
data class LlmRequest(
    val system: String? = null,
    val turns: List<LlmTurn>,
    val tools: List<LlmTool> = emptyList(),
    val toolChoice: ToolChoice = ToolChoice.Auto,
    val responseSchemaJson: String? = null,
    val maxTokens: Int = DEFAULT_MAX_TOKENS,
) {
    companion object {
        /** The default for [maxTokens]. */
        const val DEFAULT_MAX_TOKENS: Int = 4096
    }
}

/**
 * One turn of the conversation.
 *
 * @since 1.14.0
 */
sealed interface LlmTurn {
    /** A turn written by the script. */
    data class User(
        val parts: List<UserPart>,
    ) : LlmTurn

    /**
     * A turn written by the model, taken from [LlmResponse.assistantTurn] and appended to [LlmRequest.turns] as is.
     * Script code can't construct one (see [CerealPlatformApi]).
     *
     * @property text The turn's text blocks, concatenated.
     * @property toolCalls The tool calls the model made in this turn.
     * @property providerPayload Opaque provider data (such as thinking blocks or thought signatures) that the client
     *                           sends back unchanged with this turn.
     */
    class Assistant
        @CerealPlatformApi
        constructor(
            val text: String,
            val toolCalls: List<LlmToolCall>,
            @property:CerealPlatformApi val providerPayload: String?,
        ) : LlmTurn {
            @OptIn(CerealPlatformApi::class)
            override fun equals(other: Any?): Boolean =
                other is Assistant &&
                    text == other.text &&
                    toolCalls == other.toolCalls &&
                    providerPayload == other.providerPayload

            @OptIn(CerealPlatformApi::class)
            override fun hashCode(): Int = listOf(text, toolCalls, providerPayload).hashCode()

            override fun toString(): String = "Assistant(text=$text, toolCalls=$toolCalls)"
        }
}

/**
 * A part of a [LlmTurn.User].
 *
 * @since 1.14.0
 */
sealed interface UserPart {
    /** Plain text. */
    data class Text(
        val text: String,
    ) : UserPart

    /** An image, sent to the provider base64-encoded. */
    class Image(
        val bytes: ByteArray,
        val mediaType: ImageMediaType,
    ) : UserPart {
        override fun equals(other: Any?): Boolean = other is Image && mediaType == other.mediaType && bytes.contentEquals(other.bytes)

        override fun hashCode(): Int = 31 * bytes.contentHashCode() + mediaType.hashCode()

        override fun toString(): String = "Image(${bytes.size} bytes, mediaType=$mediaType)"
    }

    /**
     * The result of running a tool the model called.
     *
     * @property toolCallId The [LlmToolCall.id] this answers.
     * @property text The result as text.
     * @property isError `true` when the tool failed and [text] describes the failure.
     */
    data class ToolResult(
        val toolCallId: String,
        val text: String,
        val isError: Boolean = false,
    ) : UserPart
}

/**
 * Image formats the model accepts.
 *
 * @since 1.14.0
 */
enum class ImageMediaType(
    val mimeType: String,
) {
    Jpeg("image/jpeg"),
    Png("image/png"),
    Gif("image/gif"),
    Webp("image/webp"),
}

/**
 * A tool the model may call.
 *
 * @property name Tool name the model refers to in [LlmToolCall.name].
 * @property description What the tool does and when to use it.
 * @property inputSchemaJson JSON Schema for the tool's arguments, within the subset described on [LlmRequest]. The
 *                           client validates each call's arguments against it.
 * @since 1.14.0
 */
data class LlmTool(
    val name: String,
    val description: String,
    val inputSchemaJson: String,
)

/**
 * Whether the model may call tools on this turn. Forcing a specific tool isn't supported by every provider, so it
 * isn't offered.
 *
 * @since 1.14.0
 */
enum class ToolChoice {
    /** The model decides whether to call a tool. */
    Auto,

    /** The model must not call a tool. */
    None,
}
