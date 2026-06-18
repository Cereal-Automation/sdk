package com.cereal.test.components

import com.cereal.sdk.component.artifact.ArtifactComponent
import com.cereal.test.util.Logger

/**
 * In-memory [ArtifactComponent] for tests. Records every emitted artifact in [emitted] so assertions can be made
 * after a script run, and logs each emission to the console.
 */
class RecordingArtifactComponent : ArtifactComponent {
    private val logger = Logger("ArtifactComponent")

    /** Artifacts emitted by the script under test, in emission order. */
    val emitted: MutableList<EmittedArtifact> = mutableListOf()

    override suspend fun emit(
        name: String,
        bytes: ByteArray,
        mimeType: String?,
    ) {
        logger.logMessage("Emit artifact '$name' (${bytes.size} bytes, mimeType=${mimeType ?: "<inferred>"}).")
        emitted += EmittedArtifact(name, mimeType, bytes)
    }
}

/** A single artifact captured by [RecordingArtifactComponent]. */
data class EmittedArtifact(
    val name: String,
    val mimeType: String?,
    val bytes: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmittedArtifact) return false
        return name == other.name &&
            mimeType == other.mimeType &&
            bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (mimeType?.hashCode() ?: 0)
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}
