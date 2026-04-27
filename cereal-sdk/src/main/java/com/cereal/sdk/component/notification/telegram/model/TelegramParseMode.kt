package com.cereal.sdk.component.notification.telegram.model

/**
 * Enum representing the different parse modes supported by Telegram Bot API.
 */
enum class TelegramParseMode {
    /**
     * HTML parse mode - supports HTML tags like <b>, <i>, <a>, <code>, <pre>
     */
    HTML,

    /**
     * Markdown parse mode - supports Markdown formatting
     */
    MARKDOWN,

    /**
     * MarkdownV2 parse mode - supports MarkdownV2 formatting with enhanced features
     */
    MARKDOWN_V2,
}
