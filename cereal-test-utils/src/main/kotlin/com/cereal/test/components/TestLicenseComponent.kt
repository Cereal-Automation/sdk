package com.cereal.test.components

import com.cereal.sdk.component.license.HttpResponse
import com.cereal.sdk.component.license.LicenseComponent

class TestLicenseComponent : LicenseComponent {
    override suspend fun checkScriptLicense(
        publicScriptId: String,
        salt: String,
    ): HttpResponse =
        throw UnsupportedOperationException(
            "Checking script license is not yet supported by the test utils, please mock the LicenseChecker.checkAccess function.",
        )
}
