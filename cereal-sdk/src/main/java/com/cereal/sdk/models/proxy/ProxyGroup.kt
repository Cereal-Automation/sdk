package com.cereal.sdk.models.proxy

/**
 * Represents a user maintained collection of proxy endpoints. Useful for selecting a pool of proxies to rotate
 * through during script execution.
 *
 * @param id Stable identifier for the group.
 * @param name Display name shown to the user.
 * @param numberOfItems Number of proxies contained (may be approximate).
 */
data class ProxyGroup(
    val id: String,
    val name: String,
    val numberOfItems: Int,
)
