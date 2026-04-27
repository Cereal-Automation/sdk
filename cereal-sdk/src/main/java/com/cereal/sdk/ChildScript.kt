package com.cereal.sdk

/**
 * Annotation applied to secondary (child) scripts that can be launched programmatically from another script
 * via [com.cereal.sdk.component.script.ScriptLauncherComponent]. The annotated class must implement [Script].
 *
 * Each child script must have a stable, unique [id]. This value is used for persistence (e.g., restoring state on
 * application restart) and must not be changed after a version has shipped to users. The [name] can be changed freely
 * between releases and is shown to the user in the UI.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ChildScript(
    /**
     * A unique identifier for the child script. This value is used to store the scripts state when the application is
     * closed and restored when the application opens so make sure to not change this value after you've released the
     * first version of your script.
     */
    val id: String,
    /**
     * A name for the child script used to make this script recognizable for the user once this script has started.
     * Unlike the [id] you're free to change this value between releases.
     */
    val name: String,
)
