package com.jakubmeysner.legitnik.domain.sdcatcard

import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardRawData
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardType
import org.bouncycastle.asn1.ASN1BitString
import org.bouncycastle.asn1.ASN1GeneralizedTime
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.ASN1ObjectIdentifier
import org.bouncycastle.asn1.ASN1OctetString
import org.bouncycastle.asn1.ASN1PrintableString
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.ASN1UTF8String
import org.bouncycastle.cms.CMSSignedData

fun SDCATCardRawData.toParsed(): SDCATCardParsedData {
    val cms = CMSSignedData(this.rawMessage.toByteArray())
    val sequence = ASN1Sequence.getInstance(cms.signedContent.content)

    val version = (sequence.getObjectAt(0) as ASN1Integer).intValueExact()

    if (
        when (type) {
            SDCATCardType.STUDENT -> version !in listOf(1, 2)
            SDCATCardType.DOCTORAL_CANDIDATE -> version != 1
            SDCATCardType.ACADEMIC_TEACHER -> version !in listOf(3, 4)
        }
    ) {
        throw IllegalArgumentException("Unsupported raw data version")
    }

    val chipSerialNumber = (sequence.getObjectAt(1) as ASN1PrintableString).string
    val universityOrIssuerName = (sequence.getObjectAt(2) as ASN1UTF8String).string

    val surname = (sequence.getObjectAt(3) as ASN1Sequence).objects.asSequence()
        .map { (it as ASN1UTF8String).string }
        .toList()

    val givenNames = (sequence.getObjectAt(4) as ASN1Sequence).objects.asSequence()
        .map { (it as ASN1UTF8String).string }
        .toList()

    val albumOrCardNumber = (sequence.getObjectAt(5) as ASN1PrintableString).string
    val editionNumber = (sequence.getObjectAt(6) as ASN1PrintableString).string

    val expiryDate = (sequence.getObjectAt(
        if (type == SDCATCardType.ACADEMIC_TEACHER) 7 else 8
    ) as ASN1GeneralizedTime).date

    if (
        (type == SDCATCardType.STUDENT && version == 1) || type == SDCATCardType.DOCTORAL_CANDIDATE
    ) {
        val peselNumber = (sequence.getObjectAt(7) as ASN1PrintableString).string

        return if (type == SDCATCardType.DOCTORAL_CANDIDATE) {
            SDCATCardParsedData.DoctoralCandidateCardParsedData(
                version,
                chipSerialNumber,
                universityOrIssuerName,
                surname,
                givenNames,
                albumOrCardNumber,
                editionNumber,
                peselNumber,
                expiryDate,
            )
        } else {
            SDCATCardParsedData.StudentCardParsedBasicData(
                version,
                chipSerialNumber,
                universityOrIssuerName,
                surname,
                givenNames,
                albumOrCardNumber,
                editionNumber,
                peselNumber,
                expiryDate,
            )
        }
    }

    val startIndex = if (type == SDCATCardType.ACADEMIC_TEACHER) 8 else 9
    val issueDate = (sequence.getObjectAt(startIndex + 0) as ASN1GeneralizedTime).date

    if (type == SDCATCardType.ACADEMIC_TEACHER && version == 3) {
        return SDCATCardParsedData.AcademicTeacherCardParsedBasicData(
            version,
            chipSerialNumber,
            universityOrIssuerName,
            surname,
            givenNames,
            albumOrCardNumber,
            editionNumber,
            expiryDate,
            issueDate,
        )
    }

    val cancellationUrl = (sequence.getObjectAt(startIndex + 1) as ASN1UTF8String).string
    val photoHashFunction = sequence.getObjectAt(startIndex + 2) as ASN1ObjectIdentifier
    val photoHash = sequence.getObjectAt(startIndex + 3) as ASN1BitString
    val photoEfIdentifier = sequence.getObjectAt(startIndex + 4) as ASN1OctetString

    return if (type == SDCATCardType.ACADEMIC_TEACHER) {
        SDCATCardParsedData.AcademicTeacherCardParsedExtendedData(
            version,
            chipSerialNumber,
            universityOrIssuerName,
            surname,
            givenNames,
            albumOrCardNumber,
            editionNumber,
            expiryDate,
            issueDate,
            cancellationUrl,
            photoHashFunction,
            photoHash,
            photoEfIdentifier,
        )
    } else {
        val peselNumber = (sequence.getObjectAt(7) as ASN1PrintableString).string

        SDCATCardParsedData.StudentCardParsedExtendedData(
            version,
            chipSerialNumber,
            universityOrIssuerName,
            surname,
            givenNames,
            albumOrCardNumber,
            editionNumber,
            peselNumber,
            expiryDate,
            issueDate,
            cancellationUrl,
            photoHashFunction,
            photoHash,
            photoEfIdentifier,
        )
    }
}
