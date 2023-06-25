package com.jerboa.ui.components.common

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.jerboa.db.Account
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.post.PostViewModel


@Composable
fun PostSwipeWrapper(
    navController: NavController,
    homeViewModel: HomeViewModel,
    postViewModel: PostViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    account: Account?,
    content: @Composable () -> Unit,
) {
    if (appSettingsViewModel.appSettings.value?.swipeBetweenPosts == true) {
        SwipeBetweenPosts(
            homeViewModel = homeViewModel,
            postViewModel = postViewModel,
            account = account,
            content = content
        )
    } else {
        SwipeToNavigateBack(navController = navController, content = content)
    }
}
