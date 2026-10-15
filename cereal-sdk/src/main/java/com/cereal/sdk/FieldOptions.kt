package com.cereal.sdk

import com.cereal.sdk.statemodifier.ScriptConfig

/**
 * A source of fetched choices for a text field, attached through [ScriptConfigurationItem.options].
 *
 * The client renders the field as an editable combo box that always accepts free text, and calls [load] when the user
 * opens the dropdown or presses its refresh. It never calls it on a keystroke, blur, Save or Start. The call runs off
 * the UI thread as a cancellable coroutine with a fixed timeout (about 15 seconds, a client constant). A new trigger,
 * an edit to one of the [dependsOn] fields, or closing the form cancels the call in flight. The client does not skip
 * the call when [dependsOn] fields are empty, so [load] must handle that itself. Results are cached until a
 * [dependsOn] value changes.
 *
 * Form-time code runs in-process with no network restriction, because scripts are trusted code on a single-user
 * desktop app (see `docs/adr/0001-form-time-code-is-trusted.md`).
 *
 * Rules the client enforces when it loads the script (a violation is a definition error and the script will not load):
 *  - `options` may only be set on a top-level `String` or `String?` item. Any other return type, including a field
 *    inside a [ScriptConfigurationListItem], is rejected.
 *  - The implementation must be an `object`, not a `class`; the client reads the singleton instance, as it does for
 *    [ScriptConfigurationItem.stateModifier].
 *
 * Example:
 * ```kotlin
 * object ModelOptions : FieldOptions {
 *     override val dependsOn = setOf("apiKey")
 *
 *     override suspend fun load(config: ScriptConfig): List<FieldOption> {
 *         val key = (config.valueForKey("apiKey") as? SecretScriptConfigValue)?.value
 *             ?: throw IllegalStateException("Enter your API key to list models")
 *         return fetchModels(key).map { FieldOption(it.id, it.displayName) }
 *     }
 * }
 * ```
 */
interface FieldOptions {
    /**
     * The [ScriptConfigurationItem.keyName]s of the fields [load] reads. Only these fields are passed to [load].
     */
    val dependsOn: Set<String>

    /**
     * Loads the options for the field.
     *
     * @param config holds only the [dependsOn] fields, with their current form values whether saved or not. Any other
     * key returns [com.cereal.sdk.statemodifier.ScriptConfigValue.NullScriptConfigValue]. A `Secret` field arrives as
     * [com.cereal.sdk.statemodifier.ScriptConfigValue.SecretScriptConfigValue].
     *
     * @return the options in display order. The client shows them as received, so filter, sort and label them here.
     *
     * @throws Exception to report a failure. The exception's message is shown inline under the field as the failure
     * text, with a Retry action; free text entry stays available.
     */
    suspend fun load(config: ScriptConfig): List<FieldOption>
}

/**
 * One choice returned by [FieldOptions.load].
 *
 * @property id the value stored in the configuration when the option is picked.
 * @property label optional display text shown above the id. When null, only the id is shown.
 */
data class FieldOption(
    val id: String,
    val label: String? = null,
)

/**
 * Default [FieldOptions] used when a configuration item declares no options source. Depends on nothing and loads no
 * options; the field renders as a plain text field.
 */
object NoFieldOptions : FieldOptions {
    override val dependsOn: Set<String> = emptySet()

    override suspend fun load(config: ScriptConfig): List<FieldOption> = emptyList()
}
