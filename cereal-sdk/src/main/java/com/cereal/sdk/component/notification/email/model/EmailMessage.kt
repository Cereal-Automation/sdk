package com.cereal.sdk.component.notification.email.model

/**
 * Represents an email message to be sent through SMTP.
 */
data class EmailMessage(
    val to: String? = null,
    val from: String? = null,
    val subject: String,
    val body: String,
    val smtpHost: String? = null,
    val smtpPort: Int? = null,
    val username: String? = null,
    val password: String? = null,
    val useTls: Boolean? = true,
)
