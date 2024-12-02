package com.jakubmeysner.legitnik.data.sdcatcard

import eu.europa.esig.dss.enumerations.SubIndication

data class SDCATCardValidationResult(
    val signatureValid: Boolean,
    val signatureSubIndication: SubIndication?,
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
