package com.jakubmeysner.legitnik.ui.sdcatcardsaved.details

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SDCATCardSavedDetailsRoute(
    val idString: String,
)

fun NavGraphBuilder.sdcatCardSavedDetailsDestination(
    navigateToSDCATCardList: () -> Unit,
    popBackStack: () -> Boolean,
) {
    composable<SDCATCardSavedDetailsRoute> {
        SDCATCardSavedDetailsScreen(
            navigateToSDCATCardList = navigateToSDCATCardList,
            popBackStack = popBackStack,
        )
    }
}

fun NavController.navigateToSDCATCardSavedDetails(id: UUID) {
    navigate(route = SDCATCardSavedDetailsRoute(idString = id.toString()))
}
