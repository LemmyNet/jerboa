package com.jerboa.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.settings.SettingsActivity

private const val settingsRoutePattern = "settings"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.settingsScreen(
    navController: NavController,
    accountViewModel: AccountViewModel,
) {
    composable(
        route = settingsRoutePattern,
    ) {
        SettingsActivity(
            navController = navController,
            accountViewModel = accountViewModel,
        )
    }
}
