package com.jakubmeysner.legitnik.ui.sdcatcardreader.components

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.jakubmeysner.legitnik.R

@Composable
fun SDCATCardReaderInterfaceUsb() {
    val context = LocalContext.current

    val hasUsbFeature = remember(context) {
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_USB_HOST)
    }

    if (!hasUsbFeature) {
        SDCATCardReaderInterfaceUnavailable(
            stringResource(R.string.sdcat_card_reader_interface_usb_unavailable)
        )
    }
}
