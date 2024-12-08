package com.jakubmeysner.legitnik.ui.sdcatcardreader.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.util.findActivity

@Composable
fun SDCATCardReaderInterfaceNfc(
    hasNfcFeature: Boolean,
    showPrompt: Boolean,
    enableNfcAdapterReaderMode: (activity: Activity) -> Unit,
    disableNfcAdapterReaderMode: (activity: Activity) -> Unit,
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    if (!hasNfcFeature || activity == null) {
        if (showPrompt) {
            SDCATCardReaderInterfaceUnavailable(
                stringResource(R.string.sdcat_card_reader_interface_nfc_unavailable)
            )
        }
    } else {
        if (showPrompt) {
            SDCATCardReaderInterfaceNfcPrompt()
        }

        LifecycleResumeEffect(Unit) {
            enableNfcAdapterReaderMode(activity)

            onPauseOrDispose {
                disableNfcAdapterReaderMode(activity)
            }
        }
    }
}
