package com.jerboa.ui.components.inbox

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.CommentReplyDeps
import com.jerboa.JerboaAppState
import com.jerboa.PrivateMessageDeps
import com.jerboa.UnreadOrAll
import com.jerboa.VoteType
import com.jerboa.api.ApiState
import com.jerboa.datatypes.types.BlockPerson
import com.jerboa.datatypes.types.CommentReplyView
import com.jerboa.datatypes.types.CreateCommentLike
import com.jerboa.datatypes.types.GetUnreadCount
import com.jerboa.datatypes.types.MarkAllAsRead
import com.jerboa.datatypes.types.MarkCommentReplyAsRead
import com.jerboa.datatypes.types.MarkPersonMentionAsRead
import com.jerboa.datatypes.types.MarkPrivateMessageAsRead
import com.jerboa.datatypes.types.SaveComment
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.isAnon
import com.jerboa.feat.doIfReadyElseDisplayInfo
import com.jerboa.getCommentParentId
import com.jerboa.getLocalizedStringForInboxTab
import com.jerboa.isScrolledToEnd
import com.jerboa.model.AccountViewModel
import com.jerboa.model.InboxViewModel
import com.jerboa.model.ReplyItem
import com.jerboa.model.SiteViewModel
import com.jerboa.newVote
import com.jerboa.pagerTabIndicatorOffset2
import com.jerboa.rootChannel
import com.jerboa.ui.components.comment.mentionnode.CommentMentionNode
import com.jerboa.ui.components.comment.replynode.CommentReplyNodeInbox
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.isLoading
import com.jerboa.ui.components.common.isRefreshing
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.privatemessage.PrivateMessage
import com.jerboa.unreadOrAllFromBool
import com.jerboa.util.InitializeRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxActivity(
    appState: JerboaAppState,
    drawerState: DrawerState,
    siteViewModel: SiteViewModel,
    accountViewModel: AccountViewModel,
    blurNSFW: Boolean,
) {
    Log.d("jerboa", "got to inbox activity")

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val inboxViewModel: InboxViewModel = viewModel()
    InitializeRoute(inboxViewModel) {
        if (!account.isAnon()) {
            inboxViewModel.resetPages()
            inboxViewModel.getReplies(
                inboxViewModel.getFormReplies(account.jwt),
            )
            inboxViewModel.getMentions(
                inboxViewModel.getFormMentions(account.jwt),
            )
            inboxViewModel.getMessages(
                inboxViewModel.getFormMessages(account.jwt),
            )
            siteViewModel.fetchUnreadCounts(GetUnreadCount(account.jwt))
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
        topBar = {
            InboxHeader(
                scrollBehavior = scrollBehavior,
                unreadCount = siteViewModel.unreadCount,
                openDrawer = {
                    scope.launch {
                        drawerState.open()
                    }
                },
                selectedUnreadOrAll = unreadOrAllFromBool(inboxViewModel.unreadOnly),
                onClickUnreadOrAll = { unreadOrAll ->
                    account.doIfReadyElseDisplayInfo(
                        appState,
                        ctx,
                        snackbarHostState,
                        scope,
                        siteViewModel,
                        accountViewModel,
                        loginAsToast = true,
                    ) {
                        inboxViewModel.resetPages()
                        inboxViewModel.updateUnreadOnly(unreadOrAll == UnreadOrAll.Unread)
                        inboxViewModel.getReplies(
                            inboxViewModel.getFormReplies(it.jwt),
                        )
                        inboxViewModel.getMentions(
                            inboxViewModel.getFormMentions(it.jwt),
                        )
                        inboxViewModel.getMessages(
                            inboxViewModel.getFormMessages(it.jwt),
                        )
                    }
                },
                onClickMarkAllAsRead = {
                    account.doIfReadyElseDisplayInfo(
                        appState,
                        ctx,
                        snackbarHostState,
                        scope,
                        siteViewModel,
                        accountViewModel,
                    ) {
                        inboxViewModel.markAllAsRead(
                            MarkAllAsRead(
                                auth = it.jwt,
                            ),
                            onComplete = {
                                siteViewModel.fetchUnreadCounts(
                                    GetUnreadCount(
                                        auth = it.jwt,
                                    ),
                                )
                                inboxViewModel.resetPages()
                                inboxViewModel.getReplies(
                                    inboxViewModel.getFormReplies(account.jwt),
                                )
                                inboxViewModel.getMentions(
                                    inboxViewModel.getFormMentions(account.jwt),
                                )
                                inboxViewModel.getMessages(
                                    inboxViewModel.getFormMessages(account.jwt),
                                )
                            },
                        )
                    }
                },
            )
        },
        content = {
            InboxTabs(
                padding = it,
                appState = appState,
                inboxViewModel = inboxViewModel,
                siteViewModel = siteViewModel,
                ctx = ctx,
                account = account,
                scope = scope,
                blurNSFW = blurNSFW,
                snackbarHostState = snackbarHostState,
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
    appState: JerboaAppState,
    inboxViewModel: InboxViewModel,
    siteViewModel: SiteViewModel,
    ctx: Context,
    account: Account,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    padding: PaddingValues,
    blurNSFW: Boolean,
) {
    val transferPrivateMessageDepsViaRoot = appState.rootChannel<PrivateMessageDeps>()
    val transferCommentReplyDepsViaRoot = appState.rootChannel<CommentReplyDeps>()

    val tabTitles = InboxTab.entries.map { getLocalizedStringForInboxTab(ctx, it) }
    val pagerState = rememberPagerState { tabTitles.size }

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
                            account.doIfReadyElseDisplayInfo(
                                appState,
                                ctx,
                                snackbarHostState,
                                scope,
                                siteViewModel,
                            ) {
                                inboxViewModel.appendReplies(
                                    it.jwt,
                                )
                            }
                        }
                    }

                    val refreshing = inboxViewModel.repliesRes.isRefreshing()

                    val refreshState = rememberPullRefreshState(
                        refreshing = refreshing,
                        onRefresh = {
                            account.doIfReadyElseDisplayInfo(
                                appState,
                                ctx,
                                snackbarHostState,
                                scope,
                                siteViewModel,
                            ) {
                                inboxViewModel.resetPageReplies()
                                inboxViewModel.getReplies(
                                    inboxViewModel.getFormReplies(it.jwt),
                                    ApiState.Refreshing,
                                )
                                siteViewModel.fetchUnreadCounts(GetUnreadCount(account.jwt))
                            }
                        },
                    )

                    val goToComment = { crv: CommentReplyView ->
                        // Go to the parent comment or post instead for context
                        val parent = getCommentParentId(crv.comment)
                        if (parent != null) {
                            appState.toComment(id = parent)
                        } else {
                            appState.toPost(id = crv.post.id)
                        }
                    }

                    val markAsRead: (CommentReplyView) -> Unit = { crv: CommentReplyView ->
                        account.doIfReadyElseDisplayInfo(
                            appState,
                            ctx,
                            snackbarHostState,
                            scope,
                            siteViewModel,
                        ) {
                            inboxViewModel.markReplyAsRead(
                                MarkCommentReplyAsRead(
                                    comment_reply_id = crv.comment_reply.id,
                                    read = !crv.comment_reply.read,
                                    auth = it.jwt,
                                ),
                                onSuccess = {
                                    siteViewModel.updateUnreadCounts(dReplies = if (crv.comment_reply.read) 1 else -1)
                                },
                            )
                        }
                    }

                    Box(modifier = Modifier.pullRefresh(refreshState)) {
                        PullRefreshIndicator(
                            refreshing,
                            refreshState,
                            Modifier
                                .align(Alignment.TopCenter)
                                .zIndex(100F),
                        )

                        if (inboxViewModel.repliesRes.isLoading()) {
                            LoadingBar()
                        }
                        when (val repliesRes = inboxViewModel.repliesRes) {
                            ApiState.Empty -> ApiEmptyText()
                            is ApiState.Failure -> ApiErrorText(repliesRes.msg)
                            is ApiState.Holder -> {
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
                                        contentType = { "comment" },
                                    ) { commentReplyView ->
                                        CommentReplyNodeInbox(
                                            commentReplyView = commentReplyView,
                                            onUpvoteClick = { cr ->
                                                account.doIfReadyElseDisplayInfo(
                                                    appState,
                                                    ctx,
                                                    snackbarHostState,
                                                    scope,
                                                    siteViewModel,
                                                ) {
                                                    inboxViewModel.likeReply(
                                                        CreateCommentLike(
                                                            comment_id = cr.comment.id,
                                                            score = newVote(cr.my_vote, VoteType.Upvote),
                                                            auth = it.jwt,
                                                        ),
                                                    )
                                                }
                                            },
                                            onDownvoteClick = { cr ->
                                                account.doIfReadyElseDisplayInfo(
                                                    appState,
                                                    ctx,
                                                    snackbarHostState,
                                                    scope,
                                                    siteViewModel,
                                                ) {
                                                    inboxViewModel.likeReply(
                                                        CreateCommentLike(
                                                            comment_id = cr.comment.id,
                                                            score = newVote(cr.my_vote, VoteType.Downvote),
                                                            auth = it.jwt,
                                                        ),
                                                    )
                                                }
                                            },
                                            onReplyClick = { cr ->
                                                appState.toCommentReply(
                                                    channel = transferCommentReplyDepsViaRoot,
                                                    replyItem = ReplyItem.CommentReplyItem(cr),
                                                    isModerator = false,
                                                )
                                            },
                                            onSaveClick = { cr ->
                                                account.doIfReadyElseDisplayInfo(
                                                    appState,
                                                    ctx,
                                                    snackbarHostState,
                                                    scope,
                                                    siteViewModel,
                                                ) {
                                                    inboxViewModel.saveReply(
                                                        SaveComment(
                                                            comment_id = cr.comment.id,
                                                            save = !cr.saved,
                                                            auth = it.jwt,
                                                        ),
                                                    )
                                                }
                                            },
                                            onMarkAsReadClick = markAsRead,
                                            onReportClick = { cv ->
                                                appState.toComment(id = cv.comment.id)
                                            },
                                            onCommentLinkClick = goToComment,
                                            onPersonClick = { personId ->
                                                appState.toProfile(id = personId)
                                            },
                                            onCommentClick = { crv ->
                                                goToComment(crv)
                                                // Do not mark already read reply as read
                                                if (!crv.comment_reply.read) {
                                                    markAsRead(crv)
                                                }
                                            },
                                            onCommunityClick = { community ->
                                                appState.toCommunity(id = community.id)
                                            },
                                            onBlockCreatorClick = { person ->
                                                account.doIfReadyElseDisplayInfo(
                                                    appState,
                                                    ctx,
                                                    snackbarHostState,
                                                    scope,
                                                    siteViewModel,
                                                ) {
                                                    inboxViewModel.blockPerson(
                                                        BlockPerson(
                                                            person_id = person.id,
                                                            block = true,
                                                            auth = it.jwt,
                                                        ),
                                                        ctx,
                                                    )
                                                }
                                            },
                                            onPostClick = {
                                                if (!commentReplyView.comment_reply.read) {
                                                    markAsRead(commentReplyView)
                                                }
                                                goToComment(commentReplyView)
                                            },
                                            account = account,
                                            showAvatar = siteViewModel.showAvatar(),
                                            blurNSFW = blurNSFW,
                                            enableDownvotes = siteViewModel.enableDownvotes(),
                                            showScores = siteViewModel.showScores(),
                                        )
                                    }
                                }
                            }
                            else -> {}
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
                            account.doIfReadyElseDisplayInfo(
                                appState,
                                ctx,
                                snackbarHostState,
                                scope,
                                siteViewModel,
                            ) {
                                inboxViewModel.appendMentions(
                                    it.jwt,
                                )
                            }
                        }
                    }

                    val loading = inboxViewModel.mentionsRes.isLoading()

                    val refreshing = inboxViewModel.mentionsRes.isRefreshing()

                    val refreshState = rememberPullRefreshState(
                        refreshing = refreshing,
                        onRefresh = {
                            account.doIfReadyElseDisplayInfo(
                                appState,
                                ctx,
                                snackbarHostState,
                                scope,
                                siteViewModel,
                            ) {
                                inboxViewModel.resetPageMentions()
                                inboxViewModel.getMentions(
                                    inboxViewModel.getFormMentions(it.jwt),
                                    ApiState.Refreshing,
                                )
                                siteViewModel.fetchUnreadCounts(GetUnreadCount(it.jwt))
                            }
                        },
                    )
                    Box(
                        modifier = Modifier
                            .pullRefresh(refreshState)
                            .fillMaxSize(),
                    ) {
                        PullRefreshIndicator(
                            refreshing,
                            refreshState,
                            Modifier
                                .align(Alignment.TopCenter)
                                .zIndex(100F),
                        )
                        if (loading) {
                            LoadingBar()
                        }

                        when (val mentionsRes = inboxViewModel.mentionsRes) {
                            ApiState.Empty -> ApiEmptyText()
                            is ApiState.Failure -> ApiErrorText(mentionsRes.msg)
                            is ApiState.Holder -> {
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
                                        contentType = { "mentions" },
                                    ) { pmv ->
                                        CommentMentionNode(
                                            personMentionView = pmv,
                                            onUpvoteClick = { pm ->
                                                account.doIfReadyElseDisplayInfo(
                                                    appState,
                                                    ctx,
                                                    snackbarHostState,
                                                    scope,
                                                    siteViewModel,
                                                ) {
                                                    inboxViewModel.likeMention(
                                                        CreateCommentLike(
                                                            comment_id = pm.comment.id,
                                                            score = newVote(pm.my_vote, VoteType.Upvote),
                                                            auth = it.jwt,
                                                        ),
                                                    )
                                                }
                                            },
                                            onDownvoteClick = { pm ->
                                                account.doIfReadyElseDisplayInfo(
                                                    appState,
                                                    ctx,
                                                    snackbarHostState,
                                                    scope,
                                                    siteViewModel,
                                                ) {
                                                    inboxViewModel.likeMention(
                                                        CreateCommentLike(
                                                            comment_id = pm.comment.id,
                                                            score = newVote(pm.my_vote, VoteType.Downvote),
                                                            auth = it.jwt,
                                                        ),
                                                    )
                                                }
                                            },
                                            onReplyClick = { pm ->
                                                appState.toCommentReply(
                                                    channel = transferCommentReplyDepsViaRoot,
                                                    replyItem = ReplyItem.MentionReplyItem(pm),
                                                    isModerator = false,
                                                )
                                            },
                                            onSaveClick = { pm ->
                                                account.doIfReadyElseDisplayInfo(
                                                    appState,
                                                    ctx,
                                                    snackbarHostState,
                                                    scope,
                                                    siteViewModel,
                                                ) {
                                                    inboxViewModel.saveMention(
                                                        SaveComment(
                                                            comment_id = pm.comment.id,
                                                            save = !pm.saved,
                                                            auth = it.jwt,
                                                        ),
                                                    )
                                                }
                                            },
                                            onMarkAsReadClick = { pm ->
                                                account.doIfReadyElseDisplayInfo(
                                                    appState,
                                                    ctx,
                                                    snackbarHostState,
                                                    scope,
                                                    siteViewModel,
                                                ) {
                                                    inboxViewModel.markPersonMentionAsRead(
                                                        MarkPersonMentionAsRead(
                                                            person_mention_id = pm.person_mention.id,
                                                            read = !pm.person_mention.read,
                                                            auth = it.jwt,
                                                        ),
                                                        onSuccess = {
                                                            siteViewModel.updateUnreadCounts(dMentions = if (pm.person_mention.read) 1 else -1)
                                                        },
                                                    )
                                                }
                                            },
                                            onReportClick = { pm ->
                                                appState.toCommentReport(id = pm.comment.id)
                                            },
                                            onLinkClick = { pm ->
                                                // Go to the parent comment or post instead for context
                                                val parent = getCommentParentId(pm.comment)
                                                if (parent != null) {
                                                    appState.toComment(id = parent)
                                                } else {
                                                    appState.toPost(id = pm.post.id)
                                                }
                                            },
                                            onPersonClick = appState::toProfile,
                                            onCommunityClick = { community ->
                                                appState.toCommunity(id = community.id)
                                            },
                                            onBlockCreatorClick = { person ->
                                                account.doIfReadyElseDisplayInfo(
                                                    appState,
                                                    ctx,
                                                    snackbarHostState,
                                                    scope,
                                                    siteViewModel,
                                                ) {
                                                    inboxViewModel.blockPerson(
                                                        BlockPerson(
                                                            person_id = person.id,
                                                            block = true,
                                                            auth = it.jwt,
                                                        ),
                                                        ctx,
                                                    )
                                                }
                                            },
                                            onPostClick = appState::toPost,
                                            account = account,
                                            showAvatar = siteViewModel.showAvatar(),
                                            blurNSFW = blurNSFW,
                                            showScores = siteViewModel.showScores(),
                                            enableDownvotes = siteViewModel.enableDownvotes(),
                                        )
                                    }
                                }
                            }
                            else -> {}
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
                            account.doIfReadyElseDisplayInfo(
                                appState,
                                ctx,
                                snackbarHostState,
                                scope,
                                siteViewModel,
                            ) {
                                inboxViewModel.appendMessages(
                                    it.jwt,
                                )
                            }
                        }
                    }

                    val loading = inboxViewModel.messagesRes.isLoading()
                    val refreshing = inboxViewModel.mentionsRes.isRefreshing()

                    val refreshState = rememberPullRefreshState(
                        refreshing = refreshing,
                        onRefresh = {
                            account.doIfReadyElseDisplayInfo(
                                appState,
                                ctx,
                                snackbarHostState,
                                scope,
                                siteViewModel,
                            ) {
                                inboxViewModel.resetPageMessages()
                                inboxViewModel.getMessages(
                                    inboxViewModel.getFormMessages(it.jwt),
                                    ApiState.Refreshing,
                                )
                                siteViewModel.fetchUnreadCounts(GetUnreadCount(it.jwt))
                            }
                        },
                    )
                    Box(
                        modifier = Modifier
                            .pullRefresh(refreshState)
                            .fillMaxSize(),
                    ) {
                        PullRefreshIndicator(
                            refreshing,
                            refreshState,
                            Modifier
                                .align(Alignment.TopCenter)
                                .zIndex(100F),
                        )

                        if (loading) {
                            LoadingBar()
                        }
                        when (val messagesRes = inboxViewModel.messagesRes) {
                            ApiState.Empty -> ApiEmptyText()
                            is ApiState.Failure -> ApiErrorText(messagesRes.msg)
                            is ApiState.Holder -> {
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
                                        contentType = { "messages" },
                                    ) { message ->
                                        PrivateMessage(
                                            myPersonId = account.id,
                                            privateMessageView = message,
                                            onReplyClick = { privateMessageView ->
                                                appState.toPrivateMessageReply(
                                                    channel = transferPrivateMessageDepsViaRoot,
                                                    privateMessageView = privateMessageView,
                                                )
                                            },
                                            onMarkAsReadClick = { pm ->
                                                inboxViewModel.markPrivateMessageAsRead(
                                                    MarkPrivateMessageAsRead(
                                                        private_message_id = pm.private_message.id,
                                                        read = !pm.private_message.read,
                                                        auth = account.jwt,
                                                    ),
                                                    onSuccess = {
                                                        siteViewModel.updateUnreadCounts(dMessages = if (pm.private_message.read) 1 else -1)
                                                    },
                                                )
                                            },
                                            onPersonClick = appState::toProfile,
                                            account = account,
                                            showAvatar = siteViewModel.showAvatar(),
                                        )
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}
