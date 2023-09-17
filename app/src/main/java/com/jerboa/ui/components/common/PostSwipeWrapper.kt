package com.jerboa.ui.components.common

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.jerboa.db.entity.Account
import com.jerboa.feat.PostNavigationGestureMode
import com.jerboa.model.AppSettingsViewModel
import com.jerboa.model.PostViewModel

@Composable
fun PostSwipeWrapper(
    navController: NavController,
    postStream: PostStream,
    postViewModel: PostViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    account: Account,
    content: @Composable () -> Unit,
) {

    when (appSettingsViewModel.appSettings.value?.postNavigationGestureMode) {
        PostNavigationGestureMode.SwipeBetween.ordinal -> {
            SwipeBetweenPosts(
                account = account,
                postStream = postStream,
                postViewModel = postViewModel,
                content = content,
            )
        }

        PostNavigationGestureMode.SwipeLeft.ordinal -> {
            SwipeToNavigateBack(onSwipeBack = {
                navController.currentBackStackEntry?.let {
                    navController.navigate(it.id)
                }
            }, content = content)
        }

        else -> {
            content()
        }
    }
}
