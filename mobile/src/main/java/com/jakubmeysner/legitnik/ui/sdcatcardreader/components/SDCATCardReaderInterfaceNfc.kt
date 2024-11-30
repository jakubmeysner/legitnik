package com.jakubmeysner.legitnik.ui.sdcatcardreader.components

import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.util.findActivity

@Composable
fun SDCATCardReaderInterfaceNfc(
    showPrompt: Boolean,
    onTagDiscovered: (tag: Tag) -> Unit,
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    val nfcManager = remember(context) { context.getSystemService(NfcManager::class.java) }
    val nfcAdapter: NfcAdapter? = remember(nfcManager) { nfcManager.defaultAdapter }

    if (nfcAdapter == null || activity == null) {
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
            nfcAdapter.enableReaderMode(
                activity,
                onTagDiscovered,
                NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B,
                null
            )

            onPauseOrDispose {
                nfcAdapter.disableReaderMode(activity)
            }
        }
    }
}
