package com.jakubmeysner.legitnik.ui.sdcatcardreader.components

import android.hardware.usb.UsbDevice
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jakubmeysner.legitnik.R

@Composable
fun SDCATCardReaderInterfaceUsb(
    hasUsbHostFeature: Boolean,
    showPrompt: Boolean,
    deviceSelectEnabled: Boolean,
    selectedUsbDeviceName: String?,
    selectUsbDevice: (usbDevice: UsbDevice?) -> Unit,
) {
    if (!hasUsbHostFeature) {
        if (showPrompt) {
            SDCATCardReaderInterfaceUnavailable(
                stringResource(R.string.sdcat_card_reader_interface_usb_unavailable)
            )
        }
    } else {
        SDCATCardReaderInterfaceUsbDeviceSelect(
            enabled = deviceSelectEnabled,
            selectedUsbDeviceName = selectedUsbDeviceName,
            selectUsbDevice = selectUsbDevice,
        )

        if (showPrompt) {
            if (selectedUsbDeviceName == null) {
                SDCATCardReaderInterfaceUsbDeviceSelectPrompt()
            } else {
                SDCATCardReaderInterfaceUsbInsertCardPrompt()
            }
        }
    }
}
