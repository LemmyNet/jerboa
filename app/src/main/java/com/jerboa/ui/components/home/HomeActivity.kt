package com.jerboa.ui.components.home

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jerboa.VoteType
import com.jerboa.closeDrawer
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.fetchInitialData
import com.jerboa.loginFirstToast
import com.jerboa.openLink
import com.jerboa.scrollToTop
import com.jerboa.ui.components.common.BottomAppBarAll
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.post.PostListings
import com.jerboa.ui.components.post.edit.PostEditViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun HomeActivity(
    navController: NavController,
    homeViewModel: HomeViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    postEditViewModel: PostEditViewModel
) {
    Log.d("jerboa", "got to home activity")

    val scope = rememberCoroutineScope()
    val postListState = rememberLazyListState()
    val scaffoldState = rememberScaffoldState()
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                MainTopBar(
                    scope = scope,
                    postListState = postListState,
                    scaffoldState = scaffoldState,
                    homeViewModel = homeViewModel,
                    account = account,
                    ctx = ctx,
                    navController = navController
                )
            },
            drawerShape = MaterialTheme.shapes.small,
            drawerElevation = 2.dp,
            drawerContent = {
                MainDrawer(
                    siteViewModel = siteViewModel,
                    navController = navController,
                    accountViewModel = accountViewModel,
                    homeViewModel = homeViewModel,
                    scope = scope,
                    scaffoldState = scaffoldState,
                    ctx = ctx
                )
            },
            content = {
                MainPostListingsContent(
                    padding = it,
                    homeViewModel = homeViewModel,
                    postEditViewModel = postEditViewModel,
                    account = account,
                    ctx = ctx,
                    navController = navController,
                    postListState = postListState
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
                    }
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "TODO")
                }
            },
            bottomBar = {
                BottomAppBarAll(
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
                    navController = navController
                )
            }
        )
    }
}

@Composable
fun MainPostListingsContent(
    homeViewModel: HomeViewModel,
    postEditViewModel: PostEditViewModel,
    account: Account?,
    ctx: Context,
    navController: NavController,
    padding: PaddingValues,
    postListState: LazyListState
) {
    PostListings(
        listState = postListState,
        padding = padding,
        posts = homeViewModel.posts,
        onUpvoteClick = { postView ->
            homeViewModel.likePost(
                voteType = VoteType.Upvote,
                postView = postView,
                account = account,
                ctx = ctx
            )
        },
        onDownvoteClick = { postView ->
            homeViewModel.likePost(
                voteType = VoteType.Downvote,
                postView = postView,
                account = account,
                ctx = ctx
            )
        },
        onPostClick = { postView ->
            navController.navigate(route = "post/${postView.post.id}")
        },
        onPostLinkClick = { url ->
            openLink(url, ctx)
        },
        onSaveClick = { postView ->
            account?.also { acct ->
                homeViewModel.savePost(
                    postView = postView,
                    account = acct,
                    ctx = ctx
                )
            }
        },
        onBlockCommunityClick = {
            account?.also { acct ->
                homeViewModel.blockCommunity(
                    community = it,
                    account = acct,
                    ctx = ctx
                )
            }
        },
        onBlockCreatorClick = {
            account?.also { acct ->
                homeViewModel.blockCreator(
                    creator = it,
                    account = acct,
                    ctx = ctx
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
                    ctx = ctx
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
                ctx = ctx
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
                    ctx = ctx
                )
            }
        },
        account = account
    )
}

@Composable
fun MainDrawer(
    siteViewModel: SiteViewModel,
    navController: NavController,
    accountViewModel: AccountViewModel,
    homeViewModel: HomeViewModel,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    ctx: Context
) {
    val accounts = accountViewModel.allAccounts.value
    val account = getCurrentAccount(accountViewModel)

    Drawer(
        myUserInfo = siteViewModel.siteRes?.my_user,
        unreadCounts = homeViewModel.unreadCountResponse,
        accountViewModel = accountViewModel,
        navController = navController,
        onSwitchAccountClick = { acct ->
            accountViewModel.removeCurrent()
            accountViewModel.setCurrent(acct.id)

            fetchInitialData(
                account = acct,
                siteViewModel = siteViewModel,
                homeViewModel = homeViewModel
            )

            closeDrawer(scope, scaffoldState)
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
                        homeViewModel = homeViewModel
                    )

                    closeDrawer(scope, scaffoldState)
                }
            }
        },
        onClickListingType = { listingType ->
            homeViewModel.fetchPosts(
                account = account,
                clear = true,
                changeListingType = listingType,
                ctx = ctx
            )
            closeDrawer(scope, scaffoldState)
        },
        onCommunityClick = { community ->
            navController.navigate(route = "community/${community.id}")
            closeDrawer(scope, scaffoldState)
        },
        onClickProfile = {
            account?.id?.also {
                navController.navigate(route = "profile/$it")
                closeDrawer(scope, scaffoldState)
            }
        },
        onClickSaved = {
            account?.id?.also {
                navController.navigate(route = "profile/$it?saved=${true}")
                closeDrawer(scope, scaffoldState)
            }
        },
        onClickInbox = {
            account?.also {
                navController.navigate(route = "inbox")
            } ?: run {
                loginFirstToast(ctx)
            }
            closeDrawer(scope, scaffoldState)
        },
        onClickSettings = {
            account.also {
                navController.navigate(route = "settings")
            } ?: run {
                loginFirstToast(ctx)
            }
            closeDrawer(scope, scaffoldState)
        }
    )
}

@Composable
fun MainTopBar(
    scope: CoroutineScope,
    postListState: LazyListState,
    scaffoldState: ScaffoldState,
    homeViewModel: HomeViewModel,
    account: Account?,
    ctx: Context,
    navController: NavController
) {
    Column {
        HomeHeader(
            scope = scope,
            scaffoldState = scaffoldState,
            navController = navController,
            selectedSortType = homeViewModel.sortType.value,
            selectedListingType = homeViewModel.listingType.value,
            onClickSortType = { sortType ->
                scrollToTop(scope, postListState)
                homeViewModel.fetchPosts(
                    account = account,
                    clear = true,
                    changeSortType = sortType,
                    ctx = ctx
                )
            },
            onClickListingType = { listingType ->
                scrollToTop(scope, postListState)
                homeViewModel.fetchPosts(
                    account = account,
                    clear = true,
                    changeListingType = listingType,
                    ctx = ctx
                )
            },
            onClickRefresh = {
                scrollToTop(scope, postListState)
                homeViewModel.fetchPosts(
                    account = account,
                    clear = true,
                    ctx = ctx
                )
            }
        )
        if (homeViewModel.loading.value) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}
