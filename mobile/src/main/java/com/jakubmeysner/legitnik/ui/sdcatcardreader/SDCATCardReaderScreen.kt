package com.jakubmeysner.legitnik.ui.sdcatcardreader

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SDCATCardReaderScreen(
    viewModel: SDCATCardReaderViewModel = hiltViewModel(),
) {
    Text("SDCAT Card Reader")
}
