package com.cereal.sdk.models.proxy

/**
 * Represents a concrete network proxy endpoint optionally protected by basic authentication credentials.
 *
 * @param id Stable identifier for this proxy entry.
 * @param address Hostname / IP of the proxy server.
 * @param port TCP port number.
 * @param username Optional username when authentication is required.
 * @param password Optional password when authentication is required.
 * @param protocol Network protocol the proxy speaks; defaults to [ProxyProtocol.HTTP]. Read this to
 *   decide how to connect — e.g. dial SOCKS5 rather than HTTP.
 */
data class Proxy(
    val id: String,
    val address: String,
    val port: Int,
    val username: String? = null,
    val password: String? = null,
    val protocol: ProxyProtocol = ProxyProtocol.HTTP,
)
