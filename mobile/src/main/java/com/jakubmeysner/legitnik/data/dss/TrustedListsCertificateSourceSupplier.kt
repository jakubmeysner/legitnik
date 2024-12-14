package com.jakubmeysner.legitnik.data.dss

import android.content.Context
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.util.ClassSimpleNameLoggingTag
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.europa.esig.dss.model.x509.CertificateToken
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource
import eu.europa.esig.dss.spi.x509.CommonCertificateSource
import eu.europa.esig.dss.tsl.job.TLValidationJob
import eu.europa.esig.dss.tsl.source.TLSource
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory
import java.io.File
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.days

@Singleton
class TrustedListsCertificateSourceSupplier @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) : ClassSimpleNameLoggingTag {
    private val sourceCertificateSource = CommonCertificateSource()

    private val source = TLSource().apply {
        url = TL_SOURCE_URL
        certificateSource = sourceCertificateSource
    }

    private val certificateSource = TrustedListsCertificateSource()

    private val onlineLoader = FileCacheDataLoader().apply {
        setCacheExpirationTime(TL_FILE_CACHE_EXPIRATION_TIME.inWholeMilliseconds)
        dataLoader = CommonsDataLoader()
    }

    private val validationJob = TLValidationJob().apply {
        setTrustedListSources(source)
        setTrustedListCertificateSource(certificateSource)
        setOnlineDataLoader(onlineLoader)
    }

    private fun prepare() {
        if (sourceCertificateSource.numberOfCertificates == 0) {
            applicationContext.resources.openRawResource(R.raw.dss_tl_certificate).use {
                sourceCertificateSource.addCertificate(
                    CertificateToken(
                        CertificateFactory().engineGenerateCertificate(it) as X509Certificate
                    )
                )
            }
        }

        onlineLoader.setFileCacheDirectory(
            File(
                applicationContext.cacheDir,
                TL_FILE_CACHE_DIRECTORY_NAME
            )
        )

        validationJob.onlineRefresh()
    }

    fun get(): TrustedListsCertificateSource {
        prepare()
        return certificateSource
    }

    companion object {
        const val TL_SOURCE_URL = "https://www.nccert.pl/tsl/PL_TSL.xml"
        const val TL_FILE_CACHE_DIRECTORY_NAME = "tl_dcf7e0de"
        val TL_FILE_CACHE_EXPIRATION_TIME = 7.days
    }
}
