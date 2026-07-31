package com.cereal.sdk.statemodifier

import com.cereal.sdk.models.Secret
import com.cereal.sdk.models.proxy.ProxyGroup

/**
 * Read-only view of the (possibly partial) user-provided configuration while the user is editing it or while a script
 * is executing. Values are accessed via [valueForKey] and returned as strongly typed sealed wrapper instances to
 * distinguish unset from type-mismatched scenarios.
 */
interface ScriptConfig {
    /**
     * Gets a config value for the specified key.
     *
     * @param key the key used in the ScriptConfigurationItem annotation's keyName property.
     *
     * @return [ScriptConfigValue.NullScriptConfigValue] if the user has not yet provided a value,
     *         or one of the other sealed class types if the entered value is parsable.
     *         With the exception of configuration items that have their valuePerTask set to true, the returned sealed
     *         class type depends on the return type of the configuration method.
     *
     *         Double:       DoubleScriptConfigValue
     *         Int:          IntScriptConfigValue
     *         Boolean:      BooleanScriptConfigValue
     *         String:       StringScriptConfigValue
     *         Float:        FloatScriptConfigValue
     *         Enum:         EnumScriptConfigValue
     *         Proxy:        ProxyGroupScriptConfigValue
     *         RandomProxy:  ProxyGroupScriptConfigValue
     *         List<String>: StringListScriptConfigValue
     *         List<T : ScriptConfigurationListItem>: ObjectListScriptConfigValue
     *         Secret:       SecretScriptConfigValue
     *
     *         For configuration items with their valuePerTask set to true [ScriptConfigValue.SequenceScriptConfigValue]
     *         or [ScriptConfigValue.NullScriptConfigValue] is returned. The sequence wraps the variant matching the
     *         item's return type — a per-task Secret item yields a sequence of
     *         [ScriptConfigValue.SecretScriptConfigValue], never a bare one.
     */
    fun valueForKey(key: String): ScriptConfigValue
}

/**
 * Represents the typed value (or sequence of values) associated with a configuration item key.
 * NullScriptConfigValue indicates the user has not yet entered a value.
 */
sealed class ScriptConfigValue {
    /** Double configuration value. */
    data class DoubleScriptConfigValue(
        val value: Double,
    ) : ScriptConfigValue()

    /** Int configuration value. */
    data class IntScriptConfigValue(
        val value: Int,
    ) : ScriptConfigValue()

    /** Boolean configuration value. */
    data class BooleanScriptConfigValue(
        val value: Boolean,
    ) : ScriptConfigValue()

    /** String configuration value. */
    data class StringScriptConfigValue(
        val value: String,
    ) : ScriptConfigValue()

    /** Float configuration value. */
    data class FloatScriptConfigValue(
        val value: Float,
    ) : ScriptConfigValue()

    /** Proxy group configuration value. */
    data class ProxyGroupScriptConfigValue(
        val value: ProxyGroup,
    ) : ScriptConfigValue()

    /** Enum configuration value. */
    data class EnumScriptConfigValue(
        val value: Enum<*>,
    ) : ScriptConfigValue()

    /** String list configuration value. */
    data class StringListScriptConfigValue(
        val values: List<String>,
    ) : ScriptConfigValue()

    /**
     * Complex list configuration value: the rows of a configuration item returning `List<T>` where `T`
     * is a [com.cereal.sdk.ScriptConfigurationListItem].
     *
     * Each row is exposed as its own [ScriptConfig], so a row's field is read with the same
     * [ScriptConfig.valueForKey] call used for top-level items:
     *
     * ```kotlin
     * val rows = (config.valueForKey("targets") as? ObjectListScriptConfigValue)?.items.orEmpty()
     * val firstSku = rows.firstOrNull()?.valueForKey("sku")
     * ```
     *
     * A row omits keys the user left blank, so those read back as
     * [ScriptConfigValue.NullScriptConfigValue].
     */
    data class ObjectListScriptConfigValue(
        val items: List<ScriptConfig>,
    ) : ScriptConfigValue()

    /**
     * Secret configuration value.
     *
     * A state modifier receives the entered secret so it can validate the credential's format in the
     * configuration screen — and distinguish set from unset — rather than the script failing on
     * authentication 30 seconds into a run.
     */
    data class SecretScriptConfigValue(
        val value: Secret,
    ) : ScriptConfigValue()

    /** Sequence of per-task values for an item declared with valuePerTask = true. */
    data class SequenceScriptConfigValue(
        val values: Sequence<ScriptConfigValue>,
    ) : ScriptConfigValue()

    /** Indicates no value has been supplied yet. */
    data object NullScriptConfigValue : ScriptConfigValue()
}
