# README Correctness & Compile-Check Design

**Date:** 2026-04-27
**Scope:** Fix 7 gaps between the README and the real API; add a compile-check test file to prevent future drift.

---

## Problem

The developer README contains code examples that do not match the actual SDK API. A developer following the README will encounter compile errors. The gaps fall into two categories:

- Wrong call syntax (property access vs. method calls, wrong type names, missing wrappers)
- References to things that don't exist (`getAll`, `ShowUrlResult`, `parameters {}` DSL)

## Decision

Fix the README to match the code (code is source of truth). Add a compile-check Kotlin test file so future API changes are caught at build time rather than discovered by users.

---

## Section 1 — README Fixes

Seven targeted edits to `README.md`. No structural changes.

### Gap 1 — `ComponentProvider` accessor style and naming

Every example in the Components section uses property-style access with pluralised names. The real API uses method calls with singular names.

| README (wrong) | Actual API |
|---|---|
| `provider.logger` | `provider.logger()` |
| `provider.notifications` | `provider.notification()` |
| `provider.preferences` | `provider.preference()` |
| `provider.userInteraction` | `provider.userInteraction()` |
| `provider.scriptLauncher` | `provider.scriptLauncher()` |

Fix: update every occurrence in the Components section.

### Gap 2 — `PreferenceComponent.getAll` does not exist

The Preferences section lists `getAll` among supported typed variants. The method is absent from `PreferenceComponent`.

Fix: remove `getAll` from the list.

### Gap 3 — `showUrl` signature

The User Interaction example shows:
```kotlin
provider.userInteraction.showUrl("Login", "https://example.com/login", shouldFinish = true)
```
The real signature is:
```kotlin
suspend fun showUrl(title: String, url: String, shouldFinish: (request: WebResourceRequest) -> Boolean): WebResourceRequest
```
`shouldFinish` is a lambda, not a Boolean.

Fix: replace with a realistic lambda example:
```kotlin
provider.userInteraction().showUrl(
    title = "Login",
    url = "https://example.com/login",
    shouldFinish = { it.url.startsWith("https://example.com/success") },
)
```

### Gap 4 — `scriptLauncher.start` requires `Class`, not `KClass`

The Child Scripts example passes `ChildScript::class` (a `KClass`). The actual parameter type is `Class<out Script<*>>`.

Fix: change to `ChildScript::class.java`.

### Gap 5 — `parameters {}` DSL and `put()` do not exist

The Child Scripts example uses:
```kotlin
parameters {
    put("order_id", "12345")
}
```
`ScriptParameters` has no `parameters {}` builder and no generic `put()`. It has typed methods: `putString`, `putInteger`, `putLong`, etc.

Fix: replace with the real construction pattern:
```kotlin
ScriptParameters().apply {
    putString("order_id", "12345")
}
```

### Gap 6 — `ShowUrlResult` does not exist

The Testing section seeds the factory with:
```kotlin
factory.showUrlResults = listOf(ShowUrlResult(...))
```
The field type is `List<WebResourceRequest>` and `ShowUrlResult` does not exist.

Fix: replace with a real `WebResourceRequest` construction:
```kotlin
factory.showUrlResults = listOf(
    WebResourceRequest(
        method = "GET",
        requestHeaders = emptyMap(),
        url = "https://example.com/success",
        postData = null,
    )
)
```

### Gap 7 — `runner.run` is a suspend function

The Testing section calls `runner.run(config, factory)` bare. `TestScriptRunner.run` is `suspend`, so it must be called inside a coroutine scope.

Fix: wrap in `runTest {}` and add a brief note:

```kotlin
@Test
fun `script succeeds when item is available`() = runTest {
    // ...
    val runner = TestScriptRunner(script)
    runner.run(config, factory)

    assertEquals(ScriptStatus.SUCCESS, runner.status)
}
```

---

## Section 2 — Compile-Check Test File

**Location:** `cereal-sdk/src/test/java/com/cereal/sdk/ReadmeExamples.kt`

**Purpose:** One `@Test` per README code block. Each test does the minimum to make the snippet compile against the real API. The tests do not need to assert behaviour — the compiler is the assertion. If a future API change breaks a signature, the build fails before any user sees it.

**Constraints:**
- Uses `mockk` (already a test dependency, used in `TestScriptApi.kt`) for `ScriptConfiguration` mocks
- Uses `TestComponentProviderFactory` from `cereal-test-utils` for the `ComponentProvider`
- All tests that call `runner.run` are wrapped in `runTest {}`
- The file is marked with a top-level comment explaining its purpose (compile-check, not behaviour-check)

**Tests to include:**

| Test name | Covers |
|---|---|
| `readme script lifecycle compiles` | `Script` interface / lifecycle skeleton |
| `readme configuration annotation compiles` | `@ScriptConfigurationItem`, `ScriptConfiguration` |
| `readme state modifier compiles` | `StateModifier`, `Visibility`, `ScriptConfig.valueForKey` |
| `readme logger compiles` | `provider.logger().info/warn/error/debug` |
| `readme preferences compiles` | `provider.preference().setString/getString` |
| `readme notifications compiles` | `provider.notification().sendNotification`, `notification {}` DSL |
| `readme user interaction compiles` | `provider.userInteraction().showUrl` with lambda, `requestInput`, `showContinueButton` |
| `readme child scripts compiles` | `@ChildScript`, `provider.scriptLauncher().start`, `ScriptParameters` |
| `readme test runner compiles` | `TestScriptRunner`, `TestComponentProviderFactory`, `runTest` |

---

## Out of Scope

- API changes (all fixes are documentation-only)
- Adding a `parameters {}` DSL to `ScriptParameters` (separate decision)
- Fixing KDoc comments inside source files
- Updating `API_COMPATIBILITY.md`
