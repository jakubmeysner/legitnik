package com.jakubmeysner.legitnik.ui.sdcatcardsaved.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data class SDCATCardSavedDetailsRoute(
    val idString: String,
)

fun NavGraphBuilder.sdcatCardSavedDetailsDestination() {
    composable<SDCATCardSavedDetailsRoute> {
        SDCATCardSavedDetailsScreen()
    }
}
