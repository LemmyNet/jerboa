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
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jerboa.*
import com.jerboa.api.ApiState
import com.jerboa.datatypes.types.BlockPerson
import com.jerboa.datatypes.types.CommentReplyView
import com.jerboa.datatypes.types.CommentSortType
import com.jerboa.datatypes.types.CreateCommentLike
import com.jerboa.datatypes.types.GetPersonMentions
import com.jerboa.datatypes.types.GetPrivateMessages
import com.jerboa.datatypes.types.GetReplies
import com.jerboa.datatypes.types.GetUnreadCount
import com.jerboa.datatypes.types.MarkAllAsRead
import com.jerboa.datatypes.types.MarkCommentReplyAsRead
import com.jerboa.datatypes.types.MarkPersonMentionAsRead
import com.jerboa.datatypes.types.MarkPrivateMessageAsRead
import com.jerboa.datatypes.types.SaveComment
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.comment.mentionnode.CommentMentionNode
import com.jerboa.ui.components.comment.reply.ReplyItem
import com.jerboa.ui.components.comment.replynode.CommentReplyNode
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.CommentReplyDeps
import com.jerboa.ui.components.common.InitializeRoute
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.PrivateMessageDeps
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.rootChannel
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.common.toComment
import com.jerboa.ui.components.common.toCommentReply
import com.jerboa.ui.components.common.toCommentReport
import com.jerboa.ui.components.common.toCommunity
import com.jerboa.ui.components.common.toPost
import com.jerboa.ui.components.common.toPrivateMessageReply
import com.jerboa.ui.components.common.toProfile
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.privatemessage.PrivateMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxActivity(
    navController: NavController,
    siteViewModel: SiteViewModel,
    accountViewModel: AccountViewModel,
) {
    Log.d("jerboa", "got to inbox activity")

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)
    val unreadCount = siteViewModel.getUnreadCountTotal()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val inboxViewModel: InboxViewModel = viewModel()
    InitializeRoute(inboxViewModel) {
        if (account != null) {
            inboxViewModel.resetPage()
            inboxViewModel.getReplies(
                GetReplies(
                    auth = account.jwt,
                ),
            )
            inboxViewModel.getMentions(
                GetPersonMentions(
                    auth = account.jwt,
                ),
            )
            inboxViewModel.getMessages(
                GetPrivateMessages(
                    auth = account.jwt,
                ),
            )
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            InboxHeader(
                scrollBehavior = scrollBehavior,
                unreadCount = unreadCount,
                navController = navController,
                selectedUnreadOrAll = unreadOrAllFromBool(inboxViewModel.unreadOnly),
                onClickUnreadOrAll = { unreadOrAll ->
                    account?.also { acct ->
                        inboxViewModel.resetPage()
                        inboxViewModel.updateUnreadOnly(unreadOrAll == UnreadOrAll.Unread)
                        inboxViewModel.getReplies(
                            GetReplies(
                                unread_only = inboxViewModel.unreadOnly,
                                sort = CommentSortType.New,
                                page = inboxViewModel.page,
                                auth = acct.jwt,
                            ),
                        )
                        inboxViewModel.getMentions(
                            GetPersonMentions(
                                unread_only = inboxViewModel.unreadOnly,
                                sort = CommentSortType.New,
                                page = inboxViewModel.page,
                                auth = acct.jwt,
                            ),
                        )
                        inboxViewModel.getMessages(
                            GetPrivateMessages(
                                unread_only = inboxViewModel.unreadOnly,
                                page = inboxViewModel.page,
                                auth = acct.jwt,
                            ),
                        )
                    }
                },
                onClickMarkAllAsRead = {
                    account?.also { acct ->
                        inboxViewModel.markAllAsRead(
                            MarkAllAsRead(
                                auth = acct.jwt,
                            ),
                        )
                        // TODO test this
                        // Update site counts
                        siteViewModel.fetchUnreadCounts(
                            GetUnreadCount(
                                auth = acct.jwt,
                            ),
                        )
                    }
                },
            )
        },
        content = {
            InboxTabs(
                padding = it,
                navController = navController,
                inboxViewModel = inboxViewModel,
                siteViewModel = siteViewModel,
                ctx = ctx,
                account = account,
                scope = scope,
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
    siteViewModel: SiteViewModel,
    ctx: Context,
    account: Account?,
    scope: CoroutineScope,
    padding: PaddingValues,
) {
    val transferPrivateMessageDepsViaRoot = navController.rootChannel<PrivateMessageDeps>()
    val transferCommentReplyDepsViaRoot = navController.rootChannel<CommentReplyDeps>()

    val tabTitles = InboxTab.values().map { getLocalizedStringForInboxTab(ctx, it) }
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
        HorizontalPager(
            pageCount = tabTitles.size,
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxSize(),
        ) { tabIndex ->
            when (tabIndex) {
                InboxTab.Replies.ordinal -> {
                    val listState = rememberLazyListState()

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
                                inboxViewModel.nextPage()
                                inboxViewModel.appendReplies(
                                    GetReplies(
                                        unread_only = inboxViewModel.unreadOnly,
                                        sort = CommentSortType.New,
                                        page = inboxViewModel.page,
                                        auth = acct.jwt,
                                    ),
                                )
                            }
                        }
                    }

                    val loading = when (inboxViewModel.repliesRes) {
                        ApiState.Loading -> true
                        else -> false
                    }

                    val refreshState = rememberPullRefreshState(
                        refreshing = loading,
                        onRefresh = {
                            account?.also { acct ->
                                inboxViewModel.resetPage()
                                inboxViewModel.getReplies(
                                    GetReplies(
                                        unread_only = inboxViewModel.unreadOnly,
                                        sort = CommentSortType.New,
                                        page = inboxViewModel.page,
                                        auth = acct.jwt,
                                    ),
                                )
                            }
                        },
                    )

                    val goToComment = { crv: CommentReplyView ->
                        // Go to the parent comment or post instead for context
                        val parent = getCommentParentId(crv.comment)
                        if (parent != null) {
                            navController.toComment(id = parent)
                        } else {
                            navController.toPost(id = crv.post.id)
                        }
                    }

                    val markAsRead = { crv: CommentReplyView ->
                        account?.also { acct ->
                            inboxViewModel.markReplyAsRead(
                                MarkCommentReplyAsRead(
                                    comment_reply_id = crv.comment_reply.id,
                                    read = !crv.comment_reply.read,
                                    auth = acct.jwt,
                                ),
                            )
                            siteViewModel.fetchUnreadCounts(
                                GetUnreadCount(
                                    auth = acct.jwt,
                                ),
                            )
                        }
                    }

                    Box(modifier = Modifier.pullRefresh(refreshState)) {
                        PullRefreshIndicator(loading, refreshState, Modifier.align(Alignment.TopCenter))
                        when (val repliesRes = inboxViewModel.repliesRes) {
                            ApiState.Empty -> ApiEmptyText()
                            is ApiState.Failure -> ApiErrorText(repliesRes.msg)
                            ApiState.Loading -> LoadingBar()
                            is ApiState.Success -> {
                                val replies = repliesRes.data.replies
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .simpleVerticalScrollbar(listState),
                                ) {
                                    items(
                                        replies,
                                        key = { reply -> reply.comment_reply.id },
                                    ) { commentReplyView ->
                                        CommentReplyNode(
                                            commentReplyView = commentReplyView,
                                            onUpvoteClick = { cr ->
                                                account?.also { acct ->
                                                    inboxViewModel.likeReply(
                                                        CreateCommentLike(
                                                            comment_id = cr.comment.id,
                                                            score = newVote(cr.my_vote, VoteType.Upvote),
                                                            auth = acct.jwt,
                                                        ),
                                                    )
                                                }
                                            },
                                            onDownvoteClick = { cr ->
                                                account?.also { acct ->
                                                    inboxViewModel.likeReply(
                                                        CreateCommentLike(
                                                            comment_id = cr.comment.id,
                                                            score = newVote(cr.my_vote, VoteType.Downvote),
                                                            auth = acct.jwt,
                                                        ),
                                                    )
                                                }
                                            },
                                            onReplyClick = { cr ->
                                                navController.toCommentReply(
                                                    channel = transferCommentReplyDepsViaRoot,
                                                    replyItem = ReplyItem.CommentReplyItem(cr),
                                                    isModerator = false,
                                                )
                                            },
                                            onSaveClick = { cr ->
                                                account?.also { acct ->
                                                    inboxViewModel.saveReply(
                                                        SaveComment(
                                                            comment_id = cr.comment.id,
                                                            save = !cr.saved,
                                                            auth = acct.jwt,
                                                        ),
                                                    )
                                                }
                                            },
                                            onMarkAsReadClick = { crv -> markAsRead(crv) },
                                            onReportClick = { cv ->
                                                navController.toComment(id = cv.comment.id)
                                            },
                                            onCommentLinkClick = goToComment,
                                            onPersonClick = { personId ->
                                                navController.toProfile(id = personId)
                                            },
                                            onCommentClick = { crv ->
                                                goToComment(crv)
                                                markAsRead(crv)
                                            },
                                            onCommunityClick = { community ->
                                                navController.toCommunity(id = community.id)
                                            },
                                            onBlockCreatorClick = { person ->
                                                account?.also { acct ->
                                                    inboxViewModel.blockPerson(
                                                        BlockPerson(
                                                            person_id = person.id,
                                                            block = true,
                                                            auth = acct.jwt,
                                                        ),
                                                        ctx,
                                                    )
                                                }
                                            },
                                            onPostClick = { postId ->
                                                navController.toPost(id = postId)
                                            },
                                            account = account,
                                            showAvatar = siteViewModel.showAvatar(),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                InboxTab.Mentions.ordinal -> {
                    val listState = rememberLazyListState()

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
                                inboxViewModel.nextPage()
                                inboxViewModel.appendMentions(
                                    GetPersonMentions(
                                        unread_only = inboxViewModel.unreadOnly,
                                        sort = CommentSortType.New,
                                        page = inboxViewModel.page,
                                        auth = acct.jwt,
                                    ),
                                )
                            }
                        }
                    }

                    val loading = when (inboxViewModel.mentionsRes) {
                        ApiState.Loading -> true
                        else -> false
                    }

                    val refreshState = rememberPullRefreshState(
                        refreshing = loading,
                        onRefresh = {
                            account?.also { acct ->
                                inboxViewModel.resetPage()
                                inboxViewModel.getMentions(
                                    GetPersonMentions(
                                        unread_only = inboxViewModel.unreadOnly,
                                        sort = CommentSortType.New,
                                        page = inboxViewModel.page,
                                        auth = acct.jwt,
                                    ),
                                )
                            }
                        },
                    )
                    Box(modifier = Modifier.pullRefresh(refreshState).fillMaxSize()) {
                        PullRefreshIndicator(loading, refreshState, Modifier.align(Alignment.TopCenter))
                        when (val mentionsRes = inboxViewModel.mentionsRes) {
                            ApiState.Empty -> ApiEmptyText()
                            is ApiState.Failure -> ApiErrorText(mentionsRes.msg)
                            ApiState.Loading -> LoadingBar()
                            is ApiState.Success -> {
                                val mentions = mentionsRes.data.mentions
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .simpleVerticalScrollbar(listState),
                                ) {
                                    items(
                                        mentions,
                                        key = { mention -> mention.person_mention.id },
                                    ) { pmv ->
                                        CommentMentionNode(
                                            personMentionView = pmv,
                                            onUpvoteClick = { pm ->
                                                account?.also { acct ->
                                                    inboxViewModel.likeMention(
                                                        CreateCommentLike(
                                                            comment_id = pm.comment.id,
                                                            score = newVote(pm.my_vote, VoteType.Upvote),
                                                            auth = acct.jwt,
                                                        ),
                                                    )
                                                }
                                            },
                                            onDownvoteClick = { pm ->
                                                account?.also { acct ->
                                                    inboxViewModel.likeMention(
                                                        CreateCommentLike(
                                                            comment_id = pm.comment.id,
                                                            score = newVote(pm.my_vote, VoteType.Downvote),
                                                            auth = acct.jwt,
                                                        ),
                                                    )
                                                }
                                            },
                                            onReplyClick = { pm ->
                                                navController.toCommentReply(
                                                    channel = transferCommentReplyDepsViaRoot,
                                                    replyItem = ReplyItem.MentionReplyItem(pm),
                                                    isModerator = false,
                                                )
                                            },
                                            onSaveClick = { pm ->
                                                account?.also { acct ->
                                                    inboxViewModel.saveMention(
                                                        SaveComment(
                                                            comment_id = pm.comment.id,
                                                            save = !pm.saved,
                                                            auth = acct.jwt,
                                                        ),
                                                    )
                                                }
                                            },
                                            onMarkAsReadClick = { pm ->
                                                account?.also { acct ->
                                                    inboxViewModel.markPersonMentionAsRead(
                                                        MarkPersonMentionAsRead(
                                                            person_mention_id = pm.person_mention.id,
                                                            read = !pm.person_mention.read,
                                                            auth = acct.jwt,
                                                        ),
                                                    )
                                                    siteViewModel.fetchUnreadCounts(
                                                        GetUnreadCount(
                                                            auth = acct.jwt,
                                                        ),
                                                    )
                                                }
                                            },
                                            onReportClick = { pm ->
                                                navController.toCommentReport(id = pm.comment.id)
                                            },
                                            onLinkClick = { pm ->
                                                // Go to the parent comment or post instead for context
                                                val parent = getCommentParentId(pm.comment)
                                                if (parent != null) {
                                                    navController.toComment(id = parent)
                                                } else {
                                                    navController.toPost(id = pm.post.id)
                                                }
                                            },
                                            onPersonClick = { personId ->
                                                navController.toProfile(id = personId)
                                            },
                                            onCommunityClick = { community ->
                                                navController.toCommunity(id = community.id)
                                            },
                                            onBlockCreatorClick = { person ->
                                                account?.also { acct ->
                                                    inboxViewModel.blockPerson(
                                                        BlockPerson(
                                                            person_id = person.id,
                                                            block = true,
                                                            auth = acct.jwt,
                                                        ),
                                                        ctx,
                                                    )
                                                }
                                            },
                                            onPostClick = { postId ->
                                                navController.toPost(id = postId)
                                            },
                                            account = account,
                                            showAvatar = siteViewModel.showAvatar(),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                InboxTab.Messages.ordinal -> {
                    val listState = rememberLazyListState()

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
                                inboxViewModel.nextPage()
                                inboxViewModel.appendMessages(
                                    GetPrivateMessages(
                                        unread_only = inboxViewModel.unreadOnly,
                                        page = inboxViewModel.page,
                                        auth = acct.jwt,
                                    ),
                                )
                            }
                        }
                    }

                    val loading = when (inboxViewModel.messagesRes) {
                        ApiState.Loading -> true
                        else -> false
                    }

                    val refreshState = rememberPullRefreshState(
                        refreshing = loading,
                        onRefresh = {
                            account?.also { acct ->
                                inboxViewModel.resetPage()
                                inboxViewModel.getMessages(
                                    GetPrivateMessages(
                                        unread_only = inboxViewModel.unreadOnly,
                                        page = inboxViewModel.page,
                                        auth = acct.jwt,
                                    ),
                                )
                            }
                        },
                    )
                    Box(modifier = Modifier.pullRefresh(refreshState).fillMaxSize()) {
                        PullRefreshIndicator(loading, refreshState, Modifier.align(Alignment.TopCenter))
                        when (val messagesRes = inboxViewModel.messagesRes) {
                            ApiState.Empty -> ApiEmptyText()
                            is ApiState.Failure -> ApiErrorText(messagesRes.msg)
                            ApiState.Loading -> LoadingBar()
                            is ApiState.Success -> {
                                val messages = messagesRes.data.private_messages
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .simpleVerticalScrollbar(listState),
                                ) {
                                    items(
                                        messages,
                                        key = { message -> message.private_message.id },
                                    ) { message ->
                                        account?.also { acct ->
                                            PrivateMessage(
                                                myPersonId = acct.id,
                                                privateMessageView = message,
                                                onReplyClick = { privateMessageView ->
                                                    navController.toPrivateMessageReply(
                                                        channel = transferPrivateMessageDepsViaRoot,
                                                        privateMessageView = privateMessageView,
                                                    )
                                                },
                                                onMarkAsReadClick = { pm ->
                                                    inboxViewModel.markPrivateMessageAsRead(
                                                        MarkPrivateMessageAsRead(
                                                            private_message_id = pm.private_message.id,
                                                            read = !pm.private_message.read,
                                                            auth = acct.jwt,
                                                        ),
                                                    )
                                                    siteViewModel.fetchUnreadCounts(
                                                        GetUnreadCount(
                                                            auth = acct.jwt,
                                                        ),
                                                    )
                                                },
                                                onPersonClick = { personId ->
                                                    navController.toProfile(id = personId)
                                                },
                                                account = acct,
                                                showAvatar = siteViewModel.showAvatar(),
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
    }
}
