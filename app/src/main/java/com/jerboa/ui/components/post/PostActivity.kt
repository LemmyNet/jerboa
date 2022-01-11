package com.jerboa.ui.components.post

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.jerboa.VoteType
import com.jerboa.buildCommentsTree
import com.jerboa.datatypes.api.GetPost
import com.jerboa.db.AccountViewModel
import com.jerboa.getCurrentAccount
import com.jerboa.openLink
import com.jerboa.ui.components.comment.CommentNode

@Composable
fun PostActivity(
    postId: Int,
    postViewModel: PostViewModel = viewModel(),
    postListingsViewModel: PostListingsViewModel = viewModel(),
    accountViewModel: AccountViewModel = viewModel(),
    navController: NavController,
) {

    Log.d("jerboa", "got to post activity")

    val ctx = LocalContext.current
    val listState = rememberLazyListState()

    val account = getCurrentAccount(accountViewModel = accountViewModel)
    val commentNodes = buildCommentsTree(postViewModel.comments)

    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = postViewModel.loading && postViewModel
            .postView !== null
    )

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            topBar = {
                Column {
                    PostListingHeader(navController = navController)
                    if (postViewModel.loading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            content = {
                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = {
                        postViewModel.fetchPost(
                            GetPost(
                                id = postId,
                                auth = account?.jwt,
                            )
                        )
                    },
                ) {
                    postViewModel.postView?.also { postView ->
                        LazyColumn(state = listState) {
                            item {
                                PostListing(
                                    postView = postView,
                                    fullBody = true,
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
                                    onSaveClick = {
                                        postViewModel.savePost(
                                            account = account,
                                            ctx = ctx
                                        )
                                    },
                                    onReplyClick = {
                                        postViewModel.replyToCommentParent = null
                                        navController.navigate("commentReply")
                                    },
                                    onPostLinkClick = { url ->
                                        openLink(url, ctx)
                                    },
                                    showReply = true,
                                )
                            }
                            // Don't use CommentNodes here, otherwise lazy scrolling wont work
                            // Can't really do scrolling well here either because of tree
                            itemsIndexed(commentNodes) { _, node ->
                                CommentNode(
                                    node = node,
                                    onUpvoteClick = { commentView ->
                                        postViewModel.likeComment(
                                            commentView = commentView,
                                            voteType = VoteType.Upvote,
                                            account = account,
                                            ctx = ctx,
                                        )
                                    },
                                    onDownvoteClick = { commentView ->
                                        postViewModel.likeComment(
                                            commentView = commentView,
                                            voteType = VoteType.Downvote,
                                            account = account,
                                            ctx = ctx,
                                        )
                                    },
                                    onReplyClick = { commentView ->
                                        postViewModel.replyToCommentParent = commentView
                                        navController.navigate("commentReply")
                                    },
                                    onSaveClick = { commentView ->
                                        postViewModel.saveComment(
                                            commentView = commentView,
                                            account = account,
                                            ctx = ctx,
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}
