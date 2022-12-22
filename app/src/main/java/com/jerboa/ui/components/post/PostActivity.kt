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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import arrow.core.Either
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.jerboa.VoteType
import com.jerboa.db.AccountViewModel
import com.jerboa.isModerator
import com.jerboa.openLink
import com.jerboa.ui.components.comment.commentNodeItems
import com.jerboa.ui.components.comment.edit.CommentEditViewModel
import com.jerboa.ui.components.comment.reply.CommentReplyViewModel
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.post.edit.PostEditViewModel

@Composable
fun PostActivity(
    postViewModel: PostViewModel,
    accountViewModel: AccountViewModel,
    commentEditViewModel: CommentEditViewModel,
    commentReplyViewModel: CommentReplyViewModel,
    postEditViewModel: PostEditViewModel,
    navController: NavController
) {
    Log.d("jerboa", "got to post activity")

    val ctx = LocalContext.current

    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = postViewModel.loading && postViewModel
            .postView.value !== null
    )

    // Holds expanded comment ids
    val unExpandedComments = remember { mutableStateListOf<Int>() }

    val listState = rememberLazyListState()

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            topBar = {
                Column {
                    SimpleTopAppBar("Comments", navController = navController)
                    if (postViewModel.loading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            content = { padding ->
                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = {
                        postViewModel.postView.value?.also { postView ->
                            postViewModel.fetchPost(
                                id = postView.post.id,
                                account = account,
                                ctx = ctx
                            )
                        }
                    }
                ) {
                    postViewModel.postView.value?.also { postView ->
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.padding(padding)
                                .simpleVerticalScrollbar(listState)
                        ) {
                            item {
                                PostListing(
                                    postView = postView,
                                    showCommunityName = true,
                                    onUpvoteClick = {
                                        postViewModel.likePost(
                                            voteType = VoteType.Upvote,
                                            account = account,
                                            ctx = ctx
                                        )
                                        // TODO will need to pass in postlistingsviewmodel
                                        // for the Home page to also be updated
                                    },
                                    onDownvoteClick = {
                                        postViewModel.likePost(
                                            voteType = VoteType.Downvote,
                                            account = account,
                                            ctx = ctx
                                        )
                                    },
                                    onSaveClick = {
                                        account?.also { acct ->
                                            postViewModel.savePost(
                                                account = acct,
                                                ctx = ctx
                                            )
                                        }
                                    },
                                    onBlockCommunityClick = {
                                        account?.also { acct ->
                                            postViewModel.blockCommunity(
                                                account = acct,
                                                ctx = ctx
                                            )
                                        }
                                    },
                                    onBlockCreatorClick = {
                                        account?.also { acct ->
                                            postViewModel.blockCreator(
                                                creator = it,
                                                account = acct,
                                                ctx = ctx
                                            )
                                        }
                                    },
                                    onReplyClick = { postView ->
                                        commentReplyViewModel.initialize(Either.Right(postView))
                                        navController.navigate("commentReply")
                                    },
                                    onPostLinkClick = { url ->
                                        openLink(url, ctx)
                                    },
                                    onCommunityClick = { community ->
                                        navController.navigate(route = "community/${community.id}")
                                    },
                                    onPersonClick = { personId ->
                                        navController.navigate(route = "profile/$personId")
                                    },
                                    onEditPostClick = { postView ->
                                        postEditViewModel.initialize(postView)
                                        navController.navigate("postEdit")
                                    },
                                    onDeletePostClick = {
                                        account?.also { acct ->
                                            postViewModel.deletePost(
                                                account = acct,
                                                ctx = ctx
                                            )
                                        }
                                    },
                                    onReportClick = { postView ->
                                        navController.navigate("postReport/${postView.post.id}")
                                    },
                                    onPostClick = {}, // Do nothing
                                    showReply = true,
                                    account = account,
                                    isModerator = isModerator(
                                        postView.creator,
                                        postViewModel
                                            .moderators
                                    ),
                                    fullBody = true
                                )
                            }
                            commentNodeItems(
                                nodes = postViewModel.commentTree,
                                isExpanded = { commentId -> !unExpandedComments.contains(commentId) },
                                toggleExpanded = { commentId ->
                                    if (unExpandedComments.contains(commentId)) {
                                        unExpandedComments.remove(commentId)
                                    } else {
                                        unExpandedComments.add(commentId)
                                    }
                                },
                                onMarkAsReadClick = {},
                                onUpvoteClick = { commentView ->
                                    account?.also { acct ->
                                        postViewModel.likeComment(
                                            commentView = commentView,
                                            voteType = VoteType.Upvote,
                                            account = acct,
                                            ctx = ctx
                                        )
                                    }
                                },
                                onDownvoteClick = { commentView ->
                                    account?.also { acct ->
                                        postViewModel.likeComment(
                                            commentView = commentView,
                                            voteType = VoteType.Downvote,
                                            account = acct,
                                            ctx = ctx
                                        )
                                    }
                                },
                                onReplyClick = { commentView ->
                                    commentReplyViewModel.initialize(Either.Left(commentView))
                                    navController.navigate("commentReply")
                                },
                                onSaveClick = { commentView ->
                                    account?.also { acct ->
                                        postViewModel.saveComment(
                                            commentView = commentView,
                                            account = acct,
                                            ctx = ctx
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
                                            ctx = ctx
                                        )
                                    }
                                },
                                onReportClick = { commentView ->
                                    navController.navigate(
                                        "commentReport/${commentView.comment
                                            .id}"
                                    )
                                },
                                onBlockCreatorClick = {
                                    account?.also { acct ->
                                        postViewModel.blockCreator(
                                            creator = it,
                                            account = acct,
                                            ctx = ctx
                                        )
                                    }
                                },
                                onCommunityClick = { community ->
                                    navController.navigate(route = "community/${community.id}")
                                },
                                onPostClick = {}, // Do nothing
                                account = account,
                                moderators = postViewModel.moderators
                            )
                        }
                    }
                }
            }
        )
    }
}
