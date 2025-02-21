package com.jakubmeysner.legitnik.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jakubmeysner.legitnik.ui.theme.LegitnikTheme

@Composable
fun ScreenPreviewScaffold(
    selectedTopLevelRoute: Route,
    content: @Composable () -> Unit,
) {
    LegitnikTheme(darkTheme = true) {
        Scaffold(
            bottomBar = {
                MyNavigationBar(
                    selectedTopLevelRoute = selectedTopLevelRoute,
                    navigate = {},
                )
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                content()
            }
        }
    }
}
