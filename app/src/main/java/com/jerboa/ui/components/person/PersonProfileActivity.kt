package com.jerboa.ui.components.person

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.jerboa.*
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.comment.CommentNode
import com.jerboa.ui.components.comment.edit.CommentEditViewModel
import com.jerboa.ui.components.comment.edit.commentEditClickWrapper
import com.jerboa.ui.components.community.CommunityViewModel
import com.jerboa.ui.components.community.communityClickWrapper
import com.jerboa.ui.components.home.BottomAppBarAll
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.inbox.inboxClickWrapper
import com.jerboa.ui.components.post.InboxViewModel
import com.jerboa.ui.components.post.PostListings
import com.jerboa.ui.components.post.PostViewModel
import com.jerboa.ui.components.post.postClickWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PersonProfileActivity(
    navController: NavController,
    personProfileViewModel: PersonProfileViewModel,
    postViewModel: PostViewModel,
    communityViewModel: CommunityViewModel,
    accountViewModel: AccountViewModel,
    homeViewModel: HomeViewModel,
    inboxViewModel: InboxViewModel,
    commentEditViewModel: CommentEditViewModel,
) {

    Log.d("jerboa", "got to person activity")

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val ctx = LocalContext.current
    val accounts by accountViewModel.allAccounts.observeAsState()
    val account = getCurrentAccount(accounts = accounts)

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                Column {
                    personProfileViewModel.res?.person_view?.person?.name?.also {
                        PersonProfileHeader(
                            personName = it,
                            selectedSortType = personProfileViewModel.sortType.value,
                            onClickSortType = { sortType ->
                                personProfileViewModel.fetchPersonDetails(
                                    id = personProfileViewModel.personId.value!!,
                                    account = account,
                                    clear = true,
                                    changeSortType = sortType,
                                    ctx = ctx,
                                )
                            },
                            navController = navController,
                        )
                    }

                    if (personProfileViewModel.loading.value) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            content = {
                UserTabs(
                    navController = navController,
                    personProfileViewModel = personProfileViewModel,
                    postViewModel = postViewModel,
                    communityViewModel = communityViewModel,
                    ctx = ctx,
                    account = account,
                    scope = scope,
                    commentEditViewModel = commentEditViewModel,
                )
            },
            bottomBar = {
                BottomAppBarAll(
                    unreadCounts = homeViewModel.unreadCountResponse,
                    onClickProfile = {
                        account?.id?.also {
                            personClickWrapper(
                                personProfileViewModel = personProfileViewModel,
                                personId = it,
                                account = account,
                                navController = navController,
                                ctx = ctx,
                            )
                        }
                    },
                    onClickInbox = {
                        inboxClickWrapper(inboxViewModel, account, navController, ctx)
                    },
                    navController = navController,
                )
            }
        )
    }
}

enum class UserTab {
    About,
    Posts,
    Comments,
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun UserTabs(
    navController: NavController,
    personProfileViewModel: PersonProfileViewModel,
    communityViewModel: CommunityViewModel,
    ctx: Context,
    account: Account?,
    scope: CoroutineScope,
    postViewModel: PostViewModel,
    commentEditViewModel: CommentEditViewModel,
) {
    val tabTitles = UserTab.values().map { it.toString() }
    val pagerState = rememberPagerState()

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions -> // 3.
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(
                        pagerState,
                        tabPositions
                    )
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(text = title) }
                )
            }
        }
        HorizontalPager(
            count = tabTitles.size,
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxSize()
        ) { tabIndex ->
            when (tabIndex) {
                UserTab.About.ordinal -> {
                    personProfileViewModel.res?.person_view?.also {
                        PersonProfileTopSection(
                            personView = it
                        )
                    }
                }
                UserTab.Posts.ordinal -> {
                    PostListings(
                        posts = personProfileViewModel.posts,
                        onUpvoteClick = { postView ->
                            personProfileViewModel.likePost(
                                voteType = VoteType.Upvote,
                                postView = postView,
                                account = account,
                                ctx = ctx,
                            )
                        },
                        onDownvoteClick = { postView ->
                            personProfileViewModel.likePost(
                                voteType = VoteType.Downvote,
                                postView = postView,
                                account = account,
                                ctx = ctx,
                            )
                        },
                        onPostClick = { postView ->
                            postClickWrapper(
                                postViewModel = postViewModel,
                                postId = postView.post.id,
                                account = account,
                                navController = navController,
                                ctx = ctx,
                            )
                        },
                        onPostLinkClick = { url ->
                            openLink(url, ctx)
                        },
                        onSaveClick = { postView ->
                            personProfileViewModel.savePost(
                                postView = postView,
                                account = account,
                                ctx = ctx,
                            )
                        },
                        onCommunityClick = { community ->
                            communityClickWrapper(
                                communityViewModel,
                                community.id,
                                account,
                                navController,
                                ctx = ctx,
                            )
                        },
                        onSwipeRefresh = {
                            personProfileViewModel.personId.value?.also {
                                personProfileViewModel.fetchPersonDetails(
                                    id = it,
                                    account = account,
                                    clear = true,
                                    ctx = ctx,
                                )
                            }
                        },
                        loading = personProfileViewModel.loading.value &&
                            personProfileViewModel.page.value == 1 &&
                            personProfileViewModel.posts.isNotEmpty(),
                        isScrolledToEnd = {
                            personProfileViewModel.personId.value?.also {
                                personProfileViewModel.fetchPersonDetails(
                                    id = it,
                                    account = account,
                                    nextPage = true,
                                    ctx = ctx,
                                )
                            }
                        },
                        onPersonClick = { personId ->
                            personClickWrapper(
                                personProfileViewModel = personProfileViewModel,
                                personId = personId,
                                account = account,
                                navController = navController,
                                ctx = ctx,
                            )
                        },
                        account = account,
                    )
                }
                UserTab.Comments.ordinal -> {
                    val nodes = sortNodes(commentsToFlatNodes(personProfileViewModel.comments))
                    LazyColumn {
                        items(nodes) { node ->
                            CommentNode(
                                node = node,
                                onUpvoteClick = { commentView ->
                                    account?.also { acct ->
                                        personProfileViewModel.likeComment(
                                            commentView = commentView,
                                            voteType = VoteType.Upvote,
                                            account = acct,
                                            ctx = ctx,
                                        )
                                    }
                                },
                                onDownvoteClick = { commentView ->
                                    account?.also { acct ->
                                        personProfileViewModel.likeComment(
                                            commentView = commentView,
                                            voteType = VoteType.Downvote,
                                            account = acct,
                                            ctx = ctx,
                                        )
                                    }
                                },
                                onReplyClick = { commentView ->
                                    // To do replies from elsewhere than postView,
                                    // you need to refetch that post view
                                    postViewModel.replyToCommentParent = commentView
                                    postViewModel.fetchPost(
                                        id = commentView.post.id,
                                        account = account,
                                        ctx = ctx,
                                    )
                                    navController.navigate("commentReply")
                                },
                                onSaveClick = { commentView ->
                                    account?.also { acct ->
                                        personProfileViewModel.saveComment(
                                            commentView = commentView,
                                            account = acct,
                                            ctx = ctx,
                                        )
                                    }
                                },
                                onPersonClick = { personId ->
                                    personClickWrapper(
                                        personProfileViewModel,
                                        personId,
                                        account,
                                        navController,
                                        ctx
                                    )
                                },
                                onCommunityClick = { community ->
                                    communityClickWrapper(
                                        communityViewModel = communityViewModel,
                                        communityId = community.id,
                                        account = account,
                                        navController = navController,
                                        ctx = ctx,
                                    )
                                },
                                onPostClick = { postId ->
                                    postClickWrapper(
                                        postViewModel = postViewModel,
                                        postId = postId,
                                        account = account,
                                        navController = navController,
                                        ctx = ctx,
                                    )
                                },
                                onEditCommentClick = { commentView ->
                                    commentEditClickWrapper(
                                        commentEditViewModel,
                                        commentView,
                                        navController,
                                    )
                                },
                                showPostAndCommunityContext = true,
                                account = account,
                                moderators = listOf()
                            )
                        }
                    }
                }
            }
        }
    }
}
