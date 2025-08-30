package com.jerboa.ui.components.home

import android.util.Log
import androidx.activity.compose.ReportDrawn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.isAnon
import com.jerboa.db.entity.isReady
import com.jerboa.feat.BlurNSFW
import com.jerboa.feat.PostActionBarMode
import com.jerboa.feat.SwipeToActionPreset
import com.jerboa.feat.VoteType
import com.jerboa.feat.doIfReadyElseDisplayInfo
import com.jerboa.feat.newVote
import com.jerboa.model.AccountViewModel
import com.jerboa.model.AppSettingsViewModel
import com.jerboa.model.HomeViewModel
import com.jerboa.model.ReplyItem
import com.jerboa.model.SiteViewModel
import com.jerboa.scrollToTop
import com.jerboa.ui.components.ban.BanFromCommunityReturn
import com.jerboa.ui.components.ban.BanPersonReturn
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.JerboaLoadingBar
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.apiErrorToast
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.getPostViewMode
import com.jerboa.ui.components.common.isRefreshing
import com.jerboa.ui.components.post.PostListings
import com.jerboa.ui.components.post.PostViewReturn
import com.jerboa.ui.components.post.edit.PostEditReturn
import com.jerboa.ui.components.remove.post.PostRemoveReturn
import it.vercruysse.lemmyapi.datatypes.CreatePostLike
import it.vercruysse.lemmyapi.datatypes.DeletePost
import it.vercruysse.lemmyapi.datatypes.FeaturePost
import it.vercruysse.lemmyapi.datatypes.HidePost
import it.vercruysse.lemmyapi.datatypes.LockPost
import it.vercruysse.lemmyapi.datatypes.MarkPostAsRead
import it.vercruysse.lemmyapi.datatypes.PersonView
import it.vercruysse.lemmyapi.datatypes.PostView
import it.vercruysse.lemmyapi.datatypes.SavePost
import it.vercruysse.lemmyapi.datatypes.Tagline
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(
    appState: JerboaAppState,
    homeViewModel: HomeViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    showVotingArrowsInListView: Boolean,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    drawerState: DrawerState,
    blurNSFW: BlurNSFW,
    showPostLinkPreviews: Boolean,
    markAsReadOnScroll: Boolean,
    postActionBarMode: PostActionBarMode,
    swipeToActionPreset: SwipeToActionPreset,
    disableVideoAutoplay: Boolean,
    padding: PaddingValues,
) {
    Log.d("jerboa", "got to home screen")

    val scope = rememberCoroutineScope()
    val postListState = homeViewModel.lazyListState
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    // Used for benchmarks TODO: make a .benchmark build that correctly filters
    //  out the benchmark stuff from the actual app, like testtags
    // val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)
    // Forget snackbars of previous accounts
    val snackbarHostState = remember(account) { SnackbarHostState() }

    appState.ConsumeReturn<PostView>(PostEditReturn.POST_VIEW, homeViewModel::updatePost)
    appState.ConsumeReturn<PostView>(PostRemoveReturn.POST_VIEW, homeViewModel::updatePost)
    appState.ConsumeReturn<PostView>(PostViewReturn.POST_VIEW, homeViewModel::updatePost)
    appState.ConsumeReturn<PersonView>(BanPersonReturn.PERSON_VIEW, homeViewModel::updateBanned)
    appState.ConsumeReturn<BanFromCommunityData>(BanFromCommunityReturn.BAN_DATA_VIEW, homeViewModel::updateBannedFromCommunity)

    LaunchedEffect(account) {
        if (!account.isAnon() && !account.isReady()) {
            account.doIfReadyElseDisplayInfo(
                appState,
                ctx,
                snackbarHostState,
                scope,
                siteViewModel,
                accountViewModel,
            ) {}
        }
    }

    val baseModifier =
        Modifier
            .padding(padding)
            .consumeWindowInsets(padding)
            .systemBarsPadding()

    Scaffold(
        modifier =
            baseModifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .semantics { testTagsAsResourceId = true },
        snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
        topBar = {
            MainTopBar(
                scrollToTop = {
                    scrollToTop(scope, postListState)
                },
                openDrawer = {
                    scope.launch {
                        drawerState.open()
                    }
                },
                homeViewModel = homeViewModel,
                appSettingsViewModel = appSettingsViewModel,
                scrollBehavior = scrollBehavior,
                onClickSiteInfo = appState::toSiteSideBar,
            )
        },
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                MainPostListingsContent(
                    homeViewModel = homeViewModel,
                    siteViewModel = siteViewModel,
                    appSettingsViewModel = appSettingsViewModel,
                    account = account,
                    appState = appState,
                    postListState = postListState,
                    showVotingArrowsInListView = showVotingArrowsInListView,
                    useCustomTabs = useCustomTabs,
                    usePrivateTabs = usePrivateTabs,
                    blurNSFW = blurNSFW,
                    showPostLinkPreviews = showPostLinkPreviews,
                    markAsReadOnScroll = markAsReadOnScroll,
                    snackbarHostState = snackbarHostState,
                    postActionBarMode = postActionBarMode,
                    swipeToActionPreset = swipeToActionPreset,
                    disableVideoAutoplay = disableVideoAutoplay,
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
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
                            community = null,
                        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPostListingsContent(
    homeViewModel: HomeViewModel,
    siteViewModel: SiteViewModel,
    account: Account,
    appState: JerboaAppState,
    postListState: LazyListState,
    appSettingsViewModel: AppSettingsViewModel,
    showVotingArrowsInListView: Boolean,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: BlurNSFW,
    showPostLinkPreviews: Boolean,
    snackbarHostState: SnackbarHostState,
    markAsReadOnScroll: Boolean,
    postActionBarMode: PostActionBarMode,
    swipeToActionPreset: SwipeToActionPreset,
    disableVideoAutoplay: Boolean,
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var taglines: List<Tagline>? = null
    when (val siteRes = siteViewModel.siteRes) {
        ApiState.Loading -> LoadingBar()
        is ApiState.Failure -> ApiErrorText(siteRes.msg)
        is ApiState.Success -> {
            taglines = siteRes.data.taglines
        }

        else -> {}
    }

    ReportDrawn()

    PullToRefreshBox(
        isRefreshing = homeViewModel.postsRes.isRefreshing(),
        onRefresh = homeViewModel::refreshPosts,
    ) {
        JerboaLoadingBar(homeViewModel.postsRes)

        val posts: List<PostView> = when (val postsRes = homeViewModel.postsRes) {
            is ApiState.Failure -> {
                apiErrorToast(ctx, postsRes.msg)
                listOf()
            }

            is ApiState.Holder -> postsRes.data
            else -> listOf()
        }

        PostListings(
            posts = posts,
            admins = siteViewModel.admins(),
            // No community moderators available here
            moderators = null,
            contentAboveListings = { if (taglines !== null) Taglines(taglines = taglines) },
            onUpvoteClick = { postView ->
                account.doIfReadyElseDisplayInfo(
                    appState,
                    ctx,
                    snackbarHostState,
                    scope,
                    siteViewModel,
                ) {
                    homeViewModel.likePost(
                        CreatePostLike(
                            post_id = postView.post.id,
                            score = newVote(postView.my_vote, VoteType.Upvote),
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
                ) {
                    homeViewModel.likePost(
                        CreatePostLike(
                            post_id = postView.post.id,
                            score = newVote(postView.my_vote, VoteType.Downvote),
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
                ) {
                    homeViewModel.savePost(
                        SavePost(
                            post_id = postView.post.id,
                            save = !postView.saved,
                        ),
                    )
                }
            },
            onReplyClick = { pv ->
                appState.toCommentReply(
                    replyItem = ReplyItem.PostItem(pv),
                )
            },
            onEditPostClick = { postView ->
                appState.toPostEdit(
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
                ) {
                    homeViewModel.deletePost(
                        DeletePost(
                            post_id = postView.post.id,
                            deleted = !postView.post.deleted,
                        ),
                    )
                }
            },
            onHidePostClick = { postView ->
                account.doIfReadyElseDisplayInfo(
                    appState,
                    ctx,
                    snackbarHostState,
                    scope,
                    siteViewModel,
                ) {
                    homeViewModel.hidePost(
                        HidePost(
                            post_ids = listOf(postView.post.id),
                            hide = !postView.hidden,
                        ),
                        ctx,
                    )
                }
            },
            onReportClick = { postView ->
                appState.toPostReport(id = postView.post.id)
            },
            onRemoveClick = { pv ->
                appState.toPostRemove(post = pv.post)
            },
            onBanPersonClick = { p ->
                appState.toBanPerson(p)
            },
            onBanFromCommunityClick = { d ->
                appState.toBanFromCommunity(banData = d)
            },
            onLockPostClick = { pv ->
                account.doIfReadyElseDisplayInfo(
                    appState,
                    ctx,
                    snackbarHostState,
                    scope,
                    siteViewModel,
                ) {
                    homeViewModel.lockPost(
                        LockPost(
                            post_id = pv.post.id,
                            locked = !pv.post.locked,
                        ),
                    )
                }
            },
            onFeaturePostClick = { data ->
                account.doIfReadyElseDisplayInfo(
                    appState,
                    ctx,
                    snackbarHostState,
                    scope,
                    siteViewModel,
                ) {
                    homeViewModel.featurePost(
                        FeaturePost(
                            post_id = data.post.id,
                            featured = !data.featured,
                            feature_type = data.type,
                        ),
                    )
                }
            },
            onViewPostVotesClick = appState::toPostLikes,
            onCommunityClick = { community ->
                appState.toCommunity(id = community.id)
            },
            onPersonClick = appState::toProfile,
            loadMorePosts = {
                homeViewModel.appendPosts()
            },
            account = account,
            listState = postListState,
            postViewMode = getPostViewMode(appSettingsViewModel),
            showVotingArrowsInListView = showVotingArrowsInListView,
            enableDownVotes = siteViewModel.enableDownvotes(),
            showAvatar = siteViewModel.showAvatar(),
            useCustomTabs = useCustomTabs,
            usePrivateTabs = usePrivateTabs,
            blurNSFW = blurNSFW,
            showPostLinkPreviews = showPostLinkPreviews,
            appState = appState,
            markAsReadOnScroll = markAsReadOnScroll,
            onMarkAsRead = { postView ->
                if (!account.isAnon() && !postView.read) {
                    homeViewModel.markPostAsRead(
                        MarkPostAsRead(
                            post_ids = listOf(postView.post.id),
                            read = true,
                        ),
                        postView,
                        appState,
                    )
                }
            },
            showIfRead = true,
            voteDisplayMode = siteViewModel.voteDisplayMode(),
            postActionBarMode = postActionBarMode,
            showPostAppendRetry = homeViewModel.postsRes is ApiState.AppendingFailure,
            swipeToActionPreset = swipeToActionPreset,
            disableVideoAutoplay = disableVideoAutoplay,
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
                homeViewModel.resetPosts()
            },
            onClickListingType = { listingType ->
                scrollToTop()
                homeViewModel.updateListingType(listingType)
                homeViewModel.resetPosts()
            },
            onClickPostViewMode = {
                appSettingsViewModel.updatedPostViewMode(it.ordinal)
            },
            onClickRefresh = {
                scrollToTop()
                homeViewModel.resetPosts()
            },
            onClickSiteInfo = onClickSiteInfo,
        )
    }
}
