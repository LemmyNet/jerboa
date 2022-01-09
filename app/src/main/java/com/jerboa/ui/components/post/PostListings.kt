package com.jerboa.ui.components.post

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.samplePostView
import com.jerboa.isScrolledToEnd

@Composable
fun PostListings(
    posts: List<PostView>,
    onUpvoteClick: (postView: PostView) -> Unit = {},
    onDownvoteClick: (postView: PostView) -> Unit = {},
    onPostClick: (postView: PostView) -> Unit = {},
    onSwipeRefresh: () -> Unit = {},
    loading: Boolean = false,
    isScrolledToEnd: () -> Unit = {},
) {
    val listState = rememberLazyListState()

    SwipeRefresh(
        state = rememberSwipeRefreshState(false),
        onRefresh = onSwipeRefresh,
    ) {
        LazyColumn(state = listState) {
            // List of items
            itemsIndexed(posts) { index, postView ->
                PostListing(
                    postView = postView,
                    onUpvoteClick = onUpvoteClick,
                    onDownvoteClick = onDownvoteClick,
                    onPostClick = onPostClick,
                )
            }
        }
    }

    // observer when reached end of list
    val endOfListReached by remember {
        derivedStateOf {
            listState.isScrolledToEnd()
        }
    }

    // act when end of list reached
    val ctx = LocalContext.current
    if (endOfListReached) {
        LaunchedEffect(endOfListReached) {
            isScrolledToEnd()
        }
    }
}

@Preview
@Composable
fun PreviewPostListings() {
    PostListings(
        posts = listOf(samplePostView, samplePostView),
    )
}
