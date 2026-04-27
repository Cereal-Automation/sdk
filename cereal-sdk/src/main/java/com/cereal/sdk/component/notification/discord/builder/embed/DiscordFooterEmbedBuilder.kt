package com.cereal.sdk.component.notification.discord.builder.embed

import com.cereal.sdk.component.notification.NotificationDsl
import com.cereal.sdk.component.notification.discord.model.embed.FooterEmbed

/** Builder for a footer section inside a Discord embed. */
@NotificationDsl
class DiscordFooterEmbedBuilder {
    var text: String? = null
    var iconUrl: String? = null
    var proxyIconUrl: String? = null

    fun text(text: () -> String?) {
        this.text = text()
    }

    fun iconUrl(iconUrl: () -> String?) {
        this.iconUrl = iconUrl()
    }

    fun proxyIconUrl(proxyIconUrl: () -> String?) {
        this.proxyIconUrl = proxyIconUrl()
    }

    fun build(): FooterEmbed =
        FooterEmbed(
            text = text,
            iconUrl = iconUrl,
            proxyIconUrl = proxyIconUrl,
        )
}
