package com.jakubmeysner.legitnik.ui.sdcatcardsaved.list

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SDCATCardSavedListScreen(
    viewModel: SDCATCardSavedListViewModel = hiltViewModel(),
) {
    Text(text = "SDCATCardSavedListScreen")
}
