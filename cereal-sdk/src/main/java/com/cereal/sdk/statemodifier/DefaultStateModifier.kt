package com.cereal.sdk.statemodifier

/**
 * Default no-op [StateModifier] implementation used when a configuration item does not specify a custom one.
 * Leaves the field visible (optional) and reports no validation errors.
 */
object DefaultStateModifier : StateModifier {
    override fun getVisibility(scriptConfig: ScriptConfig) = Visibility.VisibleOptional

    override fun getError(scriptConfig: ScriptConfig) = null
}
