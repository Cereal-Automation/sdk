package com.cereal.sdk.component.notification.discord.model.embed

/**
 * Represents an image in a Discord embed.
 */
data class ImageEmbed(
    val url: String? = null,
    val proxyUrl: String? = null,
    val height: Int? = null,
    val width: Int? = null,
)
