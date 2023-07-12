package com.jerboa.ui.components.community

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import arrow.core.Either
import com.jerboa.R
import com.jerboa.VoteType
import com.jerboa.api.ApiState
import com.jerboa.datatypes.types.BlockCommunity
import com.jerboa.datatypes.types.BlockPerson
import com.jerboa.datatypes.types.CommunityId
import com.jerboa.datatypes.types.CreatePostLike
import com.jerboa.datatypes.types.DeletePost
import com.jerboa.datatypes.types.FollowCommunity
import com.jerboa.datatypes.types.GetCommunity
import com.jerboa.datatypes.types.GetPosts
import com.jerboa.datatypes.types.GetSite
import com.jerboa.datatypes.types.PostView
import com.jerboa.datatypes.types.SavePost
import com.jerboa.datatypes.types.SortType
import com.jerboa.datatypes.types.SubscribedType
import com.jerboa.isLoading
import com.jerboa.isRefreshing
import com.jerboa.model.AccountViewModel
import com.jerboa.model.AppSettingsViewModel
import com.jerboa.model.CommunityViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.newVote
import com.jerboa.scrollToTop
import com.jerboa.shareLink
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.ConsumeReturn
import com.jerboa.ui.components.common.CreatePostDeps
import com.jerboa.ui.components.common.InitializeRoute
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.PostEditDeps
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.getPostViewMode
import com.jerboa.ui.components.common.rootChannel
import com.jerboa.ui.components.common.toCommunity
import com.jerboa.ui.components.common.toCreatePost
import com.jerboa.ui.components.common.toPost
import com.jerboa.ui.components.common.toPostEdit
import com.jerboa.ui.components.common.toPostReport
import com.jerboa.ui.components.common.toProfile
import com.jerboa.ui.components.common.toView
import com.jerboa.ui.components.post.PostListings
import com.jerboa.ui.components.post.edit.PostEditReturn
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CommunityActivity(
    communityArg: Either<CommunityId, String>,
    navController: NavController,
    communityViewModel: CommunityViewModel,
    siteViewModel: SiteViewModel,
    accountViewModel: AccountViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    showVotingArrowsInListView: Boolean,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: Boolean,
) {
    Log.d("jerboa", "got to community activity")
    val transferCreatePostDepsViaRoot = navController.rootChannel<CreatePostDeps>()
    val transferPostEditDepsViaRoot = navController.rootChannel<PostEditDeps>()

    val scope = rememberCoroutineScope()
    val postListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    navController.ConsumeReturn<PostView>(PostEditReturn.POST_VIEW) { pv ->
        if (communityViewModel.initialized) communityViewModel.updatePost(pv)
    }

    InitializeRoute(communityViewModel) {
        val communityId = communityArg.fold({ it }, { null })
        val communityName = communityArg.fold({ null }, { it })

        communityViewModel.resetPage()
        account?.let {
            communityViewModel.updateSortType(SortType.values().getOrElse(account.defaultSortType) { siteViewModel.sortType })
        }
        communityViewModel.getCommunity(
            form = GetCommunity(
                id = communityId,
                name = communityName,
                auth = account?.jwt,
            ),
        )
        communityViewModel.getPosts(
            form =
            GetPosts(
                community_id = communityId,
                community_name = communityName,
                page = communityViewModel.page,
                sort = communityViewModel.sortType,
                auth = account?.jwt,
            ),
        )
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = communityViewModel.postsRes.isRefreshing(),
        onRefresh = {
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
                        ApiState.Refreshing,
                    )
                }

                else -> {}
            }
        },
        // Needs to be lower else it can hide behind the top bar
        refreshingOffset = 150.dp,
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                when (val communityRes = communityViewModel.communityRes) {
                    ApiState.Empty -> ApiEmptyText()
                    is ApiState.Failure -> ApiErrorText(communityRes.msg)
                    ApiState.Loading -> {
                        LoadingBar()
                    }

                    is ApiState.Success -> {
                        val communityId = communityRes.data.community_view.community.id
                        CommunityHeader(
                            scrollBehavior = scrollBehavior,
                            communityName = communityRes.data.community_view.community.name,
                            selectedSortType = communityViewModel.sortType,
                            onClickRefresh = {
                                scrollToTop(scope, postListState)
                                communityViewModel.resetPage()
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
                    else -> {}
                }
            }
        },
        content = { padding ->
            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                // zIndex needed bc some elements of a post get drawn above it.
                PullRefreshIndicator(communityViewModel.postsRes.isRefreshing(), pullRefreshState, Modifier.align(Alignment.TopCenter).zIndex(100F))
                // Can't be in ApiState.Loading, because of infinite scrolling
                if (communityViewModel.postsRes.isLoading()) {
                    LoadingBar(padding = padding)
                }
                when (val postsRes = communityViewModel.postsRes) {
                    ApiState.Empty -> ApiEmptyText()
                    is ApiState.Failure -> ApiErrorText(postsRes.msg)
                    is ApiState.Holder -> {
                        PostListings(
                            posts = postsRes.data.posts.toImmutableList(),
                            contentAboveListings = {
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
                                                        onSuccess = {
                                                            siteViewModel.getSite(
                                                                form = GetSite(
                                                                    auth = acct.jwt,
                                                                ),
                                                            )
                                                        },
                                                    )
                                                }
                                            },
                                            blurNSFW = blurNSFW,
                                        )
                                    }

                                    else -> {}
                                }
                            },
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
                                navController.toPost(id = postView.post.id)
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
                                navController.toPostEdit(
                                    channel = transferPostEditDepsViaRoot,
                                    postView = postView,
                                )
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
                                navController.toPostReport(id = postView.post.id)
                            },
                            onCommunityClick = { community ->
                                navController.toCommunity(id = community.id)
                            },
                            onPersonClick = { personId ->
                                navController.toProfile(id = personId)
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
                            onShareClick = { url ->
                                shareLink(url, ctx)
                            },
                            isScrolledToEnd = {
                                when (val communityRes = communityViewModel.communityRes) {
                                    is ApiState.Success -> {
                                        communityViewModel.appendPosts(
                                            communityRes.data.community_view.community.id,
                                            account?.jwt,
                                        )
                                    }

                                    else -> {}
                                }
                            },
                            account = account,
                            showCommunityName = false,
                            padding = padding,
                            listState = postListState,
                            postViewMode = getPostViewMode(appSettingsViewModel),
                            enableDownVotes = siteViewModel.enableDownvotes(),
                            showAvatar = siteViewModel.showAvatar(),
                            showVotingArrowsInListView = showVotingArrowsInListView,
                            useCustomTabs = useCustomTabs,
                            usePrivateTabs = usePrivateTabs,
                            blurNSFW = blurNSFW,
                            openImageViewer = navController::toView,
                        )
                    }
                    else -> {}
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
                                navController.toCreatePost(
                                    channel = transferCreatePostDepsViaRoot,
                                    community = communityRes.data.community_view.community,
                                )
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = stringResource(R.string.floating_createPost),
                        )
                    }
                }

                else -> {}
            }
        },
    )
}
