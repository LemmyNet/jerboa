package com.jerboa.ui.components.inbox

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.jerboa.*
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.ui.components.comment.mentionnode.CommentMentionNode
import com.jerboa.ui.components.comment.reply.CommentReplyViewModel
import com.jerboa.ui.components.comment.reply.ReplyItem
import com.jerboa.ui.components.comment.replynode.CommentReplyNode
import com.jerboa.ui.components.common.BottomAppBarAll
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.privatemessage.PrivateMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxActivity(
    navController: NavController,
    appSettingsViewModel: AppSettingsViewModel,
    inboxViewModel: InboxViewModel,
    homeViewModel: HomeViewModel,
    accountViewModel: AccountViewModel,
    commentReplyViewModel: CommentReplyViewModel,
    siteViewModel: SiteViewModel,
) {
    Log.d("jerboa", "got to inbox activity")

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)
    val unreadCount = homeViewModel.unreadCountResponse?.let { unreadCountTotal(it) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            InboxHeader(
                scrollBehavior = scrollBehavior,
                unreadCount = unreadCount,
                navController = navController,
                selectedUnreadOrAll = unreadOrAllFromBool(inboxViewModel.unreadOnly.value),
                onClickUnreadOrAll = { unreadOrAll ->
                    account?.also { acct ->
                        inboxViewModel.fetchReplies(
                            account = acct,
                            clear = true,
                            changeUnreadOnly = unreadOrAll == UnreadOrAll.Unread,
                            ctx = ctx,
                        )
                        inboxViewModel.fetchPersonMentions(
                            account = acct,
                            clear = true,
                            changeUnreadOnly = unreadOrAll == UnreadOrAll.Unread,
                            ctx = ctx,
                        )
                        inboxViewModel.fetchPrivateMessages(
                            account = acct,
                            clear = true,
                            changeUnreadOnly = unreadOrAll == UnreadOrAll.Unread,
                            ctx = ctx,
                        )
                    }
                },
                onClickMarkAllAsRead = {
                    account?.also { acct ->
                        inboxViewModel.markAllAsRead(
                            account = acct,
                            ctx = ctx,
                        )
                        homeViewModel.markAllAsRead()
                    }
                },
            )
        },
        content = {
            InboxTabs(
                padding = it,
                navController = navController,
                commentReplyViewModel = commentReplyViewModel,
                inboxViewModel = inboxViewModel,
                homeViewModel = homeViewModel,
                ctx = ctx,
                account = account,
                scope = scope,
                siteViewModel = siteViewModel,
            )
        },
        bottomBar = {
            BottomAppBarAll(
                showBottomNav = appSettingsViewModel.appSettings.value?.showBottomNav,
                screen = "inbox",
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

enum class InboxTab {
    Replies,
    Mentions,
    Messages,
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun InboxTabs(
    navController: NavController,
    inboxViewModel: InboxViewModel,
    homeViewModel: HomeViewModel,
    ctx: Context,
    account: Account?,
    scope: CoroutineScope,
    commentReplyViewModel: CommentReplyViewModel,
    padding: PaddingValues,
    siteViewModel: SiteViewModel,
) {
    val tabTitles = InboxTab.values().map { it.toString() }
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
            tabs = {
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
            },
        )
        if (inboxViewModel.loading.value) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        HorizontalPager(
            pageCount = tabTitles.size,
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxSize(),
        ) { tabIndex ->
            when (tabIndex) {
                InboxTab.Replies.ordinal -> {
                    val listState = rememberLazyListState()
                    val loading = inboxViewModel.loading.value &&
                        inboxViewModel.page.value == 1 &&
                        inboxViewModel.replies.isNotEmpty()

                    // observer when reached end of list
                    val endOfListReached by remember {
                        derivedStateOf {
                            listState.isScrolledToEnd()
                        }
                    }

                    // act when end of list reached
                    if (endOfListReached) {
                        LaunchedEffect(Unit) {
                            account?.also { acct ->
                                if (inboxViewModel.replies.size > 0) {
                                    inboxViewModel.fetchReplies(
                                        account = acct,
                                        nextPage = true,
                                        ctx = ctx,
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.pullRefresh(
                            state = rememberPullRefreshState(
                                refreshing = loading,
                                onRefresh = {
                                    account?.also { acct ->
                                        inboxViewModel.fetchReplies(
                                            account = acct,
                                            clear = true,
                                            ctx = ctx,
                                        )
                                    }
                                },
                            ),
                        ),
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .simpleVerticalScrollbar(listState),
                        ) {
                            items(
                                inboxViewModel.replies,
                                key = { reply -> reply.comment_reply.id },
                            ) { crv ->
                                CommentReplyNode(
                                    commentReplyView = crv,
                                    onUpvoteClick = { commentReplyView ->
                                        account?.also { acct ->
                                            inboxViewModel.likeCommentReply(
                                                commentReplyView = commentReplyView,
                                                voteType = VoteType.Upvote,
                                                account = acct,
                                                ctx = ctx,
                                            )
                                        }
                                    },
                                    onDownvoteClick = { commentView ->
                                        account?.also { acct ->
                                            inboxViewModel.likeCommentReply(
                                                commentReplyView = commentView,
                                                voteType = VoteType.Downvote,
                                                account = acct,
                                                ctx = ctx,
                                            )
                                        }
                                    },
                                    onReplyClick = { commentReplyView ->
                                        commentReplyViewModel.initialize(
                                            ReplyItem
                                                .CommentReplyItem(commentReplyView),
                                        )
                                        navController.navigate("commentReply")
                                    },
                                    onSaveClick = { commentReplyView ->
                                        account?.also { acct ->
                                            inboxViewModel.saveCommentReply(
                                                commentReplyView = commentReplyView,
                                                account = acct,
                                                ctx = ctx,
                                            )
                                        }
                                    },
                                    onMarkAsReadClick = { commentReplyView ->
                                        account?.also { acct ->
                                            inboxViewModel.markReplyAsRead(
                                                commentReplyView = commentReplyView,
                                                account = acct,
                                                ctx = ctx,
                                            )
                                            homeViewModel.updateUnreads(commentReplyView)
                                        }
                                    },
                                    onReportClick = { commentView ->
                                        navController.navigate("commentReport/${commentView.comment.id}")
                                    },
                                    onCommentLinkClick = { commentView ->
                                        // Go to the parent comment or post instead for context
                                        val parent = getCommentParentId(commentView.comment)
                                        val route = if (parent != null) {
                                            "comment/$parent"
                                        } else {
                                            "post/${commentView.post.id}"
                                        }
                                        navController.navigate(route)
                                    },
                                    onPersonClick = { personId ->
                                        navController.navigate(route = "profile/$personId")
                                    },
                                    onCommunityClick = { community ->
                                        navController.navigate(route = "community/${community.id}")
                                    },
                                    onBlockCreatorClick = {
                                        account?.also { acct ->
                                            inboxViewModel.blockCreator(
                                                creator = it,
                                                account = acct,
                                                ctx = ctx,
                                            )
                                        }
                                    },
                                    onPostClick = { postId ->
                                        navController.navigate(route = "post/$postId")
                                    },
                                    account = account,
                                    showAvatar = siteViewModel.siteRes?.my_user?.local_user_view?.local_user?.show_avatars ?: true,
                                )
                            }
                        }
                    }
                }
                InboxTab.Mentions.ordinal -> {
                    val listState = rememberLazyListState()
                    val loading = inboxViewModel.loading.value &&
                        inboxViewModel.page.value == 1 &&
                        inboxViewModel.replies.isNotEmpty()

                    // observer when reached end of list
                    val endOfListReached by remember {
                        derivedStateOf {
                            listState.isScrolledToEnd()
                        }
                    }

                    // act when end of list reached
                    if (endOfListReached) {
                        LaunchedEffect(Unit) {
                            account?.also { acct ->
                                if (inboxViewModel.mentions.size > 0) {
                                    inboxViewModel.fetchPersonMentions(
                                        account = acct,
                                        nextPage = true,
                                        ctx = ctx,
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.pullRefresh(
                            state = rememberPullRefreshState(
                                loading,
                                onRefresh = {
                                    account?.also { acct ->
                                        inboxViewModel.fetchPersonMentions(
                                            account = acct,
                                            clear = true,
                                            ctx = ctx,
                                        )
                                    }
                                },
                            ),
                        ),
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .simpleVerticalScrollbar(listState),
                        ) {
                            items(
                                inboxViewModel.mentions,
                                key = { mention -> mention.person_mention.id },
                            ) { pmv ->
                                CommentMentionNode(
                                    personMentionView = pmv,
                                    onUpvoteClick = { personMentionView ->
                                        account?.also { acct ->
                                            inboxViewModel.likeMention(
                                                personMentionView = personMentionView,
                                                voteType = VoteType.Upvote,
                                                account = acct,
                                                ctx = ctx,
                                            )
                                        }
                                    },
                                    onDownvoteClick = { personMentionView ->
                                        account?.also { acct ->
                                            inboxViewModel.likeMention(
                                                personMentionView = personMentionView,
                                                voteType = VoteType.Downvote,
                                                account = acct,
                                                ctx = ctx,
                                            )
                                        }
                                    },
                                    onReplyClick = { personMentionView ->
                                        commentReplyViewModel.initialize(
                                            ReplyItem
                                                .MentionReplyItem(personMentionView),
                                        )
                                        navController.navigate("commentReply")
                                    },
                                    onSaveClick = { personMentionView ->
                                        account?.also { acct ->
                                            inboxViewModel.saveMention(
                                                personMentionView = personMentionView,
                                                account = acct,
                                                ctx = ctx,
                                            )
                                        }
                                    },
                                    onMarkAsReadClick = { personMentionView ->
                                        account?.also { acct ->
                                            inboxViewModel.markPersonMentionAsRead(
                                                personMentionView = personMentionView,
                                                account = acct,
                                                ctx = ctx,
                                            )
                                            homeViewModel.updateUnreads(personMentionView)
                                        }
                                    },
                                    onReportClick = { personMentionView ->
                                        navController.navigate(
                                            "commentReport/${personMentionView
                                                .comment
                                                .id}",
                                        )
                                    },
                                    onLinkClick = { personMentionView ->
                                        // Go to the parent comment or post instead for context
                                        val parent = getCommentParentId(personMentionView.comment)
                                        val route = if (parent != null) {
                                            "comment/$parent"
                                        } else {
                                            "post/${personMentionView.post.id}"
                                        }
                                        navController.navigate(route)
                                    },
                                    onPersonClick = { personId ->
                                        navController.navigate(route = "profile/$personId")
                                    },
                                    onCommunityClick = { community ->
                                        navController.navigate(route = "community/${community.id}")
                                    },
                                    onBlockCreatorClick = {
                                        account?.also { acct ->
                                            inboxViewModel.blockCreator(
                                                creator = it,
                                                account = acct,
                                                ctx = ctx,
                                            )
                                        }
                                    },
                                    onPostClick = { postId ->
                                        navController.navigate(route = "post/$postId")
                                    },
                                    account = account,
                                    showAvatar = siteViewModel.siteRes?.my_user?.local_user_view?.local_user?.show_avatars ?: true,
                                )
                            }
                        }
                    }
                }
                InboxTab.Messages.ordinal -> {
                    val listState = rememberLazyListState()
                    val loading = inboxViewModel.loading.value &&
                        inboxViewModel.page.value == 1 &&
                        inboxViewModel.messages.isNotEmpty()

                    // observer when reached end of list
                    val endOfListReached by remember {
                        derivedStateOf {
                            listState.isScrolledToEnd()
                        }
                    }

                    // act when end of list reached
                    if (endOfListReached) {
                        LaunchedEffect(Unit) {
                            account?.also { acct ->
                                if (inboxViewModel.messages.size > 0) {
                                    inboxViewModel.fetchPrivateMessages(
                                        account = acct,
                                        nextPage = true,
                                        ctx = ctx,
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.pullRefresh(
                            state = rememberPullRefreshState(
                                loading,
                                onRefresh = {
                                    account?.also { acct ->
                                        inboxViewModel.fetchPrivateMessages(
                                            account = acct,
                                            clear = true,
                                            ctx = ctx,
                                        )
                                    }
                                },
                            ),
                        ),
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .simpleVerticalScrollbar(listState),
                        ) {
                            items(
                                inboxViewModel.messages,
                                key = { message -> message.private_message.id },
                            ) { message ->
                                account?.also { acct ->
                                    PrivateMessage(
                                        myPersonId = acct.id,
                                        privateMessageView = message,
                                        onReplyClick = { privateMessageView ->
                                            inboxViewModel.replyToPrivateMessageView =
                                                privateMessageView
                                            navController.navigate("privateMessageReply")
                                        },
                                        onMarkAsReadClick = { privateMessageView ->
                                            inboxViewModel.markPrivateMessageAsRead(
                                                privateMessageView = privateMessageView,
                                                account = acct,
                                                ctx = ctx,
                                            )
                                            homeViewModel.updateUnreads(privateMessageView)
                                        },
                                        onPersonClick = { personId ->
                                            navController.navigate(route = "profile/$personId")
                                        },
                                        account = acct,
                                        showAvatar = siteViewModel.siteRes?.my_user?.local_user_view?.local_user?.show_avatars ?: true,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
