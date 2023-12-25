package com.jerboa.ui.components.search

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.JerboaAppState
import com.jerboa.PostViewMode
import com.jerboa.VoteType
import com.jerboa.api.ApiState
import com.jerboa.commentsToFlatNodes
import com.jerboa.db.entity.AppSettings
import com.jerboa.feat.doIfReadyElseDisplayInfo
import com.jerboa.isScrolledToEnd
import com.jerboa.model.AccountViewModel
import com.jerboa.model.ReplyItem
import com.jerboa.model.SearchListViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.newVote
import com.jerboa.ui.components.comment.commentNodeItems
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.components.post.PostListing
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockPerson
import it.vercruysse.lemmyapi.v0x19.datatypes.CommunityFollowerView
import it.vercruysse.lemmyapi.v0x19.datatypes.CreateCommentLike
import it.vercruysse.lemmyapi.v0x19.datatypes.CreatePostLike
import it.vercruysse.lemmyapi.v0x19.datatypes.DeleteComment
import it.vercruysse.lemmyapi.v0x19.datatypes.DeletePost
import it.vercruysse.lemmyapi.v0x19.datatypes.SaveComment
import it.vercruysse.lemmyapi.v0x19.datatypes.SavePost
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

object SearchListReturn {
    const val COMMUNITY = "search-list::return(community)"
}

@Composable
fun SearchActivity(
    appState: JerboaAppState,
    selectCommunityMode: Boolean = false,
    followList: ImmutableList<CommunityFollowerView>,
    appSettings: AppSettings,
    drawerState: DrawerState,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
) {
    Log.d("SearchActivity", "Arrived")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showSearchOptions by rememberSaveable { mutableStateOf(false) }
    val searchListViewModel: SearchListViewModel =
        viewModel(factory = SearchListViewModel.Companion.Factory(followList, selectCommunityMode))

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
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
                    if (searchListViewModel.loading || searchListViewModel.searchRes is ApiState.Loading) {
                        LoadingBar()
                    }

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
                                currentListing = searchListViewModel.currentListing,
                                setCurrentListing = {
                                    searchListViewModel.currentListing = it
                                    searchListViewModel.updateSearch()
                                },
                            )
                        }
                    }

                    when (val communitiesRes = searchListViewModel.searchRes) {
                        is ApiState.Failure -> ApiErrorText(communitiesRes.msg)
                        is ApiState.Success -> {
                            val listState = rememberLazyListState()

                            val endOfListReached by remember {
                                derivedStateOf {
                                    listState.isScrolledToEnd()
                                }
                            }

                            if (endOfListReached) {
                                LaunchedEffect(Unit) {
                                    searchListViewModel.searchNextPage(ctx)
                                }
                            }

                            val nodes by remember { mutableStateOf(commentsToFlatNodes(communitiesRes.data.comments)) }

                            // Holds the un-expanded comment ids
                            val unExpandedComments = remember { mutableStateListOf<Int>() }
                            val commentsWithToggledActionBar = remember { mutableStateListOf<Int>() }

                            val toggleExpanded = { commentId: Int ->
                                if (unExpandedComments.contains(commentId)) {
                                    unExpandedComments.remove(commentId)
                                } else {
                                    unExpandedComments.add(commentId)
                                }
                            }

                            val toggleActionBar = { commentId: Int ->
                                if (commentsWithToggledActionBar.contains(commentId)) {
                                    commentsWithToggledActionBar.remove(commentId)
                                } else {
                                    commentsWithToggledActionBar.add(commentId)
                                }
                            }

                            val showActionBarByDefault = true

                            LazyColumn(
                                state = listState,
                                modifier = Modifier.simpleVerticalScrollbar(listState),
                            ) {
                                searchPersonListings(
                                    personViews = communitiesRes.data.users,
                                    onPersonClick = { personView ->
                                        appState.toProfile(id = personView.person.id)
                                    },
                                )
                                searchCommunityListings(
                                    communities = communitiesRes.data.communities,
                                    onClickCommunity = { cs ->
                                        if (selectCommunityMode) {
                                            appState.apply {
                                                addReturn(SearchListReturn.COMMUNITY, cs)
                                                navigateUp()
                                            }
                                        } else {
                                            appState.toCommunity(id = cs.id)
                                        }
                                    },
                                    blurNSFW = appSettings.blurNSFW,
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
                                        appState.toComment(id = cv.comment.id)
                                    },
                                    onUpvoteClick = { cv ->
                                        account.doIfReadyElseDisplayInfo(
                                            appState,
                                            ctx,
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
                                            snackbarHostState,
                                            scope,
                                            loginAsToast = true,
                                        ) {
                                            account.doIfReadyElseDisplayInfo(
                                                appState,
                                                ctx,
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
                                    showAvatar = siteViewModel.showAvatar(),
                                    showScores = siteViewModel.showScores(),
                                    blurNSFW = appSettings.blurNSFW,
                                )
                                items(communitiesRes.data.posts) { postView ->
                                    PostListing(
                                        postView = postView,
                                        onUpvoteClick = { pv ->
                                            account.doIfReadyElseDisplayInfo(
                                                appState,
                                                ctx,
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
                                        onPostClick = {},
                                        onSaveClick = { pv ->
                                            account.doIfReadyElseDisplayInfo(
                                                appState,
                                                ctx,
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
                                        showScores = siteViewModel.showScores(),
                                        appState = appState,
                                        showIfRead = false,
                                        blurNSFW = appSettings.blurNSFW,
                                        showPostLinkPreview = appSettings.showPostLinkPreviews,
                                        postActionbarMode = appSettings.postActionbarMode,
                                        showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
                                        useCustomTabs = appSettings.useCustomTabs,
                                        usePrivateTabs = appSettings.usePrivateTabs,
                                    )
                                    Divider()
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
