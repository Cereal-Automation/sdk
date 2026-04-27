# Cereal Developer SDK

Libraries for building and testing [Cereal](https://cereal-automation.com) scripts.

Full documentation at **[docs.cereal-automation.com](https://docs.cereal-automation.com/)**.

---

## Modules

| Module | Purpose |
|--------|---------|
| `cereal-sdk` | Core framework — implement this in your script |
| `cereal-test-utils` | Test runner and mock components for unit tests |

---

## Setup

Add the Cereal Maven repository and declare the dependencies in your `build.gradle.kts`:

```kotlin
repositories {
    // See docs.cereal-automation.com for the repository URL
    maven("https://...")
}

dependencies {
    implementation("com.cereal-automation:cereal-sdk:<version>")
    testImplementation("com.cereal-automation:cereal-test-utils:<version>")
}
```

---

## Writing a Script

Every script implements `Script<T : ScriptConfiguration>` and goes through three lifecycle phases:

```kotlin
class MyScript : Script<MyScript.Config> {

    override suspend fun onStart(
        configuration: Config,
        provider: ComponentProvider,
    ): Boolean {
        // Called once before execution starts.
        // Return false to abort before execute() is called.
        return true
    }

    override suspend fun execute(
        configuration: Config,
        provider: ComponentProvider,
        statusUpdate: suspend (String) -> Unit,
    ): ExecutionResult {
        statusUpdate("Running…")

        // Return one of:
        //   ExecutionResult.Loop(message, delayMs)  — reschedule after delay (min 50 ms)
        //   ExecutionResult.Success(message)         — finish successfully
        //   ExecutionResult.Error(message)           — finish with error
        return ExecutionResult.Success("Done")
    }

    override suspend fun onFinish(
        configuration: Config,
        provider: ComponentProvider,
    ) {
        // Called once after execution ends (success or error).
    }

    interface Config : ScriptConfiguration {
        @ScriptConfigurationItem(keyName = "target_url", name = "Target URL", position = 0)
        fun targetUrl(): String

        @ScriptConfigurationItem(keyName = "retry_count", name = "Retry count", position = 1)
        fun retryCount(): Int = 3
    }
}
```

---

## Configuration

Declare configuration fields as methods on an interface that extends `ScriptConfiguration`, annotated with `@ScriptConfigurationItem`.

**Supported types:** `String`, `Int`, `Long`, `Double`, `Float`, `Short`, `Byte`, `Boolean`, enums, `Proxy`, `RandomProxy`, `ProxyGroup`, `AccountGroup`, `BillingProfileGroup`.

Key annotation properties:

| Property | Description |
|----------|-------------|
| `keyName` | Stable identifier — **never change after release** |
| `name` | Label shown in the UI |
| `description` | Help text shown in the UI |
| `position` | Display order |
| `valuePerTask` | Allow different values per task |
| `isScriptIdentifier` | Marks which field identifies a script instance |

Provide a **default value** by returning one from the interface method:

```kotlin
fun retryCount(): Int = 3
fun enabled(): Boolean = true
```

### Dynamic visibility with StateModifier

Implement `StateModifier` and reference it via `stateModifier` on `@ScriptConfigurationItem` to conditionally show/hide fields or attach validation errors:

```kotlin
class ShowWhenEnabled : StateModifier {
    override fun getVisibility(config: ScriptConfig): Visibility =
        if (config.valueForKey("enabled") == BooleanScriptConfigValue(true))
            Visibility.VisibleRequired
        else
            Visibility.Hidden

    override fun getError(config: ScriptConfig): String? = null
}
```

---

## Components

Access runtime services through `ComponentProvider`:

### Logger

```kotlin
provider.logger.info("message")
provider.logger.warn("message")
provider.logger.error("message")
provider.logger.debug("message")
```

### Preferences (persistent key-value store)

```kotlin
provider.preferences.setString("key", "value")
val value = provider.preferences.getString("key")
```

Typed variants: `getString/setString`, `getInt/setInt`, `getLong/setLong`, `getFloat/setFloat`, `getBoolean/setBoolean`, `delete`, `getAll`.

### Notifications

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

### User Interaction

```kotlin
// Open a URL in a WebView
provider.userInteraction.showUrl("Login", "https://example.com/login", shouldFinish = true)

// Ask the user for text input
val input = provider.userInteraction.requestInput("Enter code", "Check your email")

// Show a continue button the user must click to proceed
provider.userInteraction.showContinueButton()
```

### Child Scripts

Annotate secondary scripts with `@ChildScript` and launch them via `ScriptLauncherComponent`:

```kotlin
@ChildScript(id = "stable_child_id", name = "My Child Script")
class ChildScript : Script<ChildScript.Config> { ... }

// In parent script:
val handle = provider.scriptLauncher.start(ChildScript::class, parameters {
    put("order_id", "12345")
})
```

> `@ChildScript.id` must be stable — **never change it after release**.

---

## Testing

Use `cereal-test-utils` to test scripts without the Cereal runtime.

```kotlin
@Test
fun `script succeeds when item is available`() {
    val script = MyScript()
    val config = mockk<MyScript.Config> {
        every { targetUrl() } returns "https://example.com"
        every { retryCount() } returns 1
    }

    val factory = TestComponentProviderFactory()
    // Optionally seed mock responses:
    // factory.requestInputResults = listOf("myinput")
    // factory.showUrlResults = listOf(ShowUrlResult(...))

    val runner = TestScriptRunner(script)
    runner.run(config, factory)

    assertEquals(ScriptStatus.SUCCESS, runner.status)
}
```

`TestComponentProviderFactory` provides in-memory implementations of all components. Logged messages, stored preferences, and sent notifications are all accessible after the run for assertions.

---

## API Stability

`cereal-sdk` tracks its public API via the [Kotlin Binary Compatibility Validator](https://github.com/Kotlin/binary-compatibility-validator). Symbols annotated with `@InternalApi` are excluded from stability guarantees — do not depend on them.
