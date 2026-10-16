package com.cereal.sdk

import com.cereal.sdk.component.ComponentProvider
import com.cereal.sdk.component.llm.ImageMediaType
import com.cereal.sdk.component.llm.LlmComponent
import com.cereal.sdk.component.llm.LlmException
import com.cereal.sdk.component.llm.LlmRequest
import com.cereal.sdk.component.llm.LlmResponse
import com.cereal.sdk.component.llm.LlmTurn
import com.cereal.sdk.component.llm.ToolChoice
import com.cereal.sdk.component.llm.UserPart
import com.cereal.sdk.testscript.llm.LlmConfiguration
import com.cereal.sdk.testscript.llm.LlmScript
import com.cereal.test.ComponentProviderFactory
import com.cereal.test.ScriptStatus
import com.cereal.test.TestScriptRunner
import com.cereal.test.components.TestComponentProviderFactory
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class LlmComponentTest {
    @Test
    fun `script falls back when no AI provider is set up`() =
        runBlocking {
            val runner = TestScriptRunner(LlmScript())

            runner.run(mockk<LlmConfiguration>(), TestComponentProviderFactory())

            assertEquals(ScriptStatus.Success, runner.status)
        }

    @Test
    fun `script request reaches the component with its parts and the default token limit`() =
        runBlocking {
            val received = mutableListOf<LlmRequest>()
            val llm =
                object : LlmComponent {
                    override suspend fun complete(request: LlmRequest): LlmResponse {
                        received += request
                        throw LlmException.Unavailable("offline")
                    }
                }
            val runner = TestScriptRunner(LlmScript())

            runner.run(mockk<LlmConfiguration>(), factoryWith(llm))

            assertEquals(ScriptStatus.Error, runner.status)
            val request = received.single()
            assertEquals(4096, request.maxTokens)
            assertEquals(ToolChoice.None, request.toolChoice)
            assertEquals(
                LlmTurn.User(
                    listOf(
                        UserPart.Text("What is in this picture?"),
                        UserPart.Image(byteArrayOf(1, 2, 3), ImageMediaType.Png),
                        UserPart.ToolResult(toolCallId = "call-1", text = "42", isError = true),
                    ),
                ),
                request.turns.single(),
            )
        }

    private fun factoryWith(llm: LlmComponent) =
        object : ComponentProviderFactory {
            private val delegate = TestComponentProviderFactory().create()

            override fun create(): ComponentProvider =
                object : ComponentProvider by delegate {
                    override fun llm(): LlmComponent = llm
                }
        }
}
