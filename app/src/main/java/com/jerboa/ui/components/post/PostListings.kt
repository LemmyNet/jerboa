package com.jerboa.ui.components.post

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.samplePostView
import com.jerboa.db.Account

@Composable
fun PostListings(
    posts: List<PostView>,
    onItemClicked: (postView: PostView) -> Unit = {},
    onUpvoteClick: (postView: PostView) -> Unit = {},
    onDownvoteClick: (postView: PostView) -> Unit = {},
    navController: NavController? = null,
    account: Account? = null,
) {
    // Remember our own LazyListState, can be
    // used to move to any position in the column.
    val listState = rememberLazyListState()

    LazyColumn(state = listState) {
        // List of items
        items(posts) { postView ->
            PostListing(
                postView = postView,
                onItemClicked = onItemClicked,
                onUpvoteClick = onUpvoteClick,
                onDownvoteClick = onDownvoteClick,
                navController = navController,
                account = account,
            )
        }
    }
}

@Preview
@Composable
fun PreviewPostListings() {
    PostListings(
        posts = listOf(samplePostView, samplePostView)
    )
}
