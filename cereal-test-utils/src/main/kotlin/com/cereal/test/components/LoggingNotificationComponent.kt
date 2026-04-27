package com.cereal.test.components

import com.cereal.sdk.component.notification.Notification
import com.cereal.sdk.component.notification.NotificationComponent
import com.cereal.test.util.Logger

class LoggingNotificationComponent : NotificationComponent {
    private val logger = Logger("NotificationComponent")

    override suspend fun sendNotification(notification: Notification) {
        logger.logMessage("Show desktop notification with title '${notification.title}' and message '${notification.message}'.")
        logger.logMessage("Send discord notification: ${notification.discordMessage}.")
    }
}
