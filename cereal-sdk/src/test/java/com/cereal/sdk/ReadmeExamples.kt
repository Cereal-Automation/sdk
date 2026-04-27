package com.cereal.sdk

// Compile-check tests for README.md code examples.
// These tests verify that every README code block compiles against the real API.
// Runtime assertions are intentionally absent — the Kotlin compiler is the assertion.

import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.ScriptConfiguration
import com.cereal.sdk.ScriptConfigurationItem
import com.cereal.sdk.component.ComponentProvider
import com.cereal.sdk.component.notification.notification
import com.cereal.sdk.component.notification.telegram.model.TelegramParseMode
import com.cereal.sdk.component.script.ScriptParameters
import com.cereal.sdk.component.userinteraction.WebResourceRequest
import com.cereal.sdk.statemodifier.ScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue.BooleanScriptConfigValue
import com.cereal.sdk.statemodifier.StateModifier
import com.cereal.sdk.statemodifier.Visibility
import com.cereal.sdk.testscript.child.TestChildConfiguration
import com.cereal.sdk.testscript.child.TestChildScript
import com.cereal.test.TestScriptRunner
import com.cereal.test.components.TestComponentProviderFactory
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ReadmeExamples {

    // ------------------------------------------------------------------
    // Shared config interface matching the README "Writing a Script" example
    // ------------------------------------------------------------------

    interface ReadmeConfig : ScriptConfiguration {
        @ScriptConfigurationItem(
            keyName = "target_url",
            name = "Target URL",
            description = "URL the script will operate on",
            position = 0,
        )
        fun targetUrl(): String

        @ScriptConfigurationItem(
            keyName = "retry_count",
            name = "Retry count",
            description = "How many times to retry on failure",
            position = 1,
        )
        fun retryCount(): Int
    }

    // ------------------------------------------------------------------
    // Script lifecycle
    // ------------------------------------------------------------------

    @Test
    fun `readme script lifecycle compiles`() =
        runBlocking {
            val script =
                object : Script<ReadmeConfig> {
                    override suspend fun onStart(
                        configuration: ReadmeConfig,
                        provider: ComponentProvider,
                    ): Boolean = true

                    override suspend fun execute(
                        configuration: ReadmeConfig,
                        provider: ComponentProvider,
                        statusUpdate: suspend (String) -> Unit,
                    ): ExecutionResult {
                        statusUpdate("Running…")
                        return ExecutionResult.Success("Done")
                    }

                    override suspend fun onFinish(
                        configuration: ReadmeConfig,
                        provider: ComponentProvider,
                    ) {}
                }
            val config =
                mockk<ReadmeConfig> {
                    every { targetUrl() } returns "https://example.com"
                    every { retryCount() } returns 1
                }
            val runner = TestScriptRunner(script)
            runner.run(config, TestComponentProviderFactory())
        }

    // ------------------------------------------------------------------
    // StateModifier
    // ------------------------------------------------------------------

    @Test
    fun `readme state modifier compiles`() {
        class ShowWhenEnabled : StateModifier {
            override fun getVisibility(scriptConfig: ScriptConfig): Visibility =
                if (scriptConfig.valueForKey("enabled") == BooleanScriptConfigValue(true)) {
                    Visibility.VisibleRequired
                } else {
                    Visibility.Hidden
                }

            override fun getError(scriptConfig: ScriptConfig): String? = null
        }
        @Suppress("UNUSED_VARIABLE")
        val modifier = ShowWhenEnabled()
    }

    // ------------------------------------------------------------------
    // Logger
    // ------------------------------------------------------------------

    @Test
    fun `readme logger compiles`() =
        runBlocking {
            val provider = TestComponentProviderFactory().create()
            provider.logger().info("message")
            provider.logger().warn("message")
            provider.logger().error("message")
            provider.logger().debug("message")
        }

    // ------------------------------------------------------------------
    // Preferences
    // ------------------------------------------------------------------

    @Test
    fun `readme preferences compiles`() =
        runBlocking {
            val provider = TestComponentProviderFactory().create()
            provider.preference().setString("key", "value")
            @Suppress("UNUSED_VARIABLE")
            val value = provider.preference().getString("key")
        }

    // ------------------------------------------------------------------
    // Notifications
    // ------------------------------------------------------------------

    @Test
    fun `readme notifications compiles`() =
        runBlocking {
            val provider = TestComponentProviderFactory().create()
            provider.notification().sendNotification(
                notification("Checkout complete") {
                    title { "Order placed" }
                    discordMessage {
                        content { "Successfully checked out." }
                    }
                    telegramMessage {
                        text { "Successfully checked out." }
                        parseMode { TelegramParseMode.HTML }
                    }
                },
            )
        }

    // ------------------------------------------------------------------
    // User interaction — showUrl
    // ------------------------------------------------------------------

    @Test
    fun `readme user interaction showUrl compiles`() {
        runBlocking {
            val factory = TestComponentProviderFactory()
            factory.showUrlResults =
                listOf(
                    WebResourceRequest(
                        method = "GET",
                        requestHeaders = emptyMap(),
                        url = "https://example.com/success",
                        postData = null,
                    ),
                )
            val provider = factory.create()
            provider.userInteraction().showUrl(
                title = "Login",
                url = "https://example.com/login",
                shouldFinish = { it.url.startsWith("https://example.com/success") },
            )
        }
    }

    // ------------------------------------------------------------------
    // User interaction — requestInput
    // ------------------------------------------------------------------

    @Test
    fun `readme user interaction requestInput compiles`() =
        runBlocking {
            val factory = TestComponentProviderFactory()
            factory.requestInputResults = listOf("myinput")
            val provider = factory.create()
            @Suppress("UNUSED_VARIABLE")
            val input = provider.userInteraction().requestInput("Enter code", "Check your email")
        }

    // ------------------------------------------------------------------
    // User interaction — showContinueButton
    // ------------------------------------------------------------------

    @Test
    fun `readme user interaction showContinueButton compiles`() =
        runBlocking {
            val provider = TestComponentProviderFactory().create()
            provider.userInteraction().showContinueButton()
        }

    // ------------------------------------------------------------------
    // Child scripts — ScriptParameters construction and start() signature
    // ------------------------------------------------------------------

    @Test
    fun `readme child scripts compiles`() {
        runBlocking {
            val childConfig =
                mockk<TestChildConfiguration> {
                    every { keyString() } returns "test"
                }
            val factory = TestComponentProviderFactory()
            factory.childScriptConfigurations = mapOf(TestChildScript::class.java to childConfig)
            val provider = factory.create()
            val parameters =
                ScriptParameters().apply {
                    putString("order_id", "12345")
                }
            provider.scriptLauncher().start(TestChildScript::class.java, parameters)
        }
    }

    // ------------------------------------------------------------------
    // Test runner
    // ------------------------------------------------------------------

    @Test
    fun `readme test runner compiles`() =
        runBlocking {
            val script =
                object : Script<ReadmeConfig> {
                    override suspend fun onStart(
                        configuration: ReadmeConfig,
                        provider: ComponentProvider,
                    ): Boolean = true

                    override suspend fun execute(
                        configuration: ReadmeConfig,
                        provider: ComponentProvider,
                        statusUpdate: suspend (String) -> Unit,
                    ): ExecutionResult = ExecutionResult.Success("Done")

                    override suspend fun onFinish(
                        configuration: ReadmeConfig,
                        provider: ComponentProvider,
                    ) {}
                }
            val config =
                mockk<ReadmeConfig> {
                    every { targetUrl() } returns "https://example.com"
                    every { retryCount() } returns 1
                }
            val factory = TestComponentProviderFactory()
            val runner = TestScriptRunner(script)
            runner.run(config, factory)
        }
}
