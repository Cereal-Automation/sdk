# Changelog

## Unreleased

- Docs: scripts that call `llm()` must declare the `llm` Script feature with `"features": ["llm"]` in `manifest.json`. Clients that support it fail undeclared calls with an error telling the author to add it. It doesn't replace the `sdk_version` requirement (`1.14.0` or higher), because older clients ignore `features`.

## 1.15.0

- **Multiple entry scripts:** clients bundling 1.15.0 read the manifest's `scripts`, a list of entry scripts (each with `id`, `name`, `script` and optional `instructions`) the user picks from. With `scripts` they are the only entries, so the top-level `script` and `instructions` can be left out and the main script is listed in `scripts` too. Packages with `scripts` must set `"sdk_version": "1.15.0"` (or higher); older clients can't read a manifest without `script` and don't list the package. See the manifest docs.
- **Removed** fetched field options (`FieldOptions`, `FieldOption`, `NoFieldOptions` and the `options` attribute on `@ScriptConfigurationItem`) and task actions (`TaskAction`, `Script.taskActions()` and `TestScriptRunner.runTaskAction`), both added in 1.13.0. No released script used them. `StateModifier.getPlaceholder`, `@ScriptConfigurationSections` and `Secret` fields in list rows stay.

## 1.14.0

- Added `LlmComponent` (`com.cereal.sdk.component.llm`), reached through `ComponentProvider.llm()`: scripts call the model the user configured in Settings → AI with `complete(LlmRequest): LlmResponse`, without handling the API key. Includes `LlmRequest`, `LlmTurn`, `UserPart`, `ImageMediaType`, `LlmTool`, `LlmToolCall`, `ToolChoice`, `LlmResponse`, `StopReason`, `LlmUsage`, the sealed `LlmException`, and the `@CerealPlatformApi` opt-in that keeps assistant turns and responses platform-built.
- **Scripts using `llm()` must set `"sdk_version": "1.14.0"` (or higher) in `manifest.json`.** The field is optional and nothing fills it in; without it, clients older than 1.14.0 fail the script at runtime instead of asking the user to update.
- `ComponentProvider` gained an abstract method. Custom `ComponentProvider` implementations (outside `cereal-test-utils`) must add `llm()`; scripts built on older SDKs keep working.
