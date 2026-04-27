package com.cereal.sdk.component.userinteraction

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
     * Presents a continue button to the user (outside the browser flow). When clicked, the suspended call resumes and
     * script execution proceeds. Use this for simple acknowledgements or manual gating without web navigation.
     */
    suspend fun showContinueButton()
}
