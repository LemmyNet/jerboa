package com.jerboa.ui.components.post

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.jerboa.CommentEditDeps
import com.jerboa.CommentReplyDeps
import com.jerboa.ConsumeReturn
import com.jerboa.JerboaAppState
import com.jerboa.PostEditDeps
import com.jerboa.PostViewMode
import com.jerboa.R
import com.jerboa.VoteType
import com.jerboa.api.ApiState
import com.jerboa.buildCommentsTree
import com.jerboa.datatypes.types.BlockCommunity
import com.jerboa.datatypes.types.BlockPerson
import com.jerboa.datatypes.types.CommentId
import com.jerboa.datatypes.types.CommentSortType
import com.jerboa.datatypes.types.CommentView
import com.jerboa.datatypes.types.CreateCommentLike
import com.jerboa.datatypes.types.CreatePostLike
import com.jerboa.datatypes.types.DeleteComment
import com.jerboa.datatypes.types.DeletePost
import com.jerboa.datatypes.types.PostId
import com.jerboa.datatypes.types.PostView
import com.jerboa.datatypes.types.SaveComment
import com.jerboa.datatypes.types.SavePost
import com.jerboa.getCommentParentId
import com.jerboa.getDepthFromComment
import com.jerboa.getLocalizedCommentSortTypeName
import com.jerboa.isModerator
import com.jerboa.model.AccountViewModel
import com.jerboa.model.PostViewModel
import com.jerboa.model.ReplyItem
import com.jerboa.model.SiteViewModel
import com.jerboa.newVote
import com.jerboa.rootChannel
import com.jerboa.scrollToNextParentComment
import com.jerboa.scrollToPreviousParentComment
import com.jerboa.shareLink
import com.jerboa.ui.components.comment.ShowCommentContextButtons
import com.jerboa.ui.components.comment.commentNodeItems
import com.jerboa.ui.components.comment.edit.CommentEditReturn
import com.jerboa.ui.components.comment.reply.CommentReplyReturn
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.ApiErrorToast
import com.jerboa.ui.components.common.CommentNavigationBottomAppBar
import com.jerboa.ui.components.common.CommentSortOptionsDialog
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.isLoading
import com.jerboa.ui.components.common.isRefreshing
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.post.edit.PostEditReturn
import com.jerboa.util.InitializeRoute

@Composable
fun CommentsHeaderTitle(
    selectedSortType: CommentSortType,
) {
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
    blurNSFW: Boolean,
    showPostLinkPreview: Boolean,
) {
    Log.d("jerboa", "got to post activity")
    val transferCommentEditDepsViaRoot = appState.rootChannel<CommentEditDeps>()
    val transferCommentReplyDepsViaRoot = appState.rootChannel<CommentReplyDeps>()
    val transferPostEditDepsViaRoot = appState.rootChannel<PostEditDeps>()

    val ctx = LocalContext.current

    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val postViewModel: PostViewModel = viewModel()

    appState.ConsumeReturn<PostView>(PostEditReturn.POST_VIEW) { pv ->
        if (postViewModel.initialized) postViewModel.updatePost(pv)
    }

    appState.ConsumeReturn<CommentView>(CommentReplyReturn.COMMENT_VIEW) { cv ->
        if (postViewModel.initialized) postViewModel.appendComment(cv)
    }

    appState.ConsumeReturn<CommentView>(CommentEditReturn.COMMENT_VIEW) { cv ->
        if (postViewModel.initialized) postViewModel.updateComment(cv)
    }

    InitializeRoute(postViewModel) {
        postViewModel.initialize(id = id)
        postViewModel.getData(account)
    }

    val onClickSortType = { commentSortType: CommentSortType ->
        postViewModel.updateSortType(commentSortType)
        postViewModel.getData(account)
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

    val pullRefreshState = rememberPullRefreshState(
        refreshing = postViewModel.postRes.isRefreshing(),
        onRefresh = {
            postViewModel.getData(account, ApiState.Refreshing)
        },
        // Needs to be lower else it can hide behind the top bar
        refreshingOffset = 150.dp,
    )

    if (showSortOptions) {
        CommentSortOptionsDialog(
            selectedSortType = selectedSortType,
            onDismissRequest = { showSortOptions = false },
            onClickSortType = {
                showSortOptions = false
                onClickSortType(it)
            },
            siteVersion = siteViewModel.siteVersion(),
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = Modifier
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
                            onClick = appState::popBackStack,
                        ) {
                            Icon(
                                Icons.Outlined.ArrowBack,
                                contentDescription = stringResource(R.string.topAppBar_back),
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            showSortOptions = !showSortOptions
                        }) {
                            Icon(
                                Icons.Outlined.Sort,
                                contentDescription = stringResource(R.string.selectSort),
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
        },
        content = { padding ->
            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                parentListStateIndexes.clear()
                lazyListIndexTracker = 2
                PullRefreshIndicator(
                    postViewModel.postRes.isRefreshing(),
                    pullRefreshState,
                    // zIndex needed bc some elements of a post get drawn above it.
                    Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(100f),
                )
                when (val postRes = postViewModel.postRes) {
                    is ApiState.Loading ->
                        LoadingBar(padding)
                    is ApiState.Failure -> ApiErrorText(postRes.msg)
                    is ApiState.Success -> {
                        val postView = postRes.data.post_view

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .padding(top = padding.calculateTopPadding())
                                .simpleVerticalScrollbar(listState)
                                .testTag("jerboa:comments"),
                        ) {
                            item(key = "${postView.post.id}_listing", "post_listing") {
                                PostListing(
                                    postView = postView,
                                    onUpvoteClick = { pv ->
                                        account?.also { acct ->
                                            postViewModel.likePost(
                                                CreatePostLike(
                                                    post_id = pv.post.id,
                                                    score = newVote(
                                                        postView.my_vote,
                                                        VoteType.Upvote,
                                                    ),
                                                    auth = acct.jwt,
                                                ),
                                            )
                                        }
                                        // TODO will need to pass in postlistingsviewmodel
                                        // for the Home page to also be updated
                                    },
                                    onDownvoteClick = { pv ->
                                        account?.also { acct ->
                                            postViewModel.likePost(
                                                CreatePostLike(
                                                    post_id = pv.post.id,
                                                    score = newVote(
                                                        postView.my_vote,
                                                        VoteType.Downvote,
                                                    ),
                                                    auth = acct.jwt,
                                                ),
                                            )
                                        }
                                    },
                                    onReplyClick = { pv ->
                                        val isModerator = isModerator(pv.creator, postRes.data.moderators)
                                        appState.toCommentReply(
                                            channel = transferCommentReplyDepsViaRoot,
                                            replyItem = ReplyItem.PostItem(pv),
                                            isModerator = isModerator,
                                        )
                                    },
                                    onPostClick = {},
                                    onSaveClick = { pv ->
                                        account?.also { acct ->
                                            postViewModel.savePost(
                                                SavePost(
                                                    post_id = pv.post.id,
                                                    save = !pv.saved,
                                                    auth = acct.jwt,
                                                ),
                                            )
                                        }
                                    },
                                    onCommunityClick = { community ->
                                        appState.toCommunity(id = community.id)
                                    },
                                    onEditPostClick = { pv ->
                                        appState.toPostEdit(
                                            channel = transferPostEditDepsViaRoot,
                                            postView = pv,
                                        )
                                    },
                                    onDeletePostClick = { pv ->
                                        account?.also { acct ->
                                            postViewModel.deletePost(
                                                DeletePost(
                                                    post_id = pv.post.id,
                                                    deleted = pv.post.deleted,
                                                    auth = acct.jwt,
                                                ),
                                            )
                                        }
                                    },
                                    onReportClick = { pv ->
                                        appState.toPostReport(id = pv.post.id)
                                    },
                                    onPersonClick = appState::toProfile,
                                    onBlockCommunityClick = { c ->
                                        account?.also { acct ->
                                            postViewModel.blockCommunity(
                                                BlockCommunity(
                                                    community_id = c.id,
                                                    block = true,
                                                    auth = acct.jwt,
                                                ),
                                                ctx,
                                            )
                                        }
                                    },
                                    onBlockCreatorClick = { person ->
                                        account?.also { acct ->
                                            postViewModel.blockPerson(
                                                BlockPerson(
                                                    person_id = person.id,
                                                    block = true,
                                                    auth = acct.jwt,
                                                ),
                                                ctx,
                                            )
                                        }
                                    },
                                    onShareClick = { url ->
                                        shareLink(url, ctx)
                                    },
                                    showReply = true, // Do nothing
                                    isModerator = isModerator(
                                        postView.creator,
                                        postRes.data.moderators,
                                    ),
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
                                    openImageViewer = appState::toView,
                                    openLink = appState::openLink,
                                    showPostLinkPreview = showPostLinkPreview,
                                    showIfRead = false,
                                )
                            }

                            if (postViewModel.commentsRes.isLoading()) {
                                item(contentType = "loadingbar") {
                                    LoadingBar()
                                }
                            }

                            when (val commentsRes = postViewModel.commentsRes) {
                                is ApiState.Failure -> item(key = "error") {
                                    ApiErrorToast(
                                        commentsRes.msg,
                                    )
                                }

                                is ApiState.Holder -> {
                                    val commentTree = buildCommentsTree(
                                        commentsRes.data.comments,
                                        postViewModel.isCommentView(),
                                    )

                                    val firstComment =
                                        commentTree.firstOrNull()?.commentView?.comment
                                    val depth = getDepthFromComment(firstComment)
                                    val commentParentId = getCommentParentId(firstComment)
                                    val showContextButton = depth != null && depth > 0

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
                                            ShowCommentContextButtons(
                                                postView.post.id,
                                                commentParentId = commentParentId,
                                                showContextButton = showContextButton,
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
                                            account?.also { acct ->
                                                postViewModel.likeComment(
                                                    CreateCommentLike(
                                                        comment_id = cv.comment.id,
                                                        score = newVote(
                                                            cv.my_vote,
                                                            VoteType.Upvote,
                                                        ),
                                                        auth = acct.jwt,
                                                    ),
                                                )
                                            }
                                        },
                                        onDownvoteClick = { cv ->
                                            account?.also { acct ->
                                                postViewModel.likeComment(
                                                    CreateCommentLike(
                                                        comment_id = cv.comment.id,
                                                        score = newVote(
                                                            cv.my_vote,
                                                            VoteType.Downvote,
                                                        ),
                                                        auth = acct.jwt,
                                                    ),
                                                )
                                            }
                                        },
                                        onReplyClick = { cv ->
                                            val isModerator = isModerator(cv.creator, postRes.data.moderators)
                                            appState.toCommentReply(
                                                channel = transferCommentReplyDepsViaRoot,
                                                replyItem = ReplyItem.CommentItem(cv),
                                                isModerator = isModerator,
                                            )
                                        },
                                        onSaveClick = { cv ->
                                            account?.also { acct ->
                                                postViewModel.saveComment(
                                                    SaveComment(
                                                        comment_id = cv.comment.id,
                                                        save = !cv.saved,
                                                        auth = acct.jwt,
                                                    ),
                                                )
                                            }
                                        },
                                        onPersonClick = appState::toProfile,
                                        onHeaderClick = { commentView -> toggleExpanded(commentView.comment.id) },
                                        onHeaderLongClick = { commentView -> toggleActionBar(commentView.comment.id) },
                                        onEditCommentClick = { cv ->
                                            appState.toCommentEdit(
                                                channel = transferCommentEditDepsViaRoot,
                                                commentView = cv,
                                            )
                                        },
                                        onDeleteCommentClick = { cv ->
                                            account?.also { acct ->
                                                postViewModel.deleteComment(
                                                    DeleteComment(
                                                        comment_id = cv.comment.id,
                                                        deleted = !cv.comment.deleted,
                                                        auth = acct.jwt,
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
                                                account = account,

                                            )
                                        },
                                        onBlockCreatorClick = { person ->
                                            account?.also { acct ->
                                                postViewModel.blockPerson(
                                                    BlockPerson(
                                                        person_id = person.id,
                                                        block = true,
                                                        auth = acct.jwt,
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
                                        moderators = postRes.data.moderators,
                                        enableDownVotes = siteViewModel.enableDownvotes(),
                                        showAvatar = siteViewModel.showAvatar(),
                                        isCollapsedByParent = false,
                                        showCollapsedCommentContent = showCollapsedCommentContent,
                                        showActionBar = { commentId ->
                                            showActionBarByDefault xor commentsWithToggledActionBar.contains(
                                                commentId,
                                            )
                                        },
                                        blurNSFW = blurNSFW,
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
