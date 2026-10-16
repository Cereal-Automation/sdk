package com.cereal.sdk.component

import com.cereal.sdk.component.artifact.ArtifactComponent
import com.cereal.sdk.component.license.LicenseComponent
import com.cereal.sdk.component.llm.LlmComponent
import com.cereal.sdk.component.logger.LoggerComponent
import com.cereal.sdk.component.notification.NotificationComponent
import com.cereal.sdk.component.preference.PreferenceComponent
import com.cereal.sdk.component.script.ScriptLauncherComponent
import com.cereal.sdk.component.userinteraction.UserInteractionComponent

/**
 * Aggregates all per-script components made available to a running script instance.
 *
 * A fresh provider instance is created for every started script so that any state maintained by components
 * can be scoped appropriately. Implementations should be thread-safe if scripts may call these methods from
 * multiple coroutines.
 */
interface ComponentProvider {
    /** Returns a logger for emitting diagnostic information to the host application's log system. */
    fun logger(): LoggerComponent

    /** Returns the license component used to validate script licensing. */
    fun license(): LicenseComponent

    /** Returns the notification component for user facing toast / push style notifications. */
    fun notification(): NotificationComponent

    /** Returns the preference component for simple key/value persistence local to the user environment. */
    fun preference(): PreferenceComponent

    /** Returns the script launcher used to start child scripts programmatically. */
    fun scriptLauncher(): ScriptLauncherComponent

    /** Returns the user interaction component used for browser / UI interactions with the user. */
    fun userInteraction(): UserInteractionComponent

    /** Returns the artifact component for emitting downloadable file output produced by the script. */
    fun artifact(): ArtifactComponent

    /**
     * Returns the LLM component for calling the model the user configured in Settings → AI.
     *
     * Scripts that call this must set `"sdk_version": "1.14.0"` (or higher) in `manifest.json`.
     *
     * @since 1.14.0
     */
    fun llm(): LlmComponent
}
