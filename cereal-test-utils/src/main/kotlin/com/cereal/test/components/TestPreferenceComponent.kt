package com.cereal.test.components

import com.cereal.sdk.component.preference.PreferenceComponent
import com.cereal.test.util.Logger

class TestPreferenceComponent : PreferenceComponent {
    private val logger = Logger("ScriptPreferenceComponent")

    override suspend fun delete(key: String) {
        logger.logMessage("Deleting value from key `$key`")
        inMemoryStorage.remove(key)
    }

    override suspend fun getString(key: String): String? {
        logger.logMessage("Getting string value from key `$key`")

        return inMemoryStorage[key] as? String
    }

    override suspend fun setString(
        key: String,
        value: String,
    ) {
        logger.logMessage("Setting string value from key `$key` with value `$value`")
        inMemoryStorage[key] = value
    }

    override suspend fun getInt(key: String): Int? {
        logger.logMessage("Getting int value from key `$key`")
        return inMemoryStorage[key] as? Int
    }

    override suspend fun setInt(
        key: String,
        value: Int,
    ) {
        logger.logMessage("Setting integer value from key `$key` with value `$value`")
        inMemoryStorage[key] = value
    }

    override suspend fun getLong(key: String): Long? {
        logger.logMessage("Getting long value from key `$key`")
        return inMemoryStorage[key] as? Long
    }

    override suspend fun setLong(
        key: String,
        value: Long,
    ) {
        logger.logMessage("Setting long value from key `$key` with value `$value`")
        inMemoryStorage[key] = value
    }

    override suspend fun getFloat(key: String): Float? {
        logger.logMessage("Getting float value from key `$key`")
        return inMemoryStorage[key] as? Float
    }

    override suspend fun setFloat(
        key: String,
        value: Float,
    ) {
        logger.logMessage("Setting float value from key `$key` with value `$value`")
        inMemoryStorage[key] = value
    }

    override suspend fun getBoolean(key: String): Boolean? {
        logger.logMessage("Getting boolean value from key `$key`")
        return inMemoryStorage[key] as? Boolean
    }

    override suspend fun setBoolean(
        key: String,
        value: Boolean,
    ) {
        logger.logMessage("Setting boolean value from key `$key` with value `$value`")
        inMemoryStorage[key] = value
    }

    companion object {
        private val inMemoryStorage = mutableMapOf<String, Any?>()
    }
}
