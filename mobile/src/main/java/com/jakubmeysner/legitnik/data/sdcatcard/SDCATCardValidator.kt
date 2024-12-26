package com.jakubmeysner.legitnik.data.sdcatcard

import com.jakubmeysner.legitnik.data.dss.MessageSignatureValidator
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x500.style.BCStyle
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SDCATCardValidator @Inject constructor(
    private val messageSignatureValidator: MessageSignatureValidator,
) {
    fun getValidationResult(data: SDCATCardDataInterface): SDCATCardValidationResult {
        val signatureValidationReports = messageSignatureValidator.validate(
            data.parsedData.message,
            data.parsedData.certificate
        )

        val name = X500Name(data.parsedData.certificate.subjectX500Principal.name)

        val organizations: List<String> = name.getRDNs(BCStyle.O).flatMap { rdn ->
            rdn.typesAndValues.map { it.value.toString() }
        }

        val commonNames: List<String> = name.getRDNs(BCStyle.CN).flatMap { rdn ->
            rdn.typesAndValues.map { it.value.toString() }
        }

        val issuerMatchesCertificateSubject = organizations.map { it.lowercase() }
            .contains(data.parsedData.content.universityOrIssuerName.lowercase())

        val authorizationStrings = COMMON_NAME_AUTHORIZATION_STRINGS.getValue(data.rawData.type)
            .map { it.lowercase() }

        val certificateSubjectAuthorized = commonNames.map { it.lowercase() }
            .any { authorizationStrings.contains(it) }

        val notExpired = data.parsedData.content.expiryDate.after(Date())

        val result = SDCATCardValidationResult(
            signatureValidationReports = signatureValidationReports,
            signatureValid = signatureValidationReports.simpleReport.isValid(
                signatureValidationReports.simpleReport.firstSignatureId
            ),
            issuerMatchesCertificateSubject = issuerMatchesCertificateSubject,
            certificateSubjectAuthorized = certificateSubjectAuthorized,
            notExpired = notExpired,
        )

        return result
    }

    companion object {
        val COMMON_NAME_AUTHORIZATION_STRINGS = mapOf(
            SDCATCardType.STUDENT to listOf(
                "osoba upoważniona do wystawiania legitymacji studenckiej"
            ),
            SDCATCardType.DOCTORAL_CANDIDATE to listOf(
                "osoba upoważniona do wystawiania legitymacji doktoranta"
            ),
            SDCATCardType.ACADEMIC_TEACHER to listOf(
                "upoważniony do wystawiania legitymacji",
                "upoważniona do wystawiania legitymacji"
            ),
        )
    }
}
