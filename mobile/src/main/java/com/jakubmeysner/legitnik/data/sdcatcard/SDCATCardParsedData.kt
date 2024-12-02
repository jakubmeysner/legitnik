package com.jakubmeysner.legitnik.data.sdcatcard

import org.bouncycastle.cms.CMSSignedData
import java.security.cert.X509Certificate

data class SDCATCardParsedData(
    val message: CMSSignedData,
    val content: SDCATCardParsedContent,
    val certificate: X509Certificate,
)
