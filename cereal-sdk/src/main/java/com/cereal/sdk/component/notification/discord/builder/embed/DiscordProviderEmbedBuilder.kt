package com.cereal.sdk.component.notification.discord.builder.embed

import com.cereal.sdk.component.notification.NotificationDsl
import com.cereal.sdk.component.notification.discord.model.embed.ProviderEmbed

/** Builder for a provider section inside a Discord embed. */
@NotificationDsl
class DiscordProviderEmbedBuilder {
    var name: String? = null
    var url: String? = null

    fun name(name: () -> String?) {
        this.name = name()
    }

    fun url(url: () -> String?) {
        this.url = url()
    }

    fun build(): ProviderEmbed =
        ProviderEmbed(
            name = name,
            url = url,
        )
}
