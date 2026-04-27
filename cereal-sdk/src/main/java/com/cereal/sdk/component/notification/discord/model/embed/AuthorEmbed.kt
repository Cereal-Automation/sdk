package com.cereal.sdk.component.notification.discord.model.embed

/**
 * Represents the author information in a Discord embed.
 */
data class AuthorEmbed(
    val name: String? = null,
    val url: String? = null,
    val iconUrl: String? = null,
    val proxyIconUrl: String? = null,
)
