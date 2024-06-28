package com.jerboa.ui.components.viewvotes.post

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
import com.jerboa.model.PostLikesViewModel
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.JerboaLoadingBar
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.TriggerWhenReachingEnd
import com.jerboa.ui.components.common.isRefreshing
import com.jerboa.ui.components.viewvotes.ViewVotesBody
import it.vercruysse.lemmyapi.datatypes.PostId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostLikesScreen(
    appState: JerboaAppState,
    postId: PostId,
    onBack: () -> Unit,
) {
    Log.d("jerboa", "got to post likes screen")

    val postLikesViewModel: PostLikesViewModel = viewModel(factory = PostLikesViewModel.Companion.Factory(postId))

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                text = stringResource(R.string.post_votes),
                onClickBack = onBack,
            )
        },
        content = { padding ->

            val listState = rememberLazyListState()

            TriggerWhenReachingEnd(listState, false) {
                postLikesViewModel.appendLikes()
            }

            PullToRefreshBox(
                modifier = Modifier.padding(padding),
                isRefreshing = postLikesViewModel.likesRes.isRefreshing(),
                onRefresh = {
                    postLikesViewModel.resetPage()
                    postLikesViewModel.getLikes(ApiState.Refreshing)
                },
            ) {
                JerboaLoadingBar(postLikesViewModel.likesRes)

                when (val likesRes = postLikesViewModel.likesRes) {
                    ApiState.Empty -> ApiEmptyText()
                    is ApiState.Failure -> ApiErrorText(likesRes.msg)
                    is ApiState.Holder -> {
                        val likes = likesRes.data.post_likes
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
