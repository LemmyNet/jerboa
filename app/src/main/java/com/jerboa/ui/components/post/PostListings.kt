package com.jerboa.ui.components.post

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.JerboaAppState
import com.jerboa.PostViewMode
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.datatypes.PostFeatureData
import com.jerboa.datatypes.sampleLinkPostView
import com.jerboa.datatypes.samplePostView
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.isScrolledToEnd
import com.jerboa.rememberJerboaAppState
import com.jerboa.ui.components.common.RetryLoadingPosts
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.theme.SMALL_PADDING
import it.vercruysse.lemmyapi.v0x19.datatypes.CommentId
import it.vercruysse.lemmyapi.v0x19.datatypes.Community
import it.vercruysse.lemmyapi.v0x19.datatypes.CommunityModeratorView
import it.vercruysse.lemmyapi.v0x19.datatypes.Person
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonView
import it.vercruysse.lemmyapi.v0x19.datatypes.PostId
import it.vercruysse.lemmyapi.v0x19.datatypes.PostView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun PostListings(
    posts: ImmutableList<PostView>,
    admins: ImmutableList<PersonView>,
    moderators: ImmutableList<CommunityModeratorView>?,
    contentAboveListings: @Composable () -> Unit = {},
    onUpvoteClick: (postView: PostView) -> Unit,
    onDownvoteClick: (postView: PostView) -> Unit,
    onPostClick: (postView: PostView) -> Unit,
    onSaveClick: (postView: PostView) -> Unit,
    onEditPostClick: (postView: PostView) -> Unit,
    onDeletePostClick: (postView: PostView) -> Unit,
    onReportClick: (postView: PostView) -> Unit,
    onRemoveClick: (postView: PostView) -> Unit,
    onBanPersonClick: (person: Person) -> Unit,
    onBanFromCommunityClick: (banData: BanFromCommunityData) -> Unit,
    onLockPostClick: (postView: PostView) -> Unit,
    onFeaturePostClick: (data: PostFeatureData) -> Unit,
    onViewPostVotesClick: (PostId) -> Unit,
    onViewCommentVotesClick: (CommentId) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    loadMorePosts: () -> Unit,
    account: Account,
    showCommunityName: Boolean = true,
    padding: PaddingValues = PaddingValues(0.dp),
    listState: LazyListState,
    postViewMode: PostViewMode,
    showVotingArrowsInListView: Boolean,
    enableDownVotes: Boolean,
    showAvatar: Boolean,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: Int,
    showPostLinkPreviews: Boolean,
    appState: JerboaAppState,
    markAsReadOnScroll: Boolean,
    onMarkAsRead: (postView: PostView) -> Unit,
    showIfRead: Boolean,
    showScores: Boolean,
    postActionbarMode: Int,
    showPostAppendRetry: Boolean,
) {
    LazyColumn(
        state = listState,
        modifier =
            Modifier
                .padding(padding)
                .fillMaxSize()
                .simpleVerticalScrollbar(listState)
                .testTag("jerboa:posts"),
    ) {
        item(contentType = "aboveContent") {
            contentAboveListings()
        }
        // List of items
        itemsIndexed(
            items = posts,
            contentType = { _, _ -> "Post" },
        ) { index, postView ->
            PostListing(
                postView = postView,
                admins = admins,
                moderators = moderators,
                useCustomTabs = useCustomTabs,
                usePrivateTabs = usePrivateTabs,
                onUpvoteClick = onUpvoteClick,
                onDownvoteClick = onDownvoteClick,
                onPostClick = onPostClick,
                onSaveClick = onSaveClick,
                onCommunityClick = onCommunityClick,
                onEditPostClick = onEditPostClick,
                onDeletePostClick = onDeletePostClick,
                onReportClick = onReportClick,
                onRemoveClick = onRemoveClick,
                onBanPersonClick = onBanPersonClick,
                onBanFromCommunityClick = onBanFromCommunityClick,
                onLockPostClick = onLockPostClick,
                onFeaturePostClick = onFeaturePostClick,
                onViewVotesClick = onViewPostVotesClick,
                onPersonClick = onPersonClick,
                showCommunityName = showCommunityName,
                fullBody = false,
                account = account,
                postViewMode = postViewMode,
                showVotingArrowsInListView = showVotingArrowsInListView,
                enableDownVotes = enableDownVotes,
                showAvatar = showAvatar,
                blurNSFW = blurNSFW,
                appState = appState,
                showPostLinkPreview = showPostLinkPreviews,
                showIfRead = showIfRead,
                showScores = showScores,
                postActionbarMode = postActionbarMode,
            ).let {
                if (!postView.read && markAsReadOnScroll) {
                    DisposableEffect(key1 = postView.post.id) {
                        onDispose {
                            if (listState.isScrollInProgress && index < listState.firstVisibleItemIndex) {
                                onMarkAsRead(postView)
                            }
                        }
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(bottom = SMALL_PADDING))
        }

        if (showPostAppendRetry) {
            item(contentType = "retry_posts") {
                RetryLoadingPosts(loadMorePosts)
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
    if (endOfListReached && !showPostAppendRetry) {
        LaunchedEffect(Unit) {
            loadMorePosts()
        }
    }
}

@Preview
@Composable
fun PreviewPostListings() {
    PostListings(
        posts = persistentListOf(samplePostView, sampleLinkPostView),
        admins = persistentListOf(),
        moderators = persistentListOf(),
        onUpvoteClick = {},
        onDownvoteClick = {},
        onPostClick = {},
        onSaveClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onReportClick = {},
        onRemoveClick = {},
        onBanPersonClick = {},
        onBanFromCommunityClick = {},
        onLockPostClick = {},
        onFeaturePostClick = {},
        onViewPostVotesClick = {},
        onViewCommentVotesClick = {},
        onCommunityClick = {},
        onPersonClick = {},
        loadMorePosts = {},
        account = AnonAccount,
        listState = rememberLazyListState(),
        postViewMode = PostViewMode.Card,
        showVotingArrowsInListView = true,
        enableDownVotes = true,
        showAvatar = true,
        useCustomTabs = false,
        usePrivateTabs = false,
        blurNSFW = 1,
        showPostLinkPreviews = true,
        appState = rememberJerboaAppState(),
        markAsReadOnScroll = false,
        onMarkAsRead = {},
        showIfRead = true,
        showScores = true,
        postActionbarMode = 0,
        showPostAppendRetry = false,
    )
}
