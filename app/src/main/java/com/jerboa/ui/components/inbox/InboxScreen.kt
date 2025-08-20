package com.jerboa.ui.components.inbox

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.UnreadOrAll
import com.jerboa.api.ApiState
import com.jerboa.db.entity.Account
import com.jerboa.feat.BlurNSFW
import com.jerboa.feat.VoteType
import com.jerboa.feat.doIfReadyElseDisplayInfo
import com.jerboa.feat.newVote
import com.jerboa.getCommentParentId
import com.jerboa.model.AccountViewModel
import com.jerboa.model.InboxViewModel
import com.jerboa.model.ReplyItem
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.comment.mentionnode.CommentMentionNode
import com.jerboa.ui.components.comment.replynode.CommentReplyNodeInbox
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.JerboaLoadingBar
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.TriggerWhenReachingEnd
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.isRefreshing
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.privatemessage.PrivateMessage
import com.jerboa.unreadOrAllFromBool
import it.vercruysse.lemmyapi.datatypes.BlockPerson
import it.vercruysse.lemmyapi.datatypes.CommentReplyView
import it.vercruysse.lemmyapi.datatypes.CreateCommentLike
import it.vercruysse.lemmyapi.datatypes.MarkCommentReplyAsRead
import it.vercruysse.lemmyapi.datatypes.MarkPersonMentionAsRead
import it.vercruysse.lemmyapi.datatypes.MarkPrivateMessageAsRead
import it.vercruysse.lemmyapi.datatypes.SaveComment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    appState: JerboaAppState,
    drawerState: DrawerState,
    siteViewModel: SiteViewModel,
    accountViewModel: AccountViewModel,
    blurNSFW: BlurNSFW,
    padding: PaddingValues? = null,
) {
    Log.d("jerboa", "got to inbox screen")

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val inboxViewModel: InboxViewModel = viewModel(factory = InboxViewModel.Companion.Factory(account, siteViewModel))

    val baseModifier = if (padding == null) {
        Modifier
    } else {
        // https://issuetracker.google.com/issues/249727298
        // Else it also applies the padding above the ime (keyboard)
        Modifier
            .padding(padding)
            .consumeWindowInsets(padding)
            .systemBarsPadding()
    }

    Scaffold(
        modifier = baseModifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                            inboxViewModel.getFormReplies(),
                        )
                        inboxViewModel.getMentions(
                            inboxViewModel.getFormMentions(),
                        )
                        inboxViewModel.getMessages(
                            inboxViewModel.getFormMessages(),
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
                            onComplete = {
                                siteViewModel.fetchUnreadCounts()
                                inboxViewModel.resetPages()
                                inboxViewModel.getReplies(
                                    inboxViewModel.getFormReplies(),
                                )
                                inboxViewModel.getMentions(
                                    inboxViewModel.getFormMentions(),
                                )
                                inboxViewModel.getMessages(
                                    inboxViewModel.getFormMessages(),
                                )
                            },
                        )
                    }
                },
            )
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                InboxTabs(
                    appState = appState,
                    inboxViewModel = inboxViewModel,
                    siteViewModel = siteViewModel,
                    ctx = ctx,
                    account = account,
                    scope = scope,
                    blurNSFW = blurNSFW,
                    snackbarHostState = snackbarHostState,
                )
            }
        },
    )
}

enum class InboxTab(
    @param:StringRes val textId: Int,
) {
    Replies(R.string.inbox_screen_replies),
    Mentions(R.string.inbox_screen_mentions),
    Messages(R.string.inbox_screen_messages),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxTabs(
    appState: JerboaAppState,
    inboxViewModel: InboxViewModel,
    siteViewModel: SiteViewModel,
    ctx: Context,
    account: Account,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    blurNSFW: BlurNSFW,
) {
    val pagerState = rememberPagerState { InboxTab.entries.size }

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            tabs = {
                InboxTab.entries.forEachIndexed { index, tab ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(text = stringResource(id = tab.textId)) },
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

                    TriggerWhenReachingEnd(listState, false) {
                        inboxViewModel.appendReplies()
                    }

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
                                ),
                                onSuccess = {
                                    siteViewModel.updateUnreadCounts(dReplies = if (crv.comment_reply.read) 1 else -1)
                                },
                            )
                        }
                    }

                    PullToRefreshBox(
                        isRefreshing = inboxViewModel.repliesRes.isRefreshing(),
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
                                    inboxViewModel.getFormReplies(),
                                    ApiState.Refreshing,
                                )
                                siteViewModel.fetchUnreadCounts()
                            }
                        },
                    ) {
                        JerboaLoadingBar(inboxViewModel.repliesRes)

                        when (val repliesRes = inboxViewModel.repliesRes) {
                            ApiState.Empty -> ApiEmptyText()
                            is ApiState.Failure -> ApiErrorText(repliesRes.msg)
                            is ApiState.Holder -> {
                                val replies = repliesRes.data.replies
                                LazyColumn(
                                    state = listState,
                                    modifier =
                                        Modifier
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
                                                        ),
                                                    )
                                                }
                                            },
                                            onReplyClick = { cr ->
                                                appState.toCommentReply(
                                                    replyItem = ReplyItem.CommentReplyItem(cr),
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
                                            voteDisplayMode = siteViewModel.voteDisplayMode(),
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

                    TriggerWhenReachingEnd(listState, false) {
                        inboxViewModel.appendMentions()
                    }

                    PullToRefreshBox(
                        isRefreshing = inboxViewModel.mentionsRes.isRefreshing(),
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
                                    inboxViewModel.getFormMentions(),
                                    ApiState.Refreshing,
                                )
                                siteViewModel.fetchUnreadCounts()
                            }
                        },
                    ) {
                        JerboaLoadingBar(inboxViewModel.mentionsRes)

                        when (val mentionsRes = inboxViewModel.mentionsRes) {
                            ApiState.Empty -> ApiEmptyText()
                            is ApiState.Failure -> ApiErrorText(mentionsRes.msg)
                            is ApiState.Holder -> {
                                val mentions = mentionsRes.data.mentions
                                LazyColumn(
                                    state = listState,
                                    modifier =
                                        Modifier
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
                                            admins = siteViewModel.admins(),
                                            // No community moderators available here
                                            moderators = null,
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
                                                        ),
                                                    )
                                                }
                                            },
                                            onReplyClick = { pm ->
                                                appState.toCommentReply(
                                                    replyItem = ReplyItem.MentionReplyItem(pm),
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
                                                        ),
                                                        onSuccess = {
                                                            siteViewModel.updateUnreadCounts(
                                                                dMentions = if (pm.person_mention.read) 1 else -1,
                                                            )
                                                        },
                                                    )
                                                }
                                            },
                                            onReportClick = { pm ->
                                                appState.toCommentReport(id = pm.comment.id)
                                            },
                                            onRemoveClick = { pm ->
                                                appState.toCommentRemove(
                                                    comment = pm.comment,
                                                )
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
                                                        ),
                                                        ctx,
                                                    )
                                                }
                                            },
                                            onPostClick = appState::toPost,
                                            account = account,
                                            showAvatar = siteViewModel.showAvatar(),
                                            blurNSFW = blurNSFW,
                                            voteDisplayMode = siteViewModel.voteDisplayMode(),
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

                    TriggerWhenReachingEnd(listState, false) {
                        inboxViewModel.appendMessages()
                    }

                    PullToRefreshBox(
                        isRefreshing = inboxViewModel.messagesRes.isRefreshing(),
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
                                    inboxViewModel.getFormMessages(),
                                    ApiState.Refreshing,
                                )
                                siteViewModel.fetchUnreadCounts()
                            }
                        },
                    ) {
                        JerboaLoadingBar(inboxViewModel.messagesRes)

                        when (val messagesRes = inboxViewModel.messagesRes) {
                            ApiState.Empty -> ApiEmptyText()
                            is ApiState.Failure -> ApiErrorText(messagesRes.msg)
                            is ApiState.Holder -> {
                                val messages = messagesRes.data.private_messages
                                LazyColumn(
                                    state = listState,
                                    modifier =
                                        Modifier
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
                                                    privateMessageView = privateMessageView,
                                                )
                                            },
                                            onMarkAsReadClick = { pm ->
                                                inboxViewModel.markPrivateMessageAsRead(
                                                    MarkPrivateMessageAsRead(
                                                        private_message_id = pm.private_message.id,
                                                        read = !pm.private_message.read,
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
