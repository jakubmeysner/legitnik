package com.jakubmeysner.legitnik.ui.sdcatcardreader.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.ui.components.FullScreenPrompt

@Composable
fun SDCATCardReaderInterfaceNfcPrompt() {
    FullScreenPrompt(
        icon = ImageVector.vectorResource(R.drawable.mdi_contactless_payment_circle),
        title = stringResource(R.string.sdcat_card_reader_interface_nfc_prompt),
        subtitle = stringResource(R.string.sdcat_card_reader_interface_nfc_prompt_disclaimer),
    )
}
