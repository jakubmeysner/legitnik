package com.jakubmeysner.legitnik.ui.sdcatcardsaved

import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.jakubmeysner.legitnik.ui.sdcatcardsaved.details.sdcatCardSavedDetailsDestination
import com.jakubmeysner.legitnik.ui.sdcatcardsaved.list.SDCATCardSavedListRoute
import com.jakubmeysner.legitnik.ui.sdcatcardsaved.list.sdcatCardSavedListDestination
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
object SDCATCardSavedRoute

fun NavGraphBuilder.sdcatCardSavedDestination(
    navigateToSDCATCardList: () -> Unit,
    navigateToSDCATCardSavedDetails: (id: UUID) -> Unit,
    popBackStack: () -> Boolean,
    showSnackbar: suspend (visuals: SnackbarVisuals) -> SnackbarResult,
) {
    navigation<SDCATCardSavedRoute>(startDestination = SDCATCardSavedListRoute) {
        sdcatCardSavedListDestination(
            navigateToSDCATCardSavedDetails = navigateToSDCATCardSavedDetails,
        )

        sdcatCardSavedDetailsDestination(
            navigateToSDCATCardList = navigateToSDCATCardList,
            popBackStack = popBackStack,
            showSnackbar = showSnackbar,
        )
    }
}
