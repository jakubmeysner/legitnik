package com.jakubmeysner.legitnik.util

import android.hardware.usb.UsbDevice

fun UsbDevice.getFriendlyName(): String = productName ?: manufacturerName ?: deviceName
