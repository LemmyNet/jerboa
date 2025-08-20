package com.jerboa.ui.components.reports

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.UnreadOrAll
import com.jerboa.api.ApiState
import com.jerboa.db.entity.Account
import com.jerboa.feat.BlurNSFW
import com.jerboa.feat.doIfReadyElseDisplayInfo
import com.jerboa.model.AccountViewModel
import com.jerboa.model.ReportsViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.JerboaLoadingBar
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.TriggerWhenReachingEnd
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.isRefreshing
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.unreadOrAllFromBool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    appState: JerboaAppState,
    drawerState: DrawerState,
    siteViewModel: SiteViewModel,
    accountViewModel: AccountViewModel,
    blurNSFW: BlurNSFW,
    padding: PaddingValues? = null,
) {
    Log.d("jerboa", "got to reports screen")

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val reportsViewModel: ReportsViewModel =
        viewModel(factory = ReportsViewModel.Companion.Factory(account, siteViewModel))

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

    Scaffold(
        modifier = baseModifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
        topBar = {
            ReportsHeader(
                scrollBehavior = scrollBehavior,
                unreadCount = siteViewModel.unreadReportCount,
                openDrawer = {
                    scope.launch {
                        drawerState.open()
                    }
                },
                selectedUnreadOrAll = unreadOrAllFromBool(reportsViewModel.unresolvedOnly),
                onClickUnreadOrAll = { unreadOrAll ->
                    account.doIfReadyElseDisplayInfo(
                        appState,
                        ctx,
                        snackbarHostState,
                        scope,
                        siteViewModel,
                        accountViewModel,
                        loginAsToast = true,
                    ) {
                        reportsViewModel.resetPages()
                        reportsViewModel.updateUnresolvedOnly(unreadOrAll == UnreadOrAll.Unread)
                        reportsViewModel.listPostReports(
                            reportsViewModel.getFormPostReports(),
                        )
                        reportsViewModel.listCommentReports(
                            reportsViewModel.getFormCommentReports(),
                        )
                        reportsViewModel.listMessageReports(
                            reportsViewModel.getFormMessageReports(),
                        )
                    }
                },
            )
        },
        content = {
            ReportsTabs(
                padding = it,
                appState = appState,
                reportsViewModel = reportsViewModel,
                siteViewModel = siteViewModel,
                ctx = ctx,
                account = account,
                scope = scope,
                blurNSFW = blurNSFW,
                snackbarHostState = snackbarHostState,
            )
        },
    )
}

enum class ReportsTab(
    @param:StringRes val textId: Int,
    val adminOnly: Boolean = false,
) {
    Posts(R.string.person_profile_screen_posts),
    Comments(R.string.post_screen_comments),
    Messages(R.string.inbox_screen_messages, true),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsTabs(
    appState: JerboaAppState,
    reportsViewModel: ReportsViewModel,
    siteViewModel: SiteViewModel,
    ctx: Context,
    account: Account,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    padding: PaddingValues,
    blurNSFW: BlurNSFW,
) {
    val tabs = remember(account.isAdmin) {
        ReportsTab.entries
            .filter { account.isAdmin || !it.adminOnly }
    }
    val pagerState = rememberPagerState { tabs.size }

    Column(
        modifier = Modifier.padding(padding),
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            tabs = {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(text = stringResource(id = tab.textId)) },
                    )
                }
            },
        )
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxSize(),
        ) { tabIndex ->
            when (tabIndex) {
                ReportsTab.Posts.ordinal -> {
                    val listState = rememberLazyListState()

                    TriggerWhenReachingEnd(listState, false) {
                        account.doIfReadyElseDisplayInfo(
                            appState,
                            ctx,
                            snackbarHostState,
                            scope,
                            siteViewModel,
                        ) {
                            reportsViewModel.appendPostReports()
                        }
                    }

                    PullToRefreshBox(
                        isRefreshing = reportsViewModel.postReportsRes.isRefreshing(),
                        onRefresh = {
                            account.doIfReadyElseDisplayInfo(
                                appState,
                                ctx,
                                snackbarHostState,
                                scope,
                                siteViewModel,
                            ) {
                                reportsViewModel.resetPagePostReports()
                                reportsViewModel.listPostReports(
                                    reportsViewModel.getFormPostReports(),
                                    ApiState.Refreshing,
                                )
                                siteViewModel.fetchUnreadReportCount()
                            }
                        },
                    ) {
                        JerboaLoadingBar(reportsViewModel.postReportsRes)

                        when (val reportsRes = reportsViewModel.postReportsRes) {
                            ApiState.Empty -> ApiEmptyText()
                            is ApiState.Failure -> ApiErrorText(reportsRes.msg)
                            is ApiState.Holder -> {
                                val reports = reportsRes.data.post_reports
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .simpleVerticalScrollbar(listState),
                                ) {
                                    items(
                                        reports,
                                        key = { report -> report.post_report.id },
                                        contentType = { "postReport" },
                                    ) { reportView ->
                                        PostReportItem(
                                            postReportView = reportView,
                                            blurNSFW = blurNSFW,
                                            showAvatar = siteViewModel.showAvatar(),
                                            onCommunityClick = { community ->
                                                appState.toCommunity(id = community.id)
                                            },
                                            onPersonClick = appState::toProfile,
                                            onPostClick = { pv ->
                                                appState.toPost(id = pv.post.id)
                                            },
                                            onResolveClick = { form ->
                                                account.doIfReadyElseDisplayInfo(
                                                    appState,
                                                    ctx,
                                                    snackbarHostState,
                                                    scope,
                                                    siteViewModel,
                                                ) {
                                                    reportsViewModel.resolvePostReport(
                                                        form = form,
                                                    )
                                                }
                                            },
                                        )
                                    }
                                }
                            }

                            else -> {}
                        }
                    }
                }

                ReportsTab.Comments.ordinal -> {
                    val listState = rememberLazyListState()

                    TriggerWhenReachingEnd(listState, false) {
                        account.doIfReadyElseDisplayInfo(
                            appState,
                            ctx,
                            snackbarHostState,
                            scope,
                            siteViewModel,
                        ) {
                            reportsViewModel.appendCommentReports()
                        }
                    }

                    PullToRefreshBox(
                        isRefreshing = reportsViewModel.commentReportsRes.isRefreshing(),
                        onRefresh = {
                            account.doIfReadyElseDisplayInfo(
                                appState,
                                ctx,
                                snackbarHostState,
                                scope,
                                siteViewModel,
                            ) {
                                reportsViewModel.resetPageCommentReports()
                                reportsViewModel.listCommentReports(
                                    reportsViewModel.getFormCommentReports(),
                                    ApiState.Refreshing,
                                )
                                siteViewModel.fetchUnreadReportCount()
                            }
                        },
                    ) {
                        JerboaLoadingBar(reportsViewModel.commentReportsRes)

                        when (val reportsRes = reportsViewModel.commentReportsRes) {
                            ApiState.Empty -> ApiEmptyText()
                            is ApiState.Failure -> ApiErrorText(reportsRes.msg)
                            is ApiState.Holder -> {
                                val reports = reportsRes.data.comment_reports
                                LazyColumn(
                                    state = listState,
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .simpleVerticalScrollbar(listState),
                                ) {
                                    items(
                                        reports,
                                        key = { report -> report.comment_report.id },
                                        contentType = { "commentReport" },
                                    ) { reportView ->
                                        CommentReportItem(
                                            commentReportView = reportView,
                                            showAvatar = siteViewModel.showAvatar(),
                                            onPersonClick = appState::toProfile,
                                            onCommentClick = appState::toComment,
                                            onResolveClick = { form ->
                                                account.doIfReadyElseDisplayInfo(
                                                    appState,
                                                    ctx,
                                                    snackbarHostState,
                                                    scope,
                                                    siteViewModel,
                                                ) {
                                                    reportsViewModel.resolveCommentReport(
                                                        form = form,
                                                    )
                                                }
                                            },
                                        )
                                    }
                                }
                            }

                            else -> {}
                        }
                    }
                }

                ReportsTab.Messages.ordinal -> {
                    val listState = rememberLazyListState()

                    TriggerWhenReachingEnd(listState, false) {
                        account.doIfReadyElseDisplayInfo(
                            appState,
                            ctx,
                            snackbarHostState,
                            scope,
                            siteViewModel,
                        ) {
                            reportsViewModel.appendMessageReports()
                        }
                    }

                    PullToRefreshBox(
                        isRefreshing = reportsViewModel.messageReportsRes.isRefreshing(),
                        onRefresh = {
                            account.doIfReadyElseDisplayInfo(
                                appState,
                                ctx,
                                snackbarHostState,
                                scope,
                                siteViewModel,
                            ) {
                                reportsViewModel.resetPageMessageReports()
                                reportsViewModel.listMessageReports(
                                    reportsViewModel.getFormMessageReports(),
                                    ApiState.Refreshing,
                                )
                                siteViewModel.fetchUnreadReportCount()
                            }
                        },
                    ) {
                        JerboaLoadingBar(reportsViewModel.messageReportsRes)

                        when (val reportsRes = reportsViewModel.messageReportsRes) {
                            ApiState.Empty -> ApiEmptyText()
                            is ApiState.Failure -> ApiErrorText(reportsRes.msg)
                            is ApiState.Holder -> {
                                val reports = reportsRes.data.private_message_reports
                                LazyColumn(
                                    state = listState,
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .simpleVerticalScrollbar(listState),
                                ) {
                                    items(
                                        reports,
                                        key = { report -> report.private_message_report.id },
                                        contentType = { "messageReport" },
                                    ) { reportView ->

                                        MessageReportItem(
                                            messageReportView = reportView,
                                            onResolveClick = { form ->
                                                account.doIfReadyElseDisplayInfo(
                                                    appState,
                                                    ctx,
                                                    snackbarHostState,
                                                    scope,
                                                    siteViewModel,
                                                ) {
                                                    reportsViewModel.resolveMessageReport(
                                                        form = form,
                                                    )
                                                }
                                            },
                                            onPersonClick = appState::toProfile,
                                            showAvatar = siteViewModel.showAvatar(),
                                        )
                                    }
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }
}
