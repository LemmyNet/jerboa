package com.jerboa.ui.components.community

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import arrow.core.Either
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.db.entity.isAnon
import com.jerboa.feat.BlurNSFW
import com.jerboa.feat.PostActionBarMode
import com.jerboa.feat.SwipeToActionPreset
import com.jerboa.feat.VoteType
import com.jerboa.feat.changeBlurTypeInsideCommunity
import com.jerboa.feat.doIfReadyElseDisplayInfo
import com.jerboa.feat.newVote
import com.jerboa.feat.shareLink
import com.jerboa.hostName
import com.jerboa.model.AccountViewModel
import com.jerboa.model.AppSettingsViewModel
import com.jerboa.model.CommunityViewModel
import com.jerboa.model.ReplyItem
import com.jerboa.model.SiteViewModel
import com.jerboa.scrollToTop
import com.jerboa.ui.components.ban.BanFromCommunityReturn
import com.jerboa.ui.components.ban.BanPersonReturn
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.JerboaLoadingBar
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.getPostViewMode
import com.jerboa.ui.components.common.isRefreshing
import com.jerboa.ui.components.post.PostListings
import com.jerboa.ui.components.post.PostViewReturn
import com.jerboa.ui.components.post.edit.PostEditReturn
import com.jerboa.ui.components.remove.post.PostRemoveReturn
import it.vercruysse.lemmyapi.datatypes.BlockCommunity
import it.vercruysse.lemmyapi.datatypes.CommunityId
import it.vercruysse.lemmyapi.datatypes.CreatePostLike
import it.vercruysse.lemmyapi.datatypes.DeletePost
import it.vercruysse.lemmyapi.datatypes.FeaturePost
import it.vercruysse.lemmyapi.datatypes.FollowCommunity
import it.vercruysse.lemmyapi.datatypes.HidePost
import it.vercruysse.lemmyapi.datatypes.LockPost
import it.vercruysse.lemmyapi.datatypes.MarkPostAsRead
import it.vercruysse.lemmyapi.datatypes.PersonView
import it.vercruysse.lemmyapi.datatypes.PostView
import it.vercruysse.lemmyapi.datatypes.SavePost
import it.vercruysse.lemmyapi.dto.SubscribedType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    communityArg: Either<CommunityId, String>,
    appState: JerboaAppState,
    siteViewModel: SiteViewModel,
    accountViewModel: AccountViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    showVotingArrowsInListView: Boolean,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
    blurNSFW: BlurNSFW,
    showPostLinkPreviews: Boolean,
    markAsReadOnScroll: Boolean,
    postActionBarMode: PostActionBarMode,
    swipeToActionPreset: SwipeToActionPreset,
    disableVideoAutoplay: Boolean,
) {
    Log.d("jerboa", "got to community screen")

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val account = getCurrentAccount(accountViewModel)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val communityViewModel: CommunityViewModel =
        viewModel(factory = CommunityViewModel.Companion.Factory(communityArg))
    val postListState = communityViewModel.lazyListState

    appState.ConsumeReturn<PostView>(PostEditReturn.POST_VIEW, communityViewModel::updatePost)
    appState.ConsumeReturn<PostView>(PostRemoveReturn.POST_VIEW, communityViewModel::updatePost)
    appState.ConsumeReturn<PostView>(PostViewReturn.POST_VIEW, communityViewModel::updatePost)
    appState.ConsumeReturn<PersonView>(BanPersonReturn.PERSON_VIEW, communityViewModel::updateBanned)
    appState.ConsumeReturn<BanFromCommunityData>(
        BanFromCommunityReturn.BAN_DATA_VIEW,
        communityViewModel::updateBannedFromCommunity,
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                when (val communityRes = communityViewModel.communityRes) {
                    ApiState.Empty -> ApiEmptyText()
                    is ApiState.Failure -> ApiErrorText(communityRes.msg)
                    ApiState.Loading -> {
                        LoadingBar()
                    }

                    is ApiState.Success -> {
                        val communityId = communityRes.data.community_view.community.id
                        val instance = hostName(communityRes.data.community_view.community.actor_id)
                        val communityName =
                            communityRes.data.community_view.community.name +
                                if (instance != null) "@$instance" else ""
                        CommunityHeader(
                            scrollBehavior = scrollBehavior,
                            communityName = communityName,
                            selectedSortType = communityViewModel.sortType,
                            onClickRefresh = {
                                // TODO scroll to top doesnt seem to work
                                scrollToTop(scope, postListState)
                                communityViewModel.resetPosts()
                            },
                            onClickPostViewMode = {
                                appSettingsViewModel.updatedPostViewMode(it.ordinal)
                            },
                            onClickSortType = { sortType ->
                                scrollToTop(scope, postListState)
                                communityViewModel.updateSortType(sortType)
                                communityViewModel.resetPosts()
                            },
                            onBlockCommunityClick = {
                                account.doIfReadyElseDisplayInfo(
                                    appState,
                                    ctx,
                                    snackbarHostState,
                                    scope,
                                    siteViewModel,
                                    accountViewModel,
                                ) {
                                    communityViewModel.blockCommunity(
                                        BlockCommunity(
                                            community_id = communityId,
                                            block = !communityRes.data.community_view.blocked,
                                        ),
                                        ctx = ctx,
                                    )
                                }
                            },
                            onClickCommunityInfo = { appState.toCommunitySideBar(communityRes.data) },
                            onClickCommunityShare = {
                                shareLink(
                                    communityRes.data.community_view.community.actor_id,
                                    ctx,
                                )
                            },
                            onClickBack = appState::navigateUp,
                            selectedPostViewMode = getPostViewMode(appSettingsViewModel),
                            isBlocked = communityRes.data.community_view.blocked,
                        )
                    }

                    else -> {}
                }
            }
        },
        content = { padding ->
            PullToRefreshBox(
                modifier = Modifier.padding(padding),
                isRefreshing = communityViewModel.postsRes.isRefreshing(),
                onRefresh = communityViewModel::refreshPosts,
            ) {
                // Can't be inside ApiState.Loading, because can be holder and loading at same time
                JerboaLoadingBar(communityViewModel.postsRes)

                when (val postsRes = communityViewModel.postsRes) {
                    ApiState.Empty -> ApiEmptyText()
                    is ApiState.Failure -> ApiErrorText(postsRes.msg)
                    is ApiState.Holder -> {
                        val communityRes = communityViewModel.communityRes
                        val moderators =
                            remember(communityRes) {
                                when (communityRes) {
                                    is ApiState.Success -> communityRes.data.moderators
                                    else -> {
                                        null
                                    }
                                }
                            }

                        PostListings(
                            posts = postsRes.data,
                            admins = siteViewModel.admins(),
                            moderators = remember(moderators) { moderators?.map { it.moderator.id } },
                            contentAboveListings = {
                                when (communityRes) {
                                    is ApiState.Success -> {
                                        CommunityTopSection(
                                            communityView = communityRes.data.community_view,
                                            onClickFollowCommunity = { cfv ->
                                                account.doIfReadyElseDisplayInfo(
                                                    appState,
                                                    ctx,
                                                    snackbarHostState,
                                                    scope,
                                                    siteViewModel,
                                                    accountViewModel,
                                                ) {
                                                    communityViewModel.followCommunity(
                                                        form =
                                                            FollowCommunity(
                                                                community_id = cfv.community.id,
                                                                follow = cfv.subscribed == SubscribedType.NotSubscribed,
                                                            ),
                                                        onSuccess = {
                                                            siteViewModel.getSite()
                                                        },
                                                    )
                                                }
                                            },
                                            blurNSFW = blurNSFW.changeBlurTypeInsideCommunity(),
                                        )
                                    }

                                    else -> {}
                                }
                            },
                            onUpvoteClick = { postView ->
                                account.doIfReadyElseDisplayInfo(
                                    appState,
                                    ctx,
                                    snackbarHostState,
                                    scope,
                                    siteViewModel,
                                    accountViewModel,
                                ) {
                                    communityViewModel.likePost(
                                        form = CreatePostLike(
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
                                    accountViewModel,
                                ) {
                                    communityViewModel.likePost(
                                        form = CreatePostLike(
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
                                    accountViewModel,
                                ) {
                                    communityViewModel.savePost(
                                        form = SavePost(
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
                                    accountViewModel,
                                ) {
                                    communityViewModel.deletePost(
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
                                    accountViewModel,
                                ) {
                                    communityViewModel.hidePost(
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
                                    accountViewModel,
                                ) {
                                    communityViewModel.lockPost(
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
                                    accountViewModel,
                                ) {
                                    communityViewModel.featurePost(
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
                            onPersonClick = { personId ->
                                appState.toProfile(id = personId)
                            },
                            loadMorePosts = {
                                communityViewModel.appendPosts()
                            },
                            account = account,
                            showCommunityName = false,
                            listState = postListState,
                            postViewMode = getPostViewMode(appSettingsViewModel),
                            showVotingArrowsInListView = showVotingArrowsInListView,
                            enableDownVotes = siteViewModel.enableDownvotes(),
                            showAvatar = siteViewModel.showAvatar(),
                            useCustomTabs = useCustomTabs,
                            usePrivateTabs = usePrivateTabs,
                            blurNSFW = blurNSFW.changeBlurTypeInsideCommunity(),
                            showPostLinkPreviews = showPostLinkPreviews,
                            appState = appState,
                            markAsReadOnScroll = markAsReadOnScroll,
                            onMarkAsRead = { postView ->
                                if (!account.isAnon() && !postView.read) {
                                    communityViewModel.markPostAsRead(
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
                            showPostAppendRetry = communityViewModel.postsRes is ApiState.AppendingFailure,
                            swipeToActionPreset = swipeToActionPreset,
                            disableVideoAutoplay = disableVideoAutoplay,
                        )
                    }

                    else -> {}
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            when (val communityRes = communityViewModel.communityRes) {
                is ApiState.Success -> {
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
                                    community = communityRes.data.community_view.community,
                                )
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = stringResource(R.string.floating_createPost),
                        )
                    }
                }

                else -> {}
            }
        },
    )
}
