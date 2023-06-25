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
import com.jerboa.api.ApiState
import com.jerboa.datatypes.types.GetPosts
import com.jerboa.db.Account
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.post.PostViewModel
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun SwipeBetweenPosts(
    homeViewModel: HomeViewModel,
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
            val res = homeViewModel.postsRes
            if (res is ApiState.Success) {
                res.data.posts
                    .mapIndexed { index, postView -> index to postView }
                    .firstOrNull { it.second.post.id == postViewModel.id?.swap()?.getOrNull() }
                    ?.first?.let { currIndex ->
                        if (currIndex + 1 >= res.data.posts.size - 1) {
                            val nextIndex = res.data.posts.size
                            homeViewModel.nextPage()
                            homeViewModel.appendPosts(
                                GetPosts(
                                    page = homeViewModel.page,
                                    sort = homeViewModel.sortType,
                                    type_ = homeViewModel.listingType,
                                    auth = account?.jwt,
                                ),
                            ).invokeOnCompletion {
                                val newRes = homeViewModel.postsRes
                                if (newRes is ApiState.Success && newRes.data.posts.size > nextIndex) {
                                    postViewModel.initialize(newRes.data.posts[nextIndex].post.id.left())
                                    postViewModel.getData(account)
                                }
                            }
                        } else if (currIndex >= 0) {
                            postViewModel.initialize(res.data.posts[currIndex + 1].post.id.left())
                            postViewModel.getData(account)
                        }
                        Unit
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
            val res = homeViewModel.postsRes
            if (res is ApiState.Success) {
                val currIndex = res.data.posts
                    .mapIndexed { index, postView -> index to postView }
                    .firstOrNull { it.second.post.id == postViewModel.id?.swap()?.getOrNull() }
                    ?.first
                if (currIndex != null && currIndex > 0 && currIndex < res.data.posts.size) {
                    val nextId = res.data.posts[currIndex - 1].post.id
                    postViewModel.initialize(nextId.left())
                    postViewModel.getData(account)
                }
            }
        },
        background = MaterialTheme.colorScheme.background,
    )

    SwipeableActionsBox(
        startActions = listOf(previousPostAction),
        endActions = listOf(nextPostAction),
    ) {
        content()
    }
}
