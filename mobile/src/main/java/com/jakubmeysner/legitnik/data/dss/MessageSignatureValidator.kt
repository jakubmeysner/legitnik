package com.jakubmeysner.legitnik.data.dss

import android.content.Context
import com.jakubmeysner.legitnik.R
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.europa.esig.dss.cades.signature.CMSSignedDocument
import eu.europa.esig.dss.model.x509.CertificateToken
import eu.europa.esig.dss.service.crl.OnlineCRLSource
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource
import eu.europa.esig.dss.spi.x509.CommonCertificateSource
import eu.europa.esig.dss.validation.CommonCertificateVerifier
import eu.europa.esig.dss.validation.SignedDocumentValidator
import eu.europa.esig.dss.validation.reports.Reports
import org.bouncycastle.cms.CMSSignedData
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageSignatureValidator @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val trustedListsCertificateSourceSupplier: TrustedListsCertificateSourceSupplier,
) {
    fun validate(message: CMSSignedData, certificate: X509Certificate): Reports {
        val document = CMSSignedDocument(message)

        val signingCertificateSource = CommonCertificateSource().apply {
            addCertificate(CertificateToken(certificate))
        }

        val certificateVerifier = CommonCertificateVerifier().apply {
            ocspSource = OnlineOCSPSource()
            crlSource = OnlineCRLSource()
            addTrustedCertSources(trustedListsCertificateSourceSupplier.get())
        }

        val documentValidator = SignedDocumentValidator.fromDocument(document).apply {
            setSigningCertificateSource(signingCertificateSource)
            setCertificateVerifier(certificateVerifier)
        }

        return applicationContext.resources.openRawResource(R.raw.dss_validation_policy).use {
            documentValidator.validateDocument(it)
        }
    }
}
