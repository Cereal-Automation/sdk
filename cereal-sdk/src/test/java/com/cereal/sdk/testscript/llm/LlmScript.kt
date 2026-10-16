package com.cereal.sdk.testscript.llm

import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.ScriptConfiguration
import com.cereal.sdk.component.ComponentProvider
import com.cereal.sdk.component.llm.ImageMediaType
import com.cereal.sdk.component.llm.LlmException
import com.cereal.sdk.component.llm.LlmRequest
import com.cereal.sdk.component.llm.LlmTool
import com.cereal.sdk.component.llm.LlmTurn
import com.cereal.sdk.component.llm.StopReason
import com.cereal.sdk.component.llm.ToolChoice
import com.cereal.sdk.component.llm.UserPart

interface LlmConfiguration : ScriptConfiguration

/** Uses the whole script-facing LLM surface the way a script module would, and falls back when no provider is set up. */
class LlmScript : Script<LlmConfiguration> {
    override suspend fun onStart(
        configuration: LlmConfiguration,
        provider: ComponentProvider,
    ) = true

    override suspend fun execute(
        configuration: LlmConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult {
        val request =
            LlmRequest(
                system = "You describe images.",
                turns =
                    listOf(
                        LlmTurn.User(
                            listOf(
                                UserPart.Text("What is in this picture?"),
                                UserPart.Image(byteArrayOf(1, 2, 3), ImageMediaType.Png),
                                UserPart.ToolResult(toolCallId = "call-1", text = "42", isError = true),
                            ),
                        ),
                    ),
                tools = listOf(LlmTool("lookup", "Looks a product up.", """{"type":"object","additionalProperties":false}""")),
                toolChoice = ToolChoice.None,
                responseSchemaJson = """{"type":"object","properties":{"label":{"type":"string"}},"additionalProperties":false}""",
            )
        return try {
            val response = provider.llm().complete(request)
            val nextTurns = request.turns + response.assistantTurn
            when (response.stopReason) {
                StopReason.EndTurn -> ExecutionResult.Success(response.structuredJson ?: response.text)
                StopReason.ToolUse -> ExecutionResult.Success("${response.toolCalls.size} tool calls after ${nextTurns.size} turns")
                StopReason.MaxTokens, StopReason.Refusal -> ExecutionResult.Error("No answer (${response.usage.output} tokens)")
            }
        } catch (e: LlmException.NotConfigured) {
            ExecutionResult.Success("Skipped the model: ${e.message}")
        } catch (e: LlmException) {
            ExecutionResult.Error(e.message ?: "LLM call failed")
        }
    }

    override suspend fun onFinish(
        configuration: LlmConfiguration,
        provider: ComponentProvider,
    ) {}
}
