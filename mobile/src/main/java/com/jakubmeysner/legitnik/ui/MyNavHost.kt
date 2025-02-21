package com.jakubmeysner.legitnik.ui

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jakubmeysner.legitnik.R
import com.jakubmeysner.legitnik.ui.parking.ParkingRoute
import com.jakubmeysner.legitnik.ui.parking.details.navigateToParkingLotDetails
import com.jakubmeysner.legitnik.ui.parking.map.navigateToParkingLotMap
import com.jakubmeysner.legitnik.ui.parking.parkingDestination
import com.jakubmeysner.legitnik.ui.sdcatcardreader.SDCATCardReaderRoute
import com.jakubmeysner.legitnik.ui.sdcatcardreader.sdcatCardReaderDestination
import com.jakubmeysner.legitnik.ui.sdcatcardsaved.SDCATCardSavedRoute
import com.jakubmeysner.legitnik.ui.sdcatcardsaved.details.navigateToSDCATCardSavedDetails
import com.jakubmeysner.legitnik.ui.sdcatcardsaved.list.navigateToSDCATCardSavedList
import com.jakubmeysner.legitnik.ui.sdcatcardsaved.sdcatCardSavedDestination
import com.jakubmeysner.legitnik.ui.settings.SettingsRoute
import com.jakubmeysner.legitnik.ui.settings.settingsDestination

interface Route

data class TopLevelRouteInfo<T : Route>(
    val route: T,
    val nameResourceId: Int,
    val selectedIcon: ImageVectorOrResourceId,
    val notSelectedIcon: ImageVectorOrResourceId,
)

sealed class ImageVectorOrResourceId {
    data class Vector(val imageVector: ImageVector) : ImageVectorOrResourceId()
    data class VectorResourceId(val imageVectorResourceId: Int) : ImageVectorOrResourceId()

    @Composable
    fun getImageVector(): ImageVector {
        return when (this) {
            is Vector -> this.imageVector
            is VectorResourceId -> ImageVector.vectorResource(this.imageVectorResourceId)
        }
    }
}

val topLevelRouteInfos = listOf(
    TopLevelRouteInfo(
        ParkingRoute,
        R.string.navigation_bar_parking,
        ImageVectorOrResourceId.Vector(Icons.Default.Place),
        ImageVectorOrResourceId.Vector(Icons.Outlined.Place)
    ),
    TopLevelRouteInfo(
        SDCATCardReaderRoute,
        R.string.navigation_bar_sdcat_card_reader,
        ImageVectorOrResourceId.VectorResourceId(R.drawable.mdi_smart_card_reader),
        ImageVectorOrResourceId.VectorResourceId(R.drawable.smart_card_reader_outline)
    ),
    TopLevelRouteInfo(
        SDCATCardSavedRoute,
        R.string.navigation_bar_sdcat_card_saved,
        ImageVectorOrResourceId.VectorResourceId(R.drawable.mdi_card_multiple),
        ImageVectorOrResourceId.VectorResourceId(R.drawable.mdi_card_multiple_outline),
    ),
    TopLevelRouteInfo(
        SettingsRoute,
        R.string.navigation_bar_settings,
        ImageVectorOrResourceId.Vector(Icons.Default.Settings),
        ImageVectorOrResourceId.Vector(Icons.Outlined.Settings)
    )
)

@Composable
fun MyNavigationBar(
    selectedTopLevelRoute: Route?,
    navigate: (topLevelRoute: Route) -> Unit,
) {
    NavigationBar {
        topLevelRouteInfos.forEach { topLevelRoute ->
            val selected = topLevelRoute.route == selectedTopLevelRoute

            NavigationBarItem(
                icon = {
                    Icon(
                        if (selected) topLevelRoute.selectedIcon.getImageVector()
                        else topLevelRoute.notSelectedIcon.getImageVector(),
                        contentDescription = null
                    )
                },
                label = {
                    Text(stringResource(topLevelRoute.nameResourceId))
                },
                selected = selected,
                onClick = {
                    navigate(topLevelRoute.route)
                }
            )
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun MyNavHost() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val showSnackbar: suspend (visuals: SnackbarVisuals) -> SnackbarResult = { visuals ->
        snackbarHostState.showSnackbar(visuals)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val selectedTopLevelRoute = topLevelRouteInfos.find { topLevelRouteInfo ->
                currentDestination?.hierarchy?.any {
                    it.hasRoute(topLevelRouteInfo.route::class)
                } == true
            }?.route

            MyNavigationBar(
                selectedTopLevelRoute = selectedTopLevelRoute,
                navigate = {
                    navController.navigate(it) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ParkingRoute,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            }
        ) {
            parkingDestination(
                navigateToParkingLotMap = navController::navigateToParkingLotMap,
                onNavigateToParkingLotDetails = navController::navigateToParkingLotDetails,
                onShowSnackbar = showSnackbar
            )

            sdcatCardReaderDestination(
                onShowSnackbar = showSnackbar,
            )

            sdcatCardSavedDestination(
                navigateToSDCATCardList = navController::navigateToSDCATCardSavedList,
                navigateToSDCATCardSavedDetails = navController::navigateToSDCATCardSavedDetails,
                popBackStack = navController::popBackStack,
                showSnackbar = showSnackbar,
            )

            settingsDestination()
        }
    }
}
