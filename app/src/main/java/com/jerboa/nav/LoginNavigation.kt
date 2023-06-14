package com.jerboa.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.composable
import com.jerboa.DEFAULT_LEMMY_INSTANCES
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.login.LoginActivity
import com.jerboa.ui.components.login.LoginViewModel

private const val loginRoutePattern = "login"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.loginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    homeViewModel: HomeViewModel,
) {
    composable(
        route = loginRoutePattern,
        deepLinks = DEFAULT_LEMMY_INSTANCES.map { instance ->
            navDeepLink { uriPattern = "${instance}/${loginRoutePattern}" }
        },
    ) {
        LoginActivity(
            navController = navController,
            loginViewModel = loginViewModel,
            accountViewModel = accountViewModel,
            siteViewModel = siteViewModel,
            homeViewModel = homeViewModel,
        )
    }
}

fun NavController.showLogin() {
    navigate(loginRoutePattern)
}
