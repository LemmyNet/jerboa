package com.jerboa.ui.components.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.PostViewMode
import com.jerboa.datatypes.CommunitySafe
import com.jerboa.datatypes.PersonSafe
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.Tagline
import com.jerboa.datatypes.sampleLinkPostView
import com.jerboa.datatypes.samplePostView
import com.jerboa.db.Account
import com.jerboa.isScrolledToEnd
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.home.Tagline
import com.jerboa.ui.theme.SMALL_PADDING

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostListings(
    posts: List<PostView>,
    contentAboveListings: @Composable () -> Unit = {},
    onUpvoteClick: (postView: PostView) -> Unit,
    onDownvoteClick: (postView: PostView) -> Unit,
    onPostClick: (postView: PostView) -> Unit,
    onSaveClick: (postView: PostView) -> Unit,
    onEditPostClick: (postView: PostView) -> Unit,
    onDeletePostClick: (postView: PostView) -> Unit,
    onReportClick: (postView: PostView) -> Unit,
    onCommunityClick: (community: CommunitySafe) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    onBlockCommunityClick: (community: CommunitySafe) -> Unit,
    onBlockCreatorClick: (person: PersonSafe) -> Unit,
    onSwipeRefresh: () -> Unit,
    onLinkClick: (String) -> Unit,
    loading: Boolean = false,
    isScrolledToEnd: () -> Unit,
    account: Account?,
    showCommunityName: Boolean = true,
    padding: PaddingValues = PaddingValues(0.dp),
    listState: LazyListState,
    taglines: List<Tagline>?,
    postViewMode: PostViewMode,
    showVotingArrowsInListView: Boolean,
    enableDownVotes: Boolean,
    showAvatar: Boolean,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = loading,
        onRefresh = onSwipeRefresh,
    )

    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        PullRefreshIndicator(loading, pullRefreshState, Modifier.align(Alignment.TopCenter))
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(padding)
                .simpleVerticalScrollbar(listState),
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
                    onSaveClick = onSaveClick,
                    onCommunityClick = onCommunityClick,
                    onEditPostClick = onEditPostClick,
                    onDeletePostClick = onDeletePostClick,
                    onReportClick = onReportClick,
                    onPersonClick = onPersonClick,
                    onBlockCommunityClick = onBlockCommunityClick,
                    onBlockCreatorClick = onBlockCreatorClick,
                    onLinkClick = onLinkClick,
                    isModerator = false,
                    showCommunityName = showCommunityName,
                    fullBody = false,
                    account = account, // TODO can't know with many posts
                    postViewMode = postViewMode,
                    showVotingArrowsInListView = showVotingArrowsInListView,
                    enableDownVotes = enableDownVotes,
                    showAvatar = showAvatar,
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
        posts = listOf(samplePostView, sampleLinkPostView),
        onUpvoteClick = {},
        onDownvoteClick = {},
        onPostClick = {},
        onSaveClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onReportClick = {},
        onCommunityClick = {},
        onPersonClick = {},
        onBlockCommunityClick = {},
        onBlockCreatorClick = {},
        onSwipeRefresh = {},
        onLinkClick = {},
        isScrolledToEnd = {},
        account = null,
        listState = rememberLazyListState(),
        taglines = null,
        postViewMode = PostViewMode.Card,
        showVotingArrowsInListView = true,
        enableDownVotes = true,
        showAvatar = true,
    )
}
