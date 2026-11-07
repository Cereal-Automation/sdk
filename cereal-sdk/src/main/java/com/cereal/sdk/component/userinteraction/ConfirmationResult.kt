package com.cereal.sdk.component.userinteraction

/**
 * How a [UserInteractionComponent.requestConfirmation] ended. It doesn't say which channel (the window, a remote
 * approval) answered.
 */
enum class ConfirmationResult {
    /** The user pressed the confirm button. */
    Confirmed,

    /** The user pressed the decline button or closed the confirmation. */
    Declined,

    /** Nobody answered within the timeout. Treat it like [Declined]. */
    Expired,
}
