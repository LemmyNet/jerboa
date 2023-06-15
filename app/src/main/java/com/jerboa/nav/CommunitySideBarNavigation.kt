package com.jerboa.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.jerboa.ui.components.community.CommunityViewModel
import com.jerboa.ui.components.community.sidebar.CommunitySidebarActivity

private const val communitySideBarRoutePattern = "communitySidebar"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.communitySideBarScreen(
    communityViewModel: CommunityViewModel,
    navController: NavController,
) {
    composable(
        route = communitySideBarRoutePattern,
    ) {
        CommunitySidebarActivity(
            communityViewModel = communityViewModel,
            navController = navController,
        )
    }
}
