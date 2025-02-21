package com.jakubmeysner.legitnik.ui.sdcatcardsaved.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.ui.components.FullScreenPrompt
import com.jakubmeysner.legitnik.ui.sdcatcardsaved.list.components.SDCATCardSavedListCard
import java.util.UUID

@Composable
fun SDCATCardSavedListScreen(
    viewModel: SDCATCardSavedListViewModel = hiltViewModel(),
    navigateToSDCATCardSavedDetails: (id: UUID) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SDCATCardSavedListScreen(
        uiState = uiState,
        navigateToSDCATCardSavedDetails = navigateToSDCATCardSavedDetails,
    )
}

@Composable
fun SDCATCardSavedListScreen(
    uiState: SDCATCardSavedListUiState,
    navigateToSDCATCardSavedDetails: (id: UUID) -> Unit,
) {
    val cards = uiState.cards

    if (cards != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 16.dp)
        ) {
            if (cards.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(space = 16.dp),
                ) {
                    for (data in cards) {
                        item(data.rawData.id) {
                            SDCATCardSavedListCard(
                                data = data,
                                navigateToSDCATCardSavedDetails = navigateToSDCATCardSavedDetails,
                            )
                        }
                    }
                }
            } else {
                FullScreenPrompt(
                    icon = ImageVector.vectorResource(R.drawable.mdi_playlist_plus),
                    title = stringResource(R.string.sdcat_card_saved_list_prompt_title),
                    subtitle = stringResource(R.string.sdcat_card_saved_list_prompt_subtitle),
                )
            }
        }
    }
}
