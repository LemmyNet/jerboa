package com.jerboa.ui.components.person

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
import com.jerboa.VoteType
import com.jerboa.db.AccountViewModel
import com.jerboa.getCurrentAccount
import com.jerboa.openLink
import com.jerboa.ui.components.community.CommunityViewModel
import com.jerboa.ui.components.community.communityClickWrapper
import com.jerboa.ui.components.home.HomeOrCommunityOrPersonHeader
import com.jerboa.ui.components.post.PostListings

@Composable
fun PersonProfileActivity(
    navController: NavController,
    personProfileViewModel: PersonProfileViewModel,
    communityViewModel: CommunityViewModel,
    accountViewModel: AccountViewModel,
) {

    Log.d("jerboa", "got to person activity")

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val ctx = LocalContext.current
    val accounts by accountViewModel.allAccounts.observeAsState()
    val account = getCurrentAccount(accounts = accounts)

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                Column {
                    HomeOrCommunityOrPersonHeader(
                        personName = personProfileViewModel.res?.person_view?.person?.name,
                        scope = scope,
                        scaffoldState = scaffoldState,
                        selectedSortType = personProfileViewModel.sortType.value,
                        onClickSortType = { sortType ->
                            personProfileViewModel.fetchPersonDetails(
                                id = personProfileViewModel.personId.value!!,
                                account = account,
                                clear = true,
                                changeSortType = sortType,
                                ctx = ctx,
                            )
                        },
                        navController = navController,
                    )
                    if (personProfileViewModel.loading.value) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            content = {
                PostListings(
                    posts = personProfileViewModel.posts,
                    contentAboveListings = {
                        personProfileViewModel.res?.person_view?.also {
                            PersonProfileTopSection(personView = it)
                        }
                    },
                    onUpvoteClick = { postView ->
                        personProfileViewModel.likePost(
                            voteType = VoteType.Upvote,
                            postView = postView,
                            account = account,
                            ctx = ctx,
                        )
                    },
                    onDownvoteClick = { postView ->
                        personProfileViewModel.likePost(
                            voteType = VoteType.Downvote,
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
                    onSaveClick = { postView ->
                        personProfileViewModel.savePost(
                            postView = postView,
                            account = account,
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
                    onSwipeRefresh = {
                        personProfileViewModel.personId.value?.also {
                            personProfileViewModel.fetchPersonDetails(
                                id = it,
                                account = account,
                                clear = true,
                                ctx = ctx,
                            )
                        }
                    },
                    loading = personProfileViewModel.loading.value &&
                        personProfileViewModel.page.value == 1 &&
                        personProfileViewModel.posts.isNotEmpty(),
                    isScrolledToEnd = {
                        personProfileViewModel.personId.value?.also {
                            personProfileViewModel.fetchPersonDetails(
                                id = it,
                                account = account,
                                nextPage = true,
                                ctx = ctx,
                            )
                        }
                    },
                    onPersonClick = { personId ->
                        personClickWrapper(
                            personProfileViewModel = personProfileViewModel,
                            personId = personId,
                            account = account,
                            navController = navController,
                            ctx = ctx,
                        )
                    },
                )
            },
        )
    }
}
