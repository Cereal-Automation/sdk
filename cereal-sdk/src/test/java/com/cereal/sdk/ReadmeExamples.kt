package com.cereal.sdk

// Compile-check tests for README.md code examples.
// These tests verify that every README code block compiles against the real API.
// Runtime assertions are intentionally absent — the Kotlin compiler is the assertion.

import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.ScriptConfiguration
import com.cereal.sdk.ScriptConfigurationItem
import com.cereal.sdk.component.ComponentProvider
import com.cereal.sdk.component.artifact.ArtifactComponent
import com.cereal.sdk.component.notification.notification
import com.cereal.sdk.component.notification.telegram.model.TelegramParseMode
import com.cereal.sdk.component.script.ScriptParameters
import com.cereal.sdk.component.userinteraction.WebResourceRequest
import com.cereal.sdk.models.Secret
import com.cereal.sdk.statemodifier.ScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue
import com.cereal.sdk.statemodifier.ScriptConfigValue.BooleanScriptConfigValue
import com.cereal.sdk.statemodifier.ScriptConfigValue.SecretScriptConfigValue
import com.cereal.sdk.statemodifier.StateModifier
import com.cereal.sdk.statemodifier.Visibility
import com.cereal.sdk.testscript.child.TestChildConfiguration
import com.cereal.sdk.testscript.child.TestChildScript
import com.cereal.test.ComponentProviderFactory
import com.cereal.test.TestScriptRunner
import com.cereal.test.components.RecordingArtifactComponent
import com.cereal.test.components.TestComponentProviderFactory
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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

    // Declared as an object, matching the README: the platform reads the singleton instance and
    // rejects a script whose state modifier is a class.
    object ShowWhenEnabled : StateModifier {
        override fun getVisibility(scriptConfig: ScriptConfig): Visibility =
            if (scriptConfig.valueForKey("enabled") == BooleanScriptConfigValue(true)) {
                Visibility.VisibleRequired
            } else {
                Visibility.Hidden
            }

        override fun getError(scriptConfig: ScriptConfig): String? = null
    }

    @Test
    fun `readme state modifier compiles`() {
        val modifier: StateModifier = ShowWhenEnabled
        val emptyConfig =
            object : ScriptConfig {
                override fun valueForKey(key: String) = ScriptConfigValue.NullScriptConfigValue
            }

        assertEquals(Visibility.Hidden, modifier.getVisibility(emptyConfig))
        assertEquals(null, modifier.getError(emptyConfig))
    }

    // ------------------------------------------------------------------
    // Secret — credentials in configuration
    // ------------------------------------------------------------------

    interface ReadmeSecretConfig : ScriptConfiguration {
        @ScriptConfigurationItem(
            keyName = "api_key",
            name = "API key",
            description = "Your service API key",
        )
        fun apiKey(): Secret

        @ScriptConfigurationItem(
            keyName = "webhook_secret",
            name = "Webhook secret",
            description = "Optional",
        )
        fun webhookSecret(): Secret?
    }

    @Test
    fun `readme secret compiles`() =
        runBlocking {
            val script =
                object : Script<ReadmeSecretConfig> {
                    override suspend fun onStart(
                        configuration: ReadmeSecretConfig,
                        provider: ComponentProvider,
                    ): Boolean = true

                    override suspend fun execute(
                        configuration: ReadmeSecretConfig,
                        provider: ComponentProvider,
                        statusUpdate: suspend (String) -> Unit,
                    ): ExecutionResult {
                        @Suppress("UNUSED_VARIABLE")
                        val token = configuration.apiKey().reveal()
                        statusUpdate("Authenticating with ${configuration.apiKey()}")
                        return ExecutionResult.Success("Done")
                    }

                    override suspend fun onFinish(
                        configuration: ReadmeSecretConfig,
                        provider: ComponentProvider,
                    ) {}
                }
            val config =
                mockk<ReadmeSecretConfig> {
                    every { apiKey() } returns Secret("an-api-key")
                    every { webhookSecret() } returns null
                }
            val runner = TestScriptRunner(script)
            runner.run(config, TestComponentProviderFactory())
        }

    // Declared as an object, matching the README: the platform reads the singleton instance and
    // rejects a script whose state modifier is a class.
    object RequireApiKeyFormat : StateModifier {
        override fun getVisibility(scriptConfig: ScriptConfig): Visibility = Visibility.VisibleRequired

        override fun getError(scriptConfig: ScriptConfig): String? {
            val value = scriptConfig.valueForKey("api_key")
            if (value !is SecretScriptConfigValue) return null
            return if (value.value.reveal().startsWith("sk-")) null else "API keys start with sk-"
        }
    }

    @Test
    fun `readme secret state modifier compiles`() {
        val modifier: StateModifier = RequireApiKeyFormat

        // A state modifier reads the entered credential, so it can validate the format while the user types.
        assertEquals(null, modifier.getError(configWith(SecretScriptConfigValue(Secret("sk-live-1")))))
        assertEquals(
            "API keys start with sk-",
            modifier.getError(configWith(SecretScriptConfigValue(Secret("nope")))),
        )

        // Unset is not an error — the required-field check owns that, not the format check.
        assertEquals(null, modifier.getError(configWith(ScriptConfigValue.NullScriptConfigValue)))
    }

    private fun configWith(value: ScriptConfigValue) =
        object : ScriptConfig {
            override fun valueForKey(key: String) = value
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
    fun `readme user interaction showUrl compiles`() =
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
            Unit
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
    fun `readme child scripts compiles`() =
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

            @Suppress("UNUSED_VARIABLE")
            val handle = provider.scriptLauncher().start(TestChildScript::class.java, parameters)
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
            // Covers the README Testing section: factory + runner wired together
            val factory = TestComponentProviderFactory()
            val runner = TestScriptRunner(script)
            runner.run(config, factory)
        }

    // ------------------------------------------------------------------
    // Artifacts
    // ------------------------------------------------------------------

    @Test
    fun `readme artifact emit compiles`() =
        runBlocking {
            val provider = TestComponentProviderFactory().create()

            val csv =
                buildString {
                    appendLine("sku,price")
                    appendLine("ABC-1,19.99")
                }.toByteArray()

            provider.artifact().emit(
                name = "results.csv",
                bytes = csv,
                mimeType = "text/csv", // optional; inferred from the name's extension when omitted
            )
        }

    /** Covers the README Testing section's delegating-factory snippet for asserting on artifacts. */
    @Test
    fun `readme artifact assertion compiles`() =
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
                        provider.artifact().emit("results.csv", "sku\n".toByteArray(), "text/csv")
                        return ExecutionResult.Success("Done")
                    }

                    override suspend fun onFinish(
                        configuration: ReadmeConfig,
                        provider: ComponentProvider,
                    ) {}
                }
            val configuration =
                mockk<ReadmeConfig> {
                    every { targetUrl() } returns "https://example.com"
                    every { retryCount() } returns 1
                }
            val scriptRunner = TestScriptRunner(script)

            val artifacts = RecordingArtifactComponent()

            val componentProviderFactory =
                object : ComponentProviderFactory {
                    private val delegate = TestComponentProviderFactory().create()

                    override fun create(): ComponentProvider =
                        object : ComponentProvider by delegate {
                            override fun artifact(): ArtifactComponent = artifacts
                        }
                }

            scriptRunner.run(configuration, componentProviderFactory)

            assertEquals("results.csv", artifacts.emitted.single().name)
        }
}
