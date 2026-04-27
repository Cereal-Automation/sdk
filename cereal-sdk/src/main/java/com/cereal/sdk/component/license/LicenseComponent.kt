package com.cereal.sdk.component.license

import java.io.IOException

/**
 * The license component is used to verify if the user of your script has a valid license. This component shouldn't be
 * used directly but instead provided to the Cereal Licensing Library. See the licensing documentation for more info.
 */
interface LicenseComponent {
    @Throws(IOException::class)
    suspend fun checkScriptLicense(
        publicScriptId: String,
        salt: String,
    ): HttpResponse
}
