package com.jerboa.ui.components.post

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import com.jerboa.PostViewMode
import com.jerboa.datatypes.sampleLinkPostView
import com.jerboa.datatypes.samplePostView
import com.jerboa.datatypes.types.Community
import com.jerboa.datatypes.types.Person
import com.jerboa.datatypes.types.PostView
import com.jerboa.db.entity.Account
import com.jerboa.isScrolledToEnd
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.theme.SMALL_PADDING
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun PostListings(
    posts: ImmutableList<PostView>,
    contentAboveListings: @Composable () -> Unit = {},
    onUpvoteClick: (postView: PostView) -> Unit,
    onDownvoteClick: (postView: PostView) -> Unit,
    onPostClick: (postView: PostView) -> Unit,
    onSaveClick: (postView: PostView) -> Unit,
    onEditPostClick: (postView: PostView) -> Unit,
    onDeletePostClick: (postView: PostView) -> Unit,
    onReportClick: (postView: PostView) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    onBlockCommunityClick: (community: Community) -> Unit,
    onBlockCreatorClick: (person: Person) -> Unit,
    onShareClick: (url: String) -> Unit,
    isScrolledToEnd: () -> Unit,
    account: Account?,
    showCommunityName: Boolean = true,
    padding: PaddingValues = PaddingValues(0.dp),
    listState: LazyListState,
    postViewMode: PostViewMode,
    showVotingArrowsInListView: Boolean,
    enableDownVotes: Boolean,
    showAvatar: Boolean,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: Boolean,
    openImageViewer: (url: String) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .simpleVerticalScrollbar(listState)
            .testTag("jerboa:posts"),
    ) {
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
                onShareClick = onShareClick,
                isModerator = false,
                showCommunityName = showCommunityName,
                fullBody = false,
                account = account, // TODO can't know with many posts
                postViewMode = postViewMode,
                showVotingArrowsInListView = showVotingArrowsInListView,
                enableDownVotes = enableDownVotes,
                showAvatar = showAvatar,
                useCustomTabs = useCustomTabs,
                usePrivateTabs = usePrivateTabs,
                blurNSFW = blurNSFW,
                openImageViewer = openImageViewer,
            )
            Divider(modifier = Modifier.padding(bottom = SMALL_PADDING))
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
        posts = persistentListOf(samplePostView, sampleLinkPostView),
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
        onShareClick = {},
        isScrolledToEnd = {},
        account = null,
        listState = rememberLazyListState(),
        postViewMode = PostViewMode.Card,
        showVotingArrowsInListView = true,
        enableDownVotes = true,
        showAvatar = true,
        useCustomTabs = false,
        usePrivateTabs = false,
        blurNSFW = true,
        openImageViewer = {},
    )
}
