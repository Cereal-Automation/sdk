package com.cereal.sdk.models

/**
 * A credential supplied by the user — an API key, a session token, a webhook secret.
 *
 * Return this from a `@ScriptConfigurationItem` function instead of [String] to mark the value as
 * sensitive. The platform then renders the input field masked, renders the value as `***` wherever
 * configuration is summarised, and keeps it out of anything built from [toString].
 *
 * Reaching the plaintext requires an explicit [reveal] call, so every place a credential is unwrapped
 * is findable with a single search:
 *
 * ```kotlin
 * interface MyConfiguration : ScriptConfiguration {
 *     @ScriptConfigurationItem(keyName = "api_key", name = "API key", description = "...")
 *     fun apiKey(): Secret
 * }
 *
 * // At execution time:
 * val client = ApiClient(token = configuration.apiKey().reveal())
 * ```
 *
 * **What this does not do.** It is not what encrypts the value — every configuration value is
 * already encrypted at rest under the user's key; [Secret] controls where a value may *appear*.
 * [reveal] is unguarded, so what a script does with the plaintext afterwards is the script's
 * business. What the type prevents is the *accidental* leak: the interpolated status message, the
 * whole-configuration debug dump.
 *
 * Default values are not supported for secrets — a default returning a credential would be a
 * hardcoded secret in source. A secret also cannot be the script identifier.
 *
 * @param value The plaintext credential.
 */
class Secret(
    private val value: String,
) {
    /**
     * Returns the plaintext credential.
     *
     * Deliberately a distinct verb so that every unwrapping site is greppable.
     */
    fun reveal(): String = value

    /** Returns a fixed mask. Never returns the wrapped value. */
    override fun toString(): String = MASK

    /**
     * Value-based equality.
     *
     * Deliberately not constant-time: the type is used for local configuration change detection, not
     * for authenticating a credential, so timing-safe comparison would be ceremony without a threat.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Secret) return false
        return value == other.value
    }

    override fun hashCode(): Int = value.hashCode()

    private companion object {
        private const val MASK = "***"
    }
}
