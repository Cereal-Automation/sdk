package com.cereal.sdk.component.notification.email.builder

import com.cereal.sdk.component.notification.email.model.EmailMessage

@DslMarker
annotation class EmailDsl

@EmailDsl
class EmailMessageBuilder {
    var to: String? = null
    var from: String? = null
    var subject: String? = null
    var body: String? = null
    var smtpHost: String? = null
    var smtpPort: Int? = null
    var username: String? = null
    var password: String? = null
    var useTls: Boolean? = true

    fun to(to: () -> String?) {
        this.to = to()
    }

    fun from(from: () -> String?) {
        this.from = from()
    }

    fun subject(subject: () -> String?) {
        this.subject = subject()
    }

    fun body(body: () -> String?) {
        this.body = body()
    }

    fun smtpHost(smtpHost: () -> String?) {
        this.smtpHost = smtpHost()
    }

    fun smtpPort(smtpPort: () -> Int?) {
        this.smtpPort = smtpPort()
    }

    fun username(username: () -> String?) {
        this.username = username()
    }

    fun password(password: () -> String?) {
        this.password = password()
    }

    fun useTls(useTls: () -> Boolean?) {
        this.useTls = useTls()
    }

    fun build(): EmailMessage =
        EmailMessage(
            to = to,
            from = from,
            subject = requireNotNull(subject) { "subject must be provided" },
            body = requireNotNull(body) { "body must be provided" },
            smtpHost = smtpHost,
            smtpPort = smtpPort,
            username = username,
            password = password,
            useTls = useTls,
        )
}
