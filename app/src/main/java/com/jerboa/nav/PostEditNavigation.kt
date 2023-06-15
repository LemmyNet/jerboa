package com.jerboa.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.community.CommunityViewModel
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel
import com.jerboa.ui.components.post.edit.PostEditActivity
import com.jerboa.ui.components.post.edit.PostEditViewModel

private const val postEditRoutePattern = "postEdit"

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.postEditScreen(
    accountViewModel: AccountViewModel,
    postEditViewModel: PostEditViewModel,
    navController: NavController,
    postViewModel: PostViewModel,
    personProfileViewModel: PersonProfileViewModel,
    communityViewModel: CommunityViewModel,
    homeViewModel: HomeViewModel,
) {
    composable(
        route = postEditRoutePattern,
    ) {
        PostEditActivity(
            postEditViewModel = postEditViewModel,
            communityViewModel = communityViewModel,
            accountViewModel = accountViewModel,
            navController = navController,
            personProfileViewModel = personProfileViewModel,
            postViewModel = postViewModel,
            homeViewModel = homeViewModel,
        )
    }
}
