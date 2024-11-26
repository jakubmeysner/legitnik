package com.jakubmeysner.legitnik.domain.sdcatcard

import org.bouncycastle.asn1.ASN1BitString
import org.bouncycastle.asn1.ASN1ObjectIdentifier
import org.bouncycastle.asn1.ASN1OctetString
import java.util.Date

sealed interface SDCATCardParsedData {
    val version: Int
    val chipSerialNumber: String
    val universityOrIssuerName: String
    val surname: List<String>
    val givenNames: List<String>
    val albumOrCardNumber: String
    val editionNumber: String
    val expiryDate: Date

    sealed interface SDCATCardParsedDataWithPeselNumber : SDCATCardParsedData {
        val peselNumber: String
    }

    sealed interface SDCATCardParsedDataWithIssueDate : SDCATCardParsedData {
        val issueDate: Date
    }

    sealed interface SDCATCardParsedDataWithCancellationUrlAndPhotoMetadata :
        SDCATCardParsedDataWithIssueDate {
        val cancellationUrl: String
        val photoHashFunction: ASN1ObjectIdentifier
        val photoHash: ASN1BitString
        val photoEfIdentifier: ASN1OctetString
    }

    sealed interface StudentCardParsedData : SDCATCardParsedDataWithPeselNumber

    data class StudentCardParsedBasicData(
        override val version: Int,
        override val chipSerialNumber: String,
        override val universityOrIssuerName: String,
        override val surname: List<String>,
        override val givenNames: List<String>,
        override val albumOrCardNumber: String,
        override val editionNumber: String,
        override val peselNumber: String,
        override val expiryDate: Date,
    ) : StudentCardParsedData

    data class StudentCardParsedExtendedData(
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
    ) : StudentCardParsedData, SDCATCardParsedDataWithCancellationUrlAndPhotoMetadata

    data class DoctoralCandidateCardParsedData(
        override val version: Int,
        override val chipSerialNumber: String,
        override val universityOrIssuerName: String,
        override val surname: List<String>,
        override val givenNames: List<String>,
        override val albumOrCardNumber: String,
        override val editionNumber: String,
        override val peselNumber: String,
        override val expiryDate: Date,
    ) : SDCATCardParsedDataWithPeselNumber

    sealed interface AcademicTeacherCardParsedData : SDCATCardParsedDataWithIssueDate

    data class AcademicTeacherCardParsedBasicData(
        override val version: Int,
        override val chipSerialNumber: String,
        override val universityOrIssuerName: String,
        override val surname: List<String>,
        override val givenNames: List<String>,
        override val albumOrCardNumber: String,
        override val editionNumber: String,
        override val expiryDate: Date,
        override val issueDate: Date,
    ) : AcademicTeacherCardParsedData

    data class AcademicTeacherCardParsedExtendedData(
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
    ) : AcademicTeacherCardParsedData, SDCATCardParsedDataWithCancellationUrlAndPhotoMetadata
}
