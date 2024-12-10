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

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val parkingLots = uiState.parkingLots

    var parkingLabels = listOf("WRO", "C13", "D20", "GEO-L", "E01") // idk czy to dobry pomysl? Bo w sumie jak często pojawiają się nowe parkingi na pwr? A takto jest dostęp do strony ustawień nawet jeśli nie ma połączenia. Thoughts?

    if (parkingLots != null) {
        parkingLabels = parkingLots.map { it.symbol }
    }

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
                onToggle = { newValue -> viewModel.toggleCategory(newValue, SettingCategory.NOTIFICATION) },
                parkingLabels = parkingLabels,
                viewModel = viewModel,
                category = SettingCategory.NOTIFICATION
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
                onToggle = { newValue -> viewModel.toggleCategory(newValue, SettingCategory.ONGOING) },
                parkingLabels = parkingLabels,
                viewModel = viewModel,
                category = SettingCategory.ONGOING
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
    viewModel: SettingsViewModel,
    category: SettingCategory
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
                    viewModel = viewModel,
                    label = label,
                    category = category,
                    index = index
                )
            }
        }
    }
}

@Composable
fun ToggleItem(
    viewModel: SettingsViewModel,
    label: String,
    category: SettingCategory,
    index: Int
) {
    // używam tu null, bo jeżeli dam initial state na np. false, to za każdym przełączeniem kategorii w ustawieniach te switche odpalają animację przełączenia.
    // idk czy to dobry pomysl
    val isCheckedFromViewModel by viewModel.isSettingEnabled(label, category).collectAsState(initial = null)

    if (isCheckedFromViewModel != null) {
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
                checked = isCheckedFromViewModel!!,
                onCheckedChange = { newValue ->
                    viewModel.toggleSetting(label, newValue, category)
                }
            )
        }
    }
}
