package com.jerboa.ui.components.home

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.jerboa.R
import com.jerboa.VoteType
import com.jerboa.api.ApiState
import com.jerboa.closeDrawer
import com.jerboa.datatypes.types.BlockCommunity
import com.jerboa.datatypes.types.BlockPerson
import com.jerboa.datatypes.types.CreatePostLike
import com.jerboa.datatypes.types.DeletePost
import com.jerboa.datatypes.types.GetPosts
import com.jerboa.datatypes.types.SavePost
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.fetchInitialData
import com.jerboa.loginFirstToast
import com.jerboa.newVote
import com.jerboa.scrollToTop
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.BottomAppBarAll
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.getPostViewMode
import com.jerboa.ui.components.post.PostListings
import com.jerboa.ui.components.post.edit.PostEditViewModel
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeActivity(
    navController: NavController,
    homeViewModel: HomeViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    postEditViewModel: PostEditViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    showVotingArrowsInListView: Boolean,
) {
    Log.d("jerboa", "got to home activity")

    val scope = rememberCoroutineScope()
    val postListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                content = {
                    MainDrawer(
                        siteViewModel = siteViewModel,
                        navController = navController,
                        accountViewModel = accountViewModel,
                        homeViewModel = homeViewModel,
                        scope = scope,
                        drawerState = drawerState,
                        ctx = ctx,
                    )
                },
            )
        },
        content = {
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    MainTopBar(
                        scope = scope,
                        postListState = postListState,
                        drawerState = drawerState,
                        homeViewModel = homeViewModel,
                        appSettingsViewModel = appSettingsViewModel,
                        account = account,
                        navController = navController,
                        scrollBehavior = scrollBehavior,
                    )
                },
                content = { padding ->
                    MainPostListingsContent(
                        padding = padding,
                        homeViewModel = homeViewModel,
                        siteViewModel = siteViewModel,
                        postEditViewModel = postEditViewModel,
                        appSettingsViewModel = appSettingsViewModel,
                        account = account,
                        ctx = ctx,
                        navController = navController,
                        postListState = postListState,
                        showVotingArrowsInListView = showVotingArrowsInListView,
                    )
                },
                floatingActionButtonPosition = FabPosition.End,
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            account?.also {
                                navController.navigate("createPost")
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
                bottomBar = {
                    BottomAppBarAll(
                        showBottomNav = appSettingsViewModel.appSettings.value?.showBottomNav,
                        screen = "home",
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
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainPostListingsContent(
    homeViewModel: HomeViewModel,
    siteViewModel: SiteViewModel,
    postEditViewModel: PostEditViewModel,
    account: Account?,
    ctx: Context,
    navController: NavController,
    padding: PaddingValues,
    postListState: LazyListState,
    appSettingsViewModel: AppSettingsViewModel,
    showVotingArrowsInListView: Boolean,
) {
    when (val siteRes = siteViewModel.siteRes) {
        ApiState.Loading ->
            LoadingBar(padding)

        ApiState.Empty -> ApiEmptyText()
        is ApiState.Failure -> ApiErrorText(siteRes.msg)
        is ApiState.Success -> {
            // TODO can be removed with 0.18.0 release
            if (siteRes.data.taglines !== null) {
                Taglines(siteRes.data.taglines)
            }
        }
    }

    val loading = homeViewModel.postsRes == ApiState.Loading || homeViewModel.fetchingMore

    val pullRefreshState = rememberPullRefreshState(
        refreshing = loading,
        onRefresh = {
            homeViewModel.resetPage()
            homeViewModel.getPosts(
                GetPosts(
                    page = homeViewModel.page,
                    sort = homeViewModel.sortType,
                    type_ = homeViewModel.listingType,
                    auth = account?.jwt,
                ),
            )
        },
    )

    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        PullRefreshIndicator(loading, pullRefreshState, Modifier.align(Alignment.TopCenter))
        // Can't be in ApiState.Loading, because of infinite scrolling
        if (loading) {
            LoadingBar(padding = padding)
        }
        when (val postsRes = homeViewModel.postsRes) {
            ApiState.Empty -> ApiEmptyText()
            is ApiState.Failure -> ApiErrorText(postsRes.msg)
            is ApiState.Success -> {
                PostListings(
                    listState = postListState,
                    padding = padding,
                    posts = postsRes.data.posts,
                    postViewMode = getPostViewMode(appSettingsViewModel),
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
                        navController.navigate(route = "post/${postView.post.id}")
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
                        postEditViewModel.initialize(postView)
                        navController.navigate("postEdit")
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
                        navController.navigate("postReport/${postView.post.id}")
                    },
                    onCommunityClick = { community ->
                        navController.navigate(route = "community/${community.id}")
                    },
                    onPersonClick = { personId ->
                        navController.navigate(route = "profile/$personId")
                    },
                    isScrolledToEnd = {
                        homeViewModel.nextPage()
                        homeViewModel.appendPosts(
                            GetPosts(
                                page = homeViewModel.page,
                                sort = homeViewModel.sortType,
                                type_ = homeViewModel.listingType,
                                auth = account?.jwt,
                            ),
                        )
                    },
                    account = account,
                    enableDownVotes = siteViewModel.enableDownvotes(),
                    showAvatar = siteViewModel.showAvatar(),
                    showVotingArrowsInListView = showVotingArrowsInListView,
                )
            }

            else -> {}
        }
    }
}

@Composable
fun MainDrawer(
    siteViewModel: SiteViewModel,
    navController: NavController,
    accountViewModel: AccountViewModel,
    homeViewModel: HomeViewModel,
    scope: CoroutineScope,
    ctx: Context,
    drawerState: DrawerState,
) {
    val accounts = accountViewModel.allAccounts.value
    val account = getCurrentAccount(accountViewModel)

    Drawer(
        siteRes = siteViewModel.siteRes,
        unreadCount = siteViewModel.getUnreadCountTotal(),
        accountViewModel = accountViewModel,
        navController = navController,
        isOpen = drawerState.isOpen,
        onSwitchAccountClick = { acct ->
            accountViewModel.removeCurrent()
            accountViewModel.setCurrent(acct.id)

            fetchInitialData(
                account = acct,
                siteViewModel = siteViewModel,
                homeViewModel = homeViewModel,
            )

            closeDrawer(scope, drawerState)
        },
        onSignOutClick = {
            accounts?.also { accts ->
                account?.also {
                    accountViewModel.delete(it)
                    val updatedList = accts.toMutableList()
                    updatedList.remove(it)

                    if (updatedList.isNotEmpty()) {
                        accountViewModel.setCurrent(updatedList[0].id)
                    }
                    fetchInitialData(
                        account = updatedList.getOrNull(0),
                        siteViewModel = siteViewModel,
                        homeViewModel = homeViewModel,
                    )

                    closeDrawer(scope, drawerState)
                }
            }
        },
        onClickListingType = { listingType ->
            homeViewModel.updateListingType(listingType)
            homeViewModel.resetPage()
            homeViewModel.getPosts(
                GetPosts(
                    page = homeViewModel.page,
                    sort = homeViewModel.sortType,
                    type_ = homeViewModel.listingType,
                    auth = account?.jwt,
                ),
            )
            closeDrawer(scope, drawerState)
        },
        onCommunityClick = { community ->
            navController.navigate(route = "community/${community.id}")
            closeDrawer(scope, drawerState)
        },
        onClickProfile = {
            account?.id?.also {
                navController.navigate(route = "profile/$it")
                closeDrawer(scope, drawerState)
            }
        },
        onClickSaved = {
            account?.id?.also {
                navController.navigate(route = "profile/$it?saved=${true}")
                closeDrawer(scope, drawerState)
            }
        },
        onClickInbox = {
            account?.also {
                navController.navigate(route = "inbox")
            } ?: run {
                loginFirstToast(ctx)
            }
            closeDrawer(scope, drawerState)
        },
        onClickSettings = {
            navController.navigate(route = "settings")
            closeDrawer(scope, drawerState)
        },
        onClickCommunities = {
            navController.navigate(route = "communityList")
            closeDrawer(scope, drawerState)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    scope: CoroutineScope,
    postListState: LazyListState,
    drawerState: DrawerState,
    homeViewModel: HomeViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    account: Account?,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    Column {
        HomeHeader(
            scope = scope,
            scrollBehavior = scrollBehavior,
            drawerState = drawerState,
            navController = navController,
            selectedSortType = homeViewModel.sortType,
            selectedListingType = homeViewModel.listingType,
            selectedPostViewMode = getPostViewMode(appSettingsViewModel),
            onClickSortType = { sortType ->
                scrollToTop(scope, postListState)
                homeViewModel.updateSortType(sortType)
                homeViewModel.resetPage()
                homeViewModel.getPosts(
                    GetPosts(
                        page = homeViewModel.page,
                        sort = homeViewModel.sortType,
                        type_ = homeViewModel.listingType,
                        auth = account?.jwt,
                    ),
                )
            },
            onClickListingType = { listingType ->
                scrollToTop(scope, postListState)
                homeViewModel.updateListingType(listingType)
                homeViewModel.resetPage()
                homeViewModel.getPosts(
                    GetPosts(
                        page = homeViewModel.page,
                        sort = homeViewModel.sortType,
                        type_ = homeViewModel.listingType,
                        auth = account?.jwt,
                    ),
                )
            },
            onClickPostViewMode = {
                appSettingsViewModel.updatedPostViewMode(it.ordinal)
            },
            onClickRefresh = {
                scrollToTop(scope, postListState)
                homeViewModel.resetPage()
                homeViewModel.getPosts(
                    GetPosts(
                        page = homeViewModel.page,
                        sort = homeViewModel.sortType,
                        type_ = homeViewModel.listingType,
                        auth = account?.jwt,
                    ),
                )
            },
        )
    }
}
