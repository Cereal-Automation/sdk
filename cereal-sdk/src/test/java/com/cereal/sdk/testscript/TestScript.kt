package com.cereal.sdk.testscript

import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider
import com.cereal.sdk.component.script.ScriptHandle
import com.cereal.sdk.component.script.ScriptParameters
import com.cereal.sdk.testscript.child.TestChildScript

class TestScript : Script<TestConfiguration> {
    private var testChildScriptHandle: ScriptHandle? = null

    override suspend fun onStart(
        configuration: TestConfiguration,
        provider: ComponentProvider,
    ): Boolean = true

    override suspend fun execute(
        configuration: TestConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult {
        provider.logger().info("Found boolean config value: ${configuration.keyBoolean()}")
        provider.logger().info("Found integer config value: ${configuration.keyInteger()}")
        provider.logger().info("Found float config value: ${configuration.keyFloat()}")
        provider.logger().info("Found string config value: ${configuration.keyString()}")
        provider.logger().info("Found enum config value: ${configuration.dropdownOption()}")

        if (testChildScriptHandle == null) {
            val parameters = ScriptParameters()
            parameters.putString("TEST", "Test string")
            testChildScriptHandle = provider.scriptLauncher().start(TestChildScript::class.java, parameters)
        }

        provider.logger().info("Child script status = ${testChildScriptHandle?.getStatus()}")

        return ExecutionResult.Loop("Looping...", 1 * 1000)
    }

    override suspend fun onFinish(
        configuration: TestConfiguration,
        provider: ComponentProvider,
    ) {
    }
}
