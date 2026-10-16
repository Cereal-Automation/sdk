package com.cereal.sdk.component.llm

import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.io.path.createTempDirectory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Compiles script snippets against the SDK on the test classpath, the way a script module would, to prove that
 * only code opting in to [CerealPlatformApi] can build assistant turns, responses and [LlmException.InvalidOutput].
 */
class CerealPlatformApiCompileTest {
    @Test
    fun `script code cannot construct an assistant turn without opting in`() {
        assertOptInRequired("""val turn = LlmTurn.Assistant(text = "hi", toolCalls = emptyList(), providerPayload = null)""")
    }

    @Test
    fun `script code cannot construct a response without opting in`() {
        assertOptInRequired(
            """
            val response = LlmResponse(
                text = "hi",
                toolCalls = emptyList(),
                structuredJson = null,
                stopReason = StopReason.EndTurn,
                usage = LlmUsage(0, 0, 0, 0),
                model = "m",
                providerPayload = null,
            )
            """,
        )
    }

    @Test
    fun `script code cannot construct an invalid output exception without opting in`() {
        assertOptInRequired("fun rethrow(turn: LlmTurn.Assistant) = LlmException.InvalidOutput(raw = \"{}\", assistantTurn = turn)")
    }

    @Test
    fun `script code cannot read the opaque provider payload without opting in`() {
        assertOptInRequired("fun peek(turn: LlmTurn.Assistant) = turn.providerPayload")
    }

    @Test
    fun `platform code that opts in can construct all of them`() {
        val result =
            compile(
                """
                @OptIn(CerealPlatformApi::class)
                fun build(): LlmException {
                    val response = LlmResponse(
                        text = "hi",
                        toolCalls = listOf(LlmToolCall(id = "1", name = "t", argumentsJson = "{}")),
                        structuredJson = null,
                        stopReason = StopReason.ToolUse,
                        usage = LlmUsage(1, 2, 3, 4),
                        model = "m",
                        providerPayload = "opaque",
                    )
                    val payload = response.assistantTurn.providerPayload
                    val turn = LlmTurn.Assistant(text = "hi", toolCalls = emptyList(), providerPayload = payload)
                    return LlmException.InvalidOutput(raw = "{}", assistantTurn = turn)
                }
                """,
            )

        assertEquals(ExitCode.OK, result.exitCode, result.output)
    }

    private fun assertOptInRequired(snippet: String) {
        val result = compile(snippet)

        assertEquals(ExitCode.COMPILATION_ERROR, result.exitCode, result.output)
        assertTrue("platform-only API (CerealPlatformApi)" in result.output, result.output)
    }

    private fun compile(snippet: String): CompileResult {
        val workDir = createTempDirectory("llm-opt-in").toFile()
        try {
            val source = File(workDir, "Snippet.kt")
            source.writeText("package snippet\n\nimport com.cereal.sdk.component.llm.*\n\n${snippet.trimIndent()}\n")
            val output = ByteArrayOutputStream()
            val exitCode =
                K2JVMCompiler().exec(
                    PrintStream(output),
                    source.path,
                    "-classpath",
                    System.getProperty("java.class.path"),
                    "-d",
                    File(workDir, "out").path,
                    "-no-reflect",
                    "-no-stdlib",
                )
            return CompileResult(exitCode, output.toString())
        } finally {
            workDir.deleteRecursively()
        }
    }

    private class CompileResult(
        val exitCode: ExitCode,
        val output: String,
    )
}
