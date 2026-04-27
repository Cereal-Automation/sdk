package com.cereal.sdk.component.notification.telegram.model

/**
 * Represents a Telegram message that can be sent via Bot API.
 */
data class TelegramMessage(
    val chatId: String? = null,
    val text: String,
    val parseMode: TelegramParseMode? = null,
    val disableWebPagePreview: Boolean? = null,
    val disableNotification: Boolean? = null,
    val replyToMessageId: Int? = null,
    val botToken: String? = null,
)
