package com.jerboa.ui.components.post

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jerboa.VoteType
import com.jerboa.buildCommentsTree
import com.jerboa.db.AccountViewModel
import com.jerboa.getCurrentAccount
import com.jerboa.ui.components.comment.CommentNodes

@Composable
fun PostActivity(
    postViewModel: PostViewModel = viewModel(),
    accountViewModel: AccountViewModel = viewModel(),
    navController: NavController,
    postId: Int,
) {

    Log.d("jerboa", "got to post activity")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)

//    val commentNodes = buildCommentsTree(postViewModel.comments)
    val commentNodes = buildCommentsTree(postViewModel.comments)

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            topBar = {
                PostListingHeader(navController = navController)
            },
            content = {
                if (postViewModel.loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    postViewModel.postView?.let { postView ->
                        Column {
                            PostListing(
                                postView = postView,
                                fullBody = true,
                                onUpvoteClick = {
                                    postViewModel.likePost(
                                        voteType = VoteType.Upvote,
                                        account = account,
                                        ctx = ctx,
                                    )
                                },
                                onDownvoteClick = {
                                    postViewModel.likePost(
                                        voteType = VoteType.Downvote,
                                        account = account,
                                        ctx = ctx,
                                    )
                                }
                            )
                            CommentNodes(
                                nodes = commentNodes,
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
                                }
                            )
                        }
                    }
                }
            }
        )
    }
}
