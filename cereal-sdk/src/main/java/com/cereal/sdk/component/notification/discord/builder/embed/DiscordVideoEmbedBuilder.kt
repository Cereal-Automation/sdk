package com.cereal.sdk.component.notification.discord.builder.embed

import com.cereal.sdk.component.notification.NotificationDsl
import com.cereal.sdk.component.notification.discord.model.embed.VideoEmbed

/** Builder for a video section inside a Discord embed. */
@NotificationDsl
class DiscordVideoEmbedBuilder {
    var url: String? = null
    var width: Int? = null
    var height: Int? = null

    fun url(url: () -> String?) {
        this.url = url()
    }

    fun width(width: () -> Int?) {
        this.width = width()
    }

    fun height(height: () -> Int?) {
        this.height = height()
    }

    fun build(): VideoEmbed =
        VideoEmbed(
            url = url,
            width = width,
            height = height,
        )
}
