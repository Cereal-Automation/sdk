# README Correctness Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Fix 9 mismatches between README.md code examples and the real cereal-sdk API, then add a compile-check test file that will fail the build if a future API change breaks any README snippet.

**Architecture:** Two tasks, no shared state between them. Task 1 is pure documentation. Task 2 creates a single new test file that calls every README API. All compile-check tests use `runBlocking` (already a test dependency — no new dependencies needed). Child-script and full-lifecycle tests reuse existing test fixtures (`TestChildScript`, `TestChildConfiguration`).

**Tech Stack:** Kotlin, JUnit 4 (`org.junit.Test`), MockK (`io.mockk`), `kotlinx.coroutines.runBlocking`, `cereal-test-utils` (`TestScriptRunner`, `TestComponentProviderFactory`).

---

## File Map

| Action | File |
|---|---|
| Modify | `README.md` |
| Create | `cereal-sdk/src/test/java/com/cereal/sdk/ReadmeExamples.kt` |

---

### Task 1: Fix all 9 README gaps

**Files:**
- Modify: `README.md`

The README lives at the repo root. Apply all 9 edits in a single pass. Each edit is described with the exact old text and the exact replacement text.

---

- [ ] **Step 1: Fix Gap 1 — Logger accessor (property → method call)**

Find this block in the `### Logger` section:
```kotlin
provider.logger.info("message")
provider.logger.warn("message")
provider.logger.error("message")
provider.logger.debug("message")
```
Replace with:
```kotlin
provider.logger().info("message")
provider.logger().warn("message")
provider.logger().error("message")
provider.logger().debug("message")
```

---

- [ ] **Step 2: Fix Gap 2 — Preferences accessor (property + plural → method + singular)**

Find this block in the `### Preferences` section:
```kotlin
provider.preferences.setString("key", "value")
val value = provider.preferences.getString("key")
```
Replace with:
```kotlin
provider.preference().setString("key", "value")
val value = provider.preference().getString("key")
```

---

- [ ] **Step 3: Fix Gap 3 — Remove non-existent `getAll` from preferences list**

Find this sentence:
```
Typed variants: `getString/setString`, `getInt/setInt`, `getLong/setLong`, `getFloat/setFloat`, `getBoolean/setBoolean`, `delete`, `getAll`.
```
Replace with:
```
Typed variants: `getString/setString`, `getInt/setInt`, `getLong/setLong`, `getFloat/setFloat`, `getBoolean/setBoolean`, `delete`.
```

---

- [ ] **Step 4: Fix Gap 4 — Notifications accessor (property + plural → method + singular) and Telegram parseMode type**

Find this block in the `### Notifications` section:
```kotlin
provider.notifications.sendNotification(
    notification("Checkout complete") {
        title { "Order placed" }
        discordMessage {
            content { "Successfully checked out." }
        }
        telegramMessage {
            text { "Successfully checked out." }
            parseMode { "HTML" }
        }
    }
)
```
Replace with:
```kotlin
provider.notification().sendNotification(
    notification("Checkout complete") {
        title { "Order placed" }
        discordMessage {
            content { "Successfully checked out." }
        }
        telegramMessage {
            text { "Successfully checked out." }
            parseMode { TelegramParseMode.HTML }
        }
    }
)
```

---

- [ ] **Step 5: Fix Gap 5 — User interaction accessor + showUrl signature**

Find this block in the `### User Interaction` section:
```kotlin
// Open a URL in a WebView
provider.userInteraction.showUrl("Login", "https://example.com/login", shouldFinish = true)

// Ask the user for text input
val input = provider.userInteraction.requestInput("Enter code", "Check your email")

// Show a continue button the user must click to proceed
provider.userInteraction.showContinueButton()
```
Replace with:
```kotlin
// Open a URL in a WebView
provider.userInteraction().showUrl(
    title = "Login",
    url = "https://example.com/login",
    shouldFinish = { it.url.startsWith("https://example.com/success") },
)

// Ask the user for text input
val input = provider.userInteraction().requestInput("Enter code", "Check your email")

// Show a continue button the user must click to proceed
provider.userInteraction().showContinueButton()
```

---

- [ ] **Step 6: Fix Gap 6 — Child scripts: accessor, KClass → Class, and parameters DSL**

Find this block in the `### Child Scripts` section:
```kotlin
// In parent script:
val handle = provider.scriptLauncher.start(ChildScript::class, parameters {
    put("order_id", "12345")
})
```
Replace with:
```kotlin
// In parent script:
val parameters = ScriptParameters().apply {
    putString("order_id", "12345")
}
val handle = provider.scriptLauncher().start(ChildScript::class.java, parameters)
```

---

- [ ] **Step 7: Fix Gap 7 — Testing section: ShowUrlResult → WebResourceRequest**

Find this comment block in the `## Testing` section:
```kotlin
// factory.requestInputResults = listOf("myinput")
// factory.showUrlResults = listOf(ShowUrlResult(...))
```
Replace with:
```kotlin
// factory.requestInputResults = listOf("myinput")
// factory.showUrlResults = listOf(
//     WebResourceRequest(method = "GET", requestHeaders = emptyMap(), url = "https://...", postData = null)
// )
```

---

- [ ] **Step 8: Fix Gap 8 — Testing section: runner.run needs a coroutine scope**

Find this in the `## Testing` section:
```kotlin
@Test
fun `script succeeds when item is available`() {
    val script = MyScript()
```
Replace with:
```kotlin
@Test
fun `script succeeds when item is available`() = runBlocking {
    val script = MyScript()
```

---

- [ ] **Step 9: Fix Gap 9 — Testing section: ScriptStatus.SUCCESS → ScriptStatus.Success**

Find this in the `## Testing` section:
```kotlin
assertEquals(ScriptStatus.SUCCESS, runner.status)
```
Replace with:
```kotlin
assertEquals(ScriptStatus.Success, runner.status)
```

---

- [ ] **Step 10: Commit**

```bash
git add README.md
git commit -m "fix: correct 9 README code examples to match actual SDK API"
```

---

### Task 2: Add compile-check test file

**Files:**
- Create: `cereal-sdk/src/test/java/com/cereal/sdk/ReadmeExamples.kt`

No new Gradle dependencies are needed. `runBlocking`, `mockk`, `kotlin.test`, and `cereal-test-utils` are already declared in `cereal-sdk/cereal-sdk.gradle.kts`.

---

- [ ] **Step 1: Create `ReadmeExamples.kt`**

Create `cereal-sdk/src/test/java/com/cereal/sdk/ReadmeExamples.kt` with the following content:

```kotlin
package com.cereal.sdk

// Compile-check tests for README.md code examples.
// These tests verify that every README code block compiles against the real API.
// Runtime assertions are intentionally absent — the Kotlin compiler is the assertion.

import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.ScriptConfiguration
import com.cereal.sdk.ScriptConfigurationItem
import com.cereal.sdk.component.ComponentProvider
import com.cereal.sdk.component.notification.notification
import com.cereal.sdk.component.notification.telegram.model.TelegramParseMode
import com.cereal.sdk.component.script.ScriptParameters
import com.cereal.sdk.component.userinteraction.WebResourceRequest
import com.cereal.sdk.statemodifier.ScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue.BooleanScriptConfigValue
import com.cereal.sdk.statemodifier.StateModifier
import com.cereal.sdk.statemodifier.Visibility
import com.cereal.sdk.testscript.child.TestChildConfiguration
import com.cereal.sdk.testscript.child.TestChildScript
import com.cereal.test.TestScriptRunner
import com.cereal.test.components.TestComponentProviderFactory
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ReadmeExamples {

    // ------------------------------------------------------------------
    // Shared config interface matching the README "Writing a Script" example
    // ------------------------------------------------------------------

    interface ReadmeConfig : ScriptConfiguration {
        @ScriptConfigurationItem(
            keyName = "target_url",
            name = "Target URL",
            description = "URL the script will operate on",
            position = 0,
        )
        fun targetUrl(): String

        @ScriptConfigurationItem(
            keyName = "retry_count",
            name = "Retry count",
            description = "How many times to retry on failure",
            position = 1,
        )
        fun retryCount(): Int
    }

    // ------------------------------------------------------------------
    // Script lifecycle
    // ------------------------------------------------------------------

    @Test
    fun `readme script lifecycle compiles`() =
        runBlocking {
            val script =
                object : Script<ReadmeConfig> {
                    override suspend fun onStart(
                        configuration: ReadmeConfig,
                        provider: ComponentProvider,
                    ): Boolean = true

                    override suspend fun execute(
                        configuration: ReadmeConfig,
                        provider: ComponentProvider,
                        statusUpdate: suspend (String) -> Unit,
                    ): ExecutionResult {
                        statusUpdate("Running…")
                        return ExecutionResult.Success("Done")
                    }

                    override suspend fun onFinish(
                        configuration: ReadmeConfig,
                        provider: ComponentProvider,
                    ) {}
                }
            val config =
                mockk<ReadmeConfig> {
                    every { targetUrl() } returns "https://example.com"
                    every { retryCount() } returns 1
                }
            val runner = TestScriptRunner(script)
            runner.run(config, TestComponentProviderFactory())
        }

    // ------------------------------------------------------------------
    // StateModifier
    // ------------------------------------------------------------------

    @Test
    fun `readme state modifier compiles`() {
        class ShowWhenEnabled : StateModifier {
            override fun getVisibility(config: ScriptConfig): Visibility =
                if (config.valueForKey("enabled") == BooleanScriptConfigValue(true)) {
                    Visibility.VisibleRequired
                } else {
                    Visibility.Hidden
                }

            override fun getError(config: ScriptConfig): String? = null
        }
        @Suppress("UNUSED_VARIABLE")
        val modifier = ShowWhenEnabled()
    }

    // ------------------------------------------------------------------
    // Logger
    // ------------------------------------------------------------------

    @Test
    fun `readme logger compiles`() =
        runBlocking {
            val provider = TestComponentProviderFactory().create()
            provider.logger().info("message")
            provider.logger().warn("message")
            provider.logger().error("message")
            provider.logger().debug("message")
        }

    // ------------------------------------------------------------------
    // Preferences
    // ------------------------------------------------------------------

    @Test
    fun `readme preferences compiles`() =
        runBlocking {
            val provider = TestComponentProviderFactory().create()
            provider.preference().setString("key", "value")
            @Suppress("UNUSED_VARIABLE")
            val value = provider.preference().getString("key")
        }

    // ------------------------------------------------------------------
    // Notifications
    // ------------------------------------------------------------------

    @Test
    fun `readme notifications compiles`() =
        runBlocking {
            val provider = TestComponentProviderFactory().create()
            provider.notification().sendNotification(
                notification("Checkout complete") {
                    title { "Order placed" }
                    discordMessage {
                        content { "Successfully checked out." }
                    }
                    telegramMessage {
                        text { "Successfully checked out." }
                        parseMode { TelegramParseMode.HTML }
                    }
                },
            )
        }

    // ------------------------------------------------------------------
    // User interaction — showUrl
    // ------------------------------------------------------------------

    @Test
    fun `readme user interaction showUrl compiles`() =
        runBlocking {
            val factory = TestComponentProviderFactory()
            factory.showUrlResults =
                listOf(
                    WebResourceRequest(
                        method = "GET",
                        requestHeaders = emptyMap(),
                        url = "https://example.com/success",
                        postData = null,
                    ),
                )
            val provider = factory.create()
            provider.userInteraction().showUrl(
                title = "Login",
                url = "https://example.com/login",
                shouldFinish = { it.url.startsWith("https://example.com/success") },
            )
        }

    // ------------------------------------------------------------------
    // User interaction — requestInput
    // ------------------------------------------------------------------

    @Test
    fun `readme user interaction requestInput compiles`() =
        runBlocking {
            val factory = TestComponentProviderFactory()
            factory.requestInputResults = listOf("myinput")
            val provider = factory.create()
            @Suppress("UNUSED_VARIABLE")
            val input = provider.userInteraction().requestInput("Enter code", "Check your email")
        }

    // ------------------------------------------------------------------
    // User interaction — showContinueButton
    // ------------------------------------------------------------------

    @Test
    fun `readme user interaction showContinueButton compiles`() =
        runBlocking {
            val provider = TestComponentProviderFactory().create()
            provider.userInteraction().showContinueButton()
        }

    // ------------------------------------------------------------------
    // Child scripts — ScriptParameters construction and start() signature
    // ------------------------------------------------------------------

    @Test
    fun `readme child scripts compiles`() =
        runBlocking {
            val childConfig =
                mockk<TestChildConfiguration> {
                    every { keyString() } returns "test"
                }
            val factory = TestComponentProviderFactory()
            factory.childScriptConfigurations = mapOf(TestChildScript::class.java to childConfig)
            val provider = factory.create()
            val parameters =
                ScriptParameters().apply {
                    putString("order_id", "12345")
                }
            provider.scriptLauncher().start(TestChildScript::class.java, parameters)
        }

    // ------------------------------------------------------------------
    // Test runner
    // ------------------------------------------------------------------

    @Test
    fun `readme test runner compiles`() =
        runBlocking {
            val script =
                object : Script<ReadmeConfig> {
                    override suspend fun onStart(
                        configuration: ReadmeConfig,
                        provider: ComponentProvider,
                    ): Boolean = true

                    override suspend fun execute(
                        configuration: ReadmeConfig,
                        provider: ComponentProvider,
                        statusUpdate: suspend (String) -> Unit,
                    ): ExecutionResult = ExecutionResult.Success("Done")

                    override suspend fun onFinish(
                        configuration: ReadmeConfig,
                        provider: ComponentProvider,
                    ) {}
                }
            val config =
                mockk<ReadmeConfig> {
                    every { targetUrl() } returns "https://example.com"
                    every { retryCount() } returns 1
                }
            val factory = TestComponentProviderFactory()
            val runner = TestScriptRunner(script)
            runner.run(config, factory)
        }
}
```

---

- [ ] **Step 2: Run the tests to confirm they compile and pass**

```bash
cd /path/to/sdk && ./gradlew :cereal-sdk:test
```

Expected: `BUILD SUCCESSFUL` with all tests in `ReadmeExamples` passing. If any test fails, the error message will name the API mismatch — fix it before continuing.

---

- [ ] **Step 3: Commit**

```bash
git add cereal-sdk/src/test/java/com/cereal/sdk/ReadmeExamples.kt
git commit -m "test: add compile-check tests for README code examples"
```
