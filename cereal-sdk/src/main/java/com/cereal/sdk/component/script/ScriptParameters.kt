package com.cereal.sdk.component.script

import java.time.Instant

/**
 * Heterogeneous, type-safe (caller enforced) key/value container passed when starting child scripts. Designed for
 * lightweight parameter handoff – not long term persistence. The mapping stores raw values; retrieval helpers cast to
 * the requested type and return null (or provided default) when absent.
 *
 * Keys are case-sensitive. This class is not thread-safe; confine instances to a single coroutine or externally
 * synchronize if mutating from multiple threads.
 */
class ScriptParameters {
    val mapping: MutableMap<String, Any> = HashMap()

    /**
     * Removes all elements from the mapping.
     */
    fun clear() {
        mapping.clear()
    }

    /**
     * Removes any entry with the given key from the mapping.
     */
    fun remove(key: String) {
        mapping.remove(key)
    }

    /**
     * Retrieve all stored key value pairs.
     */
    fun getAll(): Map<String, Any> = mapping

    /**
     * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key.
     */
    fun getByte(key: String): Byte? = mapping[key] as Byte?

    /**
     * Returns the value associated with the given key, or [defaultValue] if no mapping of the desired type exists for the given key.
     */
    fun getByte(
        key: String,
        defaultValue: Byte,
    ): Byte = mapping.getOrDefault(key, defaultValue) as Byte

    /**
     * Inserts a byte value into the mapping, replacing any existing value for the given key.
     */
    fun putByte(
        key: String,
        value: Byte,
    ) {
        mapping[key] = value
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key.
     */
    fun getString(key: String): String? = mapping[key] as String?

    /**
     * Returns the value associated with the given key, or [defaultValue] if no mapping of the desired type exists for the given key or if a null value is explicitly associated with the given key.
     */
    fun getString(
        key: String,
        defaultValue: String,
    ): String = mapping.getOrDefault(key, defaultValue) as String

    /**
     * Inserts a string value into the mapping, replacing any existing value for the given key.
     */
    fun putString(
        key: String,
        value: String,
    ) {
        mapping[key] = value
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key.
     */
    fun getFloat(key: String): Float? = mapping[key] as Float?

    /**
     * Returns the value associated with the given key, or [defaultValue] if no mapping of the desired type exists for the given key.
     */
    fun getFloat(
        key: String,
        defaultValue: Float,
    ): Float = mapping.getOrDefault(key, defaultValue) as Float

    /**
     * Inserts a float value into the mapping, replacing any existing value for the given key.
     */
    fun putFloat(
        key: String,
        value: Float,
    ) {
        mapping[key] = value
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key.
     */
    fun getInteger(key: String): Int? = mapping[key] as Int?

    /**
     * Returns the value associated with the given key, or [defaultValue] if no mapping of the desired type exists for the given key.
     */
    fun getInteger(
        key: String,
        defaultValue: Int,
    ): Int = mapping.getOrDefault(key, defaultValue) as Int

    /**
     * Inserts a integer value into the mapping, replacing any existing value for the given key.
     */
    fun putInteger(
        key: String,
        value: Int,
    ) {
        mapping[key] = value
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key.
     */
    fun getShort(key: String): Short? = mapping[key] as Short?

    /**
     * Returns the value associated with the given key, or [defaultValue] if no mapping of the desired type exists for the given key.
     */
    fun getShort(
        key: String,
        defaultValue: Short,
    ): Short = mapping.getOrDefault(key, defaultValue) as Short

    /**
     * Inserts a short value into the mapping, replacing any existing value for the given key.
     */
    fun putShort(
        key: String,
        value: Short,
    ) {
        mapping[key] = value
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key.
     */
    fun getLong(key: String): Long? = mapping[key] as Long?

    /**
     * Returns the value associated with the given key, or [defaultValue] if no mapping of the desired type exists for the given key.
     */
    fun getLong(
        key: String,
        defaultValue: Long,
    ): Long = mapping.getOrDefault(key, defaultValue) as Long

    /**
     * Inserts a long value into the mapping, replacing any existing value for the given key.
     */
    fun putLong(
        key: String,
        value: Long,
    ) {
        mapping[key] = value
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key.
     */
    fun getDouble(key: String): Double? = mapping[key] as Double?

    /**
     * Returns the value associated with the given key, or [defaultValue] if no mapping of the desired type exists for the given key.
     */
    fun getDouble(
        key: String,
        defaultValue: Double,
    ): Double = mapping.getOrDefault(key, defaultValue) as Double

    /**
     * Inserts a double value into the mapping, replacing any existing value for the given key.
     */
    fun putDouble(
        key: String,
        value: Double,
    ) {
        mapping[key] = value
    }

    /**
     * Returns the value associated with the given key, or null if no mapping of the desired type exists for the given key.
     */
    fun getInstant(key: String): Instant? = mapping[key] as Instant?

    /**
     * Returns the value associated with the given key, or [defaultValue] if no mapping of the desired type exists for the given key.
     */
    fun getInstant(
        key: String,
        defaultValue: Instant,
    ): Instant = mapping.getOrDefault(key, defaultValue) as Instant

    /**
     * Inserts a date value into the mapping, replacing any existing value for the given key.
     */
    fun putInstant(
        key: String,
        value: Instant,
    ) {
        mapping[key] = value
    }
}
