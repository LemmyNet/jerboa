package com.jerboa.ui.components.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.JerboaAppState
import com.jerboa.SelectionVisibilityState
import com.jerboa.datatypes.BanData
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.datatypes.PostFeatureData
import com.jerboa.datatypes.sampleGetSiteRes
import com.jerboa.datatypes.sampleLinkPostView
import com.jerboa.datatypes.sampleMyUserInfo
import com.jerboa.datatypes.samplePostView
import com.jerboa.db.APP_SETTINGS_DEFAULT
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.db.entity.AppSettings
import com.jerboa.rememberJerboaAppState
import com.jerboa.ui.components.common.RetryLoadingPosts
import com.jerboa.ui.components.common.TriggerWhenReachingEnd
import com.jerboa.ui.components.common.selectedItemContainerColor
import com.jerboa.ui.theme.MEDIUM_PADDING
import it.vercruysse.lemmyapi.datatypes.Community
import it.vercruysse.lemmyapi.datatypes.GetSiteResponse
import it.vercruysse.lemmyapi.datatypes.MyUserInfo
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PostId
import it.vercruysse.lemmyapi.datatypes.PostView

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostListings(
    posts: List<PostView>,
    siteRes: GetSiteResponse,
    myUserInfo: MyUserInfo?,
    appSettings: AppSettings,
    contentAboveListings: @Composable () -> Unit = {},
    onUpvoteClick: (postView: PostView) -> Unit,
    onDownvoteClick: (postView: PostView) -> Unit,
    onPostClick: (postView: PostView) -> Unit,
    onSaveClick: (postView: PostView) -> Unit,
    onReplyClick: (postView: PostView) -> Unit,
    onEditPostClick: (postView: PostView) -> Unit,
    onDeletePostClick: (postView: PostView) -> Unit,
    onHidePostClick: (postView: PostView) -> Unit,
    onReportClick: (postView: PostView) -> Unit,
    onRemoveClick: (postView: PostView) -> Unit,
    onBanPersonClick: (banData: BanData) -> Unit,
    onBanFromCommunityClick: (banData: BanFromCommunityData) -> Unit,
    onLockPostClick: (postView: PostView) -> Unit,
    onFeaturePostClick: (data: PostFeatureData) -> Unit,
    onViewPostVotesClick: (PostId) -> Unit,
    onCommunityClick: (community: Community) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    loadMorePosts: () -> Unit,
    account: Account,
    showCommunityName: Boolean = true,
    listState: LazyListState,
    appState: JerboaAppState,
    onMarkAsRead: (postView: PostView) -> Unit,
    showIfRead: Boolean,
    showPostAppendRetry: Boolean,
    selectionState: SelectionVisibilityState<PostId>,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
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

            val selected = when (selectionState) {
                is SelectionVisibilityState.ShowSelection -> selectionState.selectedItem == postView.post.id
                SelectionVisibilityState.NoSelection -> false
            }

            Column(
                modifier = Modifier
                    .background(selectedItemContainerColor(selected)),
            ) {
                PostListing(
                    postView = postView,
                    siteRes = siteRes,
                    myUserInfo = myUserInfo,
                    appSettings = appSettings,
                    onUpvoteClick = onUpvoteClick,
                    onDownvoteClick = onDownvoteClick,
                    onReplyClick = onReplyClick,
                    onPostClick = onPostClick,
                    onSaveClick = onSaveClick,
                    onCommunityClick = onCommunityClick,
                    onEditPostClick = onEditPostClick,
                    onDeletePostClick = onDeletePostClick,
                    onHidePostClick = onHidePostClick,
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
                    appState = appState,
                    showIfRead = showIfRead,
                ).let {
                    if (postView.post_actions?.read_at == null && appSettings.markAsReadOnScroll) {
                        DisposableEffect(key1 = postView.post.id) {
                            onDispose {
                                if (listState.isScrollInProgress && index < listState.firstVisibleItemIndex) {
                                    onMarkAsRead(postView)
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(top = MEDIUM_PADDING),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }

        if (showPostAppendRetry) {
            item(contentType = "retry_posts") {
                RetryLoadingPosts(loadMorePosts)
            }
        }
    }

    TriggerWhenReachingEnd(listState, showPostAppendRetry, loadMorePosts)
}

@Preview
@Composable
fun PreviewPostListings() {
    PostListings(
        posts = listOf(samplePostView, sampleLinkPostView),
        myUserInfo = sampleMyUserInfo,
        siteRes = sampleGetSiteRes,
        appSettings = APP_SETTINGS_DEFAULT,
        onUpvoteClick = {},
        onDownvoteClick = {},
        onPostClick = {},
        onSaveClick = {},
        onEditPostClick = {},
        onDeletePostClick = {},
        onHidePostClick = {},
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
        appState = rememberJerboaAppState(),
        onMarkAsRead = {},
        showIfRead = true,
        showPostAppendRetry = false,
        onReplyClick = {},
        selectionState = SelectionVisibilityState.NoSelection,
    )
}
