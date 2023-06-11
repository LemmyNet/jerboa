@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.post

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import arrow.core.Either
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.jerboa.PostViewMode
import com.jerboa.R
import com.jerboa.VoteType
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.getCommentParentId
import com.jerboa.getDepthFromComment
import com.jerboa.isModerator
import com.jerboa.openLink
import com.jerboa.ui.components.comment.ShowCommentContextButtons
import com.jerboa.ui.components.comment.commentNodeItems
import com.jerboa.ui.components.comment.edit.CommentEditViewModel
import com.jerboa.ui.components.comment.reply.CommentReplyViewModel
import com.jerboa.ui.components.comment.reply.ReplyItem
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.post.edit.PostEditViewModel

@Composable
fun PostActivity(
    postViewModel: PostViewModel,
    siteViewModel: SiteViewModel,
    accountViewModel: AccountViewModel,
    commentEditViewModel: CommentEditViewModel,
    commentReplyViewModel: CommentReplyViewModel,
    postEditViewModel: PostEditViewModel,
    navController: NavController,
    appSettingsViewModel: AppSettingsViewModel,
    showCollapsedCommentContent: Boolean,
    showActionBarByDefault: Boolean,
    showVotingArrowsInListView: Boolean,
) {
    Log.d("jerboa", "got to post activity")

    val ctx = LocalContext.current

    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = postViewModel.loading && postViewModel
            .postView.value !== null,
    )

    // Holds expanded comment ids
    val unExpandedComments = remember { mutableStateListOf<Int>() }
    val commentsWithToggledActionBar = remember { mutableStateListOf<Int>() }

    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val firstComment = postViewModel.commentTree.firstOrNull()?.commentView?.comment
    val depth = getDepthFromComment(firstComment)
    val commentParentId = getCommentParentId(firstComment)
    val showContextButton = depth != null && depth > 0
    val enableDownVotes = siteViewModel.siteRes?.site_view?.local_site?.enable_downvotes ?: true
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                SimpleTopAppBar(
                    stringResource(R.string.post_activity_comments),
                    navController = navController,
                    scrollBehavior =
                    scrollBehavior,
                )
                if (postViewModel.loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
        content = { padding ->
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    val postId = postViewModel.postView.value?.post?.id
                    val commentId = postViewModel.commentId.value
                    val id = if (commentId != null) {
                        Either.Right(commentId)
                    } else if (postId != null) {
                        Either.Left(postId)
                    } else {
                        null
                    }

                    id?.let {
                        postViewModel.fetchPost(
                            id = it,
                            account = account,
                            ctx = ctx,
                        )
                    }
                },
            ) {
                postViewModel.postView.value?.also { postView ->
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .padding(padding)
                            .simpleVerticalScrollbar(listState),
                    ) {
                        item(key = "${postView.post.id}_listing") {
                            PostListing(
                                postView = postView,
                                onUpvoteClick = {
                                    postViewModel.likePost(
                                        voteType = VoteType.Upvote,
                                        account = account,
                                        ctx = ctx,
                                    )
                                    // TODO will need to pass in postlistingsviewmodel
                                    // for the Home page to also be updated
                                },
                                onDownvoteClick = {
                                    postViewModel.likePost(
                                        voteType = VoteType.Downvote,
                                        account = account,
                                        ctx = ctx,
                                    )
                                },
                                onReplyClick = { postView ->
                                    commentReplyViewModel.initialize(ReplyItem.PostItem(postView))
                                    navController.navigate("commentReply")
                                },
                                onPostClick = {},
                                onPostLinkClick = { url ->
                                    openLink(url, ctx, appSettingsViewModel.appSettings.value?.useCustomTabs ?: true)
                                },
                                onSaveClick = {
                                    account?.also { acct ->
                                        postViewModel.savePost(
                                            account = acct,
                                            ctx = ctx,
                                        )
                                    }
                                },
                                onCommunityClick = { community ->
                                    navController.navigate(route = "community/${community.id}")
                                },
                                onEditPostClick = { postView ->
                                    postEditViewModel.initialize(postView)
                                    navController.navigate("postEdit")
                                },
                                onDeletePostClick = {
                                    account?.also { acct ->
                                        postViewModel.deletePost(
                                            account = acct,
                                            ctx = ctx,
                                        )
                                    }
                                },
                                onReportClick = { postView ->
                                    navController.navigate("postReport/${postView.post.id}")
                                },
                                onPersonClick = { personId ->
                                    navController.navigate(route = "profile/$personId")
                                },
                                onBlockCommunityClick = {
                                    account?.also { acct ->
                                        postViewModel.blockCommunity(
                                            account = acct,
                                            ctx = ctx,
                                        )
                                    }
                                },
                                onBlockCreatorClick = {
                                    account?.also { acct ->
                                        postViewModel.blockCreator(
                                            creator = it,
                                            account = acct,
                                            ctx = ctx,
                                        )
                                    }
                                },
                                showReply = true, // Do nothing
                                isModerator = isModerator(
                                    postView.creator,
                                    postViewModel
                                        .moderators,
                                ),
                                showCommunityName = true,
                                fullBody = true,
                                account = account,
                                postViewMode = PostViewMode.Card,
                                showVotingArrowsInListView = showVotingArrowsInListView,
                                enableDownVotes = enableDownVotes,
                            )
                        }
                        item(key = "${postView.post.id}_is_comment_view") {
                            if (postViewModel.isCommentView()) {
                                postViewModel.postView.value?.post?.id?.let { postId ->
                                    ShowCommentContextButtons(
                                        postId,
                                        commentParentId = commentParentId,
                                        showContextButton = showContextButton,
                                        onPostClick = { id ->
                                            navController.navigate("post/$id")
                                        },
                                        onCommentClick = { commentId ->
                                            navController.navigate("comment/$commentId")
                                        },
                                    )
                                }
                            }
                        }
                        commentNodeItems(
                            nodes = postViewModel.commentTree,
                            isFlat = false,
                            isExpanded = { commentId -> !unExpandedComments.contains(commentId) },
                            toggleExpanded = { commentId ->
                                if (unExpandedComments.contains(commentId)) {
                                    unExpandedComments.remove(commentId)
                                } else {
                                    unExpandedComments.add(commentId)
                                }
                            },
                            toggleActionBar = { commentId ->
                                if (commentsWithToggledActionBar.contains(commentId)) {
                                    commentsWithToggledActionBar.remove(commentId)
                                } else {
                                    commentsWithToggledActionBar.add(commentId)
                                }
                            },
                            onMarkAsReadClick = {},
                            onUpvoteClick = { commentView ->
                                account?.also { acct ->
                                    postViewModel.likeComment(
                                        commentView = commentView,
                                        voteType = VoteType.Upvote,
                                        account = acct,
                                        ctx = ctx,
                                    )
                                }
                            },
                            onDownvoteClick = { commentView ->
                                account?.also { acct ->
                                    postViewModel.likeComment(
                                        commentView = commentView,
                                        voteType = VoteType.Downvote,
                                        account = acct,
                                        ctx = ctx,
                                    )
                                }
                            },
                            onReplyClick = { commentView ->
                                commentReplyViewModel.initialize(ReplyItem.CommentItem(commentView))
                                navController.navigate("commentReply")
                            },
                            onSaveClick = { commentView ->
                                account?.also { acct ->
                                    postViewModel.saveComment(
                                        commentView = commentView,
                                        account = acct,
                                        ctx = ctx,
                                    )
                                }
                            },
                            onPersonClick = { personId ->
                                navController.navigate(route = "profile/$personId")
                            },
                            onEditCommentClick = { commentView ->
                                commentEditViewModel.initialize(commentView)
                                navController.navigate("commentEdit")
                            },
                            onDeleteCommentClick = { commentView ->
                                account?.also { acct ->
                                    postViewModel.deleteComment(
                                        commentView = commentView,
                                        account = acct,
                                        ctx = ctx,
                                    )
                                }
                            },
                            onReportClick = { commentView ->
                                navController.navigate(
                                    "commentReport/${commentView.comment.id}",
                                )
                            },
                            onCommentLinkClick = { commentView ->
                                navController.navigate("comment/${commentView.comment.id}")
                            },
                            onFetchChildrenClick = {
                                postViewModel.fetchMoreChildren(
                                    commentView = it,
                                    account = account,
                                    ctx = ctx,
                                )
                            },
                            onBlockCreatorClick = {
                                account?.also { acct ->
                                    postViewModel.blockCreator(
                                        creator = it,
                                        account = acct,
                                        ctx = ctx,
                                    )
                                }
                            },
                            onCommunityClick = { community ->
                                navController.navigate(route = "community/${community.id}")
                            },
                            onPostClick = {}, // Do nothing
                            account = account,
                            moderators = postViewModel.moderators,
                            showCollapsedCommentContent = showCollapsedCommentContent,
                            isCollapsedByParent = false,
                            showActionBar = { commentId ->
                                showActionBarByDefault xor commentsWithToggledActionBar.contains(commentId)
                            },
                            enableDownVotes = enableDownVotes,
                        )
                    }
                }
            }
        },
    )
}
