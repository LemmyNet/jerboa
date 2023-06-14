package com.jerboa.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.DrawerState
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettings
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.ui.components.home.FeedActivity
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.post.edit.PostEditViewModel

private const val feedRoutePattern = "feed"

fun bottomNavDefaultRoute() = feedRoutePattern;

fun NavBackStackEntry.bottomNavIsHome(): Boolean = destination.route == feedRoutePattern

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.feedScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    postEditViewModel: PostEditViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    drawerState: DrawerState,
    appSettings: AppSettings?,
) {
    composable(feedRoutePattern) {
        FeedActivity(
            navController = navController,
            homeViewModel = homeViewModel,
            accountViewModel = accountViewModel,
            siteViewModel = siteViewModel,
            postEditViewModel = postEditViewModel,
            appSettingsViewModel = appSettingsViewModel,
            showVotingArrowsInListView = appSettings?.showVotingArrowsInListView
                ?: true,
            drawerState = drawerState,
        )
    }
}

fun NavController.bottomNavSelectHome() {
    navigate(feedRoutePattern) {
        launchSingleTop = true
        popUpTo(0)
    }
}
