package com.jerboa.ui.components.person

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import arrow.core.Either
import com.jerboa.R
import com.jerboa.VoteType
import com.jerboa.api.ApiState
import com.jerboa.commentsToFlatNodes
import com.jerboa.datatypes.types.BlockCommunity
import com.jerboa.datatypes.types.BlockPerson
import com.jerboa.datatypes.types.CommentView
import com.jerboa.datatypes.types.CreateCommentLike
import com.jerboa.datatypes.types.CreatePostLike
import com.jerboa.datatypes.types.DeleteComment
import com.jerboa.datatypes.types.DeletePost
import com.jerboa.datatypes.types.GetPersonDetails
import com.jerboa.datatypes.types.PersonId
import com.jerboa.datatypes.types.PostView
import com.jerboa.datatypes.types.SaveComment
import com.jerboa.datatypes.types.SavePost
import com.jerboa.datatypes.types.SortType
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.getLocalizedStringForUserTab
import com.jerboa.isScrolledToEnd
import com.jerboa.newVote
import com.jerboa.pagerTabIndicatorOffset2
import com.jerboa.scrollToTop
import com.jerboa.ui.components.comment.CommentNodes
import com.jerboa.ui.components.comment.edit.CommentEditReturn
import com.jerboa.ui.components.comment.reply.CommentReplyReturn
import com.jerboa.ui.components.comment.reply.ReplyItem
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.CommentEditDeps
import com.jerboa.ui.components.common.CommentReplyDeps
import com.jerboa.ui.components.common.ConsumeReturn
import com.jerboa.ui.components.common.InitializeRoute
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.PostEditDeps
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.getPostViewMode
import com.jerboa.ui.components.common.rootChannel
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.common.toComment
import com.jerboa.ui.components.common.toCommentEdit
import com.jerboa.ui.components.common.toCommentReply
import com.jerboa.ui.components.common.toCommentReport
import com.jerboa.ui.components.common.toCommunity
import com.jerboa.ui.components.common.toPost
import com.jerboa.ui.components.common.toPostEdit
import com.jerboa.ui.components.common.toPostReport
import com.jerboa.ui.components.common.toProfile
import com.jerboa.ui.components.community.CommunityLink
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.post.PostListings
import com.jerboa.ui.components.post.edit.PostEditReturn
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.util.BrowserType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonProfileActivity(
    personArg: Either<PersonId, String>,
    savedMode: Boolean,
    navController: NavController,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    showVotingArrowsInListView: Boolean,
    browserType: BrowserType,
) {
    Log.d("jerboa", "got to person activity")

    val scope = rememberCoroutineScope()
    val postListState = rememberLazyListState()
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val personProfileViewModel: PersonProfileViewModel = viewModel()

    navController.ConsumeReturn<PostView>(PostEditReturn.POST_VIEW) { pv ->
        if (personProfileViewModel.initialized) personProfileViewModel.updatePost(pv)
    }

    navController.ConsumeReturn<CommentView>(CommentEditReturn.COMMENT_VIEW) { cv ->
        if (personProfileViewModel.initialized) personProfileViewModel.updateComment(cv)
    }

    navController.ConsumeReturn<CommentView>(CommentReplyReturn.COMMENT_VIEW) { cv ->
        if (personProfileViewModel.initialized) {
            when (val res = personProfileViewModel.personDetailsRes) {
                is ApiState.Success -> {
                    if (account?.id == res.data.person_view.person.id) {
                        personProfileViewModel.insertComment(cv)
                    }
                }
                else -> {}
            }
        }
    }

    InitializeRoute(personProfileViewModel) {
        val personId = personArg.fold({ it }, { null })
        val personName = personArg.fold({ null }, { it })

        personProfileViewModel.resetPage()
        personProfileViewModel.updateSavedOnly(savedMode)
        personProfileViewModel.getPersonDetails(
            GetPersonDetails(
                person_id = personId,
                username = personName,
                sort = SortType.New,
                auth = account?.jwt,
                saved_only = savedMode,
            ),
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            when (val profileRes = personProfileViewModel.personDetailsRes) {
                is ApiState.Failure -> ApiErrorText(profileRes.msg)
                is ApiState.Success -> {
                    val person = profileRes.data.person_view.person
                    PersonProfileHeader(
                        scrollBehavior = scrollBehavior,
                        personName = if (savedMode) {
                            ctx.getString(R.string.bookmarks_activity_saved)
                        } else {
                            person.name
                        },
                        myProfile = account?.id == person.id,
                        selectedSortType = personProfileViewModel.sortType,
                        onClickSortType = { sortType ->
                            scrollToTop(scope, postListState)
                            personProfileViewModel.resetPage()
                            personProfileViewModel.updateSortType(sortType)
                            personProfileViewModel.getPersonDetails(
                                GetPersonDetails(
                                    person_id = person.id,
                                    sort = personProfileViewModel.sortType,
                                    page = personProfileViewModel.page,
                                    saved_only = personProfileViewModel.savedOnly,
                                    auth = account?.jwt,
                                ),
                            )
                        },
                        onBlockPersonClick = {
                            account?.also { acct ->
                                personProfileViewModel.blockPerson(
                                    BlockPerson(
                                        person_id = person.id,
                                        block = true,
                                        auth = acct.jwt,
                                    ),
                                    ctx,
                                )
                            }
                        },
                        onReportPersonClick = {
                            val firstComment = profileRes.data.comments.firstOrNull()
                            val firstPost = profileRes.data.posts.firstOrNull()
                            if (firstComment !== null) {
                                navController.toCommentReport(id = firstComment.comment.id)
                            } else if (firstPost !== null) {
                                navController.toPostReport(id = firstPost.post.id)
                            }
                        },
                        navController = navController,
                    )
                }

                else -> {}
            }
        },
        content = {
            UserTabs(
                savedMode = savedMode,
                padding = it,
                navController = navController,
                personProfileViewModel = personProfileViewModel,
                ctx = ctx,
                account = account,
                scope = scope,
                postListState = postListState,
                appSettingsViewModel = appSettingsViewModel,
                showVotingArrowsInListView = showVotingArrowsInListView,
                enableDownVotes = siteViewModel.enableDownvotes(),
                showAvatar = siteViewModel.showAvatar(),
                browserType = browserType,
            )
        },
    )
}

enum class UserTab {
    About,
    Posts,
    Comments,
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun UserTabs(
    savedMode: Boolean,
    navController: NavController,
    personProfileViewModel: PersonProfileViewModel,
    ctx: Context,
    account: Account?,
    scope: CoroutineScope,
    postListState: LazyListState,
    padding: PaddingValues,
    appSettingsViewModel: AppSettingsViewModel,
    showVotingArrowsInListView: Boolean,
    enableDownVotes: Boolean,
    showAvatar: Boolean,
    browserType: BrowserType,
) {
    val transferCommentEditDepsViaRoot = navController.rootChannel<CommentEditDeps>()
    val transferCommentReplyDepsViaRoot = navController.rootChannel<CommentReplyDeps>()
    val transferPostEditDepsViaRoot = navController.rootChannel<PostEditDeps>()

    val tabTitles = if (savedMode) {
        listOf(
            getLocalizedStringForUserTab(ctx, UserTab.Posts),
            getLocalizedStringForUserTab(ctx, UserTab.Comments),
        )
    } else {
        UserTab.values().map { getLocalizedStringForUserTab(ctx, it) }
    }
    val pagerState = rememberPagerState()

    val loading = personProfileViewModel.personDetailsRes == ApiState.Loading

    val pullRefreshState = rememberPullRefreshState(
        refreshing = loading,
        onRefresh = {
            when (val profileRes = personProfileViewModel.personDetailsRes) {
                is ApiState.Success -> {
                    personProfileViewModel.resetPage()
                    personProfileViewModel.getPersonDetails(
                        GetPersonDetails(
                            person_id = profileRes.data.person_view.person.id,
                            sort = personProfileViewModel.sortType,
                            page = personProfileViewModel.page,
                            saved_only = personProfileViewModel.savedOnly,
                            auth = account?.jwt,
                        ),
                    )
                }
                else -> {}
            }
        },
    )

    Column(
        modifier = Modifier.padding(padding),
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset2(
                        pagerState,
                        tabPositions,
                    ),
                )
            },
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(text = title) },
                )
            }
        }
        HorizontalPager(
            pageCount = tabTitles.size,
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxSize(),
        ) { tabIndex ->
            // Need an offset for the saved mode, which doesn't show about
            val tabI = if (!savedMode) {
                tabIndex
            } else {
                tabIndex + 1
            }
            when (tabI) {
                UserTab.About.ordinal -> {
                    when (val profileRes = personProfileViewModel.personDetailsRes) {
                        ApiState.Empty -> ApiEmptyText()
                        is ApiState.Failure -> ApiErrorText(profileRes.msg)
                        ApiState.Loading -> LoadingBar()
                        is ApiState.Success -> {
                            val listState = rememberLazyListState()

                            LazyColumn(
                                state = listState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .simpleVerticalScrollbar(listState),
                            ) {
                                item {
                                    PersonProfileTopSection(
                                        personView = profileRes.data.person_view,
                                        showAvatar = showAvatar,
                                    )
                                }
                                val moderates = profileRes.data.moderates
                                if (moderates.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = stringResource(R.string.person_profile_activity_moderates),
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.padding(MEDIUM_PADDING),
                                        )
                                    }
                                }
                                items(
                                    moderates,
                                    key = { cmv -> cmv.community.id },
                                ) { cmv ->
                                    CommunityLink(
                                        community = cmv.community,
                                        modifier = Modifier.padding(MEDIUM_PADDING),
                                        onClick = { community ->
                                            navController.toCommunity(id = community.id)
                                        },
                                        showDefaultIcon = true,
                                    )
                                }
                            }
                        }
                    }
                }

                UserTab.Posts.ordinal -> {
                    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                        PullRefreshIndicator(
                            loading,
                            pullRefreshState,
                            Modifier.align(Alignment.TopCenter).fillMaxSize(),
                        )
                        when (val profileRes = personProfileViewModel.personDetailsRes) {
                            ApiState.Empty -> ApiEmptyText()
                            is ApiState.Failure -> ApiErrorText(profileRes.msg)
                            ApiState.Loading -> LoadingBar()
                            is ApiState.Success -> {
                                PostListings(
                                    posts = profileRes.data.posts,
                                    onUpvoteClick = { pv ->
                                        account?.also { acct ->
                                            personProfileViewModel.likePost(
                                                CreatePostLike(
                                                    post_id = pv.post.id,
                                                    score = newVote(
                                                        pv.my_vote,
                                                        VoteType.Upvote,
                                                    ),
                                                    auth = acct.jwt,
                                                ),
                                            )
                                        }
                                    },
                                    onDownvoteClick = { pv ->
                                        account?.also { acct ->
                                            personProfileViewModel.likePost(
                                                CreatePostLike(
                                                    post_id = pv.post.id,
                                                    score = newVote(
                                                        pv.my_vote,
                                                        VoteType.Downvote,
                                                    ),
                                                    auth = acct.jwt,
                                                ),
                                            )
                                        }
                                    },
                                    onPostClick = { pv ->
                                        navController.toPost(id = pv.post.id)
                                    },
                                    onSaveClick = { pv ->
                                        account?.also { acct ->
                                            personProfileViewModel.savePost(
                                                SavePost(
                                                    post_id = pv.post.id,
                                                    save = !pv.saved,
                                                    auth = acct.jwt,
                                                ),
                                            )
                                        }
                                    },
                                    onEditPostClick = { pv ->
                                        navController.toPostEdit(
                                            channel = transferPostEditDepsViaRoot,
                                            postView = pv,
                                        )
                                    },
                                    onDeletePostClick = { pv ->
                                        account?.also { acct ->
                                            personProfileViewModel.deletePost(
                                                DeletePost(
                                                    post_id = pv.post.id,
                                                    deleted = !pv.post.deleted,
                                                    auth = acct.jwt,
                                                ),
                                            )
                                        }
                                    },
                                    onReportClick = { pv ->
                                        navController.toPostReport(id = pv.post.id)
                                    },
                                    onCommunityClick = { community ->
                                        navController.toCommunity(id = community.id)
                                    },
                                    onPersonClick = { personId ->
                                        navController.toProfile(id = personId)
                                    },
                                    onBlockCommunityClick = { community ->
                                        account?.also { acct ->
                                            personProfileViewModel.blockCommunity(
                                                BlockCommunity(
                                                    community_id = community.id,
                                                    block = true,
                                                    auth = acct.jwt,
                                                ),
                                                ctx,
                                            )
                                        }
                                    },
                                    onBlockCreatorClick = { person ->
                                        account?.also { acct ->
                                            personProfileViewModel.blockPerson(
                                                BlockPerson(
                                                    person_id = person.id,
                                                    block = true,
                                                    auth = acct.jwt,
                                                ),
                                                ctx = ctx,
                                            )
                                        }
                                    },
                                    isScrolledToEnd = {
                                        personProfileViewModel.nextPage()
                                        personProfileViewModel.appendData(
                                            GetPersonDetails(
                                                person_id = profileRes.data.person_view.person.id,
                                                sort = personProfileViewModel.sortType,
                                                page = personProfileViewModel.page,
                                                saved_only = personProfileViewModel.savedOnly,
                                                auth = account?.jwt,
                                            ),
                                        )
                                    },
                                    account = account,
                                    listState = postListState,
                                    postViewMode = getPostViewMode(appSettingsViewModel),
                                    enableDownVotes = enableDownVotes,
                                    showAvatar = showAvatar,
                                    showVotingArrowsInListView = showVotingArrowsInListView,
                                    browserType = browserType,
                                )
                            }
                        }
                    }
                }

                UserTab.Comments.ordinal -> {
                    when (val profileRes = personProfileViewModel.personDetailsRes) {
                        ApiState.Empty -> ApiEmptyText()
                        is ApiState.Failure -> ApiErrorText(profileRes.msg)
                        ApiState.Loading -> LoadingBar()
                        is ApiState.Success -> {
                            val nodes = commentsToFlatNodes(profileRes.data.comments)

                            val listState = rememberLazyListState()

                            // observer when reached end of list
                            val endOfListReached by remember {
                                derivedStateOf {
                                    listState.isScrolledToEnd()
                                }
                            }

                            // Holds the un-expanded comment ids
                            val unExpandedComments = remember { mutableStateListOf<Int>() }
                            val commentsWithToggledActionBar = remember { mutableStateListOf<Int>() }

                            val toggleExpanded = { commentId: Int ->
                                if (unExpandedComments.contains(commentId)) {
                                    unExpandedComments.remove(commentId)
                                } else {
                                    unExpandedComments.add(commentId)
                                }
                            }

                            val toggleActionBar = { commentId: Int ->
                                if (commentsWithToggledActionBar.contains(commentId)) {
                                    commentsWithToggledActionBar.remove(commentId)
                                } else {
                                    commentsWithToggledActionBar.add(commentId)
                                }
                            }

                            val showActionBarByDefault = true

                            // act when end of list reached
                            if (endOfListReached) {
                                LaunchedEffect(Unit) {
                                    personProfileViewModel.nextPage()
                                    personProfileViewModel.appendData(
                                        GetPersonDetails(
                                            person_id = profileRes.data.person_view.person.id,
                                            sort = personProfileViewModel.sortType,
                                            page = personProfileViewModel.page,
                                            saved_only = personProfileViewModel.savedOnly,
                                            auth = account?.jwt,
                                        ),
                                    )
                                }
                            }

                            Box(modifier = Modifier.pullRefresh(pullRefreshState).fillMaxSize()) {
                                PullRefreshIndicator(loading, pullRefreshState, Modifier.align(Alignment.TopCenter))
                                CommentNodes(
                                    nodes = nodes,
                                    increaseLazyListIndexTracker = {},
                                    addToParentIndexes = {},
                                    isFlat = true,
                                    isExpanded = { commentId -> !unExpandedComments.contains(commentId) },
                                    listState = listState,
                                    toggleExpanded = { commentId -> toggleExpanded(commentId) },
                                    toggleActionBar = { commentId -> toggleActionBar(commentId) },
                                    onMarkAsReadClick = {},
                                    onCommentClick = { cv ->
                                        navController.toComment(id = cv.comment.id)
                                    },
                                    onUpvoteClick = { cv ->
                                        account?.also { acct ->
                                            personProfileViewModel.likeComment(
                                                CreateCommentLike(
                                                    comment_id = cv.comment.id,
                                                    score = newVote(cv.my_vote, VoteType.Upvote),
                                                    auth = acct.jwt,
                                                ),
                                            )
                                        }
                                    },
                                    onDownvoteClick = { cv ->
                                        account?.also { acct ->
                                            personProfileViewModel.likeComment(
                                                CreateCommentLike(
                                                    comment_id = cv.comment.id,
                                                    score = newVote(cv.my_vote, VoteType.Downvote),
                                                    auth = acct.jwt,
                                                ),
                                            )
                                        }
                                    },
                                    onReplyClick = { cv ->
                                        navController.toCommentReply(
                                            channel = transferCommentReplyDepsViaRoot,
                                            replyItem = ReplyItem.CommentItem(cv),
                                            isModerator = false,
                                        )
                                    },
                                    onSaveClick = { cv ->
                                        account?.also { acct ->
                                            personProfileViewModel.saveComment(
                                                SaveComment(
                                                    comment_id = cv.comment.id,
                                                    save = !cv.saved,
                                                    auth = acct.jwt,
                                                ),
                                            )
                                        }
                                    },
                                    onPersonClick = { personId ->
                                        navController.toProfile(id = personId)
                                    },
                                    onHeaderClick = {},
                                    onHeaderLongClick = { commentView -> toggleActionBar(commentView.comment.id) },
                                    onCommunityClick = { community ->
                                        navController.toCommunity(id = community.id)
                                    },
                                    onPostClick = { postId ->
                                        navController.toPost(id = postId)
                                    },
                                    onEditCommentClick = { cv ->
                                        navController.toCommentEdit(
                                            channel = transferCommentEditDepsViaRoot,
                                            commentView = cv,
                                        )
                                    },
                                    onDeleteCommentClick = { cv ->
                                        account?.also { acct ->
                                            personProfileViewModel.deleteComment(
                                                DeleteComment(
                                                    comment_id = cv.comment.id,
                                                    deleted = !cv.comment.deleted,
                                                    auth = acct.jwt,
                                                ),
                                            )
                                        }
                                    },
                                    onReportClick = { cv ->
                                        navController.toCommentReport(id = cv.comment.id)
                                    },
                                    onCommentLinkClick = { cv ->
                                        navController.toComment(id = cv.comment.id)
                                    },
                                    onFetchChildrenClick = {},
                                    onBlockCreatorClick = { person ->
                                        account?.also { acct ->
                                            personProfileViewModel.blockPerson(
                                                BlockPerson(
                                                    person_id = person.id,
                                                    block = true,
                                                    auth = acct.jwt,
                                                ),
                                                ctx,
                                            )
                                        }
                                    },
                                    showPostAndCommunityContext = true,
                                    showCollapsedCommentContent = true,
                                    isCollapsedByParent = false,
                                    showActionBar = { commentId ->
                                        showActionBarByDefault xor commentsWithToggledActionBar.contains(commentId)
                                    },
                                    account = account,
                                    moderators = listOf(),
                                    enableDownVotes = enableDownVotes,
                                    showAvatar = showAvatar,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
