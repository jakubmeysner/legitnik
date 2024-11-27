package com.jakubmeysner.legitnik.domain.apdu

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.acs.smartcard.Reader


class UsbReader(manager: UsbManager?) : Reader(manager) {
    override fun isSupported(device: UsbDevice): Boolean {
        return true
    }
}
