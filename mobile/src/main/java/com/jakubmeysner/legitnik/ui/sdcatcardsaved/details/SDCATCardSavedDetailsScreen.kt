package com.jakubmeysner.legitnik.ui.sdcatcardsaved.details

import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.ui.components.FullScreenPrompt
import com.jakubmeysner.legitnik.ui.sdcatcard.SDCATCardCard
import com.jakubmeysner.legitnik.ui.sdcatcard.SDCATCardValidationDetailsDialog
import com.jakubmeysner.legitnik.util.SnackbarVisualsData
import kotlinx.coroutines.launch

@Composable
fun SDCATCardSavedDetailsScreen(
    viewModel: SDCATCardSavedDetailsViewModel = hiltViewModel(),
    navigateToSDCATCardList: () -> Unit,
    popBackStack: () -> Boolean,
    showSnackbar: suspend (visuals: SnackbarVisuals) -> SnackbarResult,
) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val card = uiState.card
    val validationResult = uiState.validationResult
    var showValidationDetails by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val hasNfcHceFeature = remember(context.packageManager) {
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (card != null) {
            if (hasNfcHceFeature) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.mdi_contactless_payment_circle),
                    contentDescription = stringResource(
                        R.string.sdcat_card_saved_details_active_for_emulation
                    ),
                    modifier = Modifier.size(size = 72.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            SDCATCardCard(
                content = card.parsedData.content,
                valid = uiState.validationResult?.valid,
                isSaved = true,
                saveCard = {},
                default = card.rawData.default,
                removeCard = {
                    scope.launch {
                        viewModel.removeCard().join()

                        if (!popBackStack()) {
                            navigateToSDCATCardList()
                        }
                    }
                },
                toggleDefault = viewModel::toggleDefault,
                onShowValidationDetails = {
                    showValidationDetails = true
                },
            )

            if (showValidationDetails && validationResult != null) {
                SDCATCardValidationDetailsDialog(
                    certificate = card.parsedData.certificate,
                    validationResult = validationResult,
                    onClose = {
                        showValidationDetails = false
                    },
                )
            }

            val validationErrorSnackbarMessage = stringResource(
                R.string.sdcat_card_saved_details_validation_error_snackbar_message
            )

            LaunchedEffect(uiState.error) {
                if (uiState.error) {
                    showSnackbar(
                        SnackbarVisualsData(
                            message = validationErrorSnackbarMessage,
                        )
                    )
                }
            }

            LifecycleResumeEffect(card.rawData.id) {
                viewModel.setActive()

                onPauseOrDispose {
                    viewModel.unsetActive()
                }
            }
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
