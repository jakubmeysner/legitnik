package com.jakubmeysner.legitnik.ui.sdcatcardreader.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.ui.components.FullScreenPrompt

@Composable
@Preview
fun SDCATCardReaderInterfaceUsbInsertCardPrompt() {
    FullScreenPrompt(
        icon = ImageVector.vectorResource(R.drawable.mdi_smart_card_reader),
        title = stringResource(R.string.sdcat_card_reader_interface_usb_insert_card_prompt),
    )
}
