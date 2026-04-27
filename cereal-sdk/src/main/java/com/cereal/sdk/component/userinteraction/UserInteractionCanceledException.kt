package com.cereal.sdk.component.userinteraction

/**
 * Thrown when a user cancels an interaction or flow that requires user input or action.
 *
 * This exception is typically used in contexts where user interaction is required, such as
 * displaying a web view or requesting user input. It is meant to notify the script
 * logic that the user has intentionally terminated the interaction.
 */
class UserInteractionCanceledException : Exception("The user canceled the request for information.")
