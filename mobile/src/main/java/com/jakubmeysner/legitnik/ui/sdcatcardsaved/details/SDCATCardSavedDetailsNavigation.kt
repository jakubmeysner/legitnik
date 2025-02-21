package com.jakubmeysner.legitnik.ui.sdcatcardsaved.details

import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jakubmeysner.legitnik.ui.Route
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SDCATCardSavedDetailsRoute(
    val idString: String,
) : Route

fun NavGraphBuilder.sdcatCardSavedDetailsDestination(
    navigateToSDCATCardList: () -> Unit,
    popBackStack: () -> Boolean,
    showSnackbar: suspend (visuals: SnackbarVisuals) -> SnackbarResult,
) {
    composable<SDCATCardSavedDetailsRoute> {
        SDCATCardSavedDetailsScreen(
            navigateToSDCATCardList = navigateToSDCATCardList,
            popBackStack = popBackStack,
            showSnackbar = showSnackbar,
        )
    }
}

fun NavController.navigateToSDCATCardSavedDetails(id: UUID) {
    navigate(route = SDCATCardSavedDetailsRoute(idString = id.toString()))
}
