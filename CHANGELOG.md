# Changelog

## 1.14.0

- Added `LlmComponent` (`com.cereal.sdk.component.llm`), reached through `ComponentProvider.llm()`: scripts call the model the user configured in Settings → AI with `complete(LlmRequest): LlmResponse`, without handling the API key. Includes `LlmRequest`, `LlmTurn`, `UserPart`, `ImageMediaType`, `LlmTool`, `LlmToolCall`, `ToolChoice`, `LlmResponse`, `StopReason`, `LlmUsage`, the sealed `LlmException`, and the `@CerealPlatformApi` opt-in that keeps assistant turns and responses platform-built.
- **Scripts using `llm()` must set `"sdk_version": "1.14.0"` (or higher) in `manifest.json`.** The field is optional and nothing fills it in; without it, clients older than 1.14.0 fail the script at runtime instead of asking the user to update.
- `ComponentProvider` gained an abstract method. Custom `ComponentProvider` implementations (outside `cereal-test-utils`) must add `llm()`; scripts built on older SDKs keep working.
