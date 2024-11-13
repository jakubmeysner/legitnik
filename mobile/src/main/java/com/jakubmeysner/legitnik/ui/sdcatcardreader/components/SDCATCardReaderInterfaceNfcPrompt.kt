package com.jakubmeysner.legitnik.ui.sdcatcardreader.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jakubmeysner.legitnik.R

@Composable
fun SDCATCardReaderInterfaceNfcPrompt() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            ImageVector.vectorResource(R.drawable.contactless_payment_circle),
            contentDescription = null,
            modifier = Modifier
                .size(128.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            stringResource(R.string.sdcat_card_reader_interface_nfc_prompt),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            stringResource(R.string.sdcat_card_reader_interface_nfc_prompt_disclaimer),
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
