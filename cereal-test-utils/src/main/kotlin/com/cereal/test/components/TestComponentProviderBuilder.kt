package com.cereal.test.components

import com.cereal.sdk.Script
import com.cereal.sdk.ScriptConfiguration
import com.cereal.sdk.component.ComponentProvider
import com.cereal.sdk.component.license.LicenseComponent
import com.cereal.sdk.component.logger.LoggerComponent
import com.cereal.sdk.component.notification.NotificationComponent
import com.cereal.sdk.component.preference.PreferenceComponent
import com.cereal.sdk.component.script.ScriptLauncherComponent
import com.cereal.sdk.component.userinteraction.UserInteractionComponent
import com.cereal.sdk.component.userinteraction.WebResourceRequest
import com.cereal.test.ComponentProviderFactory

class TestComponentProviderFactory : ComponentProviderFactory {
    /**
     * A nullable map that associates a `Class` of type `Script` with a specific `ScriptConfiguration`.
     * This variable allows for the customization of script configurations and provides a framework to supply
     * tailored configurations to different script classes during testing or runtime. If not assigned, it defaults to null.
     */
    var childScriptConfigurations: Map<Class<out Script<*>>, ScriptConfiguration>? =
        null

    /**
     * A list of [WebResourceRequest] objects used to mock results returned
     * from the `showUrl` method in the `TestUserInteractionComponent`.
     * This variable can be configured to simulate specific behaviors during tests.
     * Defaults to an empty list.
     */
    var showUrlResults: List<WebResourceRequest> = emptyList()

    /**
     * A list of [WebResourceRequest] objects used to mock results returned
     * from the `showHtml` method in the `TestUserInteractionComponent`.
     * This variable can be configured to simulate specific behaviors during tests.
     * Defaults to an empty list.
     */
    var showHtmlResults: List<WebResourceRequest> = emptyList()

    /**
     * A list of [String] objects used to mock results returned
     * from the `requestInput` method in the `TestUserInteractionComponent`.
     * This variable can be configured to simulate specific behaviors during tests.
     * Defaults to an empty list.
     */
    var requestInputResults: List<String> = emptyList()

    override fun create(): ComponentProvider =
        TestComponentProvider(
            ConsoleLoggerComponent(),
            LoggingNotificationComponent(),
            TestLicenseComponent(),
            TestPreferenceComponent(),
            TestScriptLauncherComponent(this, childScriptConfigurations),
            TestUserInteractionComponent(showUrlResults, showHtmlResults, requestInputResults),
        )
}

class TestComponentProvider(
    private val loggerComponent: LoggerComponent,
    private val notificationComponent: NotificationComponent,
    private val licenseComponent: LicenseComponent,
    private val preferenceComponent: PreferenceComponent,
    private val scriptLauncherComponent: ScriptLauncherComponent,
    private val userInteractionComponent: UserInteractionComponent,
) : ComponentProvider {
    override fun logger(): LoggerComponent = loggerComponent

    override fun license(): LicenseComponent = licenseComponent

    override fun notification(): NotificationComponent = notificationComponent

    override fun preference(): PreferenceComponent = preferenceComponent

    override fun scriptLauncher(): ScriptLauncherComponent = scriptLauncherComponent

    override fun userInteraction(): UserInteractionComponent = userInteractionComponent
}
