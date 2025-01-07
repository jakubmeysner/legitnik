package com.jakubmeysner.legitnik.ui.settings

import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.data.settings.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val parkingIds = uiState.parkingLots?.map { it.id } ?: uiState.savedParkingIds

    val isNotificationCategoryEnabled by viewModel.isCategoryEnabled(CategoryType.NOTIFICATION).collectAsState(false)
    val isTrackingCategoryEnabled by viewModel.isCategoryEnabled(CategoryType.ONGOING).collectAsState(false)

    val notificationSettingsState by viewModel
        .getSettingsStateForCategory(CategoryType.NOTIFICATION, parkingIds)
        .collectAsState(initial = emptyMap())

    val ongoingSettingsState by viewModel
        .getSettingsStateForCategory(CategoryType.ONGOING, parkingIds)
        .collectAsState(initial = emptyMap())

    val coroutineScope = rememberCoroutineScope()

    val getLabelFromId: (String) -> String = { id -> viewModel.getLabelFromId(id) }

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
                    coroutineScope.launch {
                        viewModel.toggleSetting(id, CategoryType.NOTIFICATION, newValue)
                    }
                },
                getLabel = getLabelFromId
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
                    coroutineScope.launch {
                        viewModel.toggleSetting(id, CategoryType.ONGOING, newValue)
                    }
                },
                getLabel = getLabelFromId
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
    getLabel: (String) -> String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
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
    getLabel: (String) -> String
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
