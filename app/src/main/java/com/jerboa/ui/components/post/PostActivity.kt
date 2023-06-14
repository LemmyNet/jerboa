package com.jerboa.ui.components.post

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import arrow.core.Either
import com.jerboa.PostViewMode
import com.jerboa.R
import com.jerboa.VoteType
import com.jerboa.api.ApiState
import com.jerboa.buildCommentsTree
import com.jerboa.datatypes.types.BlockCommunity
import com.jerboa.datatypes.types.BlockPerson
import com.jerboa.datatypes.types.CreateCommentLike
import com.jerboa.datatypes.types.CreatePostLike
import com.jerboa.datatypes.types.DeleteComment
import com.jerboa.datatypes.types.DeletePost
import com.jerboa.datatypes.types.SaveComment
import com.jerboa.datatypes.types.SavePost
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.getCommentParentId
import com.jerboa.getDepthFromComment
import com.jerboa.isModerator
import com.jerboa.newVote
import com.jerboa.openLink
import com.jerboa.ui.components.comment.ShowCommentContextButtons
import com.jerboa.ui.components.comment.commentNodeItems
import com.jerboa.ui.components.comment.edit.CommentEditViewModel
import com.jerboa.ui.components.comment.reply.CommentReplyViewModel
import com.jerboa.ui.components.comment.reply.ReplyItem
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.post.edit.PostEditViewModel

@OptIn(ExperimentalMaterialApi::class)
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

    val loading = when (postViewModel.postRes) {
        ApiState.Loading -> true
        else -> false
    }

    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = loading,
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
    val pullRefreshState = rememberPullRefreshState(
        refreshing = postViewModel.loading,
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
    )
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
                if (loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },

//Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                PullRefreshIndicator(postViewModel.loading, pullRefreshState, Modifier.align(Alignment.TopCenter))
        content = { padding ->
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    postViewModel.getData(account)
                },
            ) {
                when (val postRes = postViewModel.postRes) {
                    ApiState.Empty -> ApiEmptyText()
                    is ApiState.Failure -> ApiErrorText(postRes.msg)
                    ApiState.Loading -> CircularProgressIndicator()
                    is ApiState.Success -> {
                        val postView = postRes.data.post_view

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .padding(padding)
                                .simpleVerticalScrollbar(listState),
                        ) {
                            item(key = "${postView.post.id}_listing") {
                                PostListing(
                                    postView = postView,
                                    onUpvoteClick = { pv ->
                                        account?.also { acct ->
                                            postViewModel.likePost(
                                                CreatePostLike(
                                                    post_id = pv.post.id,
                                                    score = newVote(
                                                        postView.my_vote,
                                                        VoteType.Upvote,
                                                    ),
                                                    auth = acct.jwt,
                                                ),
                                            )
                                        }
                                        // TODO will need to pass in postlistingsviewmodel
                                        // for the Home page to also be updated
                                    },
                                    onDownvoteClick = { pv ->
                                        account?.also { acct ->
                                            postViewModel.likePost(
                                                CreatePostLike(
                                                    post_id = pv.post.id,
                                                    score = newVote(
                                                        postView.my_vote,
                                                        VoteType.Upvote,
                                                    ),
                                                    auth = acct.jwt,
                                                ),
                                            )
                                        }
                                    },
                                    onReplyClick = { pv ->
                                        commentReplyViewModel.initialize(ReplyItem.PostItem(pv))
                                        navController.navigate("commentReply")
                                    },
                                    onPostClick = {},
                                    onPostLinkClick = { url ->
                                        openLink(url, ctx)
                                    },
                                    onSaveClick = { pv ->
                                        account?.also { acct ->
                                            postViewModel.savePost(
                                                SavePost(
                                                    post_id = pv.post.id,
                                                    save = !pv.saved,
                                                    auth = acct.jwt,
                                                ),
                                            )
                                        }
                                    },
                                    onCommunityClick = { community ->
                                        navController.navigate(route = "community/${community.id}")
                                    },
                                    onEditPostClick = { pv ->
                                        postEditViewModel.initialize(pv)
                                        navController.navigate("postEdit")
                                    },
                                    onDeletePostClick = { pv ->
                                        account?.also { acct ->
                                            postViewModel.deletePost(
                                                DeletePost(
                                                    post_id = pv.post.id,
                                                    deleted = pv.post.deleted,
                                                    auth = acct.jwt,
                                                ),
                                            )
                                        }
                                    },
                                    onReportClick = { pv ->
                                        navController.navigate("postReport/${pv.post.id}")
                                    },
                                    onPersonClick = { personId ->
                                        navController.navigate(route = "profile/$personId")
                                    },
                                    onBlockCommunityClick = { c ->
                                        account?.also { acct ->
                                            postViewModel.blockCommunity(
                                                BlockCommunity(
                                                    community_id = c.id,
                                                    block = true,
                                                    auth = acct.jwt,
                                                ),
                                                ctx,
                                            )
                                        }
                                    },
                                    onBlockCreatorClick = { person ->
                                        account?.also { acct ->
                                            postViewModel.blockPerson(
                                                BlockPerson(
                                                    person_id = person.id,
                                                    block = true,
                                                    auth = acct.jwt,
                                                ),
                                                ctx,
                                            )
                                        }
                                    },
                                    showReply = true, // Do nothing
                                    isModerator = isModerator(
                                        postView.creator,
                                        postRes.data.moderators,
                                    ),
                                    showCommunityName = true,
                                    fullBody = true,
                                    account = account,
                                    postViewMode = PostViewMode.Card,
                                )
                            }

                            when (val commentsRes = postViewModel.commentsRes) {
                                ApiState.Empty -> item(key = "empty") { ApiEmptyText() }
                                is ApiState.Failure -> item(key = "error") {
                                    ApiErrorText(
                                        commentsRes.msg,
                                    )
                                }

                                ApiState.Loading -> item(key = "loading") { CircularProgressIndicator() }
                                is ApiState.Success -> {
                                    val commentTree = buildCommentsTree(
                                        commentsRes.data.comments,
                                        postViewModel.isCommentView(),
                                    )

                                    val firstComment =
                                        commentTree.firstOrNull()?.commentView?.comment
                                    val depth = getDepthFromComment(firstComment)
                                    val commentParentId = getCommentParentId(firstComment)
                                    val showContextButton = depth != null && depth > 0

                                    item(key = "${postView.post.id}_is_comment_view") {
                                        if (postViewModel.isCommentView()) {
                                            ShowCommentContextButtons(
                                                postView.post.id,
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

                                    commentNodeItems(
                                        nodes = commentTree,
                                        isFlat = false,
                                        isExpanded = { commentId ->
                                            !unExpandedComments.contains(
                                                commentId,
                                            )
                                        },
                                        toggleExpanded = { commentId ->
                                            if (unExpandedComments.contains(commentId)) {
                                                unExpandedComments.remove(commentId)
                                            } else {
                                                unExpandedComments.add(commentId)
                                            }
                                        },
                                        onMarkAsReadClick = {},
                                        onUpvoteClick = { cv ->
                                            account?.also { acct ->
                                                postViewModel.likeComment(
                                                    CreateCommentLike(
                                                        comment_id = cv.comment.id,
                                                        score = newVote(
                                                            cv.my_vote,
                                                            VoteType.Upvote,
                                                        ),
                                                        auth = acct.jwt,
                                                    ),
                                                )
                                            }
                                        },
                                        onDownvoteClick = { cv ->
                                            account?.also { acct ->
                                                postViewModel.likeComment(
                                                    CreateCommentLike(
                                                        comment_id = cv.comment.id,
                                                        score = newVote(
                                                            cv.my_vote,
                                                            VoteType.Downvote,
                                                        ),
                                                        auth = acct.jwt,
                                                    ),
                                                )
                                            }
                                        },
                                        onReplyClick = { cv ->
                                            commentReplyViewModel.initialize(
                                                ReplyItem.CommentItem(
                                                    cv,
                                                ),
                                            )
                                            navController.navigate("commentReply")
                                        },
                                        onSaveClick = { cv ->
                                            account?.also { acct ->
                                                postViewModel.saveComment(
                                                    SaveComment(
                                                        comment_id = cv.comment.id,
                                                        save = !cv.saved,
                                                        auth = acct.jwt,
                                                    ),
                                                )
                                            }
                                        },
                                        onPersonClick = { personId ->
                                            navController.navigate(route = "profile/$personId")
                                        },
                                        onEditCommentClick = { cv ->
                                            commentEditViewModel.initialize(cv)
                                            navController.navigate("commentEdit")
                                        },
                                        onDeleteCommentClick = { cv ->
                                            account?.also { acct ->
                                                postViewModel.deleteComment(
                                                    DeleteComment(
                                                        comment_id = cv.comment.id,
                                                        deleted = !cv.comment.deleted,
                                                        auth = acct.jwt,
                                                    ),
                                                )
                                            }
                                        },
                                        onReportClick = { cv ->
                                            navController.navigate(
                                                "commentReport/${
                                                    cv.comment
                                                        .id
                                                }",
                                            )
                                        },
                                        onCommentLinkClick = { cv ->
                                            navController.navigate("comment/${cv.comment.id}")
                                        },
                                        onFetchChildrenClick = { cv ->
                                            postViewModel.fetchMoreChildren(
                                                commentView = cv,
                                                account = account,

                                            )
                                        },
                                        onBlockCreatorClick = { person ->
                                            account?.also { acct ->
                                                postViewModel.blockPerson(
                                                    BlockPerson(
                                                        person_id = person.id,
                                                        block = true,
                                                        auth = acct.jwt,
                                                    ),
                                                    ctx,
                                                )
                                            }
                                        },
                                        onCommunityClick = { community ->
                                            navController.navigate(route = "community/${community.id}")
                                        },
                                        onPostClick = {}, // Do nothing
                                        account = account,
                                        moderators = postRes.data.moderators,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
    )
}
