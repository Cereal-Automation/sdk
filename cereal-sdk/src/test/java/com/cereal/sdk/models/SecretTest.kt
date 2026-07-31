package com.cereal.sdk.models

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The root guarantee of the secret feature: every masking behaviour downstream in the platform is a
 * consequence of [Secret.toString] never returning the wrapped value.
 */
class SecretTest {
    @Test
    fun `toString returns a fixed mask instead of the wrapped value`() {
        assertEquals("***", Secret("hunter2").toString())
    }

    @Test
    fun `the mask is fixed and does not vary with the wrapped value`() {
        assertEquals(Secret("short").toString(), Secret("a-much-longer-api-key").toString())
    }

    @Test
    fun `string interpolation renders the mask`() {
        val secret = Secret("hunter2")

        assertEquals("Authenticating with ***", "Authenticating with $secret")
    }

    @Test
    fun `an empty secret still renders the mask`() {
        assertEquals("***", Secret("").toString())
    }

    @Test
    fun `reveal returns the wrapped value`() {
        assertEquals("hunter2", Secret("hunter2").reveal())
    }

    @Test
    fun `secrets wrapping the same value are equal and share a hash code`() {
        val one = Secret("hunter2")
        val other = Secret("hunter2")

        assertEquals(one, other)
        assertEquals(one.hashCode(), other.hashCode())
    }

    @Test
    fun `secrets wrapping different values are not equal`() {
        assertNotEquals(Secret("hunter2"), Secret("hunter3"))
    }

    @Test
    fun `a secret is not equal to its own plaintext`() {
        assertNotEquals<Any>(Secret("hunter2"), "hunter2")
    }

    @Test
    fun `a secret is not equal to null or another type`() {
        val secret = Secret("hunter2")

        assertFalse(secret.equals(null))
        assertFalse(secret.equals(42))
    }

    @Test
    fun `a secret equals itself`() {
        val secret = Secret("hunter2")

        assertEquals(secret, secret)
    }

    // The type is deliberately not a data class: a generated component function would let a caller
    // destructure the plaintext out with no reveal() at the call site, which is the one thing every
    // downstream mask depends on. A generated copy function would be meaningless besides.
    @Test
    fun `no generated component function can hand out the plaintext`() {
        val componentFunctions =
            Secret::class.java.methods.filter { it.name.startsWith("component") }

        assertTrue(
            componentFunctions.isEmpty(),
            "Secret must not be a data class; found $componentFunctions",
        )
    }

    @Test
    fun `no generated copy function is exposed`() {
        assertNull(Secret::class.java.methods.find { it.name == "copy" })
    }

    // Not an inline value class: the platform reflects over configuration interfaces and drives them
    // through a dynamic proxy, which inline-class name mangling and return-type unboxing would break.
    @Test
    fun `a configuration function returning a secret keeps an unmangled name and boxed return type`() {
        val method = SecretReturning::class.java.getDeclaredMethod("apiKey")

        assertEquals(Secret::class.java, method.returnType)
    }

    private interface SecretReturning {
        fun apiKey(): Secret
    }
}
