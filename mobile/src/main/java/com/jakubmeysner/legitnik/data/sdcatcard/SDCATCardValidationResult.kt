package com.jakubmeysner.legitnik.data.sdcatcard

import eu.europa.esig.dss.validation.reports.Reports

data class SDCATCardValidationResult(
    val signatureValidationReports: Reports,
    val signatureValid: Boolean,
    val issuerMatchesCertificateSubject: Boolean,
    val certificateSubjectAuthorized: Boolean,
    val notExpired: Boolean,
) {
    val valid: Boolean
        get() = signatureValid
            && issuerMatchesCertificateSubject
            && certificateSubjectAuthorized
            && notExpired
}
