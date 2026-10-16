package com.cereal.test.components

import com.cereal.sdk.component.llm.CerealPlatformApi
import com.cereal.sdk.component.llm.LlmComponent
import com.cereal.sdk.component.llm.LlmException
import com.cereal.sdk.component.llm.LlmRequest
import com.cereal.sdk.component.llm.LlmResponse
import com.cereal.sdk.component.llm.LlmToolCall
import com.cereal.sdk.component.llm.LlmTurn
import com.cereal.sdk.component.llm.LlmUsage
import com.cereal.sdk.component.llm.StopReason
import com.cereal.test.util.Logger

/**
 * Scriptable [LlmComponent] for tests: no network, no key, no schema validation. Records every request in [requests]
 * and answers with [responder]. By default every call throws [LlmException.NotConfigured], like a user with no AI
 * provider set up.
 */
class FakeLlmComponent : LlmComponent {
    private val logger = Logger("LlmComponent")
    private val recorded = mutableListOf<LlmRequest>()

    /** Requests received from the script under test, in call order. */
    val requests: List<LlmRequest> get() = recorded

    /** Answers each request. Return an [LlmResponse] (see [llmResponse]) or throw, e.g. an [LlmException]. */
    var responder: suspend (LlmRequest) -> LlmResponse = { throw LlmException.NotConfigured() }

    /**
     * Replaces [responder] with one that serves [outcomes] in order: an [LlmResponse] is returned, a [Throwable] is
     * thrown. A call after the last outcome fails the test with an [AssertionError].
     */
    fun queue(vararg outcomes: Any) {
        outcomes.forEach {
            require(it is LlmResponse || it is Throwable) { "queue() takes LlmResponse or Throwable, got ${it::class.simpleName}." }
        }
        val remaining = ArrayDeque(outcomes.toList())
        responder = {
            when (val next = remaining.removeFirstOrNull() ?: throw AssertionError("FakeLlmComponent queue exhausted: unexpected call #${recorded.size}.")) {
                is Throwable -> throw next
                else -> next as LlmResponse
            }
        }
    }

    override suspend fun complete(request: LlmRequest): LlmResponse {
        recorded += request
        logger.logMessage("Complete request #${recorded.size} (${request.turns.size} turns, ${request.tools.size} tools).")
        return responder(request)
    }
}

/** Builds an [LlmResponse] as the fake returns it: zero usage, model `fake`, no provider payload. */
@OptIn(CerealPlatformApi::class)
fun llmResponse(
    text: String = "",
    toolCalls: List<LlmToolCall> = emptyList(),
    structuredJson: String? = null,
    stopReason: StopReason = StopReason.EndTurn,
): LlmResponse {
    require(structuredJson == null || stopReason == StopReason.EndTurn) { "structuredJson is only set when stopReason is EndTurn." }
    return LlmResponse(text, toolCalls, structuredJson, stopReason, LlmUsage(0, 0, 0, 0), "fake", null)
}

/** Builds the [LlmException.InvalidOutput] a real provider throws when the model's output fails validation. */
@OptIn(CerealPlatformApi::class)
fun invalidOutput(
    raw: String = "",
    assistantTurn: LlmTurn.Assistant = llmResponse(text = raw).assistantTurn,
): LlmException.InvalidOutput = LlmException.InvalidOutput(raw, assistantTurn)
