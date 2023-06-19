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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.jerboa.DEBOUNCE_DELAY
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.loginFirstToast
import com.jerboa.ui.components.common.BottomAppBarAll
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.home.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var fetchCommunitiesJob: Job? = null

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

    val ctx = LocalContext.current
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
                                query = search,
                                account = account,
                                ctx = ctx,
                            )
                        }
                    },
                )
            },
            content = { padding ->
                if (communityListViewModel.loading.value) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    CommunityListings(
                        communities = communityListViewModel.communityList,
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
