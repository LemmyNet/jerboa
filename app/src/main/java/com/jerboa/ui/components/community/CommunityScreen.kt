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
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import arrow.core.Either
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.SelectionVisibilityState
import com.jerboa.api.ApiState
import com.jerboa.api.toOpt
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.db.entity.isAnon
import com.jerboa.feat.BlurNSFW
import com.jerboa.feat.PostActionBarMode
import com.jerboa.feat.SwipeToActionPreset
import com.jerboa.feat.changeBlurTypeInsideCommunity
import com.jerboa.feat.doIfReadyElseDisplayInfo
import com.jerboa.feat.newVote
import com.jerboa.feat.shareLink
import com.jerboa.hostName
import com.jerboa.model.AccountViewModel
import com.jerboa.model.AppSettingsViewModel
import com.jerboa.model.CommunityViewModel
import com.jerboa.model.MyUserInfoViewModel
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
import it.vercruysse.lemmyapi.enums.CommunityFollowerState
import it.vercruysse.lemmyapi.enums.VoteAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    communityArg: Either<CommunityId, String>,
    appState: JerboaAppState,
    siteViewModel: SiteViewModel,
    myUserInfoViewModel: MyUserInfoViewModel,
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
    lowBandwidthMode: Boolean,
) {
    Log.d("jerboa", "got to community screen")

    val ctx = LocalContext.current
    val resources = LocalResources.current
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
                    ApiState.Empty -> {
                        ApiEmptyText()
                    }

                    is ApiState.Failure -> {
                        ApiErrorText(communityRes.msg)
                    }

                    ApiState.Loading -> {
                        LoadingBar()
                    }

                    is ApiState.Success -> {
                        val communityId = communityRes.data.community_view.community.id
                        val instance = hostName(communityRes.data.community_view.community.ap_id)
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
                                    resources,
                                    snackbarHostState,
                                    scope,
                                    myUserInfoViewModel,
                                    accountViewModel,
                                ) {
                                    communityViewModel.blockCommunity(
                                        BlockCommunity(
                                            community_id = communityId,
                                            block = communityRes.data.community_view.community_actions?.blocked_at == null,
                                        ),
                                        ctx = ctx,
                                    )
                                }
                            },
                            onClickCommunityInfo = { appState.toCommunitySideBar(communityRes.data) },
                            onClickCommunityShare = {
                                shareLink(
                                    communityRes.data.community_view.community.ap_id,
                                    ctx,
                                    resources,
                                )
                            },
                            onClickBack = appState::navigateUp,
                            selectedPostViewMode = getPostViewMode(appSettingsViewModel),
                            isBlocked = communityRes.data.community_view.community_actions?.blocked_at != null,
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
                    ApiState.Empty -> {
                        ApiEmptyText()
                    }

                    is ApiState.Failure -> {
                        ApiErrorText(postsRes.msg)
                    }

                    is ApiState.Holder -> {
                        val communityRes = communityViewModel.communityRes

                        PostListings(
                            posts = postsRes.data,
                            admins = siteViewModel.siteRes.toOpt()?.admins ?: emptyList(),
                            TODO
                            myUserInfo = myUserInfo,
                            localSite = localSite,
                            contentAboveListings = {
                                when (communityRes) {
                                    is ApiState.Success -> {
                                        CommunityTopSection(
                                            communityView = communityRes.data.community_view,
                                            onClickFollowCommunity = { cfv ->
                                                account.doIfReadyElseDisplayInfo(
                                                    appState,
                                                    ctx,
                                                    resources,
                                                    snackbarHostState,
                                                    scope,
                                                    myUserInfoViewModel,
                                                    accountViewModel,
                                                ) {
                                                    communityViewModel.followCommunity(
                                                        form =
                                                            FollowCommunity(
                                                                community_id = cfv.community.id,
                                                                cfv.community_actions?.follow_state != CommunityFollowerState.Accepted
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
                                    resources,
                                    snackbarHostState,
                                    scope,
                                    myUserInfoViewModel,
                                    accountViewModel,
                                ) {
                                    communityViewModel.likePost(
                                        form = CreatePostLike(
                                            post_id = postView.post.id,
                                            vote = newVote(postView.post_actions?.vote, VoteAction.UpVote),
                                        ),
                                    )
                                }
                            },
                            onDownvoteClick = { postView ->
                                account.doIfReadyElseDisplayInfo(
                                    appState,
                                    ctx,
                                    resources,
                                    snackbarHostState,
                                    scope,
                                    myUserInfoViewModel,
                                    accountViewModel,
                                ) {
                                    communityViewModel.likePost(
                                        form = CreatePostLike(
                                            post_id = postView.post.id,
                                            vote = newVote(postView.post_actions?.vote, VoteAction.DownVote),
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
                                    resources,
                                    snackbarHostState,
                                    scope,
                                    myUserInfoViewModel,
                                    accountViewModel,
                                ) {
                                    communityViewModel.savePost(
                                        form = SavePost(
                                            post_id = postView.post.id,
                                            postView.post_actions?.saved_at == null,
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
                                    resources,
                                    snackbarHostState,
                                    scope,
                                    myUserInfoViewModel,
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
                                    resources,
                                    snackbarHostState,
                                    scope,
                                    myUserInfoViewModel,
                                    accountViewModel,
                                ) {
                                    communityViewModel.hidePost(
                                        HidePost(
                                            post_id = postView.post.id,
                                            hide = postView.post_actions?.hidden_at == null,
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
                            onBanPersonClick = { pv ->
                                appState.toBanPerson(pv)
                            },
                            onBanFromCommunityClick = { d ->
                                appState.toBanFromCommunity(banData = d)
                            },
                            onLockPostClick = { pv ->
                                account.doIfReadyElseDisplayInfo(
                                    appState,
                                    ctx,
                                    resources,
                                    snackbarHostState,
                                    scope,
                                    myUserInfoViewModel,
                                    accountViewModel,
                                ) {
                                    communityViewModel.lockPost(
                                        LockPost(
                                            post_id = pv.post.id,
                                            locked = !pv.post.locked,
                                            reason = TODO(),
                                        ),
                                    )
                                }
                            },
                            onFeaturePostClick = { data ->
                                account.doIfReadyElseDisplayInfo(
                                    appState,
                                    ctx,
                                    resources,
                                    snackbarHostState,
                                    scope,
                                    myUserInfoViewModel,
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
                            useCustomTabs = useCustomTabs,
                            usePrivateTabs = usePrivateTabs,
                            blurNSFW = blurNSFW.changeBlurTypeInsideCommunity(),
                            showPostLinkPreviews = showPostLinkPreviews,
                            appState = appState,
                            markAsReadOnScroll = markAsReadOnScroll,
                            onMarkAsRead = { postView ->
                                if (!account.isAnon() && postView.post_actions?.read_at == null) {
                                    communityViewModel.markPostAsRead(
                                        MarkPostAsRead(
                                            post_id = postView.post.id,
                                            read = true,
                                        ),
                                        appState,
                                    )
                                }
                            },
                            showIfRead = true,
                            postActionBarMode = postActionBarMode,
                            showPostAppendRetry = communityViewModel.postsRes is ApiState.AppendingFailure,
                            swipeToActionPreset = swipeToActionPreset,
                            disableVideoAutoplay = disableVideoAutoplay,
                            lowBandwidthMode = lowBandwidthMode,
                            selectionState = SelectionVisibilityState.NoSelection,,
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
                                resources,
                                snackbarHostState,
                                scope,
                                myUserInfoViewModel,
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
