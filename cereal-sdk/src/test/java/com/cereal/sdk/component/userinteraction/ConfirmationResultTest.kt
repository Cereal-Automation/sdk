package com.cereal.sdk.component.userinteraction

import org.junit.Test
import kotlin.test.assertEquals

class ConfirmationResultTest {
    @Test
    fun `a confirmation ends confirmed, declined or expired and nothing else`() {
        // Clients and scripts branch on exactly these three; a fourth (or a channel) breaks the contract.
        assertEquals(listOf("Confirmed", "Declined", "Expired"), ConfirmationResult.entries.map { it.name })
    }
}
