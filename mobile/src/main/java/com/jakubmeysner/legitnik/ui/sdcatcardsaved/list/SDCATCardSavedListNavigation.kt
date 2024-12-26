package com.jakubmeysner.legitnik.ui.sdcatcardsaved.list

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object SDCATCardSavedListRoute

fun NavGraphBuilder.sdcatCardSavedListDestination() {
    composable<SDCATCardSavedListRoute> {
        SDCATCardSavedListScreen()
    }
}
