package com.jerboa.ui.components.search

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.JerboaAppState
import com.jerboa.PostViewMode
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.commentsToFlatNodes
import com.jerboa.db.entity.AppSettings
import com.jerboa.feat.VoteType
import com.jerboa.feat.doIfReadyElseDisplayInfo
import com.jerboa.feat.newVote
import com.jerboa.model.AccountViewModel
import com.jerboa.model.ReplyItem
import com.jerboa.model.SearchListViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.toBool
import com.jerboa.toEnum
import com.jerboa.ui.components.comment.commentNodeItems
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.JerboaLoadingBar
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.RetryLoadingPosts
import com.jerboa.ui.components.common.TriggerWhenReachingEnd
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.post.PostListing
import it.vercruysse.lemmyapi.datatypes.BlockPerson
import it.vercruysse.lemmyapi.datatypes.CommunityFollowerView
import it.vercruysse.lemmyapi.datatypes.CreateCommentLike
import it.vercruysse.lemmyapi.datatypes.CreatePostLike
import it.vercruysse.lemmyapi.datatypes.DeleteComment
import it.vercruysse.lemmyapi.datatypes.DeletePost
import it.vercruysse.lemmyapi.datatypes.DistinguishComment
import it.vercruysse.lemmyapi.datatypes.FeaturePost
import it.vercruysse.lemmyapi.datatypes.HidePost
import it.vercruysse.lemmyapi.datatypes.LockPost
import it.vercruysse.lemmyapi.datatypes.SaveComment
import it.vercruysse.lemmyapi.datatypes.SavePost
import it.vercruysse.lemmyapi.dto.SearchType
import kotlinx.coroutines.launch

object SearchListReturn {
    const val COMMUNITY = "search-list::return(community)"
    const val PERSON_VIEW = "search-list::return(person-view)"
    const val POST_VIEW = "search-list::return(post-view)"
    const val COMMENT_VIEW = "search-list::return(comment-view)"
}

@Composable
fun SearchScreen(
    appState: JerboaAppState,
    searchTypeMode: SearchType,
    followList: List<CommunityFollowerView>,
    appSettings: AppSettings,
    drawerState: DrawerState,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    lowBandwidthMode: Boolean,
    padding: PaddingValues? = null,
) {
    Log.d("SearchScreen", "Arrived")

    val ctx = LocalContext.current
    val resources = LocalResources.current
    val account = getCurrentAccount(accountViewModel)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val selectionMode =
        searchTypeMode == SearchType.Communities ||
            searchTypeMode == SearchType.Users ||
            searchTypeMode == SearchType.Posts ||
            searchTypeMode == SearchType.Comments

    var showSearchOptions by rememberSaveable { mutableStateOf(false) }
    val searchListViewModel: SearchListViewModel =
        viewModel(factory = SearchListViewModel.Companion.Factory(followList, searchTypeMode))

    val baseModifier = if (padding == null) {
        Modifier
    } else {
        // https://issuetracker.google.com/issues/249727298
        // Else it also applies the padding above the ime (keyboard)
        Modifier
            .padding(padding)
            .consumeWindowInsets(padding)
            .systemBarsPadding()
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            modifier = baseModifier,
            topBar = {
                SearchListHeader(
                    showSearchOptions = showSearchOptions,
                    setShowSearchOptions = { showSearchOptions = it },
                    openDrawer = {
                        appState.coroutineScope.launch {
                            drawerState.open()
                        }
                    },
                    search = searchListViewModel.q,
                    onSearchChange = searchListViewModel::onSearchChange,
                )
            },
            snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
            content = { padding ->

                Column(
                    Modifier
                        .padding(padding)
                        .imePadding(),
                ) {
                    AnimatedVisibility(
                        visible = showSearchOptions,
                        enter = expandVertically(),
                        exit = shrinkVertically(),
                    ) {
                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            SearchParametersField(
                                currentSort = searchListViewModel.currentSort,
                                setCurrentSort = {
                                    searchListViewModel.currentSort = it
                                    searchListViewModel.updateSearch()
                                },
                                currentSearchType = searchListViewModel.currentSearchType,
                                setCurrentSearchType = {
                                    searchListViewModel.currentSearchType = it
                                    searchListViewModel.updateSearch()
                                },
                                searchTypeEnabled = !selectionMode,
                                currentListing = searchListViewModel.currentListing,
                                setCurrentListing = {
                                    searchListViewModel.currentListing = it
                                    searchListViewModel.updateSearch()
                                },
                            )
                        }
                    }

                    JerboaLoadingBar(searchListViewModel.searchRes)

                    when (val searchRes = searchListViewModel.searchRes) {
                        is ApiState.Failure -> {
                            ApiErrorText(searchRes.msg, onErrorBackground = true)
                        }

                        is ApiState.Holder -> {
                            val listState = rememberLazyListState()
                            val showAppendingRetry = searchListViewModel.searchRes is ApiState.AppendingFailure

                            if (!searchListViewModel.showingFollowedSuggestions) {
                                TriggerWhenReachingEnd(listState, showAppendingRetry) {
                                    Log.d("SearchScreen", "Reached end of list, loading next page")
                                    searchListViewModel.searchNextPage()
                                }
                            }

                            val nodes by remember { mutableStateOf(commentsToFlatNodes(searchRes.data.comments)) }

                            // Holds the un-expanded comment ids
                            val unExpandedComments = remember { mutableStateListOf<Long>() }
                            val commentsWithToggledActionBar = remember { mutableStateListOf<Long>() }

                            val toggleExpanded = { commentId: Long ->
                                if (unExpandedComments.contains(commentId)) {
                                    unExpandedComments.remove(commentId)
                                } else {
                                    unExpandedComments.add(commentId)
                                }
                            }

                            val toggleActionBar = { commentId: Long ->
                                if (commentsWithToggledActionBar.contains(commentId)) {
                                    commentsWithToggledActionBar.remove(commentId)
                                } else {
                                    commentsWithToggledActionBar.add(commentId)
                                }
                            }

                            val showActionBarByDefault = true

                            LazyColumn(
                                state = listState,
                            ) {
                                searchPersonListings(
                                    personViews = searchRes.data.users,
                                    onPersonClick = { personView ->
                                        if (searchTypeMode == SearchType.Users) {
                                            appState.apply {
                                                addReturn(SearchListReturn.PERSON_VIEW, personView)
                                                navigateUp()
                                            }
                                        } else {
                                            appState.toProfile(id = personView.person.id)
                                        }
                                    },
                                )
                                searchCommunityListings(
                                    communities = searchRes.data.communities,
                                    onClickCommunity = { cs ->
                                        if (searchTypeMode == SearchType.Communities) {
                                            appState.apply {
                                                addReturn(SearchListReturn.COMMUNITY, cs)
                                                navigateUp()
                                            }
                                        } else {
                                            appState.toCommunity(id = cs.id)
                                        }
                                    },
                                    blurNSFW = appSettings.blurNSFW.toEnum(),
                                    showAvatar = siteViewModel.showAvatar() && !lowBandwidthMode,
                                )

                                commentNodeItems(
                                    nodes = nodes,
                                    increaseLazyListIndexTracker = {},
                                    addToParentIndexes = {},
                                    isFlat = true,
                                    isExpanded = { commentId -> !unExpandedComments.contains(commentId) },
                                    toggleExpanded = { commentId -> toggleExpanded(commentId) },
                                    toggleActionBar = { commentId -> toggleActionBar(commentId) },
                                    onCommentClick = { cv ->
                                        if (searchTypeMode == SearchType.Comments) {
                                            appState.apply {
                                                addReturn(SearchListReturn.COMMENT_VIEW, cv)
                                                navigateUp()
                                            }
                                        } else {
                                            appState.toComment(id = cv.comment.id)
                                        }
                                    },
                                    onUpvoteClick = { cv ->
                                        account.doIfReadyElseDisplayInfo(
                                            appState,
                                            ctx,
                                            resources,
                                            snackbarHostState,
                                            scope,
                                            loginAsToast = true,
                                        ) {
                                            searchListViewModel.likeComment(
                                                CreateCommentLike(
                                                    comment_id = cv.comment.id,
                                                    score = newVote(cv.my_vote, VoteType.Upvote),
                                                ),
                                            )
                                        }
                                    },
                                    onDownvoteClick = { cv ->
                                        account.doIfReadyElseDisplayInfo(
                                            appState,
                                            ctx,
                                            resources,
                                            snackbarHostState,
                                            scope,
                                            loginAsToast = true,
                                        ) {
                                            account.doIfReadyElseDisplayInfo(
                                                appState,
                                                ctx,
                                                resources,
                                                snackbarHostState,
                                                scope,
                                                loginAsToast = true,
                                            ) {
                                                searchListViewModel.likeComment(
                                                    CreateCommentLike(
                                                        comment_id = cv.comment.id,
                                                        score = newVote(cv.my_vote, VoteType.Downvote),
                                                    ),
                                                )
                                            }
                                        }
                                    },
                                    onReplyClick = { cv ->
                                        appState.toCommentReply(
                                            replyItem = ReplyItem.CommentItem(cv),
                                        )
                                    },
                                    onSaveClick = { cv ->
                                        account.doIfReadyElseDisplayInfo(
                                            appState,
                                            ctx,
                                            resources,
                                            snackbarHostState,
                                            scope,
                                            loginAsToast = true,
                                        ) {
                                            searchListViewModel.saveComment(
                                                SaveComment(
                                                    comment_id = cv.comment.id,
                                                    save = !cv.saved,
                                                ),
                                            )
                                        }
                                    },
                                    onPersonClick = appState::toProfile,
                                    onHeaderClick = {},
                                    onHeaderLongClick = { commentView -> toggleActionBar(commentView.comment.id) },
                                    onCommunityClick = { community ->
                                        appState.toCommunity(id = community.id)
                                    },
                                    onPostClick = { postId ->
                                        appState.toPost(id = postId)
                                    },
                                    onEditCommentClick = { cv ->
                                        appState.toCommentEdit(
                                            commentView = cv,
                                        )
                                    },
                                    onDeleteCommentClick = { cv ->
                                        account.doIfReadyElseDisplayInfo(
                                            appState,
                                            ctx,
                                            resources,
                                            snackbarHostState,
                                            scope,
                                            loginAsToast = true,
                                        ) {
                                            searchListViewModel.deleteComment(
                                                DeleteComment(
                                                    comment_id = cv.comment.id,
                                                    deleted = !cv.comment.deleted,
                                                ),
                                            )
                                        }
                                    },
                                    onReportClick = { cv ->
                                        appState.toCommentReport(id = cv.comment.id)
                                    },
                                    onCommentLinkClick = { cv ->
                                        appState.toComment(id = cv.comment.id)
                                    },
                                    onFetchChildrenClick = {},
                                    onBlockCreatorClick = { person ->
                                        account.doIfReadyElseDisplayInfo(
                                            appState,
                                            ctx,
                                            resources,
                                            snackbarHostState,
                                            scope,
                                            loginAsToast = true,
                                        ) {
                                            searchListViewModel.blockPerson(
                                                BlockPerson(
                                                    person_id = person.id,
                                                    block = true,
                                                ),
                                                ctx,
                                            )
                                        }
                                    },
                                    showPostAndCommunityContext = true,
                                    showCollapsedCommentContent = true,
                                    isCollapsedByParent = false,
                                    showActionBar = { commentId ->
                                        showActionBarByDefault xor commentsWithToggledActionBar.contains(commentId)
                                    },
                                    account = account,
                                    enableDownVotes = siteViewModel.enableDownvotes(),
                                    showAvatar = siteViewModel.showAvatar() && !lowBandwidthMode,
                                    blurNSFW = appSettings.blurNSFW.toEnum(),
                                    admins = siteViewModel.admins(),
                                    moderators = null,
                                    onMarkAsReadClick = {},
                                    onRemoveClick = { cv ->
                                        appState.toCommentRemove(comment = cv.comment)
                                    },
                                    onDistinguishClick = { cv ->
                                        account.doIfReadyElseDisplayInfo(
                                            appState,
                                            ctx,
                                            resources,
                                            snackbarHostState,
                                            scope,
                                            loginAsToast = true,
                                        ) {
                                            scope.launch {
                                                API
                                                    .getInstance()
                                                    .distinguishComment(
                                                        DistinguishComment(
                                                            comment_id = cv.comment.id,
                                                            distinguished = !cv.comment.distinguished,
                                                        ),
                                                    ).onSuccess {
                                                        searchListViewModel.updateComment(it.comment_view)
                                                    }
                                            }
                                        }
                                    },
                                    onBanPersonClick = appState::toBanPerson,
                                    onBanFromCommunityClick = appState::toBanFromCommunity,
                                    onViewVotesClick = appState::toCommentLikes,
                                    voteDisplayMode = siteViewModel.voteDisplayMode(),
                                    swipeToActionPreset = appSettings.swipeToActionPreset.toEnum(),
                                )
                                items(searchRes.data.posts) { postView ->
                                    PostListing(
                                        postView = postView,
                                        onUpvoteClick = { pv ->
                                            account.doIfReadyElseDisplayInfo(
                                                appState,
                                                ctx,
                                                resources,
                                                snackbarHostState,
                                                scope,
                                                siteViewModel,
                                                accountViewModel,
                                            ) {
                                                searchListViewModel.likePost(
                                                    CreatePostLike(
                                                        post_id = pv.post.id,
                                                        score =
                                                            newVote(
                                                                postView.my_vote,
                                                                VoteType.Upvote,
                                                            ),
                                                    ),
                                                )
                                            }
                                        },
                                        onDownvoteClick = { pv ->
                                            account.doIfReadyElseDisplayInfo(
                                                appState,
                                                ctx,
                                                resources,
                                                snackbarHostState,
                                                scope,
                                                siteViewModel,
                                                accountViewModel,
                                            ) {
                                                searchListViewModel.likePost(
                                                    CreatePostLike(
                                                        post_id = pv.post.id,
                                                        score =
                                                            newVote(
                                                                postView.my_vote,
                                                                VoteType.Downvote,
                                                            ),
                                                    ),
                                                )
                                            }
                                        },
                                        onReplyClick = { pv ->
                                            appState.toCommentReply(
                                                replyItem = ReplyItem.PostItem(pv),
                                            )
                                        },
                                        onPostClick = {
                                            if (searchTypeMode == SearchType.Posts) {
                                                appState.apply {
                                                    addReturn(SearchListReturn.POST_VIEW, it)
                                                    navigateUp()
                                                }
                                            } else {
                                                appState.toPost(id = it.post.id)
                                            }
                                        },
                                        onSaveClick = { pv ->
                                            account.doIfReadyElseDisplayInfo(
                                                appState,
                                                ctx,
                                                resources,
                                                snackbarHostState,
                                                scope,
                                                siteViewModel,
                                                accountViewModel,
                                            ) {
                                                searchListViewModel.savePost(
                                                    SavePost(
                                                        post_id = pv.post.id,
                                                        save = !pv.saved,
                                                    ),
                                                )
                                            }
                                        },
                                        onCommunityClick = { community ->
                                            appState.toCommunity(id = community.id)
                                        },
                                        onEditPostClick = { pv ->
                                            appState.toPostEdit(
                                                postView = pv,
                                            )
                                        },
                                        onDeletePostClick = { pv ->
                                            account.doIfReadyElseDisplayInfo(
                                                appState,
                                                ctx,
                                                resources,
                                                snackbarHostState,
                                                scope,
                                                siteViewModel,
                                                accountViewModel,
                                            ) {
                                                searchListViewModel.deletePost(
                                                    DeletePost(
                                                        post_id = pv.post.id,
                                                        deleted = !pv.post.deleted,
                                                    ),
                                                )
                                            }
                                        },
                                        onReportClick = { pv ->
                                            appState.toPostReport(id = pv.post.id)
                                        },
                                        onPersonClick = appState::toProfile,
                                        showReply = true, // Do nothing
                                        fullBody = true,
                                        account = account,
                                        postViewMode = PostViewMode.List,
                                        enableDownVotes = siteViewModel.enableDownvotes(),
                                        showAvatar = siteViewModel.showAvatar(),
                                        appState = appState,
                                        showIfRead = false,
                                        blurNSFW = appSettings.blurNSFW.toEnum(),
                                        showPostLinkPreview = appSettings.showPostLinkPreviews,
                                        postActionBarMode = appSettings.postActionBarMode.toEnum(),
                                        showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                                        useCustomTabs = appSettings.useCustomTabs,
                                        usePrivateTabs = appSettings.usePrivateTabs,
                                        admins = siteViewModel.admins(),
                                        moderators = null,
                                        onHidePostClick = { pv ->
                                            account.doIfReadyElseDisplayInfo(
                                                appState,
                                                ctx,
                                                resources,
                                                snackbarHostState,
                                                scope,
                                                siteViewModel,
                                                accountViewModel,
                                            ) {
                                                val hide = !pv.hidden
                                                scope.launch {
                                                    API
                                                        .getInstance()
                                                        .hidePost(
                                                            HidePost(
                                                                post_ids = listOf(pv.post.id),
                                                                hide = hide,
                                                            ),
                                                        ).onSuccess {
                                                            searchListViewModel.updatePost(pv.copy(hidden = hide))
                                                        }
                                                }
                                            }
                                        },
                                        onRemoveClick = { pv ->
                                            appState.toPostRemove(post = pv.post)
                                        },
                                        onBanPersonClick = appState::toBanPerson,
                                        onBanFromCommunityClick = appState::toBanFromCommunity,
                                        onLockPostClick = { pv ->
                                            account.doIfReadyElseDisplayInfo(
                                                appState,
                                                ctx,
                                                resources,
                                                snackbarHostState,
                                                scope,
                                                siteViewModel,
                                                accountViewModel,
                                            ) {
                                                scope.launch {
                                                    API
                                                        .getInstance()
                                                        .lockPost(
                                                            LockPost(
                                                                post_id = pv.post.id,
                                                                locked = !pv.post.locked,
                                                            ),
                                                        ).onSuccess {
                                                            searchListViewModel.updatePost(it.post_view)
                                                        }
                                                }
                                            }
                                        },
                                        onFeaturePostClick = { data ->
                                            account.doIfReadyElseDisplayInfo(
                                                appState,
                                                ctx,
                                                resources,
                                                snackbarHostState,
                                                scope,
                                                siteViewModel,
                                                accountViewModel,
                                            ) {
                                                scope.launch {
                                                    API
                                                        .getInstance()
                                                        .featurePost(
                                                            FeaturePost(
                                                                post_id = data.post.id,
                                                                featured = !data.featured,
                                                                feature_type = data.type,
                                                            ),
                                                        ).onSuccess {
                                                            searchListViewModel.updatePost(it.post_view)
                                                        }
                                                }
                                            }
                                        },
                                        onViewVotesClick = appState::toPostLikes,
                                        showCommunityName = true,
                                        voteDisplayMode = siteViewModel.voteDisplayMode(),
                                        swipeToActionPreset = appSettings.swipeToActionPreset.toEnum(),
                                        disableVideoAutoplay = appSettings.disableVideoAutoplay.toBool(),
                                        lowBandwidthMode = lowBandwidthMode,
                                    )
                                    HorizontalDivider()
                                }

                                if (showAppendingRetry) {
                                    item(contentType = "retry_posts") {
                                        RetryLoadingPosts(searchListViewModel::searchNextPage)
                                    }
                                }
                            }
                        }

                        else -> {}
                    }
                }
            },
        )
    }
}
