package com.jerboa.ui.components.home

import android.content.Context
import android.util.Log
import androidx.activity.compose.ReportDrawn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.jerboa.ConsumeReturn
import com.jerboa.CreatePostDeps
import com.jerboa.JerboaAppState
import com.jerboa.PostEditDeps
import com.jerboa.R
import com.jerboa.VoteType
import com.jerboa.api.ApiState
import com.jerboa.datatypes.types.BlockCommunity
import com.jerboa.datatypes.types.BlockPerson
import com.jerboa.datatypes.types.CreatePostLike
import com.jerboa.datatypes.types.DeletePost
import com.jerboa.datatypes.types.PostView
import com.jerboa.datatypes.types.SavePost
import com.jerboa.datatypes.types.Tagline
import com.jerboa.db.entity.Account
import com.jerboa.loginFirstToast
import com.jerboa.model.AccountViewModel
import com.jerboa.model.AppSettingsViewModel
import com.jerboa.model.HomeViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.newVote
import com.jerboa.rootChannel
import com.jerboa.scrollToTop
import com.jerboa.shareLink
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.ApiErrorToast
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.getPostViewMode
import com.jerboa.ui.components.common.isLoading
import com.jerboa.ui.components.common.isRefreshing
import com.jerboa.ui.components.post.PostListings
import com.jerboa.ui.components.post.edit.PostEditReturn
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun HomeActivity(
    appState: JerboaAppState,
    homeViewModel: HomeViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    showVotingArrowsInListView: Boolean,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    drawerState: DrawerState,
    blurNSFW: Boolean,
    showPostLinkPreviews: Boolean,
) {
    Log.d("jerboa", "got to home activity")
    val transferCreatePostDepsViaRoot = appState.rootChannel<CreatePostDeps>()

    val scope = rememberCoroutineScope()
    val postListState = homeViewModel.lazyListState
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)

    appState.ConsumeReturn<PostView>(PostEditReturn.POST_VIEW) { pv ->
        if (homeViewModel.initialized) homeViewModel.updatePost(pv)
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .semantics { testTagsAsResourceId = true },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MainTopBar(
                openDrawer = {
                    scope.launch {
                        drawerState.open()
                    }
                },
                scrollToTop = {
                    scrollToTop(scope, postListState)
                },
                homeViewModel = homeViewModel,
                appSettingsViewModel = appSettingsViewModel,
                account = account,
                scrollBehavior = scrollBehavior,
                onClickSiteInfo = appState::toSiteSideBar,
            )
        },
        content = { padding ->
            MainPostListingsContent(
                padding = padding,
                homeViewModel = homeViewModel,
                siteViewModel = siteViewModel,
                appSettingsViewModel = appSettingsViewModel,
                account = account,
                ctx = ctx,
                appState = appState,
                postListState = postListState,
                showVotingArrowsInListView = showVotingArrowsInListView,
                useCustomTabs = useCustomTabs,
                usePrivateTabs = usePrivateTabs,
                blurNSFW = blurNSFW,
                showPostLinkPreviews = showPostLinkPreviews,
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    account?.also {
                        appState.toCreatePost(
                            channel = transferCreatePostDepsViaRoot,
                            community = null,
                        )
                    } ?: run {
                        loginFirstToast(ctx)
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = stringResource(R.string.floating_createPost),
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainPostListingsContent(
    homeViewModel: HomeViewModel,
    siteViewModel: SiteViewModel,
    account: Account?,
    ctx: Context,
    appState: JerboaAppState,
    padding: PaddingValues,
    postListState: LazyListState,
    appSettingsViewModel: AppSettingsViewModel,
    showVotingArrowsInListView: Boolean,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: Boolean,
    showPostLinkPreviews: Boolean,
) {
    val transferPostEditDepsViaRoot = appState.rootChannel<PostEditDeps>()

    var taglines: List<Tagline>? = null
    when (val siteRes = siteViewModel.siteRes) {
        ApiState.Loading -> LoadingBar(padding)
        ApiState.Empty -> ApiEmptyText()
        is ApiState.Failure -> ApiErrorText(siteRes.msg)
        is ApiState.Success -> {
            taglines = siteRes.data.taglines
        }
        else -> {}
    }

    ReportDrawn()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = homeViewModel.postsRes.isRefreshing(),
        onRefresh = {
            homeViewModel.refreshPosts(account)
        },
        // Needs to be lower else it can hide behind the top bar
        refreshingOffset = 150.dp,
    )

    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        // zIndex needed bc some elements of a post get drawn above it.
        PullRefreshIndicator(
            homeViewModel.postsRes.isRefreshing(),
            pullRefreshState,
            Modifier
                .align(Alignment.TopCenter)
                .zIndex(100f),
        )
        // Can't be in ApiState.Loading, because of infinite scrolling
        if (homeViewModel.postsRes.isLoading()) {
            LoadingBar(padding = padding)
        }

        val posts = when (val postsRes = homeViewModel.postsRes) {
            is ApiState.Failure -> {
                ApiErrorToast(postsRes.msg)
                persistentListOf()
            }
            is ApiState.Holder -> postsRes.data.posts.toImmutableList()
            else -> persistentListOf()
        }

        PostListings(
            listState = postListState,
            padding = padding,
            posts = posts,
            postViewMode = getPostViewMode(appSettingsViewModel),
            contentAboveListings = { if (taglines !== null) Taglines(taglines = taglines) },
            onUpvoteClick = { postView ->
                account?.also { acct ->
                    homeViewModel.likePost(
                        CreatePostLike(
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
                    homeViewModel.likePost(
                        CreatePostLike(
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
                appState.toPost(id = postView.post.id)
            },
            onSaveClick = { postView ->
                account?.also { acct ->
                    homeViewModel.savePost(
                        SavePost(
                            post_id = postView.post.id,
                            save = !postView.saved,
                            auth = acct.jwt,
                        ),
                    )
                }
            },
            onBlockCommunityClick = { community ->
                account?.also { acct ->
                    homeViewModel.blockCommunity(
                        BlockCommunity(
                            community_id = community.id,
                            auth = acct.jwt,
                            block = true,
                        ),
                        ctx = ctx,
                    )
                }
            },
            onBlockCreatorClick = { creator ->
                account?.also { acct ->
                    homeViewModel.blockPerson(
                        BlockPerson(
                            person_id = creator.id,
                            block = true,
                            auth = acct.jwt,
                        ),
                        ctx = ctx,
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
                account?.also { acct ->
                    homeViewModel.deletePost(
                        DeletePost(
                            post_id = postView.post.id,
                            deleted = !postView.post.deleted,
                            auth = acct.jwt,
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
            onShareClick = { url ->
                shareLink(url, ctx)
            },
            isScrolledToEnd = {
                homeViewModel.appendPosts(account?.jwt)
            },
            account = account,
            enableDownVotes = siteViewModel.enableDownvotes(),
            showAvatar = siteViewModel.showAvatar(),
            showVotingArrowsInListView = showVotingArrowsInListView,
            useCustomTabs = useCustomTabs,
            usePrivateTabs = usePrivateTabs,
            blurNSFW = blurNSFW,
            showPostLinkPreviews = showPostLinkPreviews,
            openImageViewer = appState::toView,
            openLink = appState::openLink,
            instantRead = homeViewModel.instantRead,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    scrollToTop: () -> Unit,
    openDrawer: () -> Unit,
    homeViewModel: HomeViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    account: Account?,
    onClickSiteInfo: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    Column {
        HomeHeader(
            openDrawer = openDrawer,
            scrollBehavior = scrollBehavior,
            selectedSortType = homeViewModel.sortType,
            selectedListingType = homeViewModel.listingType,
            selectedPostViewMode = getPostViewMode(appSettingsViewModel),
            onClickSortType = { sortType ->
                scrollToTop()
                homeViewModel.updateSortType(sortType)
                homeViewModel.resetPosts(account)
            },
            onClickListingType = { listingType ->
                scrollToTop()
                homeViewModel.updateListingType(listingType)
                homeViewModel.resetPosts(account)
            },
            onClickPostViewMode = {
                appSettingsViewModel.updatedPostViewMode(it.ordinal)
            },
            onClickRefresh = {
                scrollToTop()
                homeViewModel.resetPosts(account)
            },
            onClickSiteInfo = onClickSiteInfo,
        )
    }
}
