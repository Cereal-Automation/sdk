package com.cereal.test

import com.cereal.sdk.component.llm.LlmException
import com.cereal.sdk.component.llm.LlmRequest
import com.cereal.sdk.component.llm.LlmTurn
import com.cereal.sdk.component.llm.LlmUsage
import com.cereal.sdk.component.llm.StopReason
import com.cereal.sdk.component.llm.UserPart
import com.cereal.test.components.TestComponentProviderFactory
import com.cereal.test.components.invalidOutput
import com.cereal.test.components.llmResponse
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FakeLlmComponentTest {
    private val factory = TestComponentProviderFactory()

    private fun ask(text: String) = LlmRequest(turns = listOf(LlmTurn.User(listOf(UserPart.Text(text)))))

    @Test
    fun `with nothing configured complete throws NotConfigured`() =
        runBlocking<Unit> {
            assertFailsWith<LlmException.NotConfigured> { factory.create().llm().complete(ask("hi")) }
        }

    @Test
    fun `queued responses and exceptions are served in order`() =
        runBlocking<Unit> {
            factory.llm.queue(llmResponse(text = "first"), LlmException.RateLimited(), llmResponse(text = "third"))
            val llm = factory.create().llm()

            assertEquals("first", llm.complete(ask("1")).text)
            assertFailsWith<LlmException.RateLimited> { llm.complete(ask("2")) }
            assertEquals("third", llm.complete(ask("3")).text)
        }

    @Test
    fun `an exhausted queue fails with an AssertionError, not an LlmException`() =
        runBlocking<Unit> {
            factory.llm.queue(llmResponse())
            val llm = factory.create().llm()
            llm.complete(ask("1"))

            assertFailsWith<AssertionError> { llm.complete(ask("2")) }
        }

    @Test
    fun `requests are recorded in call order`() =
        runBlocking<Unit> {
            factory.llm.responder = { llmResponse(text = "ok") }
            val llm = factory.create().llm()
            llm.complete(ask("1"))
            runCatching { llm.complete(ask("2")) }

            assertEquals(listOf(ask("1"), ask("2")), factory.llm.requests)
        }

    @Test
    fun `responder answers based on the request`() =
        runBlocking<Unit> {
            factory.llm.responder = { request -> llmResponse(text = "echo ${request.turns.size}") }

            assertEquals(
                "echo 1",
                factory
                    .create()
                    .llm()
                    .complete(ask("x"))
                    .text,
            )
        }

    @Test
    fun `invalidOutput carries the raw output as the assistant turn, without opting in`() =
        runBlocking<Unit> {
            factory.llm.queue(invalidOutput(raw = "{not json"))

            val e = assertFailsWith<LlmException.InvalidOutput> { factory.create().llm().complete(ask("x")) }

            assertEquals("{not json", e.raw)
            assertEquals("{not json", e.assistantTurn.text)
        }

    @Test
    fun `llmResponse defaults to zero usage, model fake and EndTurn`() {
        val response = llmResponse(text = "hi")

        assertEquals(LlmUsage(0, 0, 0, 0), response.usage)
        assertEquals("fake", response.model)
        assertEquals(StopReason.EndTurn, response.stopReason)
        assertEquals(null, response.structuredJson)
        assertEquals(emptyList(), response.toolCalls)
    }

    @Test
    fun `llmResponse rejects structuredJson unless stopReason is EndTurn`() {
        assertFailsWith<IllegalArgumentException> { llmResponse(structuredJson = "{}", stopReason = StopReason.ToolUse) }
        assertEquals("{}", llmResponse(structuredJson = "{}").structuredJson)
    }
}
