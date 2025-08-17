package com.jerboa.ui.components.settings.block

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.R
import com.jerboa.api.ApiAction
import com.jerboa.api.ApiState
import com.jerboa.model.BlockViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.ItemAndInstanceTitle
import com.jerboa.ui.components.common.JerboaLoadingBar
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.Title
import com.jerboa.ui.theme.MEDIUM_PADDING
import it.vercruysse.lemmyapi.datatypes.MyUserInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlocksScreen(
    siteViewModel: SiteViewModel,
    onBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        Log.d("BlocksScreen", "Refreshing site")
        when (val res = siteViewModel.siteRes) {
            is ApiState.Success -> siteViewModel.getSite(ApiState.Appending(res.data))
            is ApiState.Loading, is ApiState.Appending -> {}
            else -> siteViewModel.getSite()
        }
    }

    Scaffold(
        snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = stringResource(R.string.blocks), onClickBack = onBack)
        },
        content = { padding ->
            PullToRefreshBox(
                modifier = Modifier.padding(padding),
                isRefreshing = siteViewModel.siteRes is ApiState.Loading,
                onRefresh = { siteViewModel.getSite() },
            ) {
                JerboaLoadingBar(siteViewModel.siteRes)

                when (val res = siteViewModel.siteRes) {
                    is ApiState.Failure -> ApiErrorText(res.msg)
                    is ApiState.Holder -> {
                        res.data.my_user?.let {
                            BlockList(it)
                        } ?: ApiEmptyText()
                    }

                    else -> Unit
                }
            }
        },
    )
}

enum class BlocksTab(
    @param:StringRes val label: Int,
) {
    Instances(R.string.instances),
    Communities(R.string.communities),
    Users(R.string.users),
}

@Composable
fun BlockList(userInfo: MyUserInfo) {
    val ctx = LocalContext.current
    val viewModel: BlockViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val tabTitles = BlocksTab.entries.map { ctx.getString(it.label) }
    val pagerState = rememberPagerState { tabTitles.size }

    LaunchedEffect(userInfo) {
        viewModel.initData(userInfo)
    }

    Column {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
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
                    BlocksTab.Instances.ordinal -> {
                        itemsWithEmpty(
                            items = viewModel.instanceBlocks.value,
                            key = { "I${it.data.id}" },
                            emptyText = R.string.you_have_no_blocked_instances,
                        ) {
                            ItemBlockView(it.data.domain, null, it) {
                                viewModel.unBlockInstance(it.data, ctx)
                            }
                        }
                    }

                    BlocksTab.Communities.ordinal -> {
                        itemsWithEmpty(
                            items = viewModel.communityBlocks.value,
                            key = { "C${it.data.id}" },
                            emptyText = R.string.you_have_no_blocked_communities,
                        ) {
                            ItemBlockView(it.data.name, it.data.actor_id, it) {
                                viewModel.unBlockCommunity(it.data, ctx)
                            }
                        }
                    }

                    BlocksTab.Users.ordinal -> {
                        itemsWithEmpty(
                            items = viewModel.personBlocks.value,
                            key = { "U${it.data.id}" },
                            emptyText = R.string.you_have_no_blocked_users,
                        ) {
                            ItemBlockView(it.data.name, it.data.actor_id, it) {
                                viewModel.unBlockPerson(it.data, ctx)
                            }
                        }
                    }

                    else -> Unit
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
            ) {
                Text(stringResource(emptyText))
            }
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
            actorId = actor,
            local = false,
            onClick = null,
        )

        IconButton(
            onClick = onUnblock,
            enabled = action !is ApiAction.Loading,
        ) {
            when (action) {
                is ApiAction.Loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                is ApiAction.Failed -> Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = stringResource(id = R.string.retry),
                    tint = MaterialTheme.colorScheme.error,
                )

                is ApiAction.Ok -> Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(id = R.string.unblock),
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.blockListHeader(
    @StringRes resId: Int,
) {
    stickyHeader(
        contentType = "blockHeader",
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Title(stringResource(id = resId))
        }
    }
}
