@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.community

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import arrow.core.Either
import com.jerboa.VoteType
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.loginFirstToast
import com.jerboa.openLink
import com.jerboa.scrollToTop
import com.jerboa.ui.components.common.BottomAppBarAll
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.getPostViewMode
import com.jerboa.ui.components.community.list.CommunityListViewModel
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.components.post.PostListings
import com.jerboa.ui.components.post.edit.PostEditViewModel

@Composable
fun CommunityActivity(
    navController: NavController,
    communityViewModel: CommunityViewModel,
    communityListViewModel: CommunityListViewModel,
    accountViewModel: AccountViewModel,
    homeViewModel: HomeViewModel,
    postEditViewModel: PostEditViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    showVotingArrowsInListView: Boolean,
    siteViewModel: SiteViewModel,
) {
    Log.d("jerboa", "got to community activity")

    val scope = rememberCoroutineScope()
    val postListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                communityViewModel.communityView?.community?.also { com ->
                    CommunityHeader(
                        scrollBehavior = scrollBehavior,
                        communityName = com.name,
                        selectedSortType = communityViewModel.sortType.value,
                        onClickRefresh = {
                            scrollToTop(scope, postListState)
                            communityViewModel.fetchPosts(
                                communityIdOrName = Either.Left(com.id),
                                account = account,
                                clear = true,
                                ctx = ctx,
                            )
                        },
                        onClickSortType = { sortType ->
                            scrollToTop(scope, postListState)
                            communityViewModel.fetchPosts(
                                communityIdOrName = Either.Left(com.id),
                                account = account,
                                clear = true,
                                changeSortType = sortType,
                                ctx = ctx,
                            )
                        },
                        onBlockCommunityClick = {
                            account?.also { acct ->
                                communityViewModel.blockCommunity(
                                    account = acct,
                                    ctx = ctx,
                                )
                            }
                        },
                        navController = navController,
                    )
                }
                if (communityViewModel.loading.value) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
        content = {
            siteViewModel.siteRes?.site_view?.also { siteView ->
                PostListings(
                    posts = communityViewModel.posts,
                    contentAboveListings = {
                        communityViewModel.communityView?.also { cv ->
                            CommunityTopSection(
                                communityView = cv,
                                onClickFollowCommunity = { cfv ->
                                    communityViewModel.followCommunity(
                                        cv = cfv,
                                        account = account,
                                        ctx = ctx,
                                    )
                                },
                            )
                        }
                    },
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
                    onPostClick = { postView ->
                        navController.navigate(route = "post/${postView.post.id}")
                    },
                    onPostLinkClick = { url ->
                        openLink(
                            url,
                            ctx,
                            appSettingsViewModel.appSettings.value?.useCustomTabs ?: true,
                        )
                    },
                    onSaveClick = { postView ->
                        account?.also { acct ->
                            communityViewModel.savePost(
                                postView = postView,
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
                            communityViewModel.deletePost(
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
                    onBlockCommunityClick = {
                        account?.also { acct ->
                            communityViewModel.blockCommunity(
                                account = acct,
                                ctx = ctx,
                            )
                        }
                    },
                    onBlockCreatorClick = {
                        account?.also { acct ->
                            communityViewModel.blockCreator(
                                creator = it,
                                account = acct,
                                ctx = ctx,
                            )
                        }
                    },
                    onSwipeRefresh = {
                        communityViewModel.fetchPosts(
                            communityIdOrName = Either.Left(
                                communityViewModel.communityView!!
                                    .community.id,
                            ),
                            account = account,
                            clear = true,
                            ctx = ctx,
                        )
                    },
                    loading = communityViewModel.loading.value &&
                        communityViewModel.page.value == 1 &&
                        communityViewModel.posts.isNotEmpty(),
                    isScrolledToEnd = {
                        if (communityViewModel.posts.size > 0) {
                            communityViewModel.fetchPosts(
                                communityIdOrName = Either.Left(
                                    communityViewModel.communityView!!
                                        .community.id,
                                ),
                                account = account,
                                nextPage = true,
                                ctx = ctx,
                            )
                        }
                    },
                    account = account,
                    showCommunityName = false,
                    padding = it,
                    listState = postListState,
                    taglines = null,
                    postViewMode = getPostViewMode(appSettingsViewModel),
                    showVotingArrowsInListView = showVotingArrowsInListView,
                    siteView = siteView,
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    account?.also {
                        communityViewModel.communityView?.also {
                            communityListViewModel.selectCommunity(it.community)
                            navController.navigate("createPost")
                        }
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
                screen = "communityList",
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
}
