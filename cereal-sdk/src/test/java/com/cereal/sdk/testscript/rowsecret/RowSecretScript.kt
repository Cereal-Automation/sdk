package com.cereal.sdk.testscript.rowsecret

import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.ScriptConfiguration
import com.cereal.sdk.ScriptConfigurationItem
import com.cereal.sdk.ScriptConfigurationListItem
import com.cereal.sdk.component.ComponentProvider
import com.cereal.sdk.models.Secret
import com.cereal.sdk.statemodifier.ScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue
import com.cereal.sdk.statemodifier.StateModifier
import com.cereal.sdk.statemodifier.Visibility

interface AccountRow : ScriptConfigurationListItem {
    @ScriptConfigurationItem(keyName = "label", name = "Label", description = "Account label")
    fun label(): String

    @ScriptConfigurationItem(keyName = "token", name = "Token", description = "Account API token")
    fun token(): Secret

    @ScriptConfigurationItem(keyName = "backupToken", name = "Backup token", description = "Optional backup token")
    fun backupToken(): Secret?
}

interface RowSecretConfiguration : ScriptConfiguration {
    @ScriptConfigurationItem(
        keyName = "accounts",
        name = "Accounts",
        description = "Accounts to sign in with",
        stateModifier = EveryAccountHasAToken::class,
    )
    fun accounts(): List<AccountRow>
}

object EveryAccountHasAToken : StateModifier {
    override fun getVisibility(scriptConfig: ScriptConfig): Visibility = Visibility.VisibleRequired

    override fun getError(scriptConfig: ScriptConfig): String? {
        val rows = (scriptConfig.valueForKey("accounts") as? ScriptConfigValue.ListScriptConfigValue)?.items.orEmpty()
        return if (rows.all { it.valueForKey("token") is ScriptConfigValue.SecretScriptConfigValue }) {
            null
        } else {
            "Every account needs a token."
        }
    }
}

class RowSecretScript : Script<RowSecretConfiguration> {
    val revealed = mutableListOf<Triple<String, String, String?>>()

    override suspend fun onStart(
        configuration: RowSecretConfiguration,
        provider: ComponentProvider,
    ): Boolean = true

    override suspend fun execute(
        configuration: RowSecretConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult {
        configuration.accounts().forEach { revealed += Triple(it.label(), it.token().reveal(), it.backupToken()?.reveal()) }
        return ExecutionResult.Success("Signed in")
    }

    override suspend fun onFinish(
        configuration: RowSecretConfiguration,
        provider: ComponentProvider,
    ) = Unit
}
