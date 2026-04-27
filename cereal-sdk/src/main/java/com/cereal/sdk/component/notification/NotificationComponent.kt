package com.cereal.sdk.component.notification

/**
 * Component responsible for surfacing out-of-band notifications to the user and (optionally) external channels like
 * Discord. Delivery is best-effort: users can mute or disable notifications globally.
 */
interface NotificationComponent {
    /**
     * Send a notification to the user. The user can disable notifications on a global level so it's not
     * guaranteed that the user will actually get the notification. There's no way to know whether the notification
     * was successfully displayed to the user or not.
     *
     * Because notification usually attract the attention of the user immediately it should only be used in cases
     * where it's important to notify the user. If that's not the case please use the statusUpdate callback in
     * [com.cereal.sdk.Script.execute] to print the message you want to share with the user.
     */
    suspend fun sendNotification(notification: Notification)
}
