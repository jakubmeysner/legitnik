package com.jakubmeysner.legitnik.ui.sdcatcardsaved.details

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SDCATCardSavedDetailsScreen(
    viewModel: SDCATCardSavedDetailsViewModel = hiltViewModel(),
) {
    Text(text = "SDCATCardSavedDetailsScreen")
}
