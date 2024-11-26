package com.jakubmeysner.legitnik.domain.apdu

import android.nfc.tech.IsoDep

class IsoDepApduTransceiver(private val isoDep: IsoDep) : ApduTransceiver {
    override fun transceive(data: ByteArray): ByteArray {
        return isoDep.transceive(data)
    }
}
