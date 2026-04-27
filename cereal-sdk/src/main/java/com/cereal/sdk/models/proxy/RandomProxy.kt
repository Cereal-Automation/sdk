package com.cereal.sdk.models.proxy

/**
 * Functional interface that resolves to a concrete [Proxy] when invoked. Implementations may apply any selection
 * strategy (e.g. random, round‑robin, health‑based) across a [ProxyGroup] or remote source. Intended for use as a
 * configuration item return type allowing dynamic proxy assignment per task execution.
 */
interface RandomProxy {
    /** Suspends if necessary (e.g. performing IO) and returns the next [Proxy] to use. */
    suspend operator fun invoke(): Proxy
}
