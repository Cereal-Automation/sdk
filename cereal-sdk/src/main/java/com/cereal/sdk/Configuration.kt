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
 * The return type of the function determines how user input is validated and parsed. The supported return types are
 * exactly:
 *  - Int, Float, Double
 *  - Boolean
 *  - String
 *  - Enum subclasses (the platform will expose a selection list of enum constants)
 *  - List<String> (a user-editable list of text values)
 *  - List<T> where T is a [ScriptConfigurationListItem] interface (a user-editable list of records —
 *    see [ScriptConfigurationListItem] for the permitted field types and the rules it enforces)
 *  - Proxy / RandomProxy (special selectors)
 *  - [com.cereal.sdk.models.Secret] for credentials — masked on input and wherever configuration is
 *    summarised (available since SDK 1.11.0)
 *
 * Any other return type — including Long, Short, Byte and ProxyGroup — is rejected when the platform loads the
 * script, and the script will not start — as is any other List element type. Only String, Int, Float, Double and
 * Secret may be combined with [valuePerTask]. At most one item per configuration may return Proxy.
 *
 * **Default Values:**
 * You can provide a default value for configuration items by using a default implementation in the interface.
 * The default value will be pre-populated in the configuration UI when users create a new configuration.
 * Supported types for default values: Boolean, String, Int, Float, Double, and Enum. A default implementation on a
 * complex-list item ([ScriptConfigurationListItem]) is rejected when the platform loads the script — pre-seeded rows
 * are not supported.
 *
 * [com.cereal.sdk.models.Secret] deliberately does **not** support default values — a default returning a
 * credential would be a hardcoded secret in source. A secret also cannot be the script identifier, because a
 * masked identifier makes every instance of a script display identically; declaring one fails at load time.
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
