package com.cereal.sdk.testscript.options

import com.cereal.sdk.FieldOption
import com.cereal.sdk.FieldOptions
import com.cereal.sdk.ScriptConfiguration
import com.cereal.sdk.ScriptConfigurationItem
import com.cereal.sdk.models.Secret
import com.cereal.sdk.statemodifier.ScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue.SecretScriptConfigValue
import com.cereal.sdk.statemodifier.ScriptConfigValue.StringScriptConfigValue
import com.cereal.sdk.statemodifier.StateModifier
import com.cereal.sdk.statemodifier.Visibility

interface OptionsConfiguration : ScriptConfiguration {
    @ScriptConfigurationItem(keyName = "apiKey", name = "API key", description = "Key for the provider.")
    fun apiKey(): Secret

    @ScriptConfigurationItem(keyName = "provider", name = "Provider", description = "Provider name, used in the model placeholder.")
    fun provider(): String

    @ScriptConfigurationItem(
        keyName = "model",
        name = "Model",
        description = "Model to use.",
        stateModifier = ModelState::class,
        options = ModelOptions::class,
    )
    fun model(): String
}

object ModelOptions : FieldOptions {
    override val dependsOn = setOf("apiKey")

    override suspend fun load(config: ScriptConfig): List<FieldOption> {
        val key =
            (config.valueForKey("apiKey") as? SecretScriptConfigValue)?.value
                ?: throw IllegalStateException("Enter your API key to list models")
        if (key.reveal() != "valid") throw IllegalStateException("The API key was rejected")
        return listOf(FieldOption("small", "Small (recommended)"), FieldOption("large"))
    }
}

object ModelState : StateModifier {
    override fun getVisibility(scriptConfig: ScriptConfig) = Visibility.VisibleRequired

    override fun getError(scriptConfig: ScriptConfig): String? = null

    override fun getPlaceholder(scriptConfig: ScriptConfig): String? = (scriptConfig.valueForKey("provider") as? StringScriptConfigValue)?.value?.let { "$it-recommended" }
}
