package com.jerboa.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NavigateBefore
import androidx.compose.material.icons.outlined.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import arrow.core.left
import com.jerboa.R
import com.jerboa.db.Account
import com.jerboa.ui.components.post.PostViewModel
import kotlinx.coroutines.runBlocking
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun SwipeBetweenPosts(
    postStream: PostStream,
    postViewModel: PostViewModel,
    account: Account?,
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
            runBlocking {
                postStream.getNextPost(postViewModel.id?.swap()?.getOrNull())?.let {
                    postViewModel.initialize(it.left())
                    postViewModel.getData(account)
                }
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
            runBlocking {
                postStream.getPreviousPost(postViewModel.id?.swap()?.getOrNull())?.let {
                    postViewModel.initialize(it.left())
                    postViewModel.getData(account)
                }
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
