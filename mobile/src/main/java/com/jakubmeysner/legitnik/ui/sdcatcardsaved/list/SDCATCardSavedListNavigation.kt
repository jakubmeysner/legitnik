package com.jakubmeysner.legitnik.ui.sdcatcardsaved.list

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
object SDCATCardSavedListRoute

fun NavGraphBuilder.sdcatCardSavedListDestination(
    navigateToSDCATCardSavedDetails: (id: UUID) -> Unit,
) {
    composable<SDCATCardSavedListRoute> {
        SDCATCardSavedListScreen(
            navigateToSDCATCardSavedDetails = navigateToSDCATCardSavedDetails,
        )
    }
}

fun NavController.navigateToSDCATCardSavedList() {
    navigate(route = SDCATCardSavedListRoute)
}
