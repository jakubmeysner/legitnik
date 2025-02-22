package com.jakubmeysner.legitnik.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.data.settings.CategoryType
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val parkingIds = uiState.parkingLots?.map { it.id } ?: uiState.savedParkingIds

    val notificationPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.POST_NOTIFICATIONS
    )

    val isNotificationCategoryEnabled by viewModel.isCategoryEnabled(CategoryType.NOTIFICATION)
        .collectAsState(false)
    val isTrackingCategoryEnabled by viewModel.isCategoryEnabled(CategoryType.ONGOING)
        .collectAsState(false)

    val notificationSettingsState by viewModel
        .getSettingsStateForCategory(CategoryType.NOTIFICATION, parkingIds)
        .collectAsState(initial = emptyMap())

    val ongoingSettingsState by viewModel
        .getSettingsStateForCategory(CategoryType.ONGOING, parkingIds)
        .collectAsState(initial = emptyMap())

    val coroutineScope = rememberCoroutineScope()

    val parkingLotSymbols = remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(uiState.error, parkingIds) {
        if (uiState.error) {
            parkingLotSymbols.value = parkingIds.associateWith { id ->
                viewModel.getLabelFromCache(id)
            }
        } else {
            parkingLotSymbols.value = parkingIds.associateWith { id ->
                viewModel.getLabelFromId(id)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            SettingsCategory(
                title = stringResource(R.string.settings_notification_category),
                isEnabled = isNotificationCategoryEnabled,
                onToggle = { newValue ->
                    coroutineScope.launch {
                        viewModel.toggleCategory(CategoryType.NOTIFICATION, newValue)
                    }
                },
                parkingIds = parkingIds,
                settingsState = notificationSettingsState,
                onToggleSetting = { id, newValue ->
                    if (notificationPermissionState.status == PermissionStatus.Granted) {
                        coroutineScope.launch {
                            viewModel.toggleSetting(id, CategoryType.NOTIFICATION, newValue)
                        }
                    } else {
                        notificationPermissionState.launchPermissionRequest()
                    }
                },
                getLabel = { id -> parkingLotSymbols.value[id] ?: id }
            )
        }

        item {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                thickness = 1.dp
            )
        }

        item {
            SettingsCategory(
                title = stringResource(R.string.settings_ongoing_notification_category),
                isEnabled = isTrackingCategoryEnabled,
                onToggle = { newValue ->
                    coroutineScope.launch {
                        viewModel.toggleCategory(CategoryType.ONGOING, newValue)
                    }
                },
                parkingIds = parkingIds,
                settingsState = ongoingSettingsState,
                onToggleSetting = { id, newValue ->
                    if (notificationPermissionState.status == PermissionStatus.Granted) {
                        coroutineScope.launch {
                            viewModel.toggleSetting(id, CategoryType.ONGOING, newValue)
                        }
                    } else {
                        notificationPermissionState.launchPermissionRequest()
                    }
                },
                getLabel = { id -> parkingLotSymbols.value[id] ?: id }
            )
        }
    }
}

@Composable
fun SettingsCategory(
    title: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    parkingIds: List<String>,
    settingsState: Map<String, Boolean>,
    onToggleSetting: (String, Boolean) -> Unit,
    getLabel: (String) -> String,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle
            )
        }
        if (isEnabled) {
            parkingIds.forEachIndexed { index, id ->
                ToggleItem(
                    id = id,
                    index = index,
                    isChecked = settingsState[id] ?: false,
                    onToggle = onToggleSetting,
                    getLabel = getLabel
                )
            }
        }
    }
}

@Composable
fun ToggleItem(
    id: String,
    index: Int,
    isChecked: Boolean,
    onToggle: (String, Boolean) -> Unit,
    getLabel: (String) -> String,
) {
    Row(
        modifier = Modifier
            .background(
                if (index % 2 == 0) MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.05f)
                else MaterialTheme.colorScheme.background
            )
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = getLabel(id),
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(
            checked = isChecked,
            onCheckedChange = { newValue -> onToggle(id, newValue) }
        )
    }
}
