package com.cereal.sdk

import com.cereal.sdk.statemodifier.DefaultStateModifier
import com.cereal.sdk.statemodifier.ScriptConfig
import com.cereal.sdk.statemodifier.StateModifier
import kotlin.reflect.KClass

/**
 * Marker interface for a scripts configuration definition. Implementations declare one or more
 * functions annotated with [ScriptConfigurationItem] whose return type indicates the expected
 * value type. The platform reflects over these functions to build the configuration UI.
 */
interface ScriptConfiguration

/**
 * Describes a single configurable value for a script.
 *
 * Annotate zero or more parameterless functions on your [ScriptConfiguration] implementation with this annotation.
 * The return type of the function determines how user input is validated and parsed. Supported return types are:
 *  - Primitive numeric types (Int, Long, Double, Float, Short, Byte)
 *  - Boolean
 *  - String
 *  - Enum subclasses (the platform will expose a selection list of enum constants)
 *  - Proxy / RandomProxy / ProxyGroup, AccountGroup, BillingProfileGroup (special selectors)
 *
 * **Default Values:**
 * You can provide a default value for configuration items by using a default implementation in the interface.
 * The default value will be pre-populated in the configuration UI when users create a new configuration.
 * Supported types for default values: Boolean, String, Int, Float, Double, and Enum.
 *
 * Example:
 * ```kotlin
 * interface MyConfiguration : ScriptConfiguration {
 *     @ScriptConfigurationItem(keyName = "maxRetries", name = "Max Retries", description = "...")
 *     fun maxRetries(): Int = 3  // Default value of 3
 *
 *     @ScriptConfigurationItem(keyName = "mode", name = "Mode", description = "...")
 *     fun mode(): RunMode = RunMode.FAST  // Default enum value
 * }
 * ```
 *
 * For sequence-based input (per-task values) set [valuePerTask] = true and return a supported singular type; at
 * execution time [ScriptConfig.valueForKey] will yield a [com.cereal.sdk.statemodifier.ScriptConfigValue.SequenceScriptConfigValue].
 *
 * Keep [keyName] stable across releases; changing it resets persisted values. Only one item may set
 * [isScriptIdentifier] = true (ignored for per-task or child script items).
 *
 * Use [stateModifier] to dynamically hide, disable, or validate an item based on other inputs.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ScriptConfigurationItem(
    val position: Int = -1,
    val keyName: String,
    val name: String,
    val description: String,
    val valuePerTask: Boolean = false,
    val stateModifier: KClass<out StateModifier> = DefaultStateModifier::class,
    val isScriptIdentifier: Boolean = false,
)
