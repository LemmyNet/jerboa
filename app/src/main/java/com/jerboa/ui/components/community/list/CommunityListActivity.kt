package com.jerboa.ui.components.community.list

import android.util.Log
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.jerboa.model.AccountViewModel
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

private var fetchCommunitiesJob: Job? = null

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

    var search by rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()

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
                    ApiState.Empty -> ApiEmptyText()
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
