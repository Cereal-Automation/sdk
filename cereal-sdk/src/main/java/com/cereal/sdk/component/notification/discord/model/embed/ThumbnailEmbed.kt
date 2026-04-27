package com.cereal.sdk.component.notification.discord.model.embed

/**
 * Represents a thumbnail in a Discord embed.
 */
data class ThumbnailEmbed(
    val url: String? = null,
    val proxyUrl: String? = null,
    val height: Int? = null,
    val width: Int? = null,
)
