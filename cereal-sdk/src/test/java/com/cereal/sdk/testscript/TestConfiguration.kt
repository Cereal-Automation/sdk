package com.cereal.sdk.testscript

import com.cereal.sdk.ScriptConfiguration
import com.cereal.sdk.ScriptConfigurationItem
import com.cereal.sdk.models.proxy.Proxy

interface TestConfiguration : ScriptConfiguration {
    @ScriptConfigurationItem(
        keyName = "StringKey",
        name = "KeyOfString",
        description = "A very long long looooong description text which should describe the function of this configuration",
        position = 1,
    )
    fun keyString(): String = "default"

    @ScriptConfigurationItem(
        keyName = "BooleanKey",
        name = "KeyOfBoolean",
        description = "A very long long looooong description text which should describe the function of this configuration",
        position = 2,
    )
    fun keyBoolean(): Boolean = true

    @ScriptConfigurationItem(
        keyName = "IntegerKey",
        name = "KeyOfInteger",
        description = "A very long long looooong description text which should describe the function of this configuration",
        position = 3,
    )
    fun keyInteger(): Int = 5000

    @ScriptConfigurationItem(
        keyName = "FloatingKey",
        name = "KeyOfFloat",
        description = "A very long long looooong description text which should describe the function of this configuration",
        position = 4,
    )
    fun keyFloat(): Float = 1337.1337f

    @ScriptConfigurationItem(
        keyName = "DoubleKey",
        name = "KeyOfDouble",
        description = "A very long long looooong description text which should describe the function of this configuration",
        position = 5,
    )
    fun keyDouble(): Double = 1337.1337

    @ScriptConfigurationItem(
        keyName = "websiteKey",
        name = "Website",
        description = "Please select a website to run",
        position = 6,
    )
    fun dropdownOption(): TestConfigWebsite = TestConfigWebsite.THIRD_OPTION

    @ScriptConfigurationItem(
        keyName = "proxy",
        name = "Proxy",
        description = "Please select a proxy group to assign these tasks.",
        position = 7,
    )
    fun proxy(): Proxy? = null
}
