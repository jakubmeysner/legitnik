package com.jakubmeysner.legitnik

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
import com.jakubmeysner.legitnik.ui.parking.Parking
import com.jakubmeysner.legitnik.ui.parking.details.navigateToParkingLotDetails
import com.jakubmeysner.legitnik.ui.parking.parkingDestination
import com.jakubmeysner.legitnik.ui.sdcatcardreader.SDCATCardReader
import com.jakubmeysner.legitnik.ui.sdcatcardreader.sdcatCardReaderDestination
import com.jakubmeysner.legitnik.ui.settings.Settings
import com.jakubmeysner.legitnik.ui.settings.settingsDestination

data class TopLevelRoute<T : Any>(
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

val topLevelRoutes = listOf(
    TopLevelRoute(
        Parking,
        R.string.navigation_bar_parking,
        ImageVectorOrResourceId.Vector(Icons.Default.Place),
        ImageVectorOrResourceId.Vector(Icons.Outlined.Place)
    ),
    TopLevelRoute(
        SDCATCardReader,
        R.string.navigation_bar_sdcat_card_reader,
        ImageVectorOrResourceId.VectorResourceId(R.drawable.smart_card_reader),
        ImageVectorOrResourceId.VectorResourceId(R.drawable.smart_card_reader_outline)
    ),
    TopLevelRoute(
        Settings,
        R.string.navigation_bar_settings,
        ImageVectorOrResourceId.Vector(Icons.Default.Settings),
        ImageVectorOrResourceId.Vector(Icons.Outlined.Settings)
    )
)

@SuppressLint("RestrictedApi")
@Composable
fun MyNavHost() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val onShowSnackbar: suspend (visuals: SnackbarVisuals) -> SnackbarResult = { visuals ->
        snackbarHostState.showSnackbar(visuals)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                topLevelRoutes.forEach { topLevelRoute ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.hasRoute(topLevelRoute.route::class)
                    } == true

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
                            navController.navigate(topLevelRoute.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Parking,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            }
        ) {
            parkingDestination(
                onNavigateToParkingLotDetails = navController::navigateToParkingLotDetails,
                onShowSnackbar = onShowSnackbar
            )
            sdcatCardReaderDestination(onShowSnackbar = onShowSnackbar)
            settingsDestination()
        }
    }
}
