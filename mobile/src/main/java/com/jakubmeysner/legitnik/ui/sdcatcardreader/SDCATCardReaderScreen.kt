package com.jakubmeysner.legitnik.ui.sdcatcardreader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jakubmeysner.legitnik.ui.sdcatcardreader.components.SDCATCardReaderInterfaceNfc
import com.jakubmeysner.legitnik.ui.sdcatcardreader.components.SDCATCardReaderInterfaceSelect
import com.jakubmeysner.legitnik.ui.sdcatcardreader.components.SDCATCardReaderInterfaceUsb

@Composable
fun SDCATCardReaderScreen(
    viewModel: SDCATCardReaderViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(16.dp)) {
        Box(modifier = Modifier.padding(bottom = 16.dp)) {
            SDCATCardReaderInterfaceSelect(
                selectedInterface = uiState.selectedInterface,
                onSelectInterface = viewModel::selectInterface
            )
        }

        when (uiState.selectedInterface) {
            SDCATCardReaderInterface.NFC -> SDCATCardReaderInterfaceNfc(
                onTagDiscovered = viewModel::onTagDiscovered
            )

            SDCATCardReaderInterface.USB -> SDCATCardReaderInterfaceUsb()
        }
    }
}
