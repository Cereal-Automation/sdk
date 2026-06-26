package com.cereal.sdk.models.proxy

/**
 * Network protocol a [Proxy] speaks.
 *
 * A script must read [Proxy.protocol] and configure its own networking accordingly — the SDK
 * carries the protocol as data but does not itself open the connection.
 */
enum class ProxyProtocol {
    /** Standard HTTP proxy (the historical default). */
    HTTP,

    /** SOCKS5 proxy. Supports remote DNS and username/password auth (RFC 1928 / RFC 1929). */
    SOCKS5,
}
