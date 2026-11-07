package com.cereal.test.components

import com.cereal.sdk.component.userinteraction.ConfirmationResult
import com.cereal.sdk.component.userinteraction.UserInteractionComponent
import com.cereal.sdk.component.userinteraction.WebResourceRequest
import kotlin.time.Duration

/**
 * A test implementation of the [UserInteractionComponent] interface for mocking and simulating user interactions
 * such as viewing URLs or HTML content in a web view during the testing process.
 *
 * This component uses predefined lists of [WebResourceRequest] objects to simulate responses for `showUrl`
 * and `showHtml` calls. Each call increments an internal counter to retrieve subsequent
 * predefined responses. These counters ensure sequential access to the mock responses.
 *
 * @param showUrlResults A list of [WebResourceRequest] instances to mock results returned
 * from the `showUrl` method. Defaults to null.
 * @param showHtmlResults A list of [WebResourceRequest] instances to mock results returned
 * from the `showHtml` method. Defaults to null.
 * @param requestInputResults A list of [String] instances to mock results returned
 * from the `requestInput` method. Defaults to null.
 * @param requestConfirmationResults A list of [ConfirmationResult] instances to mock results returned
 * from the `requestConfirmation` method. Defaults to an empty list.
 */
class TestUserInteractionComponent(
    private val showUrlResults: List<WebResourceRequest> = emptyList(),
    private val showHtmlResults: List<WebResourceRequest> = emptyList(),
    private val requestInputResults: List<String> = emptyList(),
    private val requestConfirmationResults: List<ConfirmationResult> = emptyList(),
) : UserInteractionComponent {
    private var showUrlCounter = 0
    private var showHtmlCounter = 0
    private var requestInputCounter = 0
    private var requestConfirmationCounter = 0

    override suspend fun showUrl(
        title: String,
        url: String,
        headers: Map<String, String>,
        shouldFinish: (request: WebResourceRequest) -> Boolean,
    ): WebResourceRequest {
        val result =
            showUrlResults.getOrNull(showUrlCounter)
                ?: throw RuntimeException(
                    "No WebResourceRequest response provided. Provide a WebResourceRequest for index $showUrlCounter in TestUserInteractionComponent.showUrlResults",
                )
        showUrlCounter++

        if (!shouldFinish(result)) {
            throw RuntimeException("shouldFinish returned false for url '${result.url}'.")
        }

        return result
    }

    override suspend fun showHtml(
        title: String,
        html: String,
        shouldFinish: (request: WebResourceRequest) -> Boolean,
    ): WebResourceRequest {
        val result =
            showHtmlResults.getOrNull(showHtmlCounter)
                ?: throw RuntimeException(
                    "No WebResourceRequest response provided. Provide a WebResourceRequest for index $showHtmlCounter in TestUserInteractionComponent.showHtmlResults",
                )
        showHtmlCounter++

        if (!shouldFinish(result)) {
            throw RuntimeException("shouldFinish returned false for url '${result.url}'.")
        }

        return result
    }

    override suspend fun requestInput(
        title: String,
        description: String,
    ): String {
        val result =
            requestInputResults.getOrNull(requestInputCounter)
                ?: throw RuntimeException(
                    "No String response provided. Provide a String for index $requestInputCounter in TestUserInteractionComponent.requestInputResults",
                )
        requestInputCounter++
        return result
    }

    override suspend fun requestConfirmation(
        title: String,
        question: String,
        details: List<String>,
        url: String?,
        confirmLabel: String,
        declineLabel: String,
        timeout: Duration,
    ): ConfirmationResult {
        val result =
            requestConfirmationResults.getOrNull(requestConfirmationCounter)
                ?: throw RuntimeException(
                    "No ConfirmationResult provided. Provide a ConfirmationResult for index $requestConfirmationCounter in TestUserInteractionComponent.requestConfirmationResults",
                )
        requestConfirmationCounter++
        return result
    }

    override suspend fun showContinueButton() {
        // For testing purposes, the continue button just returns immediately
        // without any user interaction simulation
    }
}
