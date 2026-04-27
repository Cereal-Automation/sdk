package com.cereal.sdk.statemodifier

/**
 * State modifier to control the state of the fields.
 */
interface StateModifier {
    /**
     * Modifier to determine if the item must be visible on the configuration screen or not. Called every time a
     * change in the configuration occurs.
     *
     * @param scriptConfig The raw script configuration containing the values the user has already filled in while configuring
     * the script.
     *
     * @return the [Visibility] of the field.
     */
    fun getVisibility(scriptConfig: ScriptConfig): Visibility

    /**
     * Modifier to determine if the item has a valid value.
     *
     * @param scriptConfig The raw script configuration containing the values the user has already filled in while configuring
     * the script.
     *
     * @return an error if the item to which this [StateModifier] is applied has an invalid value or null
     * when the value is valid.
     */
    fun getError(scriptConfig: ScriptConfig): String?
}

/**
 * Represents the visibility state of a configuration item in the UI.
 */
enum class Visibility {
    /**
     * The item is visible and a value is required.
     */
    VisibleRequired,

    /**
     * The item is visible but providing a value is optional.
     */
    VisibleOptional,

    /**
     * The item is hidden from the UI.
     */
    Hidden,
}
