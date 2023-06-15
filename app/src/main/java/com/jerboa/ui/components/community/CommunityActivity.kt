package com.jerboa.ui.components.community

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.jerboa.R
import com.jerboa.VoteType
import com.jerboa.api.ApiState
import com.jerboa.datatypes.types.BlockCommunity
import com.jerboa.datatypes.types.BlockPerson
import com.jerboa.datatypes.types.CreatePostLike
import com.jerboa.datatypes.types.DeletePost
import com.jerboa.datatypes.types.FollowCommunity
import com.jerboa.datatypes.types.GetPosts
import com.jerboa.datatypes.types.SavePost
import com.jerboa.datatypes.types.SubscribedType
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.loginFirstToast
import com.jerboa.newVote
import com.jerboa.scrollToTop
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.BottomAppBarAll
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.getPostViewMode
import com.jerboa.ui.components.community.list.CommunityListViewModel
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.post.PostListings
import com.jerboa.ui.components.post.edit.PostEditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityActivity(
    navController: NavController,
    communityViewModel: CommunityViewModel,
    communityListViewModel: CommunityListViewModel,
    siteViewModel: SiteViewModel,
    postEditViewModel: PostEditViewModel,
    accountViewModel: AccountViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    showVotingArrowsInListView: Boolean,
) {
    Log.d("jerboa", "got to community activity")

    val scope = rememberCoroutineScope()
    val postListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                when (val communityRes = communityViewModel.communityRes) {
                    ApiState.Empty -> ApiEmptyText()
                    is ApiState.Failure -> ApiErrorText(communityRes.msg)

                    ApiState.Loading -> {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    is ApiState.Success -> {
                        val communityId = communityRes.data.community_view.community.id
                        CommunityHeader(
                            scrollBehavior = scrollBehavior,
                            communityName = communityRes.data.community_view.community.name,
                            selectedSortType = communityViewModel.sortType,
                            onClickRefresh = {
                                scrollToTop(scope, postListState)
                                communityViewModel.getPosts(
                                    GetPosts(
                                        community_id = communityId,
                                        page = communityViewModel.page,
                                        sort = communityViewModel.sortType,
                                        auth = account?.jwt,
                                    ),
                                )
                            },
                            onClickSortType = { sortType ->
                                communityViewModel.updateSortType(sortType)
                                communityViewModel.resetPage()
                                scrollToTop(scope, postListState)
                                communityViewModel.getPosts(
                                    GetPosts(
                                        community_id = communityId,
                                        page = communityViewModel.page,
                                        sort = communityViewModel.sortType,
                                        auth = account?.jwt,
                                    ),
                                )
                            },
                            onBlockCommunityClick = {
                                account?.also { acct ->
                                    communityViewModel.blockCommunity(
                                        BlockCommunity(
                                            community_id = communityId,
                                            auth = acct.jwt,
                                            block = !communityRes.data.community_view.blocked,
                                        ),
                                        ctx = ctx,
                                    )
                                }
                            },
                            navController = navController,
                        )
                    }
                }
            }
        },
        content = {
            when (val communityRes = communityViewModel.communityRes) {
                is ApiState.Success -> {
                    CommunityTopSection(
                        communityView = communityRes.data.community_view,
                        onClickFollowCommunity = { cfv ->
                            account?.also { acct ->
                                communityViewModel.followCommunity(
                                    form = FollowCommunity(
                                        community_id = cfv.community.id,
                                        follow = cfv.subscribed == SubscribedType.NotSubscribed,
                                        auth = acct.jwt,
                                    ),
                                )
                            }
                        },
                        modifier = Modifier.padding(it)
                    )
                }

                else -> {}
            }

            when (val postsRes = communityViewModel.postsRes) {
                ApiState.Empty -> ApiEmptyText()
                is ApiState.Failure -> ApiErrorText(postsRes.msg)
                // TODO
                ApiState.Loading -> CircularProgressIndicator()
                is ApiState.Success -> {
                    PostListings(
                        posts = postsRes.data.posts,
                        onUpvoteClick = { postView ->
                            account?.also { acct ->
                                communityViewModel.likePost(
                                    form = CreatePostLike(
                                        post_id = postView.post.id,
                                        score = newVote(
                                            currentVote = postView.my_vote,
                                            voteType = VoteType.Upvote,
                                        ),
                                        auth = acct.jwt,
                                    ),
                                )
                            }
                        },
                        onDownvoteClick = { postView ->
                            account?.also { acct ->
                                communityViewModel.likePost(
                                    form = CreatePostLike(
                                        post_id = postView.post.id,
                                        score = newVote(
                                            currentVote = postView.my_vote,
                                            voteType = VoteType.Downvote,
                                        ),
                                        auth = acct.jwt,
                                    ),
                                )
                            }
                        },
                        onPostClick = { postView ->
                            navController.navigate(route = "post/${postView.post.id}")
                        },
                        onSaveClick = { postView ->
                            account?.also { acct ->
                                communityViewModel.savePost(
                                    form = SavePost(
                                        post_id = postView.post.id,
                                        save = !postView.saved,
                                        auth = acct.jwt,
                                    ),
                                )
                            }
                        },
                        onEditPostClick = { postView ->
                            postEditViewModel.initialize(postView)
                            navController.navigate("postEdit")
                        },
                        onDeletePostClick = { postView ->
                            account?.also { acct ->
                                communityViewModel.deletePost(
                                    DeletePost(
                                        post_id = postView.post.id,
                                        deleted = !postView.post.deleted,
                                        auth = acct.jwt,
                                    ),
                                )
                            }
                        },
                        onReportClick = { postView ->
                            navController.navigate("postReport/${postView.post.id}")
                        },
                        onCommunityClick = { community ->
                            navController.navigate(route = "community/${community.id}")
                        },
                        onPersonClick = { personId ->
                            navController.navigate(route = "profile/$personId")
                        },
                        onBlockCommunityClick = {
                            when (val communityRes = communityViewModel.communityRes) {
                                is ApiState.Success -> {
                                    account?.also { acct ->
                                        communityViewModel.blockCommunity(
                                            form = BlockCommunity(
                                                community_id = communityRes.data.community_view.community.id,
                                                block = !communityRes.data.community_view.blocked,
                                                auth = acct.jwt,
                                            ),
                                            ctx = ctx,
                                        )
                                    }
                                }

                                else -> {}
                            }
                        },
                        onBlockCreatorClick = { person ->
                            account?.also { acct ->
                                communityViewModel.blockPerson(
                                    form = BlockPerson(
                                        person_id = person.id,
                                        block = true,
                                        auth = acct.jwt,
                                    ),
                                    ctx = ctx,
                                )
                            }
                        },
                        onSwipeRefresh = {
                            when (val communityRes = communityViewModel.communityRes) {
                                is ApiState.Success -> {
                                    communityViewModel.resetPage()
                                    communityViewModel.getPosts(
                                        form =
                                        GetPosts(
                                            community_id = communityRes.data.community_view.community.id,
                                            page = communityViewModel.page,
                                            sort = communityViewModel.sortType,
                                            auth = account?.jwt,
                                        ),
                                    )
                                }

                                else -> {}
                            }
                        },
                        // TODO
                        loading = false,
                        isScrolledToEnd = {
                            when (val communityRes = communityViewModel.communityRes) {
                                is ApiState.Success -> {
                                    communityViewModel.nextPage()
                                    communityViewModel.appendPosts(
                                        form =
                                        GetPosts(
                                            community_id = communityRes.data.community_view.community.id,
                                            page = communityViewModel.page,
                                            sort = communityViewModel.sortType,
                                            auth = account?.jwt,
                                        ),
                                    )
                                }

                                else -> {}
                            }
                        },
                        account = account,
                        showCommunityName = false,
                        padding = it,
                        listState = postListState,
                        postViewMode = getPostViewMode(appSettingsViewModel),
                        enableDownVotes = siteViewModel.enableDownvotes(),
                        showAvatar =  siteViewModel.showAvatar(),
                        showVotingArrowsInListView = showVotingArrowsInListView,
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            when (val communityRes = communityViewModel.communityRes) {
                is ApiState.Success -> {
                    FloatingActionButton(
                        onClick = {
                            account?.also {
                                communityListViewModel.selectCommunity(communityRes.data.community_view.community)
                                navController.navigate("createPost")
                            }
                        },
                    ) { Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = stringResource(R.string.floating_createPost),
                )
                    }
                }

                else -> {}
            }
        },
        bottomBar = {
            BottomAppBarAll(
                showBottomNav = appSettingsViewModel.appSettings.value?.showBottomNav,
                screen = "communityList",
                unreadCount = siteViewModel.getUnreadCountTotal(),
                onClickProfile = {
                    account?.id?.also {
                        navController.navigate(route = "profile/$it")
                    } ?: run {
                        loginFirstToast(ctx)
                    }
                },
                onClickInbox = {
                    account?.also {
                        navController.navigate(route = "inbox")
                    } ?: run {
                        loginFirstToast(ctx)
                    }
                },
                onClickSaved = {
                    account?.id?.also {
                        navController.navigate(route = "profile/$it?saved=${true}")
                    } ?: run {
                        loginFirstToast(ctx)
                    }
                },
                navController = navController,
            )
        },
    )
}
