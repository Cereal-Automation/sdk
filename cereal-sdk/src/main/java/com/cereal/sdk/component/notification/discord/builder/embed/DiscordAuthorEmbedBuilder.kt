package com.cereal.sdk.component.notification.discord.builder.embed

import com.cereal.sdk.component.notification.NotificationDsl
import com.cereal.sdk.component.notification.discord.model.embed.AuthorEmbed

/**
 * DSL builder for a Discord embed author section.
 */
@NotificationDsl
class DiscordAuthorEmbedBuilder {
    var name: String? = null
    var url: String? = null
    var iconUrl: String? = null
    var proxyIconUrl: String? = null

    fun name(name: () -> String?) {
        this.name = name()
    }

    fun url(url: () -> String?) {
        this.url = url()
    }

    fun iconUrl(iconUrl: () -> String?) {
        this.iconUrl = iconUrl()
    }

    fun proxyIconUrl(proxyIconUrl: () -> String?) {
        this.proxyIconUrl = proxyIconUrl()
    }

    fun build(): AuthorEmbed =
        AuthorEmbed(
            name = name,
            url = url,
            iconUrl = iconUrl,
            proxyIconUrl = proxyIconUrl,
        )
}
