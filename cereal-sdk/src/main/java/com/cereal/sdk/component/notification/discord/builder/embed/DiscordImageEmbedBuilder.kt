package com.cereal.sdk.component.notification.discord.builder.embed

import com.cereal.sdk.component.notification.NotificationDsl
import com.cereal.sdk.component.notification.discord.model.embed.ImageEmbed

/** Builder for an image section inside a Discord embed. */
@NotificationDsl
class DiscordImageEmbedBuilder {
    var url: String? = null
    var proxyUrl: String? = null
    var width: Int? = null
    var height: Int? = null

    fun url(url: () -> String?) {
        this.url = url()
    }

    fun proxyUrl(proxyUrl: () -> String?) {
        this.proxyUrl = proxyUrl()
    }

    fun width(width: () -> Int?) {
        this.width = width()
    }

    fun height(height: () -> Int?) {
        this.height = height()
    }

    fun build(): ImageEmbed =
        ImageEmbed(
            url = url,
            proxyUrl = proxyUrl,
            width = width,
            height = height,
        )
}
