package com.jakubmeysner.legitnik

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jakubmeysner.legitnik.ui.parking.Parking
import com.jakubmeysner.legitnik.ui.parking.parkingDestination
import com.jakubmeysner.legitnik.ui.sdcatcardreader.SDCATCardReader
import com.jakubmeysner.legitnik.ui.sdcatcardreader.sdcatCardReaderDestination
import com.jakubmeysner.legitnik.ui.settings.Settings
import com.jakubmeysner.legitnik.ui.settings.settingsDestination

data class TopLevelRoute<T : Any>(
    val route: T,
    val nameResourceId: Int,
    val selectedIcon: ImageVector,
    val notSelectedIcon: ImageVector,
)

val topLevelRoutes = listOf(
    TopLevelRoute(
        Parking,
        R.string.navigation_bar_parking,
        Icons.Default.Place,
        Icons.Outlined.Place
    ),
    TopLevelRoute(
        SDCATCardReader,
        R.string.navigation_bar_sdcat_card_reader,
        Icons.Default.CheckCircle,
        Icons.Outlined.CheckCircle
    ),
    TopLevelRoute(
        Settings,
        R.string.navigation_bar_settings,
        Icons.Default.Settings,
        Icons.Outlined.Settings
    )
)

@SuppressLint("RestrictedApi")
@Composable
fun MyNavHost() {
    val navController = rememberNavController()

    Scaffold(
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
                                if (selected) topLevelRoute.selectedIcon
                                else topLevelRoute.notSelectedIcon,
                                contentDescription = stringResource(topLevelRoute.nameResourceId)
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
            modifier = Modifier.padding(innerPadding)
        ) {
            parkingDestination()
            sdcatCardReaderDestination()
            settingsDestination()
        }
    }
}
