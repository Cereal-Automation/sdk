package com.cereal.sdk.component.license

import java.io.Closeable

/**
 * Lightweight abstraction over an HTTP response used by the licensing component to avoid bringing in a specific
 * HTTP client dependency into the public API surface.
 */
interface HttpResponse : Closeable {
    val isSuccessful: Boolean

    val code: Int

    fun header(
        name: String,
        defaultValue: String? = null,
    ): String?

    fun body(): ByteArray
}
