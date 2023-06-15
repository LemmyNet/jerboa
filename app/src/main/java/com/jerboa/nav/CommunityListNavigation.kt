package com.jerboa.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.community.list.CommunityListActivity
import com.jerboa.ui.components.community.list.CommunityListViewModel
import com.jerboa.ui.components.home.SiteViewModel

private class CommunityListArgs(val select: Boolean) {
    constructor(navBackStackEntry: NavBackStackEntry) :
        this(navBackStackEntry.arguments?.getBoolean(SELECT)!!)

    companion object {
        const val SELECT = "select"
    }
}

private const val communityListRoutePattern = "communityList?select={${CommunityListArgs.SELECT}}"

fun NavBackStackEntry.bottomNavIsSearch() = destination.route == communityListRoutePattern

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.communityListScreen(
    navController: NavController,
    communityListViewModel: CommunityListViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
) {
    composable(
        route = communityListRoutePattern,
        arguments = listOf(
            navArgument(CommunityListArgs.SELECT) {
                defaultValue = false
                type = NavType.BoolType
            },
        ),
    ) {
        val args = CommunityListArgs(it)

        // Whenever navigating here, reset the list with your followed communities
        communityListViewModel.setCommunityListFromFollowed(siteViewModel)

        CommunityListActivity(
            navController = navController,
            accountViewModel = accountViewModel,
            communityListViewModel = communityListViewModel,
            selectMode = args.select,
        )
    }
}

fun NavController.bottomNavSelectSearch() {
    navigate(communityListRoutePattern) {
        launchSingleTop = true
        popUpTo(0)
    }
}
