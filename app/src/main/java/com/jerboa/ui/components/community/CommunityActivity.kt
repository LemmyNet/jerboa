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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import arrow.core.Either
import com.jerboa.ConsumeReturn
import com.jerboa.CreatePostDeps
import com.jerboa.JerboaAppState
import com.jerboa.PostEditDeps
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
import com.jerboa.datatypes.types.MarkPostAsRead
import com.jerboa.datatypes.types.PostView
import com.jerboa.datatypes.types.SavePost
import com.jerboa.datatypes.types.SubscribedType
import com.jerboa.db.entity.getJWT
import com.jerboa.db.entity.isAnon
import com.jerboa.feat.doIfReadyElseDisplayInfo
import com.jerboa.hostName
import com.jerboa.model.AccountViewModel
import com.jerboa.model.AppSettingsViewModel
import com.jerboa.model.CommunityViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.newVote
import com.jerboa.rootChannel
import com.jerboa.scrollToTop
import com.jerboa.shareLink
import com.jerboa.toEnumSafe
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.getPostViewMode
import com.jerboa.ui.components.common.isLoading
import com.jerboa.ui.components.common.isRefreshing
import com.jerboa.ui.components.post.PostListings
import com.jerboa.ui.components.post.PostViewReturn
import com.jerboa.ui.components.post.edit.PostEditReturn
import com.jerboa.util.InitializeRoute
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CommunityActivity(
    communityArg: Either<CommunityId, String>,
    appState: JerboaAppState,
    communityViewModel: CommunityViewModel,
    siteViewModel: SiteViewModel,
    accountViewModel: AccountViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    showVotingArrowsInListView: Boolean,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: Boolean,
    showPostLinkPreviews: Boolean,
    markAsReadOnScroll: Boolean,
    postActionbarMode: Int,
) {
    Log.d("jerboa", "got to community activity")
    val transferCreatePostDepsViaRoot = appState.rootChannel<CreatePostDeps>()
    val transferPostEditDepsViaRoot = appState.rootChannel<PostEditDeps>()

    val scope = rememberCoroutineScope()
    val postListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    appState.ConsumeReturn<PostView>(PostEditReturn.POST_VIEW) { pv ->
        if (communityViewModel.initialized) communityViewModel.updatePost(pv)
    }

    appState.ConsumeReturn<PostView>(PostViewReturn.POST_VIEW) { pv ->
        if (communityViewModel.initialized) communityViewModel.updatePost(pv)
    }

    LaunchedEffect(account) {
        if (!account.isAnon()) {
            communityViewModel.updateSortType(account.defaultSortType.toEnumSafe())
        }
    }

    InitializeRoute(communityViewModel) {
        val communityId = communityArg.fold({ it }, { null })
        val communityName = communityArg.fold({ null }, { it })

        communityViewModel.resetPage()

        communityViewModel.getCommunity(
            form = GetCommunity(
                id = communityId,
                name = communityName,
                auth = account.getJWT(),
            ),
        )
        communityViewModel.getPosts(
            form =
            GetPosts(
                community_id = communityId,
                community_name = communityName,
                page = communityViewModel.page,
                sort = communityViewModel.sortType,
                auth = account.getJWT(),
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
                            auth = account.getJWT(),
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
        snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
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
                        val instance = hostName(communityRes.data.community_view.community.actor_id)
                        val communityName = communityRes.data.community_view.community.name +
                            if (instance != null) "@$instance" else ""
                        CommunityHeader(
                            scrollBehavior = scrollBehavior,
                            communityName = communityName,
                            selectedSortType = communityViewModel.sortType,
                            onClickRefresh = {
                                scrollToTop(scope, postListState)
                                communityViewModel.resetPage()
                                communityViewModel.getPosts(
                                    GetPosts(
                                        community_id = communityId,
                                        page = communityViewModel.page,
                                        sort = communityViewModel.sortType,
                                        auth = account.getJWT(),
                                    ),
                                )
                            },
                            onClickPostViewMode = {
                                appSettingsViewModel.updatedPostViewMode(it.ordinal)
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
                                        auth = account.getJWT(),
                                    ),
                                )
                            },
                            onBlockCommunityClick = {
                                account.doIfReadyElseDisplayInfo(
                                    appState,
                                    ctx,
                                    snackbarHostState,
                                    scope,
                                    siteViewModel,
                                    accountViewModel,
                                ) {
                                    communityViewModel.blockCommunity(
                                        BlockCommunity(
                                            community_id = communityId,
                                            auth = it.jwt,
                                            block = !communityRes.data.community_view.blocked,
                                        ),
                                        ctx = ctx,
                                    )
                                }
                            },
                            onClickCommunityInfo = appState::toCommunitySideBar,
                            onClickBack = appState::navigateUp,
                            selectedPostViewMode = getPostViewMode(appSettingsViewModel),
                        )
                    }
                    else -> {}
                }
            }
        },
        content = { padding ->
            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                // zIndex needed bc some elements of a post get drawn above it.
                PullRefreshIndicator(
                    communityViewModel.postsRes.isRefreshing(),
                    pullRefreshState,
                    Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(100F),
                )
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
                                                account.doIfReadyElseDisplayInfo(
                                                    appState,
                                                    ctx,
                                                    snackbarHostState,
                                                    scope,
                                                    siteViewModel,
                                                    accountViewModel,
                                                ) {
                                                    communityViewModel.followCommunity(
                                                        form = FollowCommunity(
                                                            community_id = cfv.community.id,
                                                            follow = cfv.subscribed == SubscribedType.NotSubscribed,
                                                            auth = it.jwt,
                                                        ),
                                                        onSuccess = {
                                                            siteViewModel.getSite(
                                                                form = GetSite(
                                                                    auth = it.jwt,
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
                                account.doIfReadyElseDisplayInfo(
                                    appState,
                                    ctx,
                                    snackbarHostState,
                                    scope,
                                    siteViewModel,
                                    accountViewModel,
                                ) {
                                    communityViewModel.likePost(
                                        form = CreatePostLike(
                                            post_id = postView.post.id,
                                            score = newVote(
                                                currentVote = postView.my_vote,
                                                voteType = VoteType.Upvote,
                                            ),
                                            auth = it.jwt,
                                        ),
                                    )
                                }
                            },
                            onDownvoteClick = { postView ->
                                account.doIfReadyElseDisplayInfo(
                                    appState,
                                    ctx,
                                    snackbarHostState,
                                    scope,
                                    siteViewModel,
                                    accountViewModel,
                                ) {
                                    communityViewModel.likePost(
                                        form = CreatePostLike(
                                            post_id = postView.post.id,
                                            score = newVote(
                                                currentVote = postView.my_vote,
                                                voteType = VoteType.Downvote,
                                            ),
                                            auth = it.jwt,
                                        ),
                                    )
                                }
                            },
                            onPostClick = { postView ->
                                appState.toPost(id = postView.post.id)
                            },
                            onSaveClick = { postView ->
                                account.doIfReadyElseDisplayInfo(
                                    appState,
                                    ctx,
                                    snackbarHostState,
                                    scope,
                                    siteViewModel,
                                    accountViewModel,
                                ) {
                                    communityViewModel.savePost(
                                        form = SavePost(
                                            post_id = postView.post.id,
                                            save = !postView.saved,
                                            auth = it.jwt,
                                        ),
                                    )
                                }
                            },
                            onEditPostClick = { postView ->
                                appState.toPostEdit(
                                    channel = transferPostEditDepsViaRoot,
                                    postView = postView,
                                )
                            },
                            onDeletePostClick = { postView ->
                                account.doIfReadyElseDisplayInfo(
                                    appState,
                                    ctx,
                                    snackbarHostState,
                                    scope,
                                    siteViewModel,
                                    accountViewModel,
                                ) {
                                    communityViewModel.deletePost(
                                        DeletePost(
                                            post_id = postView.post.id,
                                            deleted = !postView.post.deleted,
                                            auth = it.jwt,
                                        ),
                                    )
                                }
                            },
                            onReportClick = { postView ->
                                appState.toPostReport(id = postView.post.id)
                            },
                            onCommunityClick = { community ->
                                appState.toCommunity(id = community.id)
                            },
                            onPersonClick = { personId ->
                                appState.toProfile(id = personId)
                            },
                            onBlockCommunityClick = {
                                when (val communityRes = communityViewModel.communityRes) {
                                    is ApiState.Success -> {
                                        account.doIfReadyElseDisplayInfo(
                                            appState,
                                            ctx,
                                            snackbarHostState,
                                            scope,
                                            siteViewModel,
                                            accountViewModel,
                                        ) {
                                            communityViewModel.blockCommunity(
                                                form = BlockCommunity(
                                                    community_id = communityRes.data.community_view.community.id,
                                                    block = !communityRes.data.community_view.blocked,
                                                    auth = it.jwt,
                                                ),
                                                ctx = ctx,
                                            )
                                        }
                                    }

                                    else -> {}
                                }
                            },
                            onBlockCreatorClick = { person ->
                                account.doIfReadyElseDisplayInfo(
                                    appState,
                                    ctx,
                                    snackbarHostState,
                                    scope,
                                    siteViewModel,
                                    accountViewModel,
                                ) {
                                    communityViewModel.blockPerson(
                                        form = BlockPerson(
                                            person_id = person.id,
                                            block = true,
                                            auth = it.jwt,
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
                                            account.getJWT(),
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
                            showPostLinkPreviews = showPostLinkPreviews,
                            openImageViewer = appState::toView,
                            openLink = appState::openLink,
                            markAsReadOnScroll = markAsReadOnScroll,
                            onMarkAsRead = { postView ->
                                if (!account.isAnon() && !postView.read) {
                                    communityViewModel.markPostAsRead(
                                        MarkPostAsRead(
                                            post_id = postView.post.id,
                                            read = true,
                                            auth = account.jwt,
                                        ),
                                        appState,
                                    )
                                }
                            },
                            showIfRead = true,
                            showScores = siteViewModel.showScores(),
                            postActionbarMode = postActionbarMode,
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
                            account.doIfReadyElseDisplayInfo(
                                appState,
                                ctx,
                                snackbarHostState,
                                scope,
                                siteViewModel,
                                accountViewModel,
                                loginAsToast = false,
                            ) {
                                appState.toCreatePost(
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
