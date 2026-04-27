package com.cereal.sdk.component.userinteraction

/**
 * Represents an exception that occurs during the processing of a web resource.
 *
 * This exception is used in conjunction with web resource handling operations
 * in a web view, such as loading a URL or rendering HTML content. It provides details
 * about the error including the URL where the failure occurred, an error message, and
 * an associated error code.
 *
 * @property failedUrl The URL of the web resource that caused the exception.
 * @property errorText A description of the error that occurred.
 * @property errorCode A numerical code representing the type of error.
 */
class WebResourceException(
    val failedUrl: String,
    val errorText: String,
    val errorCode: Int,
) : Exception(errorText)
