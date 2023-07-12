package com.jerboa.ui.components.community.list

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jerboa.DEBOUNCE_DELAY
import com.jerboa.api.ApiState
import com.jerboa.datatypes.types.Search
import com.jerboa.datatypes.types.SearchType
import com.jerboa.datatypes.types.SortType
import com.jerboa.db.AccountViewModel
import com.jerboa.db.SearchHistoryViewModel
import com.jerboa.model.CommunityListViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.InitializeRoute
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.addReturn
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.toCommunity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object CommunityListReturn {
    const val COMMUNITY = "community-list::return(community)"
}

@Composable
fun CommunityListActivity(
    navController: NavController,
    accountViewModel: AccountViewModel,
    selectMode: Boolean = false,
    siteViewModel: SiteViewModel,
    blurNSFW: Boolean,
    drawerState: DrawerState,
) {
    Log.d("jerboa", "got to community list activity")

    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val communityListViewModel: CommunityListViewModel = viewModel()
    InitializeRoute(communityListViewModel) {
        // Whenever navigating here, reset the list with your followed communities
        communityListViewModel.setCommunityListFromFollowed(siteViewModel)
    }

    val searchHistoryViewModel: SearchHistoryViewModel = viewModel()

    var search by rememberSaveable { mutableStateOf("") }
    val searchHistory by searchHistoryViewModel.searchHistory.observeAsState()

    val scope = rememberCoroutineScope()
    var fetchCommunitiesJob by remember { mutableStateOf<Job?>(null) }

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            topBar = {
                CommunityListHeader(
                    openDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    search = search,
                    onSearchChange = {
                        search = it
                        fetchCommunitiesJob?.cancel()
                        fetchCommunitiesJob = scope.launch {
                            delay(DEBOUNCE_DELAY)
                            communityListViewModel.searchCommunities(
                                form = Search(
                                    q = search,
                                    type_ = SearchType.Communities,
                                    sort = SortType.TopAll,
                                    auth = account?.jwt,
                                ),
                            )
                        }
                    },
                )
            },
            content = { padding ->
                when (val communitiesRes = communityListViewModel.searchRes) {
                    ApiState.Empty -> {
                        Column(
                            modifier = Modifier
                                .padding(padding)
                                .imePadding()
                        ) {
                            searchHistory?.let { history ->
                                SearchHistoryList(
                                    history = history,
                                    onHistoryItemClicked = {
                                        scope.launch {
                                            search = it.searchTerm
                                            communityListViewModel.searchAllCommunities(
                                                it.text,
                                                account?.jwt,
                                            )
                                        }
                                    },
                                    onHistoryItemDeleted = {
                                        scope.launch {
                                            searchHistoryViewModel.delete(it)
                                        }
                                    }
                                )
                            }
                            CommunityListings(
                                communities = communityListViewModel.communities,
                                onClickCommunity = { cs ->
                                    if (selectMode) {
                                        communityListViewModel.selectCommunity(cs)
                                        navController.navigateUp()
                                    } else {
                                        navController.navigate(route = "community/${cs.id}")
                                    }
                                },
                                blurNSFW = blurNSFW,
                            )
                        }
                    }
                    is ApiState.Failure -> ApiErrorText(communitiesRes.msg)
                    ApiState.Loading -> {
                        LoadingBar(padding)
                    }

                    is ApiState.Success -> {
                        CommunityListings(
                            communities = communitiesRes.data.communities,
                            onClickCommunity = { cs ->
                                if (selectMode) {
                                    navController.apply {
                                        addReturn(CommunityListReturn.COMMUNITY, cs)
                                        navigateUp()
                                    }
                                } else {
                                    navController.toCommunity(id = cs.id)
                                }
                            },
                            modifier = Modifier
                                .padding(padding)
                                .imePadding(),
                            blurNSFW = blurNSFW,
                        )
                    }
                    else -> {}
                }
            },
        )
    }
}
