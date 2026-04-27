package com.cereal.sdk.component.notification.discord.model.embed

/**
 * Represents a field in a Discord embed.
 */
data class FieldEmbed(
    val name: String,
    val value: String,
    val inline: Boolean,
)
