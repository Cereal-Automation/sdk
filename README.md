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
        @ScriptConfigurationItem(keyName = "target_url", name = "Target URL", description = "URL the script will operate on", position = 0)
        fun targetUrl(): String

        @ScriptConfigurationItem(keyName = "retry_count", name = "Retry count", description = "How many times to retry on failure", position = 1)
        fun retryCount(): Int = 3
    }
}
```

---

## Configuration

Declare configuration fields as methods on an interface that extends `ScriptConfiguration`, annotated with `@ScriptConfigurationItem`.

**Supported types:** `String`, `Int`, `Float`, `Double`, `Boolean`, enums, `List<String>`,
`List<T : ScriptConfigurationListItem>` (see [Complex lists](#complex-lists)), `Proxy`, `RandomProxy`.

This list is exhaustive — any other return type (including `Long`, `Short` and `Byte`) is rejected when the client
loads the script, as is any other `List` element type. Of these, only `String`, `Int`, `Float` and `Double` may be
combined with `valuePerTask = true`.

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

### Complex lists

Requires client SDK **1.11.0** or newer.

When you need a list of *records* rather than a list of strings, declare the element type as an interface extending
`ScriptConfigurationListItem` and annotate each field with `@ScriptConfigurationItem`. The client renders one card per
row with the right widget per field and hands your script typed objects:

```kotlin
interface Target : ScriptConfigurationListItem {
    @ScriptConfigurationItem(keyName = "sku", name = "SKU", description = "Product identifier")
    fun sku(): String

    @ScriptConfigurationItem(keyName = "qty", name = "Quantity", description = "How many to buy")
    fun qty(): Int

    @ScriptConfigurationItem(keyName = "notify", name = "Notify", description = "Send a notification on success")
    fun notify(): Boolean?
}

interface PurchaseConfig : ScriptConfiguration {
    @ScriptConfigurationItem(keyName = "targets", name = "Targets", description = "Products to purchase")
    fun targets(): List<Target>
}
```

Read it in your script with no parsing at all:

```kotlin
for (target in configuration.targets()) {
    provider.logger().info("Buying ${target.qty()} x ${target.sku()}")
}
```

**Permitted field types:** `String`, `Int`, `Float`, `Double`, `Boolean`, enums, and their nullable forms. `Proxy`,
`RandomProxy`, nested lists and nested record types are rejected when the client loads the script.

**Cardinality and nullability:** a non-nullable `List<T>` always holds at least one row at runtime. A nullable
`List<T>?` arrives as `null` when the user supplied no rows. A nullable field within a row arrives as `null` when left
blank. Express a minimum or maximum row count through the list item's own `stateModifier`, which can count the rows.

**Concurrency:** the whole list is one configuration value for one script run and every task receives all of it. A
complex list never fans out into one task per row — that stays the job of `valuePerTask` / Task data. Splitting the
rows across tasks is your script's business.

**Rejected when the client loads the script:** `valuePerTask` or `isScriptIdentifier` on a complex-list item; a
`stateModifier` on a field inside a record (put it on the list item instead); a default implementation on a
complex-list item (pre-seeded rows are not supported); an unsupported field type; and two fields sharing a `keyName`.
Field keys are namespaced under the list item's key, so a record field may reuse a top-level item's key name.

Read the rows from a `StateModifier` through `ObjectListScriptConfigValue`, whose items are themselves `ScriptConfig`
views — so a row's field is read with the same `valueForKey` call used for top-level items:

```kotlin
object AtMostTenTargets : StateModifier {
    override fun getVisibility(config: ScriptConfig): Visibility = Visibility.VisibleRequired

    override fun getError(config: ScriptConfig): String? {
        val rows = (config.valueForKey("targets") as? ObjectListScriptConfigValue)?.items.orEmpty()
        return if (rows.size > 10) "At most 10 targets are allowed." else null
    }
}
```

In your own unit tests, supply rows by overriding the configuration method — complex lists have no default values:

```kotlin
val configuration = object : PurchaseConfig {
    override fun targets(): List<Target> =
        listOf(
            object : Target {
                override fun sku() = "ABC-123"

                override fun qty() = 2

                override fun notify() = true
            },
        )
}
```

### Dynamic visibility with StateModifier

Implement `StateModifier` and reference it via `stateModifier` on `@ScriptConfigurationItem` to conditionally show/hide fields or attach validation errors.

It must be declared as an `object`, not a `class` — the client reads the singleton instance and fails to load a script whose state modifier is a class:

```kotlin
object ShowWhenEnabled : StateModifier {
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
provider.logger().info("message")
provider.logger().warn("message")
provider.logger().error("message")
provider.logger().debug("message")
```

### Preferences (persistent key-value store)

```kotlin
provider.preference().setString("key", "value")
val value = provider.preference().getString("key")
```

Typed variants: `getString/setString`, `getInt/setInt`, `getLong/setLong`, `getFloat/setFloat`, `getBoolean/setBoolean`, `delete`.

### Notifications

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

### Artifacts (downloadable file output)

Produce files the user can download from the app after the run. Each `emit` appends a new artifact to the current
task; it never overwrites a previous one.

```kotlin
val csv = buildString {
    appendLine("sku,price")
    appendLine("ABC-1,19.99")
}.toByteArray()

provider.artifact().emit(
    name = "results.csv",
    bytes = csv,
    mimeType = "text/csv", // optional; inferred from the name's extension when omitted
)
```

The payload is held in memory, so this is meant for small, bounded outputs. `emit` throws if the host fails to
persist the artifact — wrap the call yourself if the artifact is optional.

### User Interaction

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

### Child Scripts

Annotate secondary scripts with `@ChildScript` and launch them via `ScriptLauncherComponent`:

```kotlin
@ChildScript(id = "stable_child_id", name = "My Child Script")
class ChildScript : Script<ChildScript.Config> { ... }

// In parent script:
val parameters = ScriptParameters().apply {
    putString("order_id", "12345")
}
val handle = provider.scriptLauncher().start(ChildScript::class.java, parameters)
```

> `@ChildScript.id` must be stable — **never change it after release**.

---

## Testing

Use `cereal-test-utils` to test scripts without the Cereal runtime.

```kotlin
@Test
fun `script succeeds when item is available`() = runBlocking {
    val script = MyScript()
    val config = mockk<MyScript.Config> {
        every { targetUrl() } returns "https://example.com"
        every { retryCount() } returns 1
    }

    val factory = TestComponentProviderFactory()
    // Optionally seed mock responses:
    // factory.requestInputResults = listOf("myinput")
    // factory.showUrlResults = listOf(
    //     WebResourceRequest(method = "GET", requestHeaders = emptyMap(), url = "https://...", postData = null)
    // )

    val runner = TestScriptRunner(script)
    runner.run(config, factory)

    assertEquals(ScriptStatus.Success, runner.status)
}
```

`TestComponentProviderFactory` provides in-memory implementations of all components. Artifacts are the only output it collects for assertions, via `RecordingArtifactComponent.emitted` — the bundled logger and notification components write to the console rather than to an inspectable list.

`TestComponentProviderFactory` creates its `RecordingArtifactComponent` internally and does not expose it, so to assert on emitted artifacts, supply your own through a factory that delegates everything else to the default one:

```kotlin
val artifacts = RecordingArtifactComponent()

val componentProviderFactory = object : ComponentProviderFactory {
    private val delegate = TestComponentProviderFactory().create()

    override fun create(): ComponentProvider =
        object : ComponentProvider by delegate {
            override fun artifact(): ArtifactComponent = artifacts
        }
}

scriptRunner.run(configuration, componentProviderFactory)

assertEquals("results.csv", artifacts.emitted.single().name)
```

Note that `TestPreferenceComponent` keeps its values in a store shared across instances, so preference state written by one test is visible to the next.

---

## API Stability

`cereal-sdk` tracks its public API via the [Kotlin Binary Compatibility Validator](https://github.com/Kotlin/binary-compatibility-validator). Symbols annotated with `@InternalApi` are excluded from stability guarantees — do not depend on them.
