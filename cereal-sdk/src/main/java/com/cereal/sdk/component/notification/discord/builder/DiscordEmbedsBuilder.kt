package com.cereal.sdk.component.notification.discord.builder

import com.cereal.sdk.component.notification.NotificationDsl
import com.cereal.sdk.component.notification.discord.builder.embed.DiscordAuthorEmbedBuilder
import com.cereal.sdk.component.notification.discord.builder.embed.DiscordFieldEmbedBuilder
import com.cereal.sdk.component.notification.discord.builder.embed.DiscordFooterEmbedBuilder
import com.cereal.sdk.component.notification.discord.builder.embed.DiscordImageEmbedBuilder
import com.cereal.sdk.component.notification.discord.builder.embed.DiscordProviderEmbedBuilder
import com.cereal.sdk.component.notification.discord.builder.embed.DiscordThumbnailEmbedBuilder
import com.cereal.sdk.component.notification.discord.builder.embed.DiscordVideoEmbedBuilder
import com.cereal.sdk.component.notification.discord.model.DiscordEmbed
import com.cereal.sdk.component.notification.discord.model.embed.AuthorEmbed
import com.cereal.sdk.component.notification.discord.model.embed.FieldEmbed
import com.cereal.sdk.component.notification.discord.model.embed.FooterEmbed
import com.cereal.sdk.component.notification.discord.model.embed.ImageEmbed
import com.cereal.sdk.component.notification.discord.model.embed.ProviderEmbed
import com.cereal.sdk.component.notification.discord.model.embed.ThumbnailEmbed
import com.cereal.sdk.component.notification.discord.model.embed.VideoEmbed

/**
 * DSL builder for assembling a single Discord embed object containing rich content (title, description, media,
 * provider/author attribution, and structured fields). Multiple embeds can be attached to a [DiscordMessage].
 */
@NotificationDsl
class DiscordEmbedsBuilder {
    var title: String? = null
    var type: String? = null
    var description: String? = null
    var url: String? = null
    var timestamp: String? = null
    var color: String? = null
    var footerEmbed: FooterEmbed? = null
    var imageEmbed: ImageEmbed? = null
    var thumbnailEmbed: ThumbnailEmbed? = null
    var videoEmbed: VideoEmbed? = null
    var providerEmbed: ProviderEmbed? = null
    var authorEmbed: AuthorEmbed? = null
    var fields: MutableList<FieldEmbed>? = null

    fun title(title: () -> String?) {
        this.title = title()
    }

    fun type(type: () -> String?) {
        this.type = type()
    }

    fun description(description: () -> String?) {
        this.description = description()
    }

    fun url(url: () -> String?) {
        this.url = url()
    }

    fun timestamp(timestamp: () -> String?) {
        this.timestamp = timestamp()
    }

    fun color(color: () -> String?) {
        this.color = color()
    }

    fun footer(setup: DiscordFooterEmbedBuilder.() -> Unit = {}) {
        val footerEmbedBuilder = DiscordFooterEmbedBuilder()
        footerEmbedBuilder.setup()
        this.footerEmbed = footerEmbedBuilder.build()
    }

    fun image(setup: DiscordImageEmbedBuilder.() -> Unit = {}) {
        val imageEmbedBuilder = DiscordImageEmbedBuilder()
        imageEmbedBuilder.setup()
        this.imageEmbed = imageEmbedBuilder.build()
    }

    fun thumbnail(setup: DiscordThumbnailEmbedBuilder.() -> Unit = {}) {
        val thumbnailEmbedBuilder = DiscordThumbnailEmbedBuilder()
        thumbnailEmbedBuilder.setup()
        this.thumbnailEmbed = thumbnailEmbedBuilder.build()
    }

    fun video(setup: DiscordVideoEmbedBuilder.() -> Unit = {}) {
        val videoEmbedBuilder = DiscordVideoEmbedBuilder()
        videoEmbedBuilder.setup()
        this.videoEmbed = videoEmbedBuilder.build()
    }

    fun provider(setup: DiscordProviderEmbedBuilder.() -> Unit = {}) {
        val providerEmbedBuilder = DiscordProviderEmbedBuilder()
        providerEmbedBuilder.setup()
        this.providerEmbed = providerEmbedBuilder.build()
    }

    fun author(setup: DiscordAuthorEmbedBuilder.() -> Unit = {}) {
        val authorEmbedBuilder = DiscordAuthorEmbedBuilder()
        authorEmbedBuilder.setup()
        this.authorEmbed = authorEmbedBuilder.build()
    }

    fun field(setup: DiscordFieldEmbedBuilder.() -> Unit = {}) {
        if (this.fields == null) {
            this.fields = mutableListOf()
        }
        val fieldEmbedBuilder = DiscordFieldEmbedBuilder()
        fieldEmbedBuilder.setup()
        val fieldEmbed = fieldEmbedBuilder.build()
        this.fields?.add(fieldEmbed)
    }

    fun build(): DiscordEmbed =
        DiscordEmbed(
            title = title,
            type = type,
            description = description,
            url = url,
            timestamp = timestamp,
            color = color,
            footer = footerEmbed,
            image = imageEmbed,
            thumbnail = thumbnailEmbed,
            video = videoEmbed,
            provider = providerEmbed,
            author = authorEmbed,
            fields = fields?.toList(),
        )
}
