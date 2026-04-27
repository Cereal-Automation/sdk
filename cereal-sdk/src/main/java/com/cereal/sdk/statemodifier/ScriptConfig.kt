package com.cereal.sdk.statemodifier

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
     *         Double:      DoubleScriptConfigValue
     *         Int:         IntScriptConfigValue
     *         Boolean:     BooleanScriptConfigValue
     *         String:      StringScriptConfigValue
     *         Float:       FloatScriptConfigValue
     *         Proxy:       ProxyGroupScriptConfigValue
     *         RandomProxy: ProxyGroupScriptConfigValue
     *         List<String>: StringListScriptConfigValue
     *
     *         For configuration items with their valuePerTask set to true [ScriptConfigValue.SequenceScriptConfigValue]
     *         or [ScriptConfigValue.NullScriptConfigValue] is returned.
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

    /** Sequence of per-task values for an item declared with valuePerTask = true. */
    data class SequenceScriptConfigValue(
        val values: Sequence<ScriptConfigValue>,
    ) : ScriptConfigValue()

    /** Indicates no value has been supplied yet. */
    data object NullScriptConfigValue : ScriptConfigValue()
}
