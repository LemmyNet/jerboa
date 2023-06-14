@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.community.list

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.navigation.NavController
import com.jerboa.DEBOUNCE_DELAY
import com.jerboa.api.ApiState
import com.jerboa.datatypes.types.Search
import com.jerboa.datatypes.types.SearchType
import com.jerboa.datatypes.types.SortType
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.home.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var fetchCommunitiesJob: Job? = null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityListActivity(
    navController: NavController,
    communityListViewModel: CommunityListViewModel,
    accountViewModel: AccountViewModel,
    homeViewModel: HomeViewModel,
    appSettingsViewModel: AppSettingsViewModel,
    selectMode: Boolean = false,
) {
    Log.d("jerboa", "got to community list activity")

    val account = getCurrentAccount(accountViewModel = accountViewModel)

    var search by rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()

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
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    is ApiState.Success -> {
                        CommunityListings(
                            communities = communitiesRes.data.communities,
                            onClickCommunity = { cs ->
                                if (selectMode) {
                                    communityListViewModel.selectCommunity(cs)
                                    navController.navigateUp()
                                } else {
                                    navController.navigate(route = "community/${cs.id}")
                                }
                            },
                            modifier = Modifier
                                .padding(padding)
                                .imePadding(),
                        )
                    }
                }
            },
            bottomBar = {
                BottomAppBarAll(
                    showBottomNav = appSettingsViewModel.appSettings.value?.showBottomNav,
                    screen = "communityList",
                    unreadCounts = homeViewModel.unreadCountResponse,
                    onClickProfile = {
                        account?.id?.also {
                            navController.navigate(route = "profile/$it")
                        } ?: run {
                            loginFirstToast(ctx)
                        }
                    },
                    onClickInbox = {
                        account?.also {
                            navController.navigate(route = "inbox")
                        } ?: run {
                            loginFirstToast(ctx)
                        }
                    },
                    onClickSaved = {
                        account?.id?.also {
                            navController.navigate(route = "profile/$it?saved=${true}")
                        } ?: run {
                            loginFirstToast(ctx)
                        }
                    },
                    navController = navController,
                )
            },
        )
    }
}
