package com.cereal.sdk.component.preference

/**
 * Interface for managing preferences with support for various data types.
 *
 * This component allows storing, retrieving, and deleting key-value pairs of preferences,
 * with support for common data types, such as String, Int, Long, Float, and Boolean.
 */
interface PreferenceComponent {
    /**
     * Deletes the preference associated with the given key.
     *
     * @param key The key of the preference to be deleted.
     */
    suspend fun delete(key: String)

    /**
     * Retrieves the String value associated with the specified key.
     *
     * @param key The key of the preference to be retrieved.
     * @return The String value associated with the given key, or null if the key does not exist.
     */
    suspend fun getString(key: String): String?

    /**
     * Stores a string value associated with the given key.
     *
     * @param key The key with which the specified value is to be associated.
     * @param value The string value to be stored.
     */
    suspend fun setString(
        key: String,
        value: String,
    )

    /**
     * Retrieves the Int value associated with the specified key.
     *
     * @param key The key of the preference to be retrieved.
     * @return The Int value associated with the given key, or null if the key does not exist.
     */
    suspend fun getInt(key: String): Int?

    /**
     * Stores an integer value associated with the given key.
     *
     * @param key The key with which the specified value is to be associated.
     * @param value The integer value to be stored.
     */
    suspend fun setInt(
        key: String,
        value: Int,
    )

    /**
     * Retrieves the Long value associated with the specified key.
     *
     * @param key The key of the preference to be retrieved.
     * @return The Long value associated with the given key, or null if the key does not exist.
     */
    suspend fun getLong(key: String): Long?

    /**
     * Stores a long value associated with the given key.
     *
     * @param key The key with which the specified value is to be associated.
     * @param value The long value to be stored.
     */
    suspend fun setLong(
        key: String,
        value: Long,
    )

    /**
     * Retrieves the Float value associated with the specified key.
     *
     * @param key The key of the preference to be retrieved.
     * @return The Float value associated with the given key, or null if the key does not exist.
     */
    suspend fun getFloat(key: String): Float?

    /**
     * Stores a float value associated with the given key.
     *
     * @param key The key with which the specified value is to be associated.
     * @param value The float value to be stored.
     */
    suspend fun setFloat(
        key: String,
        value: Float,
    )

    /**
     * Retrieves the Boolean value associated with the specified key.
     *
     * @param key The key of the preference to be retrieved.
     * @return The Boolean value associated with the given key, or null if the key does not exist.
     */
    suspend fun getBoolean(key: String): Boolean?

    /**
     * Stores a boolean value associated with the given key.
     *
     * @param key The key with which the specified value is to be associated.
     * @param value The boolean value to be stored.
     */
    suspend fun setBoolean(
        key: String,
        value: Boolean,
    )
}
