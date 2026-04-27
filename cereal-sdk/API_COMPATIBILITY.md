# API Binary Compatibility Validation

This module uses the [Kotlin Binary Compatibility Validator](https://github.com/Kotlin/binary-compatibility-validator)
to ensure backward compatibility of the public API in the `com.cereal.sdk` package.

## What is it?

The binary compatibility validator tracks the public API of the SDK and ensures that changes don't break backward
compatibility for existing users. It generates an API signature file (`api/cereal-sdk.api`) that captures all public
declarations.

## How it works

The plugin:

1. Generates a `.api` file containing all public API signatures
2. Validates that changes to the public API are intentional and documented
3. Prevents accidental breaking changes to the SDK

## Tasks

### `apiCheck`

Validates that the current public API matches the recorded API file.

```bash
./gradlew :cereal-sdk:apiCheck
```

This task runs automatically in CI for all PRs and pushes to master.

### `apiDump`

Updates the API file with the current public API. Use this when you intentionally make API changes.

```bash
./gradlew :cereal-sdk:apiDump
```

## Making API Changes

When you make changes to the public API:

1. Make your code changes
2. Run `./gradlew :cereal-sdk:apiCheck` - this will fail if the API changed
3. Review the changes carefully to ensure they're intentional
4. Run `./gradlew :cereal-sdk:apiDump` to update the API file
5. Commit both your code changes AND the updated `api/cereal-sdk.api` file
6. Document the API changes in your PR description

## Best Practices

- **Avoid breaking changes**: Try to maintain backward compatibility
- **Add, don't remove**: Add new functions/classes rather than modifying existing ones
- **Deprecate first**: Mark APIs as `@Deprecated` before removing them
- **Review carefully**: Always review API file changes in PRs

## Configuration

The validator is configured in `cereal-sdk.gradle.kts`:

```kotlin
apiValidation {
    validationDisabled = false
    nonPublicMarkers.add("com.cereal.sdk.internal.InternalApi")
}
```

APIs marked with `@InternalApi` are excluded from validation, allowing internal APIs to change freely.

## CI Integration

The API check runs on:

- All pull requests
- All pushes to master branch

If the check fails, you must either:

1. Fix the breaking change, OR
2. Update the API file with `apiDump` if the change is intentional

