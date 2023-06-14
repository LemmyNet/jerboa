package com.jerboa.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.composable
import com.jerboa.DEFAULT_LEMMY_INSTANCES
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.settings.account.AccountSettingsActivity
import com.jerboa.ui.components.settings.account.AccountSettingsViewModel

private const val accountSettingsRoutePattern = "accountSettings"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.accountSettingsScreen(
    navController: NavController,
    accountSettingsViewModel: AccountSettingsViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
) {
    composable(
        route = accountSettingsRoutePattern,
        deepLinks = DEFAULT_LEMMY_INSTANCES.map { instance ->
            navDeepLink { uriPattern = "${instance}/settings" }
        },
    ) {
        AccountSettingsActivity(
            navController = navController,
            accountViewModel = accountViewModel,
            siteViewModel = siteViewModel,
            accountSettingsViewModel = accountSettingsViewModel,
        )
    }
}
