package com.cereal.sdk

import com.cereal.sdk.component.ComponentProvider

/**
 * An action a user can run on an existing task from the task's menu, for example to reset state the script
 * learned. Return the actions from [Script.taskActions].
 *
 * The client lists the actions in the order the script returns them. It disables them while the task runs, so an
 * action never races a run of the same task.
 *
 * @since 1.13.0
 */
interface TaskAction<T : ScriptConfiguration> {
    /** Menu label shown to the user, e.g. "Forget learned steps". */
    val name: String

    /** Text of a confirmation dialog shown before the action runs, or null to run it without asking. */
    val confirm: String?

    /**
     * Runs the action.
     *
     * @param configuration The task's own configuration.
     * @param provider The task's own component provider, so the action reaches the same preferences and files as
     * the task's runs.
     *
     * @return A success message shown to the user, or null to show a generic one. Throw to report a failure; the
     * exception message is shown to the user.
     */
    suspend fun run(
        configuration: T,
        provider: ComponentProvider,
    ): String?
}
