package com.cereal.sdk

/**
 * Declares the sections of a [ScriptConfiguration] form, in display order. Place it on the configuration interface
 * and assign fields to a section with [ScriptConfigurationItem.section].
 *
 * Sections only affect layout: values are not nested and [ScriptConfigurationItem.keyName]s do not change, so
 * adding, renaming or reordering sections never resets persisted values. Fields without a section are shown first,
 * without a header, so a configuration without sections looks exactly as before.
 *
 * The client rejects the script at load time, and it will not start, when a field names a section key that is not
 * declared here, or when two sections share the same [Section.key].
 *
 * Example:
 * ```kotlin
 * @ScriptConfigurationSections(
 *     Section(key = "account", name = "Account"),
 *     Section(key = "advanced", name = "Advanced", collapsed = true),
 * )
 * interface MyConfiguration : ScriptConfiguration {
 *     @ScriptConfigurationItem(keyName = "email", name = "Email", description = "...", section = "account")
 *     fun email(): String
 * }
 * ```
 *
 * @since 1.13.0
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ScriptConfigurationSections(
    vararg val value: Section,
)

/**
 * One section of a configuration form, declared inside [ScriptConfigurationSections].
 *
 * @property key Identifier that fields reference through [ScriptConfigurationItem.section]. Must be unique within
 * the configuration.
 * @property name Header shown in the UI.
 * @property collapsed Whether the section starts collapsed.
 *
 * @since 1.13.0
 */
@Retention(AnnotationRetention.RUNTIME)
@Target
annotation class Section(
    val key: String,
    val name: String,
    val collapsed: Boolean = false,
)
