package com.jerboa.ui.components.post

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import arrow.core.Either
import com.jerboa.JerboaAppState
import com.jerboa.PostViewMode
import com.jerboa.R
import com.jerboa.VoteType
import com.jerboa.api.ApiState
import com.jerboa.buildCommentsTree
import com.jerboa.datatypes.getLocalizedCommentSortTypeName
import com.jerboa.db.entity.isAnon
import com.jerboa.feat.doIfReadyElseDisplayInfo
import com.jerboa.getCommentParentId
import com.jerboa.getDepthFromComment
import com.jerboa.model.AccountViewModel
import com.jerboa.model.PostViewModel
import com.jerboa.model.ReplyItem
import com.jerboa.model.SiteViewModel
import com.jerboa.newVote
import com.jerboa.scrollToNextParentComment
import com.jerboa.scrollToPreviousParentComment
import com.jerboa.ui.components.comment.ShowCommentContextButtons
import com.jerboa.ui.components.comment.commentNodeItems
import com.jerboa.ui.components.comment.edit.CommentEditReturn
import com.jerboa.ui.components.comment.reply.CommentReplyReturn
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.CommentNavigationBottomAppBar
import com.jerboa.ui.components.common.CommentSortOptionsDropdown
import com.jerboa.ui.components.common.JerboaPullRefreshIndicator
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.apiErrorToast
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.isLoading
import com.jerboa.ui.components.common.isRefreshing
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.post.edit.PostEditReturn
import it.vercruysse.lemmyapi.dto.CommentSortType
import it.vercruysse.lemmyapi.v0x19.datatypes.*

object PostViewReturn {
    const val POST_VIEW = "post-view::return(post-view)"
}

@Composable
fun CommentsHeaderTitle(selectedSortType: CommentSortType) {
    val ctx = LocalContext.current
    Column {
        Text(
            text = stringResource(R.string.post_activity_comments),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = getLocalizedCommentSortTypeName(ctx, selectedSortType),
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
    ExperimentalMaterialApi::class,
)
@Composable
fun PostActivity(
    id: Either<PostId, CommentId>,
    siteViewModel: SiteViewModel,
    accountViewModel: AccountViewModel,
    appState: JerboaAppState,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    showCollapsedCommentContent: Boolean,
    showActionBarByDefault: Boolean,
    showVotingArrowsInListView: Boolean,
    showParentCommentNavigationButtons: Boolean,
    navigateParentCommentsWithVolumeButtons: Boolean,
    blurNSFW: Int,
    showPostLinkPreview: Boolean,
    postActionbarMode: Int,
) {
    Log.d("jerboa", "got to post activity")

    val ctx = LocalContext.current

    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val postViewModel: PostViewModel = viewModel(factory = PostViewModel.Companion.Factory(id))

    appState.ConsumeReturn<PostView>(PostEditReturn.POST_VIEW, postViewModel::updatePost)
    appState.ConsumeReturn<CommentView>(CommentReplyReturn.COMMENT_VIEW, postViewModel::appendComment)
    appState.ConsumeReturn<CommentView>(CommentEditReturn.COMMENT_VIEW, postViewModel::updateComment)

    val onClickSortType = { commentSortType: CommentSortType ->
        postViewModel.updateSortType(commentSortType)
        postViewModel.getData()
    }

    val selectedSortType = postViewModel.sortType

    // Holds expanded comment ids
    val unExpandedComments = postViewModel.unExpandedComments
    val commentsWithToggledActionBar = postViewModel.commentsWithToggledActionBar
    var showSortOptions by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val listState = rememberLazyListState()
    var lazyListIndexTracker: Int
    val parentListStateIndexes = remember { mutableListOf<Int>() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val pullRefreshState =
        rememberPullRefreshState(
            refreshing = postViewModel.postRes.isRefreshing(),
            onRefresh = {
                postViewModel.getData(ApiState.Refreshing)
            },
        )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
        modifier =
            Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .semantics { testTagsAsResourceId = true }
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .focusRequester(focusRequester)
                .focusable()
                .onKeyEvent { keyEvent ->
                    if (navigateParentCommentsWithVolumeButtons) {
                        when (keyEvent.key) {
                            Key.VolumeUp -> {
                                scrollToPreviousParentComment(scope, parentListStateIndexes, listState)
                                true
                            }

                            Key.VolumeDown -> {
                                scrollToNextParentComment(scope, parentListStateIndexes, listState)
                                true
                            }

                            else -> {
                                false
                            }
                        }
                    } else {
                        false
                    }
                },
        bottomBar = {
            if (showParentCommentNavigationButtons) {
                CommentNavigationBottomAppBar(
                    scope,
                    parentListStateIndexes,
                    listState,
                )
            }
        },
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        CommentsHeaderTitle(
                            selectedSortType = selectedSortType,
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            modifier = Modifier.testTag("jerboa:back"),
                            onClick = appState::navigateUp,
                        ) {
                            Icon(
                                Icons.Outlined.ArrowBack,
                                contentDescription = stringResource(R.string.topAppBar_back),
                            )
                        }
                    },
                    actions = {
                        Box {
                            IconButton(onClick = {
                                showSortOptions = true
                            }) {
                                Icon(
                                    Icons.Outlined.Sort,
                                    contentDescription = stringResource(R.string.selectSort),
                                )
                            }

                            CommentSortOptionsDropdown(
                                expanded = showSortOptions,
                                selectedSortType = selectedSortType,
                                onDismissRequest = { showSortOptions = false },
                                onClickSortType = onClickSortType,
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
        },
        content = { padding ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState),
            ) {
                parentListStateIndexes.clear()
                lazyListIndexTracker = 2
                JerboaPullRefreshIndicator(
                    postViewModel.postRes.isRefreshing(),
                    pullRefreshState,
                    // zIndex needed bc some elements of a post get drawn above it.
                    Modifier
                        .padding(padding)
                        .align(Alignment.TopCenter)
                        .zIndex(100f),
                )
                when (val postRes = postViewModel.postRes) {
                    is ApiState.Loading ->
                        LoadingBar(padding)
                    is ApiState.Failure -> ApiErrorText(postRes.msg, padding)
                    is ApiState.Success -> {
                        val postView = postRes.data.post_view

                        if (!account.isAnon()) appState.addReturn(PostViewReturn.POST_VIEW, postView.copy(read = true))
                        LazyColumn(
                            state = listState,
                            modifier =
                                Modifier
                                    .padding(top = padding.calculateTopPadding())
                                    .simpleVerticalScrollbar(listState)
                                    .testTag("jerboa:comments"),
                        ) {
                            item(key = "${postView.post.id}_listing", "post_listing") {
                                PostListing(
                                    postView = postView,
                                    onUpvoteClick = { pv ->
                                        account.doIfReadyElseDisplayInfo(
                                            appState,
                                            ctx,
                                            snackbarHostState,
                                            scope,
                                            siteViewModel,
                                            accountViewModel,
                                        ) {
                                            postViewModel.likePost(
                                                CreatePostLike(
                                                    post_id = pv.post.id,
                                                    score =
                                                        newVote(
                                                            postView.my_vote,
                                                            VoteType.Upvote,
                                                        ),
                                                ),
                                            )
                                        }
                                    },
                                    onDownvoteClick = { pv ->
                                        account.doIfReadyElseDisplayInfo(
                                            appState,
                                            ctx,
                                            snackbarHostState,
                                            scope,
                                            siteViewModel,
                                            accountViewModel,
                                        ) {
                                            postViewModel.likePost(
                                                CreatePostLike(
                                                    post_id = pv.post.id,
                                                    score =
                                                        newVote(
                                                            postView.my_vote,
                                                            VoteType.Downvote,
                                                        ),
                                                ),
                                            )
                                        }
                                    },
                                    onReplyClick = { pv ->
                                        appState.toCommentReply(
                                            replyItem = ReplyItem.PostItem(pv),
                                        )
                                    },
                                    onPostClick = {},
                                    onSaveClick = { pv ->
                                        account.doIfReadyElseDisplayInfo(
                                            appState,
                                            ctx,
                                            snackbarHostState,
                                            scope,
                                            siteViewModel,
                                            accountViewModel,
                                        ) {
                                            postViewModel.savePost(
                                                SavePost(
                                                    post_id = pv.post.id,
                                                    save = !pv.saved,
                                                ),
                                            )
                                        }
                                    },
                                    onCommunityClick = { community ->
                                        appState.toCommunity(id = community.id)
                                    },
                                    onEditPostClick = { pv ->
                                        appState.toPostEdit(
                                            postView = pv,
                                        )
                                    },
                                    onDeletePostClick = { pv ->
                                        account.doIfReadyElseDisplayInfo(
                                            appState,
                                            ctx,
                                            snackbarHostState,
                                            scope,
                                            siteViewModel,
                                            accountViewModel,
                                        ) {
                                            postViewModel.deletePost(
                                                DeletePost(
                                                    post_id = pv.post.id,
                                                    deleted = !pv.post.deleted,
                                                ),
                                            )
                                        }
                                    },
                                    onReportClick = { pv ->
                                        appState.toPostReport(id = pv.post.id)
                                    },
                                    onPersonClick = appState::toProfile,
                                    onBlockCommunityClick = { c ->
                                        account.doIfReadyElseDisplayInfo(
                                            appState,
                                            ctx,
                                            snackbarHostState,
                                            scope,
                                            siteViewModel,
                                            accountViewModel,
                                        ) {
                                            postViewModel.blockCommunity(
                                                BlockCommunity(
                                                    community_id = c.id,
                                                    block = true,
                                                ),
                                                ctx,
                                            )
                                        }
                                    },
                                    onBlockCreatorClick = { person ->
                                        account.doIfReadyElseDisplayInfo(
                                            appState,
                                            ctx,
                                            snackbarHostState,
                                            scope,
                                            siteViewModel,
                                            accountViewModel,
                                        ) {
                                            postViewModel.blockPerson(
                                                BlockPerson(
                                                    person_id = person.id,
                                                    block = true,
                                                ),
                                                ctx,
                                            )
                                        }
                                    },
                                    showReply = true, // Do nothing
                                    showCommunityName = true,
                                    fullBody = true,
                                    account = account,
                                    postViewMode = PostViewMode.Card,
                                    enableDownVotes = siteViewModel.enableDownvotes(),
                                    showAvatar = siteViewModel.showAvatar(),
                                    showVotingArrowsInListView = showVotingArrowsInListView,
                                    useCustomTabs = useCustomTabs,
                                    usePrivateTabs = usePrivateTabs,
                                    blurNSFW = blurNSFW,
                                    appState = appState,
                                    showPostLinkPreview = showPostLinkPreview,
                                    showIfRead = false,
                                    showScores = siteViewModel.showScores(),
                                    postActionbarMode = postActionbarMode,
                                )
                            }

                            if (postViewModel.commentsRes.isLoading()) {
                                item(contentType = "loadingbar") {
                                    LoadingBar()
                                }
                            }

                            when (val commentsRes = postViewModel.commentsRes) {
                                is ApiState.Failure ->
                                    item(key = "error") {
                                        apiErrorToast(
                                            ctx,
                                            commentsRes.msg,
                                        )
                                    }

                                is ApiState.Holder -> {
                                    val commentTree =
                                        buildCommentsTree(
                                            commentsRes.data.comments,
                                            id.fold(
                                                { null },
                                                { it },
                                            ),
                                        )

                                    val toggleExpanded: (Int) -> Unit = { commentId: Int ->
                                        if (unExpandedComments.contains(commentId)) {
                                            unExpandedComments.remove(commentId)
                                        } else {
                                            unExpandedComments.add(commentId)
                                        }
                                    }

                                    val toggleActionBar: (Int) -> Unit = { commentId: Int ->
                                        if (commentsWithToggledActionBar.contains(commentId)) {
                                            commentsWithToggledActionBar.remove(commentId)
                                        } else {
                                            commentsWithToggledActionBar.add(commentId)
                                        }
                                    }

                                    item(key = "${postView.post.id}_is_comment_view", contentType = "contextButtons") {
                                        if (postViewModel.isCommentView()) {
                                            val firstCommentNodeData = commentTree.firstOrNull()

                                            val firstCommentPath = firstCommentNodeData?.getPath()

                                            val hasParent = firstCommentPath != null && getDepthFromComment(firstCommentPath) > 0

                                            val commentParentId = firstCommentPath?.let(::getCommentParentId)

                                            ShowCommentContextButtons(
                                                postView.post.id,
                                                commentParentId = commentParentId,
                                                showContextButton = hasParent,
                                                onPostClick = appState::toPost,
                                                onCommentClick = appState::toComment,
                                            )
                                        }
                                    }

                                    commentNodeItems(
                                        nodes = commentTree,
                                        increaseLazyListIndexTracker = {
                                            lazyListIndexTracker++
                                        },
                                        addToParentIndexes = {
                                            parentListStateIndexes.add(lazyListIndexTracker)
                                        },
                                        isFlat = false,
                                        isExpanded = { commentId ->
                                            !unExpandedComments.contains(
                                                commentId,
                                            )
                                        },
                                        toggleExpanded = toggleExpanded,
                                        toggleActionBar = toggleActionBar,
                                        onMarkAsReadClick = {},
                                        onCommentClick = { commentView -> toggleExpanded(commentView.comment.id) },
                                        onUpvoteClick = { cv ->
                                            account.doIfReadyElseDisplayInfo(
                                                appState,
                                                ctx,
                                                snackbarHostState,
                                                scope,
                                                siteViewModel,
                                                accountViewModel,
                                            ) {
                                                postViewModel.likeComment(
                                                    CreateCommentLike(
                                                        comment_id = cv.comment.id,
                                                        score =
                                                            newVote(
                                                                cv.my_vote,
                                                                VoteType.Upvote,
                                                            ),
                                                    ),
                                                )
                                            }
                                        },
                                        onDownvoteClick = { cv ->
                                            account.doIfReadyElseDisplayInfo(
                                                appState,
                                                ctx,
                                                snackbarHostState,
                                                scope,
                                                siteViewModel,
                                                accountViewModel,
                                            ) {
                                                postViewModel.likeComment(
                                                    CreateCommentLike(
                                                        comment_id = cv.comment.id,
                                                        score =
                                                            newVote(
                                                                cv.my_vote,
                                                                VoteType.Downvote,
                                                            ),
                                                    ),
                                                )
                                            }
                                        },
                                        onReplyClick = { cv ->
                                            appState.toCommentReply(
                                                replyItem = ReplyItem.CommentItem(cv),
                                            )
                                        },
                                        onSaveClick = { cv ->
                                            account.doIfReadyElseDisplayInfo(
                                                appState,
                                                ctx,
                                                snackbarHostState,
                                                scope,
                                                siteViewModel,
                                                accountViewModel,
                                            ) {
                                                postViewModel.saveComment(
                                                    SaveComment(
                                                        comment_id = cv.comment.id,
                                                        save = !cv.saved,
                                                    ),
                                                )
                                            }
                                        },
                                        onPersonClick = appState::toProfile,
                                        onHeaderClick = { commentView -> toggleExpanded(commentView.comment.id) },
                                        onHeaderLongClick = { commentView -> toggleActionBar(commentView.comment.id) },
                                        onEditCommentClick = { cv ->
                                            appState.toCommentEdit(
                                                commentView = cv,
                                            )
                                        },
                                        onDeleteCommentClick = { cv ->
                                            account.doIfReadyElseDisplayInfo(
                                                appState,
                                                ctx,
                                                snackbarHostState,
                                                scope,
                                                siteViewModel,
                                                accountViewModel,
                                            ) {
                                                postViewModel.deleteComment(
                                                    DeleteComment(
                                                        comment_id = cv.comment.id,
                                                        deleted = !cv.comment.deleted,
                                                    ),
                                                )
                                            }
                                        },
                                        onReportClick = { cv ->
                                            appState.toCommentReport(id = cv.comment.id)
                                        },
                                        onCommentLinkClick = { cv ->
                                            appState.toComment(id = cv.comment.id)
                                        },
                                        onFetchChildrenClick = { cv ->
                                            postViewModel.fetchMoreChildren(
                                                commentView = cv,
                                            )
                                        },
                                        onBlockCreatorClick = { person ->
                                            account.doIfReadyElseDisplayInfo(
                                                appState,
                                                ctx,
                                                snackbarHostState,
                                                scope,
                                                siteViewModel,
                                                accountViewModel,
                                            ) {
                                                postViewModel.blockPerson(
                                                    BlockPerson(
                                                        person_id = person.id,
                                                        block = true,
                                                    ),
                                                    ctx,
                                                )
                                            }
                                        },
                                        onCommunityClick = { community ->
                                            appState.toCommunity(id = community.id)
                                        },
                                        onPostClick = {}, // Do nothing
                                        account = account,
                                        enableDownVotes = siteViewModel.enableDownvotes(),
                                        showAvatar = siteViewModel.showAvatar(),
                                        isCollapsedByParent = false,
                                        showCollapsedCommentContent = showCollapsedCommentContent,
                                        showActionBar = { commentId ->
                                            showActionBarByDefault xor
                                                commentsWithToggledActionBar.contains(
                                                    commentId,
                                                )
                                        },
                                        blurNSFW = blurNSFW,
                                        showScores = siteViewModel.showScores(),
                                    )
                                    item {
                                        Spacer(modifier = Modifier.height(100.dp))
                                    }
                                }

                                else -> {}
                            }
                            if (showParentCommentNavigationButtons) {
                                item {
                                    Spacer(modifier = Modifier.height(padding.calculateBottomPadding()))
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }
        },
    )
}
