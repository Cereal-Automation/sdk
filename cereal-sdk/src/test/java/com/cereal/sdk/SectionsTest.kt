package com.cereal.sdk

import com.cereal.sdk.testscript.sections.SectionsConfiguration
import com.cereal.sdk.testscript.sections.SectionsScript
import com.cereal.test.ScriptStatus
import com.cereal.test.TestScriptRunner
import com.cereal.test.components.TestComponentProviderFactory
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SectionsTest {
    private val configuration =
        object : SectionsConfiguration {
            override fun profileName() = "Main"

            override fun email() = "a@b.c"

            override fun retries() = 5
        }

    @Test
    fun `script with sections runs and reads its values unchanged`() =
        runBlocking {
            val script = SectionsScript()
            val runner = TestScriptRunner(script)

            runner.run(configuration, TestComponentProviderFactory())

            assertEquals(ScriptStatus.Success, runner.status)
            assertEquals(listOf("Main", "a@b.c", "5"), script.seen)
        }

    @Test
    fun `declared sections and field assignments are readable at runtime with keyNames unchanged`() {
        val sections = SectionsConfiguration::class.java.getAnnotation(ScriptConfigurationSections::class.java)!!.value
        assertEquals(
            listOf(Triple("account", "Account", false), Triple("advanced", "Advanced", true)),
            sections.map { Triple(it.key, it.name, it.collapsed) },
        )

        val sectionByKeyName =
            SectionsConfiguration::class.java.methods
                .mapNotNull { it.getAnnotation(ScriptConfigurationItem::class.java) }
                .associate { it.keyName to it.section }
        assertEquals(mapOf("profile_name" to "", "email" to "account", "retries" to "advanced"), sectionByKeyName)
    }
}
