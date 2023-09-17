package com.jerboa.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NavigateBefore
import androidx.compose.material.icons.outlined.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jerboa.R
import com.jerboa.db.entity.Account
import com.jerboa.model.PostViewModel
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun SwipeBetweenPosts(
    postStream: PostStream,
    postViewModel: PostViewModel,
    account: Account,
    content: @Composable () -> Unit,
) {
    val nextPostAction = SwipeAction(
        icon = {
            Icon(
                imageVector = Icons.Outlined.NavigateNext,
                contentDescription = stringResource(id = R.string.post_next),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        onSwipe = {
            postStream.getNextPost(postViewModel.postId, account = account)?.let {
                postViewModel.postId = it
            }
        },
        background = MaterialTheme.colorScheme.background,
    )

    val previousPostAction = SwipeAction(
        icon = {
            Icon(
                imageVector = Icons.Outlined.NavigateBefore,
                contentDescription = stringResource(id = R.string.post_previous),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        onSwipe = {
            postStream.getPreviousPost(postViewModel.postId, account)?.let {
                postViewModel.postId = it
            }
        },
        background = MaterialTheme.colorScheme.background,
    )

    if (postStream.isFetchingMore()) {
        LoadingBar()
    } else {
        SwipeableActionsBox(
            startActions = listOf(previousPostAction),
            endActions = listOf(nextPostAction),
        ) {
            content()
        }
    }
}
