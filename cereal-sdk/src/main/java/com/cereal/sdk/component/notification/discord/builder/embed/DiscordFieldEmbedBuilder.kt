package com.cereal.sdk.component.notification.discord.builder.embed

import com.cereal.sdk.component.notification.NotificationDsl
import com.cereal.sdk.component.notification.discord.model.embed.FieldEmbed

/** Builder for a field entry inside a Discord embed (name/value pair) */
@NotificationDsl
class DiscordFieldEmbedBuilder {
    var name: String = ""
    var value: String = ""
    var inline: Boolean = true

    fun name(name: () -> String) {
        this.name = name()
    }

    fun value(value: () -> String) {
        this.value = value()
    }

    fun inline(inline: () -> Boolean) {
        this.inline = inline()
    }

    fun build(): FieldEmbed =
        FieldEmbed(
            name = name,
            value = value,
            inline = inline,
        )
}
