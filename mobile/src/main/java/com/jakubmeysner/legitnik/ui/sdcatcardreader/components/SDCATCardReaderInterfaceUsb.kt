package com.jakubmeysner.legitnik.ui.sdcatcardreader.components

import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.jakubmeysner.legitnik.R

@Composable
fun SDCATCardReaderInterfaceUsb(
    visible: Boolean,
    showPrompt: Boolean,
    deviceSelectEnabled: Boolean,
    createReader: (usbManager: UsbManager) -> Unit,
    selectedUsbDevice: UsbDevice?,
    selectUsbDevice: (usbDevice: UsbDevice?) -> Unit,
) {
    val context = LocalContext.current

    val hasUsbFeature = remember(context) {
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_USB_HOST)
    }

    if (!hasUsbFeature) {
        if (visible && showPrompt) {
            SDCATCardReaderInterfaceUnavailable(
                stringResource(R.string.sdcat_card_reader_interface_usb_unavailable)
            )
        }
    } else {
        val manager = remember(context) { context.getSystemService(UsbManager::class.java) }

        LaunchedEffect(manager) {
            createReader(manager)
        }

        if (visible) {
            SDCATCardReaderInterfaceUsbDeviceSelect(
                enabled = deviceSelectEnabled,
                selectedUsbDevice = selectedUsbDevice,
                selectUsbDevice = selectUsbDevice,
            )

            if (showPrompt) {
                if (selectedUsbDevice == null) {
                    SDCATCardReaderInterfaceUsbDeviceSelectPrompt()
                } else {
                    SDCATCardReaderInterfaceUsbInsertCardPrompt()
                }
            }
        }
    }
}
