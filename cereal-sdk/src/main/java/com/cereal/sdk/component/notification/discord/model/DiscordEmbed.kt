package com.cereal.sdk.component.notification.discord.model

import com.cereal.sdk.component.notification.discord.model.embed.AuthorEmbed
import com.cereal.sdk.component.notification.discord.model.embed.FieldEmbed
import com.cereal.sdk.component.notification.discord.model.embed.FooterEmbed
import com.cereal.sdk.component.notification.discord.model.embed.ImageEmbed
import com.cereal.sdk.component.notification.discord.model.embed.ProviderEmbed
import com.cereal.sdk.component.notification.discord.model.embed.ThumbnailEmbed
import com.cereal.sdk.component.notification.discord.model.embed.VideoEmbed

/**
 * Represents a Discord embed that can be included in a Discord message.
 */
data class DiscordEmbed(
    val title: String?,
    val type: String?,
    val description: String?,
    val url: String?,
    val timestamp: String?,
    val color: String?,
    val footer: FooterEmbed?,
    val image: ImageEmbed?,
    val thumbnail: ThumbnailEmbed?,
    val video: VideoEmbed?,
    val provider: ProviderEmbed?,
    val author: AuthorEmbed?,
    val fields: List<FieldEmbed>?,
)
