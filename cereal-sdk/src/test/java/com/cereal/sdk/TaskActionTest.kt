package com.cereal.sdk

import com.cereal.sdk.testscript.TestScript
import com.cereal.sdk.testscript.taskaction.TaskActionConfiguration
import com.cereal.sdk.testscript.taskaction.TaskActionScript
import com.cereal.test.TestScriptRunner
import com.cereal.test.components.TestComponentProviderFactory
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TaskActionTest {
    @Test
    fun `task action runs against the task's preferences and returns its message`() =
        runBlocking {
            val factory = TestComponentProviderFactory()
            // TestPreferenceComponent storage is shared by all providers, standing in for the task's own preferences.
            val preferences = factory.create().preference()
            preferences.setString("task-action-test-cache", "learned steps")
            val configuration = mockk<TaskActionConfiguration> { every { cacheKey() } returns "task-action-test-cache" }

            val message = TestScriptRunner(TaskActionScript()).runTaskAction("Forget learned steps", configuration, factory)

            assertEquals("Forgot learned steps", message)
            assertNull(preferences.getString("task-action-test-cache"))
        }

    @Test
    fun `scripts without task actions offer none`() {
        assertTrue(TestScript().taskActions().isEmpty())
    }
}
