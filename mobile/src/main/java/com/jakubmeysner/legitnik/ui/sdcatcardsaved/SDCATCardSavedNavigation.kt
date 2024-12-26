package com.jakubmeysner.legitnik.ui.sdcatcardsaved

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.jakubmeysner.legitnik.ui.sdcatcardsaved.details.sdcatCardSavedDetailsDestination
import com.jakubmeysner.legitnik.ui.sdcatcardsaved.list.SDCATCardSavedListRoute
import com.jakubmeysner.legitnik.ui.sdcatcardsaved.list.sdcatCardSavedListDestination
import kotlinx.serialization.Serializable

@Serializable
object SDCATCardSavedRoute

fun NavGraphBuilder.sdcatCardSavedDestination() {
    navigation<SDCATCardSavedRoute>(startDestination = SDCATCardSavedListRoute) {
        sdcatCardSavedListDestination()
        sdcatCardSavedDetailsDestination()
    }
}
