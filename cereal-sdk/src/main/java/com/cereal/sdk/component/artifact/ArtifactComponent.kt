package com.cereal.sdk.component.artifact

/**
 * Component through which a running script produces **artifacts**: named blobs of binary output (a CSV export, a
 * JSON report, a generated image, …) that the host persists against the current task and that the user can download
 * from the application after the run.
 *
 * This is the script's channel for *output the user keeps*, as opposed to:
 * - [com.cereal.sdk.component.logger.LoggerComponent] / the `statusUpdate` callback — transient text for progress.
 * - [com.cereal.sdk.component.notification.NotificationComponent] — out-of-band alerts.
 * - [com.cereal.sdk.component.preference.PreferenceComponent] — small key/value state the *script* reads back later.
 *
 * Semantics:
 * - **Append-only.** Every call to [emit] produces a new, distinct artifact scoped to the current task. Emitting the
 *   same [name] twice yields two artifacts; it never replaces a previous one.
 * - **Persisted.** Emitted artifacts outlive the run and the application session; they remain available for download
 *   until the user deletes the owning task.
 * - **In-memory payload.** The content is passed as a [ByteArray] held in memory, so this is suited to small, bounded
 *   outputs. Avoid emitting payloads large enough to exhaust the heap.
 */
interface ArtifactComponent {
    /**
     * Emits an artifact for the current task, making it available for the user to download once the run completes.
     *
     * The call returns once the artifact has been durably persisted by the host. If persistence fails (for example,
     * the host cannot write to disk) this throws, surfacing the failure to the script — the artifact is **not**
     * stored. A script that treats an artifact as optional should wrap this call in its own `try`/`catch`; otherwise
     * an uncaught failure terminates the run with an error, as with any other thrown exception.
     *
     * @param name Display name for the artifact, also used as the default filename when the user downloads it
     *             (e.g. `"results.csv"`). Include the extension you want the file to have.
     * @param bytes The artifact content. Held in memory, so keep it reasonably sized.
     * @param mimeType Optional MIME type (e.g. `"text/csv"`, `"application/json"`). When `null` the host infers it
     *                 from the [name]'s extension where possible.
     */
    suspend fun emit(
        name: String,
        bytes: ByteArray,
        mimeType: String? = null,
    )
}
