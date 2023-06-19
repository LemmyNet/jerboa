package com.jerboa.ui.components.community.list

import android.util.Log
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.DEBOUNCE_DELAY
import com.jerboa.api.ApiState
import com.jerboa.datatypes.types.Search
import com.jerboa.datatypes.types.SearchType
import com.jerboa.datatypes.types.SortType
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.InitializeRoute
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.home.SiteViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var fetchCommunitiesJob: Job? = null

@Composable
fun CommunityListActivity(
    navController: CommunityListNavController,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,

    // If the callback is provided then selecting a community will trigger this callback and the
    // activity will pop itself. Otherwise, the community page is opened. This is a replacement for
    // select mode that existed previously.
    onSelectCommunity: OnSelectCommunity?,
) {
    Log.d("jerboa", "got to community list activity")

    val account = getCurrentAccount(accountViewModel = accountViewModel)

    var search by rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    val communityListViewModel: CommunityListViewModel = viewModel()
    InitializeRoute {
        // TODO: Followed list is set from siteResponse.
        //  Following/Unfollowing communities doesn't refresh siteResponse.

        // Whenever navigating here, reset the list with your followed communities
        communityListViewModel.setCommunityListFromFollowed(siteViewModel)
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            topBar = {
                CommunityListHeader(
                    navController = navController,
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
                                if (onSelectCommunity != null) {
                                    onSelectCommunity(cs)
                                    navController.navigateUp()
                                } else {
                                    navController.toCommunity.navigate(cs.id)
                                }
                            },
                            modifier = Modifier
                                .padding(padding)
                                .imePadding(),
                        )
                    }
                }
            },
        )
    }
}
