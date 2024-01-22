package com.jerboa.ui.components.post

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.InstantScores
import com.jerboa.JerboaAppState
import com.jerboa.PostViewMode
import com.jerboa.VoteType
import com.jerboa.calculateNewInstantScores
import com.jerboa.datatypes.PostFeatureData
import com.jerboa.datatypes.sampleLinkPostView
import com.jerboa.datatypes.samplePostView
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.feat.SwipeToActionPreset
import com.jerboa.feat.SwipeToActionType
import com.jerboa.isScrolledToEnd
import com.jerboa.rememberJerboaAppState
import com.jerboa.ui.components.common.RetryLoadingPosts
import com.jerboa.ui.components.common.SwipeToAction
import com.jerboa.ui.components.common.rememberSwipeActionState
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.theme.SMALL_PADDING
import it.vercruysse.lemmyapi.v0x19.datatypes.Community
import it.vercruysse.lemmyapi.v0x19.datatypes.CommunityModeratorView
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonView
import it.vercruysse.lemmyapi.v0x19.datatypes.PostView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
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
    onLockPostClick: (postView: PostView) -> Unit,
    onFeaturePostClick: (data: PostFeatureData) -> Unit,
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
    swipeToActionPreset: Int,
) {
    val leftActions = SwipeToActionPreset.entries[swipeToActionPreset].leftActions
    val rightActions = SwipeToActionPreset.entries[swipeToActionPreset].rightActions
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
            val instantScores =
                remember {
                    mutableStateOf(
                        InstantScores(
                            myVote = postView.my_vote,
                            score = postView.counts.score,
                            upvotes = postView.counts.upvotes,
                            downvotes = postView.counts.downvotes,
                        ),
                    )
                }
            val swipeState =
                rememberSwipeActionState(
                    leftActions = leftActions,
                    rightActions = rightActions,
                    onAction = { action ->
                        when (action) {
                            SwipeToActionType.Upvote -> {
                                onUpvoteClick(postView)
                                instantScores.value =
                                    calculateNewInstantScores(
                                        instantScores.value,
                                        voteType = VoteType.Upvote,
                                    )
                            }

                            SwipeToActionType.Downvote -> {
                                onDownvoteClick(postView)
                                instantScores.value =
                                    calculateNewInstantScores(
                                        instantScores.value,
                                        voteType = VoteType.Downvote,
                                    )
                            }

                            SwipeToActionType.Save -> {
                                onSaveClick(postView)
                            }

                            SwipeToActionType.Reply -> {
                                onPostClick(postView)
                            }
                        }
                    },
                )

            val content: @Composable RowScope.() -> Unit = {
                PostListing(
                    postView = postView,
                    admins = admins,
                    moderators = moderators,
                    useCustomTabs = useCustomTabs,
                    usePrivateTabs = usePrivateTabs,
                    onUpvoteClick = {
                        onUpvoteClick(it)
                        instantScores.value =
                            calculateNewInstantScores(
                                instantScores.value,
                                voteType = VoteType.Upvote,
                            )
                    },
                    onDownvoteClick = {
                        onDownvoteClick(it)
                        instantScores.value =
                            calculateNewInstantScores(
                                instantScores.value,
                                voteType = VoteType.Downvote,
                            )
                    },
                    onPostClick = onPostClick,
                    onSaveClick = onSaveClick,
                    onCommunityClick = onCommunityClick,
                    onEditPostClick = onEditPostClick,
                    onDeletePostClick = onDeletePostClick,
                    onReportClick = onReportClick,
                    onRemoveClick = onRemoveClick,
                    onLockPostClick = onLockPostClick,
                    onFeaturePostClick = onFeaturePostClick,
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
                    instantScores = instantScores.value,
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
            SwipeToAction(
                leftActions = leftActions,
                rightActions = rightActions,
                swipeableContent = content,
                swipeState = swipeState,
            )
            Divider(modifier = Modifier.padding(bottom = SMALL_PADDING))
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
        onLockPostClick = {},
        onFeaturePostClick = {},
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
        swipeToActionPreset = 0,
    )
}
