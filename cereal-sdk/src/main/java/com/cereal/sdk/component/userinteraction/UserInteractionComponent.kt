package com.cereal.sdk.component.userinteraction

import kotlin.time.Duration

interface UserInteractionComponent {
    /**
     * Displays the given URL in a web view with the specified title and handles termination conditions.
     * Optionally supply HTTP request headers that will overwrite (replace) existing headers on each resource request.
     *
     * Backwards compatibility: existing calls without the headers argument continue to work because
     * the [headers] parameter has a default value (emptyMap()).
     *
     * @param title A human-readable title shown in the browser window / dialog.
     * @param url The initial URL to load in the embedded browser.
     * @param headers HTTP request headers to append/overwrite for every outgoing resource request. Pass an
     *                empty map to use the browser defaults. Header keys are treated case-sensitively as provided.
     * @param shouldFinish Callback invoked for each resource request. Return true to finish the interaction; the
     *                     corresponding [WebResourceRequest] will be returned from this suspend function. Returning
     *                     false continues observation.
     * @return The [WebResourceRequest] that satisfied the [shouldFinish] predicate.
     */
    suspend fun showUrl(
        title: String,
        url: String,
        headers: Map<String, String>,
        shouldFinish: (request: WebResourceRequest) -> Boolean,
    ): WebResourceRequest

    // Binary compatibility shim for callers compiled before the headers parameter was introduced.
    suspend fun showUrl(
        title: String,
        url: String,
        shouldFinish: (request: WebResourceRequest) -> Boolean,
    ): WebResourceRequest = showUrl(title, url, emptyMap(), shouldFinish)

    /**
     * Displays the given HTML content in a web view with the specified title and handles termination conditions.
     * The provided HTML is loaded directly (e.g. via an in-memory or data URL) rather than via network navigation.
     *
     * @param title A human-readable title shown in the browser window / dialog.
     * @param html Raw HTML content to display. Ensure it is self‑contained (e.g., inline styles/scripts) if external
     *             resource loading needs to be minimized.
     * @param shouldFinish Callback invoked for each subsequent resource request triggered by this content. Return true
     *                     to finish the interaction; the matching [WebResourceRequest] will be returned. Return false
     *                     to continue observing further requests.
     * @return The [WebResourceRequest] that satisfied the [shouldFinish] predicate.
     */
    suspend fun showHtml(
        title: String,
        html: String,
        shouldFinish: (request: WebResourceRequest) -> Boolean,
    ): WebResourceRequest

    /**
     * Requests a single text input from the user.
     *
     * @param title The title of the dialog or window.
     * @param description A description or prompt for the user explaining what input is needed.
     * @return The text entered by the user.
     */
    suspend fun requestInput(
        title: String,
        description: String,
    ): String

    /**
     * Asks the user to confirm a step, typically an irreversible one (placing an order, paying, sending), and suspends
     * until they answer, the confirmation expires or the caller is cancelled. Use this instead of [showHtml] with
     * links to fake URLs.
     *
     * Contract clients implement:
     * - Every text field is plain text; the client escapes and lays it out per channel. [url] is the only link.
     * - Closing the confirmation without pressing a button returns [ConfirmationResult.Declined].
     * - The client enforces [timeout]: when it passes unanswered, the client tears the confirmation down and returns
     *   [ConfirmationResult.Expired].
     * - Cancelling the caller's coroutine (e.g. the task is stopped) tears the confirmation down in every channel.
     * - A client failure (the confirmation can't be shown) throws; it never returns a result.
     * - The call owns all notifying of the confirmation (e.g. a heads-up on the user's notification channels, remote
     *   approval). Scripts don't notify about it themselves.
     *
     * Scripts calling this must set `"sdk_version": "1.17.0"` (or higher) in `manifest.json`.
     *
     * @param title The window title, e.g. naming the task.
     * @param question The headline: one plain question, e.g. "Place order on amazon.com?".
     * @param details Paragraphs shown in order below the question, e.g. what the step does and why.
     * @param url The page the step is on, shown as a link, or null for none.
     * @param confirmLabel The confirm button's text, e.g. "Yes, place order".
     * @param declineLabel The decline button's text, e.g. "No, stop here".
     * @param timeout How long the confirmation waits for an answer; [Duration.INFINITE] for no expiry.
     * @return How the confirmation ended.
     */
    suspend fun requestConfirmation(
        title: String,
        question: String,
        details: List<String>,
        url: String?,
        confirmLabel: String,
        declineLabel: String,
        timeout: Duration,
    ): ConfirmationResult

    /**
     * Presents a continue button to the user (outside the browser flow). When clicked, the suspended call resumes and
     * script execution proceeds. Use this for simple acknowledgements or manual gating without web navigation.
     */
    suspend fun showContinueButton()
}
