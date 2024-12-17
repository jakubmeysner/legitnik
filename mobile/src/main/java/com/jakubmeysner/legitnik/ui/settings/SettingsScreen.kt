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
import kotlinx.coroutines.flow.Flow
import com.jakubmeysner.legitnik.data.settings.*

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val savedParkingLabels = (viewModel.getLabelsFromSettingsCategory(SettingCategory.NOTIFICATION).collectAsState(initial = emptyList()).value +
                                viewModel.getLabelsFromSettingsCategory(SettingCategory.ONGOING).collectAsState(initial = emptyList()).value).distinct()
    val parkingLabels = uiState.parkingLots?.map { it.symbol } ?: savedParkingLabels

    val isNotificationCategoryEnabled by viewModel.isCategoryEnabled(SettingCategory.NOTIFICATION).collectAsState(false)
    val isTrackingCategoryEnabled by viewModel.isCategoryEnabled(SettingCategory.ONGOING).collectAsState(false)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            SettingsCategory(
                title = stringResource(R.string.settings_notification_category),
                isEnabled = isNotificationCategoryEnabled,
                onToggle = { newValue -> viewModel.toggleCategory(SettingCategory.NOTIFICATION, newValue) },
                parkingLabels = parkingLabels,
                getSettingState = { label -> viewModel.isSettingEnabled(label, SettingCategory.NOTIFICATION) },
                onToggleSetting = { label, newValue -> viewModel.toggleSetting(label, SettingCategory.NOTIFICATION, newValue) }
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
                onToggle = { newValue -> viewModel.toggleCategory(SettingCategory.ONGOING, newValue) },
                parkingLabels = parkingLabels,
                getSettingState = { label -> viewModel.isSettingEnabled(label, SettingCategory.ONGOING) },
                onToggleSetting = { label, newValue -> viewModel.toggleSetting(label, SettingCategory.ONGOING, newValue) }
            )
        }
    }
}

@Composable
fun SettingsCategory(
    title: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    parkingLabels: List<String>,
    getSettingState: (String) -> Flow<Boolean>,
    onToggleSetting: (String, Boolean) -> Unit
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
            parkingLabels.forEachIndexed { index, label ->
                ToggleItem(
                    label = label,
                    index = index,
                    getSettingState = getSettingState,
                    onToggle = onToggleSetting
                )
            }
        }
    }
}

@Composable
fun ToggleItem(
    label: String,
    index: Int,
    getSettingState: (String) -> Flow<Boolean>,
    onToggle: (String, Boolean) -> Unit
) {
    val isCheckedState by getSettingState(label).collectAsState(initial = null)

    if (isCheckedState != null) {
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
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Switch(
                checked = isCheckedState!!,
                onCheckedChange = { newValue -> onToggle(label, newValue) }
            )
        }
    }
}
