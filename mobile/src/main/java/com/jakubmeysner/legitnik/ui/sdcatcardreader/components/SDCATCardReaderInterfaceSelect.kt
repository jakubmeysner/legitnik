package com.jakubmeysner.legitnik.ui.sdcatcardreader.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.ui.sdcatcardreader.SDCATCardReaderInterface

private data class SDCATCardReaderInterfaceSelectOption(
    val inter: SDCATCardReaderInterface,
    val nameResourceId: Int,
    val iconVectorResourceId: Int,
)

private val sdcatCardReaderInterfaceSelectOptions = listOf(
    SDCATCardReaderInterfaceSelectOption(
        SDCATCardReaderInterface.NFC,
        R.string.sdcat_card_reader_interface_nfc,
        R.drawable.contactless_payment
    ),
    SDCATCardReaderInterfaceSelectOption(
        SDCATCardReaderInterface.USB,
        R.string.sdcat_card_reader_interface_usb,
        R.drawable.usb
    )
)

@Composable
fun SDCATCardReaderInterfaceSelect(
    enabled: Boolean,
    selectedInterface: SDCATCardReaderInterface,
    onSelectInterface: (inter: SDCATCardReaderInterface) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth(),
    ) {
        sdcatCardReaderInterfaceSelectOptions.forEachIndexed { i, option ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(i, 2),
                icon = {
                    Icon(
                        ImageVector.vectorResource(option.iconVectorResourceId),
                        contentDescription = null
                    )
                },
                selected = selectedInterface == option.inter,
                onClick = {
                    onSelectInterface(option.inter)
                },
                enabled = enabled,
            ) {
                Text(stringResource(option.nameResourceId))
            }
        }
    }
}
