package com.cereal.sdk

import com.cereal.sdk.models.Secret
import com.cereal.sdk.statemodifier.DefaultStateModifier
import com.cereal.sdk.statemodifier.ScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue
import com.cereal.sdk.statemodifier.ScriptConfigValue.SecretScriptConfigValue
import com.cereal.sdk.statemodifier.ScriptConfigValue.StringScriptConfigValue
import com.cereal.sdk.testscript.options.ModelOptions
import com.cereal.sdk.testscript.options.ModelState
import com.cereal.sdk.testscript.options.OptionsConfiguration
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class FieldOptionsTest {
    private fun config(vararg values: Pair<String, ScriptConfigValue>) =
        object : ScriptConfig {
            override fun valueForKey(key: String) = values.toMap()[key] ?: ScriptConfigValue.NullScriptConfigValue
        }

    private fun item(function: String) =
        OptionsConfiguration::class.java.getMethod(function).getAnnotation(ScriptConfigurationItem::class.java)

    @Test
    fun `load returns options in order from a secret dependsOn field`() {
        val options = runBlocking { ModelOptions.load(config("apiKey" to SecretScriptConfigValue(Secret("valid")))) }

        assertEquals(listOf(FieldOption("small", "Small (recommended)"), FieldOption("large", null)), options)
    }

    @Test
    fun `a thrown exception carries the inline failure text`() {
        val error = assertFailsWith<IllegalStateException> { runBlocking { ModelOptions.load(config()) } }

        assertEquals("Enter your API key to list models", error.message)
    }

    @Test
    fun `options attribute names the source and defaults to no options`() {
        assertEquals(ModelOptions::class, item("model").options)
        assertEquals(NoFieldOptions::class, item("apiKey").options)
        assertEquals(emptySet(), NoFieldOptions.dependsOn)
        assertEquals(emptyList(), runBlocking { NoFieldOptions.load(config()) })
    }

    @Test
    fun `placeholder defaults to null and can be overridden`() {
        assertNull(DefaultStateModifier.getPlaceholder(config()))
        assertEquals("openai-recommended", ModelState.getPlaceholder(config("provider" to StringScriptConfigValue("openai"))))
    }
}
