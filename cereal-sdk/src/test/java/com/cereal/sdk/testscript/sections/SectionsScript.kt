package com.cereal.sdk.testscript.sections

import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider

class SectionsScript : Script<SectionsConfiguration> {
    val seen = mutableListOf<String>()

    override suspend fun onStart(
        configuration: SectionsConfiguration,
        provider: ComponentProvider,
    ): Boolean = true

    override suspend fun execute(
        configuration: SectionsConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult {
        seen += listOf(configuration.profileName(), configuration.email(), configuration.retries().toString())
        return ExecutionResult.Success("done")
    }

    override suspend fun onFinish(
        configuration: SectionsConfiguration,
        provider: ComponentProvider,
    ) = Unit
}
