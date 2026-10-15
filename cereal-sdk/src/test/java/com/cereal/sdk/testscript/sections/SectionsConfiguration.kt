package com.cereal.sdk.testscript.sections

import com.cereal.sdk.ScriptConfiguration
import com.cereal.sdk.ScriptConfigurationItem
import com.cereal.sdk.ScriptConfigurationSections
import com.cereal.sdk.Section

@ScriptConfigurationSections(
    Section(key = "account", name = "Account"),
    Section(key = "advanced", name = "Advanced", collapsed = true),
)
interface SectionsConfiguration : ScriptConfiguration {
    @ScriptConfigurationItem(keyName = "profile_name", name = "Profile name", description = "Unsectioned, shown first")
    fun profileName(): String

    @ScriptConfigurationItem(keyName = "email", name = "Email", description = "Account email", section = "account")
    fun email(): String

    @ScriptConfigurationItem(keyName = "retries", name = "Retries", description = "Retry count", section = "advanced")
    fun retries(): Int
}
