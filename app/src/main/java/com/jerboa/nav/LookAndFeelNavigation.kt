package com.jerboa.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.ui.components.settings.lookandfeel.LookAndFeelActivity

private const val lookAndFeelRoutePattern = "lookAndFeel"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.lookAndFeelScreen(
    navController: NavController,
    appSettingsViewModel: AppSettingsViewModel,
) {
    composable(
        route = lookAndFeelRoutePattern,
    ) {
        LookAndFeelActivity(
            navController = navController,
            appSettingsViewModel = appSettingsViewModel,
        )
    }
}
