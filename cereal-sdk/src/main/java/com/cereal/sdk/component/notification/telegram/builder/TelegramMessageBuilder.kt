package com.cereal.sdk.component.notification.telegram.builder

import com.cereal.sdk.component.notification.telegram.model.TelegramMessage
import com.cereal.sdk.component.notification.telegram.model.TelegramParseMode

@DslMarker
annotation class TelegramDsl

@TelegramDsl
class TelegramMessageBuilder {
    var chatId: String? = null
    var text: String? = null
    var parseMode: TelegramParseMode? = null
    var disableWebPagePreview: Boolean? = null
    var disableNotification: Boolean? = null
    var replyToMessageId: Int? = null
    var botToken: String? = null

    fun chatId(chatId: () -> String?) {
        this.chatId = chatId()
    }

    fun text(text: () -> String?) {
        this.text = text()
    }

    fun parseMode(parseMode: () -> TelegramParseMode?) {
        this.parseMode = parseMode()
    }

    fun disableWebPagePreview(disableWebPagePreview: () -> Boolean?) {
        this.disableWebPagePreview = disableWebPagePreview()
    }

    fun disableNotification(disableNotification: () -> Boolean?) {
        this.disableNotification = disableNotification()
    }

    fun replyToMessageId(replyToMessageId: () -> Int?) {
        this.replyToMessageId = replyToMessageId()
    }

    fun botToken(botToken: () -> String?) {
        this.botToken = botToken()
    }

    fun build(): TelegramMessage =
        TelegramMessage(
            chatId = chatId,
            text = requireNotNull(text) { "text must be provided" },
            parseMode = parseMode,
            disableWebPagePreview = disableWebPagePreview,
            disableNotification = disableNotification,
            replyToMessageId = replyToMessageId,
            botToken = botToken,
        )
}
