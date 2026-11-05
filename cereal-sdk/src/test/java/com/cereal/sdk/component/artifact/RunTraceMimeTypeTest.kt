package com.cereal.sdk.component.artifact

import org.junit.Test
import kotlin.test.assertEquals

class RunTraceMimeTypeTest {
    @Test
    fun `run trace MIME type matches the one clients route to diagnostics`() {
        // The client matches this exact string (desktop-client RunTrace.kt); changing it breaks the contract.
        assertEquals("application/vnd.cereal.run-trace", RUN_TRACE_MIME_TYPE)
    }
}
