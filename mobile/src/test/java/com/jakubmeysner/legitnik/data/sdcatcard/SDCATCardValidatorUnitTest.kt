package com.jakubmeysner.legitnik.data.sdcatcard

import com.jakubmeysner.legitnik.data.dss.MessageSignatureValidator
import eu.europa.esig.dss.validation.reports.Reports
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.util.Date
import java.util.UUID
import javax.security.auth.x500.X500Principal
import kotlin.random.Random

class SDCATCardValidatorUnitTest {
    private lateinit var messageSignatureValidatorMock: MessageSignatureValidator
    private lateinit var validator: SDCATCardValidator
    private lateinit var testData: SDCATCardData
    private lateinit var reportsMock: Reports

    @Before
    fun setUp() {
        messageSignatureValidatorMock = mockk()
        validator = SDCATCardValidator(messageSignatureValidatorMock)
        testData = createTestData()
        reportsMock = mockk()

        coEvery {
            messageSignatureValidatorMock.validate(
                testData.parsedData.message,
                testData.parsedData.certificate
            )
        } returns reportsMock

        every {
            reportsMock.simpleReport.firstSignatureId
        } returns testFirstSignatureId

        every {
            reportsMock.simpleReport.isValid(testFirstSignatureId)
        } returns true

        every {
            testData.parsedData.certificate.subjectX500Principal
        } returns X500Principal(
            "CN=${
                SDCATCardValidator.COMMON_NAME_AUTHORIZATION_STRINGS[testData.rawData.type]?.get(0)
            }, O=${testData.parsedData.content.universityOrIssuerName}"
        )
    }

    @Test
    fun `getValidationResult should report data as fully valid when it is`() = runTest {
        assertEquals(
            SDCATCardValidationResult(
                signatureValidationReports = reportsMock,
                signatureValid = true,
                issuerMatchesCertificateSubject = true,
                certificateSubjectAuthorized = true,
                notExpired = true,
            ),
            validator.getValidationResult(testData)
        )
    }

    @Test
    fun `getValidationResult should report invalid signature`() = runTest {
        every {
            reportsMock.simpleReport.isValid(testFirstSignatureId)
        } returns false

        assertFalse(validator.getValidationResult(testData).signatureValid)
    }

    @Test
    fun `getValidationResult should report issuer and certificate subject mismatch`() = runTest {
        assertFalse(
            validator.getValidationResult(
                testData.copy(
                    parsedData = testData.parsedData.copy(
                        content = (
                            testData.parsedData.content
                                as SDCATCardParsedContent.DoctoralCandidateCardParsedContent
                            ).copy(
                                universityOrIssuerName = "Uniwersytet Warszawski"
                            )
                    )
                )
            ).issuerMatchesCertificateSubject
        )
    }

    @Test
    fun `getValidationResult should report unauthorized certificate subject`() = runTest {
        every {
            testData.parsedData.certificate.subjectX500Principal
        } returns X500Principal(
            "CN=${
                SDCATCardValidator.COMMON_NAME_AUTHORIZATION_STRINGS[SDCATCardType.STUDENT]?.get(0)
            }, O=${testData.parsedData.content.universityOrIssuerName}"
        )

        assertFalse(validator.getValidationResult(testData).certificateSubjectAuthorized)
    }

    @Test
    fun `getValidationResult should report expired card`() = runTest {
        assertFalse(
            validator.getValidationResult(
                testData.copy(
                    parsedData = testData.parsedData.copy(
                        content = (
                            testData.parsedData.content
                                as SDCATCardParsedContent.DoctoralCandidateCardParsedContent
                            ).copy(
                                expiryDate = Date().apply {
                                    year -= 1
                                }
                            )
                    )
                )
            ).notExpired
        )
    }

    companion object {
        private val testFirstSignatureId = UUID.randomUUID().toString()

        private fun createTestData(): SDCATCardData {
            return SDCATCardData(
                rawData = SDCATCardRawData(
                    type = SDCATCardType.DOCTORAL_CANDIDATE,
                    rawMessage = Random.nextBytes(10).toList(),
                    rawCertificate = Random.nextBytes(10).toList(),
                ),
                parsedData = SDCATCardParsedData(
                    message = mockk(),
                    content = SDCATCardParsedContent.DoctoralCandidateCardParsedContent(
                        version = 1,
                        chipSerialNumber = "ABC123",
                        universityOrIssuerName = "Uniwersytet Podlaski w Warszawie",
                        surname = listOf("Kowalski"),
                        givenNames = listOf("Jan"),
                        albumOrCardNumber = "123456",
                        editionNumber = "A",
                        peselNumber = "67062315846",
                        expiryDate = Date().apply {
                            year += 1
                        },
                    ),
                    certificate = mockk(),
                ),
            )
        }
    }
}
