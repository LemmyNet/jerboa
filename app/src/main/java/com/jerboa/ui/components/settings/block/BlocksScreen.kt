package com.jerboa.ui.components.settings.block

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.R
import com.jerboa.api.ApiAction
import com.jerboa.api.ApiState
import com.jerboa.model.BlockViewModel
import com.jerboa.model.MyUserInfoViewModel
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.ItemAndInstanceTitle
import com.jerboa.ui.components.common.JerboaLoadingBar
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.theme.MEDIUM_PADDING
import it.vercruysse.lemmyapi.datatypes.MyUserInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlocksScreen(
    myUserInfoViewModel: MyUserInfoViewModel,
    onBack: () -> Unit,
) {
    val blockViewModel: BlockViewModel = viewModel()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = stringResource(R.string.blocks), onClickBack = onBack)
        },
        content = { padding ->
            PullToRefreshBox(
                modifier = Modifier.padding(padding),
                isRefreshing = myUserInfoViewModel.myUserRes is ApiState.Loading,
                onRefresh = { myUserInfoViewModel.getMyUser() },
            ) {
                JerboaLoadingBar(myUserInfoViewModel.myUserRes)

                when (val res = myUserInfoViewModel.myUserRes) {
                    is ApiState.Failure -> {
                        ApiErrorText(res.msg)
                    }

                    is ApiState.Success -> {
                        BlockList(
                            blockViewModel = blockViewModel,
                            myUserInfo = res.data
                        )
                    }
                    else -> {
                    }
                }
            }
        },
    )
}

enum class BlocksTab(
    @param:StringRes val label: Int,
) {
    InstanceCommunities(R.string.communities_from_these_instances),
    InstancePersons(R.string.users_from_these_instances),
    Communities(R.string.communities),
    Users(R.string.users),
}

@Composable
fun BlockList(
    blockViewModel: BlockViewModel,
    myUserInfo: MyUserInfo
) {
    val ctx = LocalContext.current
    val resources = LocalResources.current
    val scope = rememberCoroutineScope()
    val tabTitles = BlocksTab.entries.map { resources.getString(it.label) }
    val pagerState = rememberPagerState { tabTitles.size }

    LaunchedEffect(myUserInfo) { blockViewModel.initData(myUserInfo) }

    Column {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(text = title) },
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxSize(),
        ) { tabIndex ->
            LazyColumn(contentPadding = PaddingValues(MEDIUM_PADDING, 0.dp)) {
                when (tabIndex) {
                    BlocksTab.InstanceCommunities.ordinal -> {
                        itemsWithEmpty(
                            items = blockViewModel.instanceCommunitiesBlocks.value,
                            key = { "IC${it.data.id}" },
                            emptyText = R.string.you_have_no_blocked_instances,
                        ) {
                            ItemBlockView(it.data.domain, null, it) {
                                blockViewModel.unBlockInstanceCommunities(it.data, ctx)
                            }
                        }
                    }

                    BlocksTab.InstancePersons.ordinal -> {
                        itemsWithEmpty(
                            items = blockViewModel.instancePersonsBlocks.value,
                            key = { "IP${it.data.id}" },
                            emptyText = R.string.you_have_no_blocked_instances,
                        ) {
                            ItemBlockView(it.data.domain, null, it) {
                                blockViewModel.unBlockInstancePersons(it.data, ctx)
                            }
                        }
                    }

                    BlocksTab.Communities.ordinal -> {
                        itemsWithEmpty(
                            items = blockViewModel.communityBlocks.value,
                            key = { "C${it.data.id}" },
                            emptyText = R.string.you_have_no_blocked_communities,
                        ) {
                            ItemBlockView(it.data.name, it.data.ap_id, it) {
                                blockViewModel.unBlockCommunity(it.data, ctx)
                            }
                        }
                    }

                    BlocksTab.Users.ordinal -> {
                        itemsWithEmpty(
                            items = blockViewModel.personBlocks.value,
                            key = { "U${it.data.id}" },
                            emptyText = R.string.you_have_no_blocked_users,
                        ) {
                            ItemBlockView(it.data.name, it.data.ap_id, it) {
                                blockViewModel.unBlockPerson(it.data, ctx)
                            }
                        }
                    }

                    else -> {
                    }
                }
            }
        }
    }
}

inline fun <T> LazyListScope.itemsWithEmpty(
    items: List<T>,
    noinline key: (T) -> Any,
    @StringRes emptyText: Int,
    crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit,
) {
    if (items.isEmpty()) {
        item(
            contentType = "blockEmpty",
        ) {
            Box(
                modifier = Modifier.fillParentMaxSize(),
                contentAlignment = Alignment.Center,
            ) { Text(stringResource(emptyText)) }
        }
    } else {
        items(
            items = items,
            key = key,
            contentType = { "blockItem" },
            itemContent = itemContent,
        )
    }
}

@Composable
fun ItemBlockView(
    name: String,
    actor: String?,
    action: ApiAction<*>,
    onUnblock: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ItemAndInstanceTitle(
            title = name,
            apId = actor,
            local = false,
            onClick = null,
        )

        IconButton(
            onClick = onUnblock,
            enabled = action !is ApiAction.Loading,
        ) {
            when (action) {
                is ApiAction.Loading -> {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                }

                is ApiAction.Failed -> {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = stringResource(id = R.string.retry),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }

                is ApiAction.Ok -> {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(id = R.string.unblock),
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
    }
}
