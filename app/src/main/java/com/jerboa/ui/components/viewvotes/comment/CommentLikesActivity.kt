package com.jerboa.ui.components.viewvotes.comment

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.isScrolledToEnd
import com.jerboa.model.CommentLikesViewModel
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.JerboaPullRefreshIndicator
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.isLoading
import com.jerboa.ui.components.common.isRefreshing
import com.jerboa.ui.components.viewvotes.ViewVotesBody
import com.jerboa.ui.theme.MEDIUM_PADDING
import it.vercruysse.lemmyapi.v0x19.datatypes.CommentId

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CommentLikesActivity(
    appState: JerboaAppState,
    commentId: CommentId,
    onBack: () -> Unit,
) {
    Log.d("jerboa", "got to comment likes activity")

    val commentLikesViewModel: CommentLikesViewModel = viewModel(factory = CommentLikesViewModel.Companion.Factory(commentId))

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                text = stringResource(R.string.comment_votes),
                onClickBack = onBack,
            )
        },
        content = { padding ->

            val listState = rememberLazyListState()

            // observer when reached end of list
            val endOfListReached by remember {
                derivedStateOf {
                    listState.isScrolledToEnd()
                }
            }

            // act when end of list reached
            if (endOfListReached) {
                LaunchedEffect(Unit) {
                    commentLikesViewModel.appendLikes()
                }
            }

            val refreshing = commentLikesViewModel.likesRes.isRefreshing()

            val refreshState =
                rememberPullRefreshState(
                    refreshing = refreshing,
                    onRefresh = {
                        commentLikesViewModel.resetPage()
                        commentLikesViewModel.getLikes(ApiState.Refreshing)
                    },
                )

            Box(
                modifier = Modifier.pullRefresh(refreshState)
                    .padding(
                        vertical = padding.calculateTopPadding(),
                        horizontal = MEDIUM_PADDING,
                    ),
            ) {
                JerboaPullRefreshIndicator(
                    refreshing,
                    refreshState,
                    Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(100F),
                )

                if (commentLikesViewModel.likesRes.isLoading()) {
                    LoadingBar()
                }

                when (val likesRes = commentLikesViewModel.likesRes) {
                    ApiState.Empty -> ApiEmptyText()
                    is ApiState.Failure -> ApiErrorText(likesRes.msg)
                    is ApiState.Holder -> {
                        val likes = likesRes.data.comment_likes
                        ViewVotesBody(
                            likes = likes,
                            listState = listState,
                            onPersonClick = appState::toProfile,
                        )
                    }
                    else -> {}
                }
            }
        },
    )
}
