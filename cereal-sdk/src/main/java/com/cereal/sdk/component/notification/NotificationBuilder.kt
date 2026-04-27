package com.cereal.sdk.component.notification

import com.cereal.sdk.component.notification.discord.builder.DiscordMessageBuilder
import com.cereal.sdk.component.notification.discord.model.DiscordMessage
import com.cereal.sdk.component.notification.email.builder.EmailMessageBuilder
import com.cereal.sdk.component.notification.email.model.EmailMessage
import com.cereal.sdk.component.notification.telegram.builder.TelegramMessageBuilder
import com.cereal.sdk.component.notification.telegram.model.TelegramMessage

/**
 * DSL marker limiting the notification builder scope to avoid accidental receiver confusion when nesting Discord
 * embed builders inside the notification builder.
 */
@DslMarker
annotation class NotificationDsl

/**
 * Builder used to construct a [Notification] instance. Required field is [message]; an optional [title] can be added
 * and custom message payloads for Discord, Telegram, and Email can be built.
 */
@NotificationDsl
data class NotificationBuilder(
    val message: String,
) {
    var title: String? = null
    var discordMessage: DiscordMessage? = null
    var telegramMessage: TelegramMessage? = null
    var emailMessage: EmailMessage? = null

    /** Sets an optional title displayed more prominently in the host UI (if supported). */
    fun title(title: () -> String?) {
        this.title = title()
    }

    /**
     * Configures a custom Discord webhook payload. If omitted a basic message with [message] as content is generated.
     */
    fun discordMessage(setup: DiscordMessageBuilder.() -> Unit = {}) {
        val builder = DiscordMessageBuilder()
        builder.setup()
        this.discordMessage = builder.build()
    }

    fun telegramMessage(setup: TelegramMessageBuilder.() -> Unit = {}) {
        val builder = TelegramMessageBuilder()
        builder.setup()
        this.telegramMessage = builder.build()
    }

    fun emailMessage(setup: EmailMessageBuilder.() -> Unit = {}) {
        val builder = EmailMessageBuilder()
        builder.setup()
        this.emailMessage = builder.build()
    }

    fun build(): Notification = Notification(title, message, discordMessage, telegramMessage, emailMessage)
}

/**
 * Entry point DSL function for creating a [Notification]. Example:
 *
 * notification("Done!") {
 *     title { "Build Complete" }
 *     discordMessage {
 *         content { "All tasks succeeded" }
 *     }
 * }
 */
@NotificationDsl
fun notification(
    message: String,
    setup: NotificationBuilder.() -> Unit,
): Notification {
    val notificationBuilder = NotificationBuilder(message)
    notificationBuilder.setup()
    return notificationBuilder.build()
}
