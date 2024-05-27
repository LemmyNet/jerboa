package com.jerboa.ui.components.post

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.JerboaAppState
import com.jerboa.PostViewMode
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.datatypes.PostFeatureData
import com.jerboa.datatypes.VoteDisplayMode
import com.jerboa.datatypes.sampleLinkPostView
import com.jerboa.datatypes.samplePostView
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.feat.BlurNSFW
import com.jerboa.feat.PostActionBarMode
import com.jerboa.feat.SwipeToActionPreset
import com.jerboa.rememberJerboaAppState
import com.jerboa.ui.components.common.TriggerWhenReachingEnd
import com.jerboa.ui.components.common.RetryLoadingPosts
import it.vercruysse.lemmyapi.v0x19.datatypes.Community
import it.vercruysse.lemmyapi.v0x19.datatypes.Person
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonId
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonView
import it.vercruysse.lemmyapi.v0x19.datatypes.PostId
import it.vercruysse.lemmyapi.v0x19.datatypes.PostView

@Composable
fun PostListings(
    posts: List<PostView>,
    admins: List<PersonView>,
    moderators: List<PersonId>?,
    contentAboveListings: @Composable () -> Unit = {},
    onUpvoteClick: (postView: PostView) -> Unit,
    onDownvoteClick: (postView: PostView) -> Unit,
    onPostClick: (postView: PostView) -> Unit,
    onSaveClick: (postView: PostView) -> Unit,
    onReplyClick: (postView: PostView) -> Unit,
    onEditPostClick: (postView: PostView) -> Unit,
    onDeletePostClick: (postView: PostView) -> Unit,
    onReportClick: (postView: PostView) -> Unit,
    onRemoveClick: (postView: PostView) -> Unit,
    onBanPersonClick: (person: Person) -> Unit,
    onBanFromCommunityClick: (banData: BanFromCommunityData) -> Unit,
    onLockPostClick: (postView: PostView) -> Unit,
    onFeaturePostClick: (data: PostFeatureData) -> Unit,
    onViewPostVotesClick: (PostId) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
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
    blurNSFW: BlurNSFW,
    showPostLinkPreviews: Boolean,
    appState: JerboaAppState,
    markAsReadOnScroll: Boolean,
    onMarkAsRead: (postView: PostView) -> Unit,
    showIfRead: Boolean,
    voteDisplayMode: VoteDisplayMode,
    postActionBarMode: PostActionBarMode,
    showPostAppendRetry: Boolean,
    swipeToActionPreset: SwipeToActionPreset,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
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
                onReplyClick = onReplyClick,
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
                voteDisplayMode = voteDisplayMode,
                postActionBarMode = postActionBarMode,
                swipeToActionPreset = swipeToActionPreset,
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
        }

        if (showPostAppendRetry) {
            item(contentType = "retry_posts") {
                RetryLoadingPosts(loadMorePosts)
            }
        }
    }

    TriggerWhenReachingEnd(listState, loadMorePosts, showPostAppendRetry)
}


@Preview
@Composable
fun PreviewPostListings() {
    PostListings(
        posts = listOf(samplePostView, sampleLinkPostView),
        admins = emptyList(),
        moderators = emptyList(),
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
        blurNSFW = BlurNSFW.NSFW,
        showPostLinkPreviews = true,
        appState = rememberJerboaAppState(),
        markAsReadOnScroll = false,
        onMarkAsRead = {},
        showIfRead = true,
        voteDisplayMode = VoteDisplayMode.Full,
        postActionBarMode = PostActionBarMode.Long,
        showPostAppendRetry = false,
        swipeToActionPreset = SwipeToActionPreset.TwoSides,
        onReplyClick = {},
    )
}
