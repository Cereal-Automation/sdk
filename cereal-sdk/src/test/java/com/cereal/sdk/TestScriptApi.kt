package com.cereal.sdk

import com.cereal.sdk.testscript.TestConfigWebsite
import com.cereal.sdk.testscript.TestConfiguration
import com.cereal.sdk.testscript.TestScript
import com.cereal.sdk.testscript.child.TestChildConfiguration
import com.cereal.sdk.testscript.child.TestChildScript
import com.cereal.test.TestScriptRunner
import com.cereal.test.components.TestComponentProviderFactory
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Test

class TestScriptApi {
    @Test
    fun testSuccess() =
        runBlocking {
            // Initialize script and the test script runner.
            val script = TestScript()
            val scriptRunner = TestScriptRunner(script)

            // Mock the configuration values
            val configuration =
                mockk<TestConfiguration> {
                    every { keyString() } returns "Some random string"
                    every { keyBoolean() } returns true
                    every { keyInteger() } returns 100
                    every { keyFloat() } returns 101.0f
                    every { keyDouble() } returns 102.0
                    every { dropdownOption() } returns TestConfigWebsite.THIRD_OPTION
                }
            val childConfiguration =
                mockk<TestChildConfiguration> {
                    every { keyString() } returns "Some random string"
                }
            val componentProviderFactory = TestComponentProviderFactory()
            componentProviderFactory.childScriptConfigurations =
                mapOf(
                    TestChildScript::class.java to childConfiguration,
                )

            // Run our script
            try {
                withTimeout(10000) { scriptRunner.run(configuration, componentProviderFactory) }
            } catch (e: Exception) {
                // Timeout exception is expected because else script will never return because of an infinit loop.
            }
        }
}
