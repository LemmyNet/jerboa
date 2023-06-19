package com.jerboa.ui.components.post

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.jerboa.PostViewMode
import com.jerboa.R
import com.jerboa.VoteType
import com.jerboa.api.ApiState
import com.jerboa.buildCommentsTree
import com.jerboa.datatypes.types.BlockCommunity
import com.jerboa.datatypes.types.BlockPerson
import com.jerboa.datatypes.types.CommentSortType
import com.jerboa.datatypes.types.CreateCommentLike
import com.jerboa.datatypes.types.CreatePostLike
import com.jerboa.datatypes.types.DeleteComment
import com.jerboa.datatypes.types.DeletePost
import com.jerboa.datatypes.types.SaveComment
import com.jerboa.datatypes.types.SavePost
import com.jerboa.db.AccountViewModel
import com.jerboa.getCommentParentId
import com.jerboa.getDepthFromComment
import com.jerboa.getLocalizedCommentSortTypeName
import com.jerboa.isModerator
import com.jerboa.newVote
import com.jerboa.ui.components.comment.ShowCommentContextButtons
import com.jerboa.ui.components.comment.commentNodeItems
import com.jerboa.ui.components.comment.edit.CommentEditViewModel
import com.jerboa.ui.components.comment.reply.CommentReplyViewModel
import com.jerboa.ui.components.comment.reply.ReplyItem
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.CommentSortOptionsDialog
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.post.edit.PostEditViewModel

@Composable
fun CommentsHeaderTitle(
    selectedSortType: CommentSortType,
) {
    val ctx = LocalContext.current
    Column {
        Text(
            text = stringResource(R.string.post_activity_comments),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(

            text = getLocalizedCommentSortTypeName(ctx, selectedSortType),
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PostActivity(
    postViewModel: PostViewModel,
    siteViewModel: SiteViewModel,
    accountViewModel: AccountViewModel,
    commentEditViewModel: CommentEditViewModel,
    commentReplyViewModel: CommentReplyViewModel,
    postEditViewModel: PostEditViewModel,
    navController: NavController,
    showCollapsedCommentContent: Boolean,
    showActionBarByDefault: Boolean,
    showVotingArrowsInListView: Boolean,
    onClickSortType: (CommentSortType) -> Unit,
    selectedSortType: CommentSortType,
) {
    Log.d("jerboa", "got to post activity")

    val ctx = LocalContext.current

    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val postLoading = postViewModel.postRes == ApiState.Loading

    // Holds expanded comment ids
    val unExpandedComments = remember { mutableStateListOf<Int>() }
    val commentsWithToggledActionBar = remember { mutableStateListOf<Int>() }
    var showSortOptions by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val pullRefreshState = rememberPullRefreshState(
        refreshing = postLoading,
        onRefresh = {
            postViewModel.getData(account)
        },
    )

    if (showSortOptions) {
        CommentSortOptionsDialog(
            selectedSortType = selectedSortType,
            onDismissRequest = { showSortOptions = false },
            onClickSortType = {
                showSortOptions = false
                onClickSortType(it)
            },
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        CommentsHeaderTitle(
                            selectedSortType = selectedSortType,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Outlined.ArrowBack,
                                contentDescription = stringResource(R.string.topAppBar_back),
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            showSortOptions = !showSortOptions
                        }) {
                            Icon(
                                Icons.Outlined.Sort,
                                contentDescription = stringResource(R.string.selectSort),
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
        },
        content = { padding ->
            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                PullRefreshIndicator(
                    postLoading,
                    pullRefreshState,
                    Modifier.align(Alignment.TopCenter),
                )
                when (val postRes = postViewModel.postRes) {
                    is ApiState.Loading ->
                        LoadingBar(padding)
                    is ApiState.Failure -> ApiErrorText(postRes.msg)
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
                                        val isModerator = isModerator(pv.creator, postRes.data.moderators)
                                        navController.navigate("commentReply?isModerator=$isModerator")
                                    },
                                    onPostClick = {},
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
                                    enableDownVotes = siteViewModel.enableDownvotes(),
                                    showAvatar = siteViewModel.showAvatar(),
                                    showVotingArrowsInListView = showVotingArrowsInListView,
                                )
                            }

                            when (val commentsRes = postViewModel.commentsRes) {
                                is ApiState.Loading ->
                                    item {
                                        LoadingBar()
                                    }

                                is ApiState.Failure -> item(key = "error") {
                                    ApiErrorText(
                                        commentsRes.msg,
                                    )
                                }

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
                                        toggleActionBar = { commentId ->
                                            if (commentsWithToggledActionBar.contains(commentId)) {
                                                commentsWithToggledActionBar.remove(commentId)
                                            } else {
                                                commentsWithToggledActionBar.add(commentId)
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

                                            val isModerator = isModerator(cv.creator, postRes.data.moderators)
                                            navController.navigate("commentReply?isModerator=$isModerator")
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
                                        enableDownVotes = siteViewModel.enableDownvotes(),
                                        showAvatar = siteViewModel.showAvatar(),
                                        isCollapsedByParent = false,
                                        showCollapsedCommentContent = showCollapsedCommentContent,
                                        showActionBar = { commentId ->
                                            showActionBarByDefault xor commentsWithToggledActionBar.contains(
                                                commentId,
                                            )
                                        },
                                    )
                                }

                                else -> {}
                            }
                        }
                    }

                    else -> {}
                }
            }
        },
    )
}
