package com.jakubmeysner.legitnik.ui.sdcatcardsaved.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.ui.components.FullScreenPrompt
import com.jakubmeysner.legitnik.ui.sdcatcard.SDCATCardCard
import kotlinx.coroutines.launch

@Composable
fun SDCATCardSavedDetailsScreen(
    viewModel: SDCATCardSavedDetailsViewModel = hiltViewModel(),
    navigateToSDCATCardList: () -> Unit,
    popBackStack: () -> Boolean,
) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val card = uiState.card

    Box(
        modifier = Modifier.padding(16.dp),
    ) {
        if (card != null) {
            SDCATCardCard(
                content = card.parsedData.content,
                valid = null,
                isSaved = true,
                saveCard = {},
                removeCard = {
                    scope.launch {
                        viewModel.removeCard()

                        if (!popBackStack()) {
                            navigateToSDCATCardList()
                        }
                    }
                },
                onShowValidationDetails = {},
            )
        } else if (uiState.error) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.error) {
                FullScreenPrompt(
                    icon = Icons.Default.Close,
                    title = stringResource(R.string.sdcat_card_saved_details_error_prompt_title),
                )
            }
        } else if (!uiState.loading) {
            FullScreenPrompt(
                icon = Icons.Default.Delete,
                title = stringResource(R.string.sdcat_card_saved_details_not_found_prompt_title),
                subtitle = stringResource(
                    R.string.sdcat_card_saved_details_not_found_prompt_subtitle
                ),
            )
        }
    }
}
