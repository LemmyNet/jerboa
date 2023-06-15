package com.jerboa.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.jerboa.ui.components.home.SiteSidebarActivity
import com.jerboa.ui.components.home.SiteViewModel

private const val siteSideBarRoutePattern = "siteSidebar"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.siteSideBarScreen(
    siteViewModel: SiteViewModel,
    navController: NavController,
) {
    composable(
        route = siteSideBarRoutePattern,
    ) {
        SiteSidebarActivity(
            siteViewModel = siteViewModel,
            navController = navController,
        )
    }
}
