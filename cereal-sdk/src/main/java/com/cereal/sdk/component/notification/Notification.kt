package com.cereal.sdk.component.notification

import com.cereal.sdk.component.notification.discord.model.DiscordMessage
import com.cereal.sdk.component.notification.email.model.EmailMessage
import com.cereal.sdk.component.notification.telegram.model.TelegramMessage

/**
 * Represents a user-facing notification composed of a plain text [message] (always required), an optional [title]
 * for UI emphasis, and structured message payloads for delivery to Discord, Telegram, and Email.
 *
 * Scripts should construct instances via the DSL helper [notification] / [NotificationBuilder] rather than directly.
 */
data class Notification
    @JvmOverloads
    constructor(
        val title: String?,
        val message: String,
        val discordMessage: DiscordMessage? = null,
        val telegramMessage: TelegramMessage? = null,
        val emailMessage: EmailMessage? = null,
    ) {
        /**
         * Legacy copy method for backward compatibility.
         * @deprecated Use the copy method with all message parameters.
         */
        @JvmName("copy")
        fun copy(
            title: String? = this.title,
            message: String = this.message,
            discordMessage: DiscordMessage? = this.discordMessage,
        ): Notification = Notification(title, message, discordMessage, null, null)
    }
