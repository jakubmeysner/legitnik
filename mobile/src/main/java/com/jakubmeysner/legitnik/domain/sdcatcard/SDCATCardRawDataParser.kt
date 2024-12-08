package com.jakubmeysner.legitnik.domain.sdcatcard

import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardParsedContent
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardParsedData
import com.jakubmeysner.legitnik.data.sdcatcard.SDCATCardRawDataInterface
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
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory
import java.security.cert.X509Certificate

fun SDCATCardRawDataInterface.toParsed(): SDCATCardParsedData {
    val message = CMSSignedData(rawMessage.toByteArray())

    return SDCATCardParsedData(
        message = message,
        content = parseContent(type, message),
        certificate = CertificateFactory().engineGenerateCertificate(
            rawCertificate.toByteArray().inputStream()
        ) as X509Certificate,
    )
}

fun parseContent(type: SDCATCardType, message: CMSSignedData): SDCATCardParsedContent {
    val sequence = ASN1Sequence.getInstance(message.signedContent.content)

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
            SDCATCardParsedContent.DoctoralCandidateCardParsedContent(
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
            SDCATCardParsedContent.StudentCardParsedBasicContent(
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
        return SDCATCardParsedContent.AcademicTeacherCardParsedBasicContent(
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
        SDCATCardParsedContent.AcademicTeacherCardParsedExtendedContent(
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

        SDCATCardParsedContent.StudentCardParsedExtendedContent(
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
