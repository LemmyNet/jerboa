package com.jerboa.ui.components.viewvotes.comment

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.model.CommentLikesViewModel
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.JerboaLoadingBar
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.TriggerWhenReachingEnd
import com.jerboa.ui.components.common.isRefreshing
import com.jerboa.ui.components.viewvotes.ViewVotesBody
import it.vercruysse.lemmyapi.datatypes.CommentId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentLikesScreen(
    appState: JerboaAppState,
    commentId: CommentId,
    onBack: () -> Unit,
) {
    Log.d("jerboa", "got to comment likes screen")

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

            TriggerWhenReachingEnd(listState, false) {
                commentLikesViewModel.appendLikes()
            }

            PullToRefreshBox(
                modifier = Modifier.padding(padding),
                isRefreshing = commentLikesViewModel.likesRes.isRefreshing(),
                onRefresh = {
                    commentLikesViewModel.resetPage()
                    commentLikesViewModel.getLikes(ApiState.Refreshing)
                },
            ) {
                JerboaLoadingBar(commentLikesViewModel.likesRes)

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
