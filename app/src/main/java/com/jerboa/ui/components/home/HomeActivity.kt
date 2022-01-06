package com.jerboa.ui.components.home

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
import com.jerboa.api.API
import com.jerboa.datatypes.api.GetPosts
import com.jerboa.db.AccountViewModel
import com.jerboa.getCurrentAccount
import com.jerboa.ui.components.post.PostListings
import com.jerboa.ui.components.post.PostListingsViewModel

@Composable
fun HomeActivity(
    navController: NavController,
    postListingsViewModel: PostListingsViewModel,
    accountViewModel: AccountViewModel,
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val ctx = LocalContext.current
    val accounts by accountViewModel.allAccounts.observeAsState()
    val account = getCurrentAccount(accountViewModel = accountViewModel)

    postListingsViewModel.fetchPosts(
        GetPosts(
            auth = account?.jwt
        )
    )

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                HomeHeader(scope, scaffoldState)
            },
            drawerContent = {
                Drawer(
                    accounts = accounts,
                    navController = navController,
                    onSwitchAccountClick = {
                        accountViewModel.removeDefault()
                        accountViewModel.setDefault(it.id)
                        API.changeLemmyInstance(it.instance)
                    },
                    onSignOutClick = {
                        accounts?.let { accounts ->
                            getCurrentAccount(accounts)?.let {
                                accountViewModel.delete(it)
                                val updatedList = accounts.toMutableList()
                                updatedList.remove(it)

                                if (updatedList.isNotEmpty()) {
                                    accountViewModel.setDefault(updatedList[0].id)
                                }
                            }
                        }
                    }
                )
            },
            content = {
                if (postListingsViewModel.loading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                PostListings(
                    posts = postListingsViewModel.posts,
                    onItemClicked = postListingsViewModel::onPostClicked,
                    navController = navController,
                    onUpvoteClick = {
                        postListingsViewModel.likePost(
                            voteType = VoteType.Upvote,
                            postView = it,
                            account = account,
                            ctx = ctx,
                        )
                    },
                    onDownvoteClick = {
                        postListingsViewModel.likePost(
                            voteType = VoteType.Downvote,
                            postView = it,
                            account = account,
                            ctx = ctx,
                        )
                    }
                )
            }
        )
    }
}
