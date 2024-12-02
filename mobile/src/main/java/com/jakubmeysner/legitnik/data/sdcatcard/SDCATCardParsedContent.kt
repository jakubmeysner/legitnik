package com.jakubmeysner.legitnik.data.sdcatcard

import org.bouncycastle.asn1.ASN1BitString
import org.bouncycastle.asn1.ASN1ObjectIdentifier
import org.bouncycastle.asn1.ASN1OctetString
import java.util.Date

sealed interface SDCATCardParsedContent {
    val version: Int
    val chipSerialNumber: String
    val universityOrIssuerName: String
    val surname: List<String>
    val givenNames: List<String>
    val albumOrCardNumber: String
    val editionNumber: String
    val expiryDate: Date

    sealed interface SDCATCardParsedContentWithPeselNumber : SDCATCardParsedContent {
        val peselNumber: String
    }

    sealed interface SDCATCardParsedContentWithIssueDate : SDCATCardParsedContent {
        val issueDate: Date
    }

    sealed interface SDCATCardParsedContentWithCancellationUrlAndPhotoMetadata :
        SDCATCardParsedContentWithIssueDate {
        val cancellationUrl: String
        val photoHashFunction: ASN1ObjectIdentifier
        val photoHash: ASN1BitString
        val photoEfIdentifier: ASN1OctetString
    }

    sealed interface StudentCardParsedContent : SDCATCardParsedContentWithPeselNumber

    data class StudentCardParsedBasicContent(
        override val version: Int,
        override val chipSerialNumber: String,
        override val universityOrIssuerName: String,
        override val surname: List<String>,
        override val givenNames: List<String>,
        override val albumOrCardNumber: String,
        override val editionNumber: String,
        override val peselNumber: String,
        override val expiryDate: Date,
    ) : StudentCardParsedContent

    data class StudentCardParsedExtendedContent(
        override val version: Int,
        override val chipSerialNumber: String,
        override val universityOrIssuerName: String,
        override val surname: List<String>,
        override val givenNames: List<String>,
        override val albumOrCardNumber: String,
        override val editionNumber: String,
        override val peselNumber: String,
        override val expiryDate: Date,
        override val issueDate: Date,
        override val cancellationUrl: String,
        override val photoHashFunction: ASN1ObjectIdentifier,
        override val photoHash: ASN1BitString,
        override val photoEfIdentifier: ASN1OctetString,
    ) : StudentCardParsedContent, SDCATCardParsedContentWithCancellationUrlAndPhotoMetadata

    data class DoctoralCandidateCardParsedContent(
        override val version: Int,
        override val chipSerialNumber: String,
        override val universityOrIssuerName: String,
        override val surname: List<String>,
        override val givenNames: List<String>,
        override val albumOrCardNumber: String,
        override val editionNumber: String,
        override val peselNumber: String,
        override val expiryDate: Date,
    ) : SDCATCardParsedContentWithPeselNumber

    sealed interface AcademicTeacherCardParsedContent : SDCATCardParsedContentWithIssueDate

    data class AcademicTeacherCardParsedBasicContent(
        override val version: Int,
        override val chipSerialNumber: String,
        override val universityOrIssuerName: String,
        override val surname: List<String>,
        override val givenNames: List<String>,
        override val albumOrCardNumber: String,
        override val editionNumber: String,
        override val expiryDate: Date,
        override val issueDate: Date,
    ) : AcademicTeacherCardParsedContent

    data class AcademicTeacherCardParsedExtendedContent(
        override val version: Int,
        override val chipSerialNumber: String,
        override val universityOrIssuerName: String,
        override val surname: List<String>,
        override val givenNames: List<String>,
        override val albumOrCardNumber: String,
        override val editionNumber: String,
        override val expiryDate: Date,
        override val issueDate: Date,
        override val cancellationUrl: String,
        override val photoHashFunction: ASN1ObjectIdentifier,
        override val photoHash: ASN1BitString,
        override val photoEfIdentifier: ASN1OctetString,
    ) : AcademicTeacherCardParsedContent, SDCATCardParsedContentWithCancellationUrlAndPhotoMetadata
}
