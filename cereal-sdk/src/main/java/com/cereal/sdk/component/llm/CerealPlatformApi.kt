package com.cereal.sdk.component.llm

/**
 * Marks SDK API that only the Cereal platform (the client, and `cereal-test-utils`) may use.
 *
 * It guards the construction of [LlmTurn.Assistant], [LlmResponse] and [LlmException.InvalidOutput], and reading
 * [LlmTurn.Assistant.providerPayload]. Script code must not opt in: an assistant turn always comes from a real
 * [LlmResponse], so a script can't prefill one (some models reject that) and provider data such as thinking blocks
 * survives the round trip untouched. To build responses in tests, use the builders in `cereal-test-utils`.
 *
 * @since 1.14.0
 */
@RequiresOptIn(
    message = "Platform-only API (CerealPlatformApi). Scripts get assistant turns and responses from LlmComponent.complete(); tests use cereal-test-utils.",
    level = RequiresOptIn.Level.ERROR,
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY)
annotation class CerealPlatformApi
