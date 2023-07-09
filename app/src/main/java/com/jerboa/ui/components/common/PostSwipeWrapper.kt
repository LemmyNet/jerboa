package com.jerboa.ui.components.common

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.jerboa.db.Account
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.model.PostViewModel

@Composable
fun PostSwipeWrapper(
    navController: NavController,
    postStream: PostStream,
    postViewModel: PostViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    account: Account?,
    content: @Composable () -> Unit,
) {
    if (appSettingsViewModel.appSettings.value?.allowSwipingBetweenPosts == true) {
        SwipeBetweenPosts(
            postStream = postStream,
            postViewModel = postViewModel,
            account = account,
            content = content,
        )
    } else {
        SwipeToNavigateBack(navController = navController, content = content)
    }
}
