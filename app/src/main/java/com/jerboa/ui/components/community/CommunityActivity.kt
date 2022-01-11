package com.jerboa.ui.components.community

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.jerboa.VoteType
import com.jerboa.datatypes.ListingType
import com.jerboa.db.AccountViewModel
import com.jerboa.getCurrentAccount
import com.jerboa.openLink
import com.jerboa.ui.components.home.HomeOrCommunityHeader
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.post.PostListings

@Composable
fun CommunityActivity(
    navController: NavController,
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
                HomeOrCommunityHeader(
                    communityName = communityViewModel.communityView?.community?.name,
                    scope = scope,
                    scaffoldState = scaffoldState,
                    selectedSortType = communityViewModel.sortType.value,
                    selectedListingType = ListingType.Community,
                    onClickSortType = { sortType ->
                        communityViewModel.fetchPosts(
                            account = account,
                            clear = true,
                            changeSortType = sortType,
                            ctx = ctx,
                        )
                    },
                    navController = navController,
                )
                if (communityViewModel.loading.value) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            },
            content = {
                PostListings(
                    communityView = communityViewModel.communityView,
                    posts = communityViewModel.posts,
                    onUpvoteClick = { postView ->
                        communityViewModel.likePost(
                            voteType = VoteType.Upvote,
                            postView = postView,
                            account = account,
                            ctx = ctx,
                        )
                    },
                    onDownvoteClick = { postView ->
                        communityViewModel.likePost(
                            voteType = VoteType.Downvote,
                            postView = postView,
                            account = account,
                            ctx = ctx,
                        )
                    },
                    onSaveClick = { postView ->
                        communityViewModel.savePost(
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
                        communityViewModel.fetchPosts(
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
                    loading = communityViewModel.loading.value &&
                        communityViewModel.page.value == 1 &&
                        communityViewModel.posts.isNotEmpty(),
                    isScrolledToEnd = isScrolledToEnd,
                )
            },
        )
    }
}
