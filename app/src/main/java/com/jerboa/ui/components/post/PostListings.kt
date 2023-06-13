package com.jerboa.ui.components.post

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.jerboa.PostViewMode
import com.jerboa.datatypes.CommunitySafe
import com.jerboa.datatypes.PersonSafe
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.Tagline
import com.jerboa.datatypes.samplePostView
import com.jerboa.db.Account
import com.jerboa.isScrolledToEnd
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.home.Tagline
import com.jerboa.ui.theme.SMALL_PADDING

@Composable
fun PostListings(
    posts: List<PostView>,
    contentAboveListings: @Composable () -> Unit = {},
    onUpvoteClick: (postView: PostView) -> Unit,
    onDownvoteClick: (postView: PostView) -> Unit,
    onPostClick: (postView: PostView) -> Unit,
    onPostLinkClick: (url: String) -> Unit,
    onSaveClick: (postView: PostView) -> Unit,
    onEditPostClick: (postView: PostView) -> Unit,
    onDeletePostClick: (postView: PostView) -> Unit,
    onReportClick: (postView: PostView) -> Unit,
    onCommunityClick: (community: CommunitySafe) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    onBlockCommunityClick: (community: CommunitySafe) -> Unit,
    onBlockCreatorClick: (person: PersonSafe) -> Unit,
    onSwipeRefresh: () -> Unit,
    loading: Boolean = false,
    isScrolledToEnd: () -> Unit,
    account: Account?,
    showCommunityName: Boolean = true,
    padding: PaddingValues = PaddingValues(0.dp),
    listState: LazyListState,
    taglines: List<Tagline>?,
    postViewMode: PostViewMode,
    showVotingArrowsInListView: Boolean,
) {
    ReportDrawnWhen {
        !loading
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(loading),
        onRefresh = onSwipeRefresh,
        indicatorPadding = padding,
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(padding)
                .simpleVerticalScrollbar(listState)
                .testTag("jerboa:posts"),
        ) {
            item {
                taglines?.let { Tagline(it) }
            }
            // TODO this should be a .also?
            item {
                contentAboveListings()
            }

            // List of items
            items(
                posts,
                key = { postView ->
                    postView.post.id
                },
            ) { postView ->
                PostListing(
                    postView = postView,
                    onUpvoteClick = onUpvoteClick,
                    onDownvoteClick = onDownvoteClick,
                    onPostClick = onPostClick,
                    onPostLinkClick = onPostLinkClick,
                    onSaveClick = onSaveClick,
                    onCommunityClick = onCommunityClick,
                    onEditPostClick = onEditPostClick,
                    onDeletePostClick = onDeletePostClick,
                    onReportClick = onReportClick,
                    onPersonClick = onPersonClick,
                    onBlockCommunityClick = onBlockCommunityClick,
                    onBlockCreatorClick = onBlockCreatorClick,
                    isModerator = false,
                    showCommunityName = showCommunityName,
                    fullBody = false,
                    account = account, // TODO can't know with many posts
                    postViewMode = postViewMode,
                    showVotingArrowsInListView = showVotingArrowsInListView,
                )
                Divider(modifier = Modifier.padding(bottom = SMALL_PADDING))
            }
        }
    }

    // observer when reached end of list
    val endOfListReached by remember {
        derivedStateOf {
            listState.isScrolledToEnd()
        }
    }

    // Act when end of list reached
    if (endOfListReached) {
        LaunchedEffect(Unit) {
            isScrolledToEnd()
        }
    }
}

@Preview
@Composable
fun PreviewPostListings() {
    PostListings(
        posts = listOf(samplePostView, samplePostView),
        onUpvoteClick = {},
        onDownvoteClick = {},
        onPostClick = {},
        onPostLinkClick = {},
        onSaveClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onReportClick = {},
        onCommunityClick = {},
        onPersonClick = {},
        onBlockCommunityClick = {},
        onBlockCreatorClick = {},
        onSwipeRefresh = {},
        isScrolledToEnd = {},
        account = null,
        listState = rememberLazyListState(),
        taglines = null,
        postViewMode = PostViewMode.Card,
        showVotingArrowsInListView = true,
    )
}
