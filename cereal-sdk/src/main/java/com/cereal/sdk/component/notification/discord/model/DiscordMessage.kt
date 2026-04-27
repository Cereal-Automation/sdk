package com.cereal.sdk.component.notification.discord.model

/**
 * Represents a Discord message that can be sent via webhook.
 */
data class DiscordMessage(
    val username: String? = null,
    val content: String? = null,
    val avatarUrl: String? = null,
    val tts: Boolean? = null,
    val embeds: List<DiscordEmbed>? = null,
    val webhookUrl: String? = null,
)
