@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.person

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import arrow.core.Either
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.jerboa.VoteType
import com.jerboa.commentsToFlatNodes
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.isScrolledToEnd
import com.jerboa.loginFirstToast
import com.jerboa.openLink
import com.jerboa.pagerTabIndicatorOffset2
import com.jerboa.scrollToTop
import com.jerboa.ui.components.comment.CommentNodes
import com.jerboa.ui.components.comment.edit.CommentEditViewModel
import com.jerboa.ui.components.comment.reply.CommentReplyViewModel
import com.jerboa.ui.components.comment.reply.ReplyItem
import com.jerboa.ui.components.common.BottomAppBarAll
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.getPostViewMode
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.community.CommunityLink
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.post.PostListings
import com.jerboa.ui.components.post.edit.PostEditViewModel
import com.jerboa.ui.theme.MEDIUM_PADDING
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PersonProfileActivity(
    savedMode: Boolean,
    navController: NavController,
    personProfileViewModel: PersonProfileViewModel,
    accountViewModel: AccountViewModel,
    homeViewModel: HomeViewModel,
    commentEditViewModel: CommentEditViewModel,
    commentReplyViewModel: CommentReplyViewModel,
    postEditViewModel: PostEditViewModel,
    appSettingsViewModel: AppSettingsViewModel,
) {
    Log.d("jerboa", "got to person activity")

    val scope = rememberCoroutineScope()
    val postListState = rememberLazyListState()
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)
    val bottomAppBarScreen = if (savedMode) { "saved" } else { "profile" }
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            personProfileViewModel.res?.person_view?.person?.also { person ->
                PersonProfileHeader(
                    scrollBehavior = scrollBehavior,
                    personName = if (savedMode) {
                        "Saved"
                    } else {
                        person.name
                    },
                    myProfile = account?.id == person.id,
                    selectedSortType = personProfileViewModel.sortType.value,
                    onClickSortType = { sortType ->
                        scrollToTop(scope, postListState)
                        personProfileViewModel.fetchPersonDetails(
                            idOrName = Either.Left(
                                personProfileViewModel.res!!.person_view
                                    .person.id,
                            ),
                            account = account,
                            clear = true,
                            changeSortType = sortType,
                            changeSavedOnly = savedMode,
                            ctx = ctx,
                        )
                    },
                    onBlockPersonClick = {
                        account?.also { acct ->
                            personProfileViewModel.blockPerson(
                                person = person,
                                account = acct,
                                ctx = ctx,
                            )
                        }
                    },
                    onReportPersonClick = {
                        val firstComment = personProfileViewModel.comments.firstOrNull()
                        val firstPost = personProfileViewModel.posts.firstOrNull()
                        if (firstComment !== null) {
                            navController.navigate(
                                "commentReport/${firstComment.comment.id}",
                            )
                        } else if (firstPost !== null) {
                            navController.navigate(
                                "postReport/${firstPost.post.id}",
                            )
                        }
                    },
                    navController = navController,
                )
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
                commentEditViewModel = commentEditViewModel,
                commentReplyViewModel = commentReplyViewModel,
                postEditViewModel = postEditViewModel,
                appSettingsViewModel = appSettingsViewModel,
            )
        },
        bottomBar = {
            BottomAppBarAll(
                showBottomNav = appSettingsViewModel.appSettings.value?.showBottomNav,
                screen = bottomAppBarScreen,
                unreadCounts = homeViewModel.unreadCountResponse,
                onClickProfile = {
                    account?.id?.also {
                        navController.navigate(route = "profile/$it")
                    }
                },
                onClickInbox = {
                    account?.also {
                        navController.navigate(route = "inbox")
                    } ?: run {
                        loginFirstToast(ctx)
                    }
                },
                onClickSaved = {
                    account?.id?.also {
                        navController.navigate(route = "profile/$it?saved=${true}")
                    } ?: run {
                        loginFirstToast(ctx)
                    }
                },
                navController = navController,
            )
        },
    )
}

enum class UserTab {
    About,
    Posts,
    Comments,
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun UserTabs(
    savedMode: Boolean,
    navController: NavController,
    personProfileViewModel: PersonProfileViewModel,
    ctx: Context,
    account: Account?,
    scope: CoroutineScope,
    postListState: LazyListState,
    commentEditViewModel: CommentEditViewModel,
    commentReplyViewModel: CommentReplyViewModel,
    postEditViewModel: PostEditViewModel,
    padding: PaddingValues,
    appSettingsViewModel: AppSettingsViewModel,
) {
    val tabTitles = if (savedMode) {
        listOf(UserTab.Posts.name, UserTab.Comments.name)
    } else {
        UserTab.values().map { it.toString() }
    }
    val pagerState = rememberPagerState()

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
        if (personProfileViewModel.loading.value) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        HorizontalPager(
            count = tabTitles.size,
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
                    val listState = rememberLazyListState()

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                            .simpleVerticalScrollbar(listState),
                    ) {
                        item {
                            personProfileViewModel.res?.person_view?.also {
                                PersonProfileTopSection(
                                    personView = it,
                                )
                            }
                        }
                        personProfileViewModel.res?.moderates?.also { moderates ->
                            if (moderates.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Moderates",
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
                                        navController.navigate(route = "community/${community.id}")
                                    },
                                )
                            }
                        }
                    }
                }
                UserTab.Posts.ordinal -> {
                    PostListings(
                        posts = personProfileViewModel.posts,
                        onUpvoteClick = { postView ->
                            personProfileViewModel.likePost(
                                voteType = VoteType.Upvote,
                                postView = postView,
                                account = account,
                                ctx = ctx,
                            )
                        },
                        onDownvoteClick = { postView ->
                            personProfileViewModel.likePost(
                                voteType = VoteType.Downvote,
                                postView = postView,
                                account = account,
                                ctx = ctx,
                            )
                        },
                        onPostClick = { postView ->
                            navController.navigate(route = "post/${postView.post.id}")
                        },
                        onPostLinkClick = { url ->
                            openLink(url, ctx)
                        },
                        onSaveClick = { postView ->
                            account?.also { acct ->
                                personProfileViewModel.savePost(
                                    postView = postView,
                                    account = acct,
                                    ctx = ctx,
                                )
                            }
                        },
                        onEditPostClick = { postView ->
                            postEditViewModel.initialize(postView)
                            navController.navigate("postEdit")
                        },
                        onDeletePostClick = { postView ->
                            account?.also { acct ->
                                personProfileViewModel.deletePost(
                                    postView = postView,
                                    account = acct,
                                    ctx = ctx,
                                )
                            }
                        },
                        onReportClick = { postView ->
                            navController.navigate("postReport/${postView.post.id}")
                        },
                        onCommunityClick = { community ->
                            navController.navigate(route = "community/${community.id}")
                        },
                        onPersonClick = { personId ->
                            navController.navigate(route = "profile/$personId")
                        },
                        onBlockCommunityClick = {
                            account?.also { acct ->
                                personProfileViewModel.blockCommunity(
                                    community = it,
                                    account = acct,
                                    ctx = ctx,
                                )
                            }
                        },
                        onBlockCreatorClick = {
                            account?.also { acct ->
                                personProfileViewModel.blockPerson(
                                    person = it,
                                    account = acct,
                                    ctx = ctx,
                                )
                            }
                        },
                        onSwipeRefresh = {
                            personProfileViewModel.res?.person_view?.person?.id?.also {
                                personProfileViewModel.fetchPersonDetails(
                                    idOrName = Either.Left(it),
                                    account = account,
                                    clear = true,
                                    changeSavedOnly = savedMode,
                                    ctx = ctx,
                                )
                            }
                        },
                        loading = personProfileViewModel.loading.value &&
                            personProfileViewModel.page.value == 1 &&
                            personProfileViewModel.posts.isNotEmpty(),
                        isScrolledToEnd = {
                            if (personProfileViewModel.posts.size > 0) {
                                personProfileViewModel.res?.person_view?.person?.id?.also {
                                    personProfileViewModel.fetchPersonDetails(
                                        idOrName = Either.Left(it),
                                        account = account,
                                        nextPage = true,
                                        changeSavedOnly = savedMode,
                                        ctx = ctx,
                                    )
                                }
                            }
                        },
                        account = account,
                        listState = postListState,
                        taglines = null,
                        postViewMode = getPostViewMode(appSettingsViewModel),
                    )
                }
                UserTab.Comments.ordinal -> {
                    val nodes = commentsToFlatNodes(personProfileViewModel.comments)

                    val listState = rememberLazyListState()
                    val loading = personProfileViewModel.loading.value &&
                        personProfileViewModel.page.value == 1 &&
                        personProfileViewModel.comments.isNotEmpty()

                    // observer when reached end of list
                    val endOfListReached by remember {
                        derivedStateOf {
                            listState.isScrolledToEnd()
                        }
                    }

                    // act when end of list reached
                    if (endOfListReached) {
                        LaunchedEffect(Unit) {
                            if (personProfileViewModel.comments.size > 0) {
                                personProfileViewModel.res?.person_view?.person?.id?.also {
                                    personProfileViewModel.fetchPersonDetails(
                                        idOrName = Either.Left(it),
                                        account = account,
                                        nextPage = true,
                                        changeSavedOnly = savedMode,
                                        ctx = ctx,
                                    )
                                }
                            }
                        }
                    }

                    SwipeRefresh(
                        state = rememberSwipeRefreshState(loading),
                        onRefresh = {
                            personProfileViewModel.res?.person_view?.person?.id?.also {
                                personProfileViewModel.fetchPersonDetails(
                                    idOrName = Either.Left(it),
                                    account = account,
                                    clear = true,
                                    changeSavedOnly = savedMode,
                                    ctx = ctx,
                                )
                            }
                        },
                    ) {
                        CommentNodes(
                            nodes = nodes,
                            isFlat = true,
                            listState = listState,
                            onMarkAsReadClick = {},
                            onUpvoteClick = { commentView ->
                                account?.also { acct ->
                                    personProfileViewModel.likeComment(
                                        commentView = commentView,
                                        voteType = VoteType.Upvote,
                                        account = acct,
                                        ctx = ctx,
                                    )
                                }
                            },
                            onDownvoteClick = { commentView ->
                                account?.also { acct ->
                                    personProfileViewModel.likeComment(
                                        commentView = commentView,
                                        voteType = VoteType.Downvote,
                                        account = acct,
                                        ctx = ctx,
                                    )
                                }
                            },
                            onReplyClick = { commentView ->
                                commentReplyViewModel.initialize(
                                    ReplyItem.CommentItem
                                        (commentView),
                                )
                                navController.navigate("commentReply")
                            },
                            onSaveClick = { commentView ->
                                account?.also { acct ->
                                    personProfileViewModel.saveComment(
                                        commentView = commentView,
                                        account = acct,
                                        ctx = ctx,
                                    )
                                }
                            },
                            onPersonClick = { personId ->
                                navController.navigate(route = "profile/$personId")
                            },
                            onCommunityClick = { community ->
                                navController.navigate(route = "community/${community.id}")
                            },
                            onPostClick = { postId ->
                                navController.navigate(route = "post/$postId")
                            },
                            onEditCommentClick = { commentView ->
                                commentEditViewModel.initialize(commentView)
                                navController.navigate("commentEdit")
                            },
                            onDeleteCommentClick = { commentView ->
                                account?.also { acct ->
                                    personProfileViewModel.deleteComment(
                                        commentView = commentView,
                                        account = acct,
                                        ctx = ctx,
                                    )
                                }
                            },
                            onReportClick = { commentView ->
                                navController.navigate("commentReport/${commentView.comment.id}")
                            },
                            onCommentLinkClick = { commentView ->
                                navController.navigate("comment/${commentView.comment.id}")
                            },
                            onFetchChildrenClick = {},
                            onBlockCreatorClick = {
                                account?.also { acct ->
                                    personProfileViewModel.blockPerson(
                                        person = it,
                                        account = acct,
                                        ctx = ctx,
                                    )
                                }
                            },
                            showPostAndCommunityContext = true,
                            account = account,
                            moderators = listOf(),
                        )
                    }
                }
            }
        }
    }
}
