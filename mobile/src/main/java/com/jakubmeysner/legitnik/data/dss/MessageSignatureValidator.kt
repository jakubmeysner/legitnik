package com.jakubmeysner.legitnik.data.dss

import android.content.Context
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.europa.esig.dss.cades.signature.CMSSignedDocument
import eu.europa.esig.dss.model.x509.CertificateToken
import eu.europa.esig.dss.service.crl.OnlineCRLSource
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource
import eu.europa.esig.dss.spi.x509.CommonCertificateSource
import eu.europa.esig.dss.validation.CommonCertificateVerifier
import eu.europa.esig.dss.validation.SignedDocumentValidator
import eu.europa.esig.dss.validation.reports.Reports
import org.bouncycastle.cms.CMSSignedData
import java.io.File
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.days

@Singleton
class MessageSignatureValidator @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val trustedListsCertificateSourceSupplier: TrustedListsCertificateSourceSupplier,
) : ClassSimpleNameLoggingTag {
    private val ocspDataLoader = FileCacheDataLoader().apply {
        setCacheExpirationTime(OCSP_FILE_CACHE_EXPIRATION_TIME.inWholeMilliseconds)
        dataLoader = OCSPDataLoader()
    }

    private val cachedOcspSource = OnlineOCSPSource(ocspDataLoader)

    private val crlDataLoader = FileCacheDataLoader().apply {
        setCacheExpirationTime(CRL_FILE_CACHE_EXPIRATION_TIME.inWholeMilliseconds)
        dataLoader = CommonsDataLoader()
    }

    private val cachedCrlSource = OnlineCRLSource(crlDataLoader)

    private fun prepare() {
        ocspDataLoader.setFileCacheDirectory(
            File(
                applicationContext.cacheDir,
                OCSP_FILE_CACHE_DIRECTORY_NAME
            )
        )

        crlDataLoader.setFileCacheDirectory(
            File(
                applicationContext.cacheDir,
                CRL_FILE_CACHE_DIRECTORY_NAME
            )
        )
    }

    fun validate(message: CMSSignedData, certificate: X509Certificate): Reports {
        prepare()
        val document = CMSSignedDocument(message)

        val signingCertificateSource = CommonCertificateSource().apply {
            addCertificate(CertificateToken(certificate))
        }

        val certificateVerifier = CommonCertificateVerifier().apply {
            ocspSource = cachedOcspSource
            crlSource = cachedCrlSource
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

    companion object {
        const val OCSP_FILE_CACHE_DIRECTORY_NAME = "ocsp_2b23c377"
        val OCSP_FILE_CACHE_EXPIRATION_TIME = 1.days
        const val CRL_FILE_CACHE_DIRECTORY_NAME = "crl_99d7412b"
        val CRL_FILE_CACHE_EXPIRATION_TIME = 1.days
    }
}
