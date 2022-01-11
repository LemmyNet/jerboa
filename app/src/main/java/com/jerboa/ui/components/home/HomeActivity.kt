package com.jerboa.ui.components.home

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.jerboa.*
import com.jerboa.api.API
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.community.CommunityViewModel
import com.jerboa.ui.components.community.communityClickWrapper
import com.jerboa.ui.components.post.PostListings
import kotlinx.coroutines.CoroutineScope

@Composable
fun HomeActivity(
    navController: NavController,
    homeViewModel: HomeViewModel,
    communityViewModel: CommunityViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    isScrolledToEnd: () -> Unit,
) {

    Log.d("jerboa", "got to community activity")

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val ctx = LocalContext.current
    val accounts by accountViewModel.allAccounts.observeAsState()
    val account = getCurrentAccount(accounts = accounts)

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                MainTopBar(
                    scope = scope,
                    scaffoldState = scaffoldState,
                    homeViewModel = homeViewModel,
                    account = account,
                    ctx = ctx,
                    navController = navController
                )
            },
            drawerShape = MaterialTheme.shapes.small,
            drawerBackgroundColor = colorShade(
                color = MaterialTheme.colors.background,
                factor = 1.2f
            ),
            drawerContent = {
                MainDrawer(
                    siteViewModel,
                    accounts,
                    navController,
                    accountViewModel,
                    homeViewModel,
                    scope,
                    scaffoldState,
                    account,
                    ctx
                )
            },
            content = {
                MainPostListingsContent(
                    homeViewModel,
                    communityViewModel,
                    account,
                    ctx,
                    navController,
                    isScrolledToEnd,
                )
            }
        )
    }
}

@Composable
fun MainPostListingsContent(
    homeViewModel: HomeViewModel,
    communityViewModel: CommunityViewModel,
    account: Account?,
    ctx: Context,
    navController: NavController,
    isScrolledToEnd: () -> Unit,
) {
    PostListings(
        posts = homeViewModel.posts,
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
        onSaveClick = { postView ->
            homeViewModel.savePost(
                postView = postView,
                account = account,
                ctx = ctx,
            )
        },
        onPostClick = { postView ->
            navController.navigate("post/${postView.post.id}?fetch=true")
        },
        onPostLinkClick = { url ->
            openLink(url, ctx)
        },
        onSwipeRefresh = {
            homeViewModel.fetchPosts(
                account = account,
                clear = true,
                ctx = ctx,
            )
        },
        onCommunityClick = { communityId ->
            communityClickWrapper(
                communityViewModel,
                communityId,
                account,
                navController,
                ctx = ctx,
            )
        },
        loading = homeViewModel.loading.value &&
            homeViewModel.page.value == 1 &&
            homeViewModel.posts.isNotEmpty(),
        isScrolledToEnd = isScrolledToEnd,
    )
}

@Composable
fun MainDrawer(
    siteViewModel: SiteViewModel,
    accounts: List<Account>?,
    navController: NavController,
    accountViewModel: AccountViewModel,
    homeViewModel: HomeViewModel,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    account: Account?,
    ctx: Context
) {
    Drawer(
        myUserInfo = siteViewModel.siteRes?.my_user,
        accounts = accounts,
        navController = navController,
        onSwitchAccountClick = {
            accountViewModel.removeCurrent()
            accountViewModel.setCurrent(it.id)
            API.changeLemmyInstance(it.instance)

            // Refetch the site
            siteViewModel.fetchSite(it.jwt)

            // Refetch the front page
            homeViewModel.fetchPosts(
                account = account,
                clear = true,
                ctx = ctx,
            )

            // Close the drawer
            closeDrawer(scope, scaffoldState)
        },
        onSignOutClick = {
            accounts?.also { accounts ->
                getCurrentAccount(accounts)?.also {
                    accountViewModel.delete(it)
                    val updatedList = accounts.toMutableList()
                    updatedList.remove(it)

                    if (updatedList.isNotEmpty()) {
                        accountViewModel.setCurrent(updatedList[0].id)
                    }
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
            closeDrawer(scope, scaffoldState)
        }
    )
}

@Composable
fun MainTopBar(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    homeViewModel: HomeViewModel,
    account: Account?,
    ctx: Context,
    navController: NavController,
) {
    Column {
        HomeOrCommunityHeader(
            scope, scaffoldState,
            selectedSortType = homeViewModel.sortType.value,
            selectedListingType = homeViewModel.listingType.value,
            onClickSortType = { sortType ->
                homeViewModel.fetchPosts(
                    account = account,
                    clear = true,
                    changeSortType = sortType,
                    ctx = ctx,
                )
            },
            onClickListingType = { listingType ->
                homeViewModel.fetchPosts(
                    account = account,
                    clear = true,
                    changeListingType = listingType,
                    ctx = ctx,
                )
            },
            navController = navController,
        )
        if (homeViewModel.loading.value) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}
