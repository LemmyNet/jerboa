package com.jerboa.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.jerboa.db.AppSettings
import com.jerboa.ui.components.settings.about.AboutActivity

private const val aboutRoutePattern = "about"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.aboutScreen(
    navController: NavController,
    appSettings: AppSettings?,
) {
    composable(
        route = aboutRoutePattern,
    ) {
        AboutActivity(
            navController = navController,
            useCustomTabs = appSettings?.useCustomTabs ?: true,
            usePrivateTabs = appSettings?.usePrivateTabs ?: false,
        )
    }
}
