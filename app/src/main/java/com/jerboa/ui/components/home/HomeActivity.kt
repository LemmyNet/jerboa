package com.jerboa.ui.components.home

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.navigation.NavController
import com.jerboa.VoteType
import com.jerboa.closeDrawer
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.fetchInitialData
import com.jerboa.loginFirstToast
import com.jerboa.openLink
import com.jerboa.scrollToTop
import com.jerboa.ui.components.common.BottomAppBarAll
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.getPostViewMode
import com.jerboa.ui.components.post.PostListings
import com.jerboa.ui.components.post.edit.PostEditViewModel
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
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
                        ctx = ctx,
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
                        Icon(imageVector = Icons.Outlined.Add, contentDescription = "TODO")
                    }
                },
                bottomBar = {
                    BottomAppBarAll(
                        showBottomNav = appSettingsViewModel.appSettings.value?.showBottomNav,
                        screen = "home",
                        unreadCounts = homeViewModel.unreadCountResponse,
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
        modifier = Modifier.semantics { testTagsAsResourceId = true }
    )
}

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
    PostListings(
        listState = postListState,
        padding = padding,
        posts = homeViewModel.posts,
        taglines = siteViewModel.siteRes?.taglines,
        postViewMode = getPostViewMode(appSettingsViewModel),
        onUpvoteClick = { postView ->
            homeViewModel.likePost(
                voteType = VoteType.Upvote,
                postView = postView,
                account = account,
                ctx = ctx,
            )
        },
        onDownvoteClick = { postView ->
            homeViewModel.likePost(
                voteType = VoteType.Downvote,
                postView = postView,
                account = account,
                ctx = ctx,
            )
        },
        onPostClick = { postView ->
            navController.navigate(route = "post/${postView.post.id}")
        },
        onPostLinkClick = { url ->
            openLink(url, ctx, appSettingsViewModel.appSettings.value?.useCustomTabs ?: true)
        },
        onSaveClick = { postView ->
            account?.also { acct ->
                homeViewModel.savePost(
                    postView = postView,
                    account = acct,
                    ctx = ctx,
                )
            }
        },
        onBlockCommunityClick = {
            account?.also { acct ->
                homeViewModel.blockCommunity(
                    community = it,
                    account = acct,
                    ctx = ctx,
                )
            }
        },
        onBlockCreatorClick = {
            account?.also { acct ->
                homeViewModel.blockCreator(
                    creator = it,
                    account = acct,
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
                    postView = postView,
                    account = acct,
                    ctx = ctx,
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
        onSwipeRefresh = {
            homeViewModel.fetchPosts(
                account = account,
                clear = true,
                ctx = ctx,
            )
        },
        loading = homeViewModel.loading.value &&
            homeViewModel.page.value == 1 &&
            homeViewModel.posts.isNotEmpty(),
        isScrolledToEnd = {
            if (homeViewModel.posts.size > 0) {
                homeViewModel.fetchPosts(
                    account = account,
                    nextPage = true,
                    ctx = ctx,
                )
            }
        },
        account = account,
        showVotingArrowsInListView = showVotingArrowsInListView,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
        myUserInfo = siteViewModel.siteRes?.my_user,
        unreadCounts = homeViewModel.unreadCountResponse,
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
            homeViewModel.fetchPosts(
                account = account,
                clear = true,
                changeListingType = listingType,
                ctx = ctx,
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
    ctx: Context,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    Column {
        HomeHeader(
            scope = scope,
            scrollBehavior = scrollBehavior,
            drawerState = drawerState,
            navController = navController,
            selectedSortType = homeViewModel.sortType.value,
            selectedListingType = homeViewModel.listingType.value,
            selectedPostViewMode = getPostViewMode(appSettingsViewModel),
            onClickSortType = { sortType ->
                scrollToTop(scope, postListState)
                homeViewModel.fetchPosts(
                    account = account,
                    clear = true,
                    changeSortType = sortType,
                    ctx = ctx,
                )
            },
            onClickListingType = { listingType ->
                scrollToTop(scope, postListState)
                homeViewModel.fetchPosts(
                    account = account,
                    clear = true,
                    changeListingType = listingType,
                    ctx = ctx,
                )
            },
            onClickPostViewMode = {
                appSettingsViewModel.updatedPostViewMode(it.ordinal)
            },
            onClickRefresh = {
                scrollToTop(scope, postListState)
                homeViewModel.fetchPosts(
                    account = account,
                    clear = true,
                    ctx = ctx,
                )
            },
        )
        if (homeViewModel.loading.value) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}
