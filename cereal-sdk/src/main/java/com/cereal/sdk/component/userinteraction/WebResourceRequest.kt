package com.cereal.sdk.component.userinteraction

/**
 * Represents a web resource request made during the loading process in a web view. This includes information
 * such as the HTTP method, request headers, and URL of the request.
 *
 * @property method The HTTP method (e.g., GET, POST) used for the request.
 * @property requestHeaders A map containing the headers associated with the request.
 * @property url The URL to which the request is being made.
 * @property postData A map containing key-value pairs of the data sent in the request body for POST requests, or null if not applicable.
 */
data class WebResourceRequest(
    val method: String,
    val requestHeaders: Map<String, String>,
    val url: String,
    val postData: Map<String, String>?,
)
