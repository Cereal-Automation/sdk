package com.cereal.sdk

import com.cereal.sdk.models.Secret
import com.cereal.sdk.statemodifier.ScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue
import com.cereal.sdk.testscript.rowsecret.AccountRow
import com.cereal.sdk.testscript.rowsecret.EveryAccountHasAToken
import com.cereal.sdk.testscript.rowsecret.RowSecretConfiguration
import com.cereal.sdk.testscript.rowsecret.RowSecretScript
import com.cereal.test.ScriptStatus
import com.cereal.test.TestScriptRunner
import com.cereal.test.components.TestComponentProviderFactory
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RowSecretTest {
    @Test
    fun `script receives row secrets it can reveal, with a nullable one set or null`() =
        runBlocking {
            fun account(
                label: String,
                token: String,
                backupToken: String?,
            ) = object : AccountRow {
                override fun label() = label

                override fun token() = Secret(token)

                override fun backupToken() = backupToken?.let(::Secret)
            }
            val script = RowSecretScript()
            val runner = TestScriptRunner(script)
            val configuration =
                object : RowSecretConfiguration {
                    override fun accounts() = listOf(account("main", "hunter2", "hunter3"), account("spare", "swordfish", null))
                }

            runner.run(configuration, TestComponentProviderFactory())

            assertEquals(ScriptStatus.Success, runner.status)
            assertEquals(listOf(Triple("main", "hunter2", "hunter3"), Triple("spare", "swordfish", null)), script.revealed)
        }

    @Test
    fun `list modifier sees a row secret as SecretScriptConfigValue`() {
        fun row(token: ScriptConfigValue) =
            object : ScriptConfig {
                override fun valueForKey(key: String) = if (key == "token") token else ScriptConfigValue.NullScriptConfigValue
            }

        fun list(vararg rows: ScriptConfig) =
            object : ScriptConfig {
                override fun valueForKey(key: String) = ScriptConfigValue.ListScriptConfigValue(rows.toList())
            }

        val withToken = row(ScriptConfigValue.SecretScriptConfigValue(Secret("hunter2")))
        val withoutToken = row(ScriptConfigValue.NullScriptConfigValue)

        assertNull(EveryAccountHasAToken.getError(list(withToken)))
        assertEquals("Every account needs a token.", EveryAccountHasAToken.getError(list(withToken, withoutToken)))
    }
}
