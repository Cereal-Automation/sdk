package com.cereal.sdk.testscript.taskaction

import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.ScriptConfiguration
import com.cereal.sdk.ScriptConfigurationItem
import com.cereal.sdk.TaskAction
import com.cereal.sdk.component.ComponentProvider

interface TaskActionConfiguration : ScriptConfiguration {
    @ScriptConfigurationItem(keyName = "cacheKey", name = "Cache key", description = "Preference key holding the learned steps.")
    fun cacheKey(): String
}

class TaskActionScript : Script<TaskActionConfiguration> {
    override suspend fun onStart(
        configuration: TaskActionConfiguration,
        provider: ComponentProvider,
    ) = true

    override suspend fun execute(
        configuration: TaskActionConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult = ExecutionResult.Success("Done")

    override suspend fun onFinish(
        configuration: TaskActionConfiguration,
        provider: ComponentProvider,
    ) {}

    override fun taskActions(): List<TaskAction<TaskActionConfiguration>> = listOf(ForgetLearnedSteps)
}

object ForgetLearnedSteps : TaskAction<TaskActionConfiguration> {
    override val name = "Forget learned steps"
    override val confirm = "The script will re-learn the steps on its next run."

    override suspend fun run(
        configuration: TaskActionConfiguration,
        provider: ComponentProvider,
    ): String {
        provider.preference().delete(configuration.cacheKey())
        return "Forgot learned steps"
    }
}
