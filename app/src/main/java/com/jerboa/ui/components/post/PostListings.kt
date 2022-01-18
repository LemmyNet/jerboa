package com.jerboa.ui.components.post

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.jerboa.datatypes.CommunitySafe
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.samplePostView
import com.jerboa.db.Account
import com.jerboa.isScrolledToEnd

@Composable
fun PostListings(
    posts: List<PostView>,
    contentAboveListings: @Composable () -> Unit = {},
    onUpvoteClick: (postView: PostView) -> Unit = {},
    onDownvoteClick: (postView: PostView) -> Unit = {},
    onPostClick: (postView: PostView) -> Unit = {},
    onPostLinkClick: (url: String) -> Unit = {},
    onSaveClick: (postView: PostView) -> Unit = {},
    onEditPostClick: (postView: PostView) -> Unit = {},
    onCommunityClick: (community: CommunitySafe) -> Unit = {},
    onPersonClick: (personId: Int) -> Unit = {},
    onSwipeRefresh: () -> Unit = {},
    loading: Boolean = false,
    isScrolledToEnd: () -> Unit = {},
    account: Account?,
) {
    val listState = rememberLazyListState()

    SwipeRefresh(
        state = rememberSwipeRefreshState(loading),
        onRefresh = onSwipeRefresh,
    ) {
        LazyColumn(
            state = listState,
        ) {

            // TODO this should be a .also?
            item {
                contentAboveListings()
            }

            // List of items
            items(posts) { postView ->
                PostListing(
                    postView = postView,
                    onUpvoteClick = onUpvoteClick,
                    onDownvoteClick = onDownvoteClick,
                    onPostClick = onPostClick,
                    onPostLinkClick = onPostLinkClick,
                    onSaveClick = onSaveClick,
                    onCommunityClick = onCommunityClick,
                    onPersonClick = onPersonClick,
                    onEditPostClick = onEditPostClick,
                    account = account,
                    isModerator = false // TODO can't know with many posts
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
        account = null,
    )
}
