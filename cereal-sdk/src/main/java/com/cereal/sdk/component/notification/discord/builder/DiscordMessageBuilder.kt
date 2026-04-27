package com.cereal.sdk.component.notification.discord.builder

import com.cereal.sdk.component.notification.NotificationDsl
import com.cereal.sdk.component.notification.discord.model.DiscordEmbed
import com.cereal.sdk.component.notification.discord.model.DiscordMessage

/**
 * DSL builder for constructing a [DiscordMessage] payload sent through a webhook. Supports specifying user-visible
 * metadata (username, avatar), content text, TTS flag, one or more rich embeds, and an override [webhookUrl] when
 * dispatching to a non-default endpoint.
 */
@NotificationDsl
class DiscordMessageBuilder {
    var username: String? = null
    var content: String? = null
    var avatarUrl: String? = null
    var textToSpeech: Boolean = false
    var embeds: MutableList<DiscordEmbed>? = null
    var webhookUrl: String? = null

    fun username(username: () -> String?) {
        this.username = username()
    }

    fun content(content: () -> String?) {
        this.content = content()
    }

    fun avatarUrl(avatarUrl: () -> String?) {
        this.avatarUrl = avatarUrl()
    }

    fun textToSpeech(textToSpeech: () -> Boolean) {
        this.textToSpeech = textToSpeech()
    }

    fun webhookUrl(webhookUrl: () -> String) {
        this.webhookUrl = webhookUrl()
    }

    fun embed(setup: DiscordEmbedsBuilder.() -> Unit = {}) {
        if (this.embeds == null) {
            this.embeds = mutableListOf()
        }
        val embedsBuilder = DiscordEmbedsBuilder()
        embedsBuilder.setup()
        embeds?.add(embedsBuilder.build())
    }

    fun build(): DiscordMessage =
        DiscordMessage(
            username = username,
            content = content,
            avatarUrl = avatarUrl,
            tts = textToSpeech,
            embeds = embeds?.toList(),
            webhookUrl = webhookUrl,
        )
}

/** Entry point DSL helper for building a [DiscordMessage]. */
@NotificationDsl
fun discordMessage(setup: DiscordMessageBuilder.() -> Unit): DiscordMessage {
    val discordMessageBuilder = DiscordMessageBuilder()
    discordMessageBuilder.setup()
    return discordMessageBuilder.build()
}
