package com.jakubmeysner.legitnik.ui.sdcatcardreader

import android.nfc.NfcAdapter
import android.nfc.NfcManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jakubmeysner.legitnik.ui.sdcatcardreader.components.SDCATCardReaderInterfaceSelect

@Composable
fun SDCATCardReaderScreen(
    viewModel: SDCATCardReaderViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val nfcManager = remember(context) { context.getSystemService(NfcManager::class.java) }
    val nfcAdapter: NfcAdapter? = remember(nfcManager) { nfcManager.defaultAdapter }

    Box(modifier = Modifier.padding(16.dp)) {
        Column {
            SDCATCardReaderInterfaceSelect(
                selectedInterface = uiState.selectedInterface,
                onSelectInterface = viewModel::selectInterface
            )

            Text(uiState.selectedInterface.name)

            if (uiState.selectedInterface == SDCATCardReaderInterface.NFC) {
                Text("NFC adapter " + if (nfcAdapter != null) "available" else "unavailable")
            }
        }
    }
}
