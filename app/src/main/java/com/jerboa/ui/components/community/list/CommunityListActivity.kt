package com.jerboa.ui.components.community.list

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.jerboa.DEBOUNCE_DELAY
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.community.CommunityViewModel
import com.jerboa.ui.components.community.communityClickWrapper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var fetchCommunitiesJob: Job? = null

@Composable
fun CommunityListActivity(
    navController: NavController,
    communityListViewModel: CommunityListViewModel,
    communityViewModel: CommunityViewModel,
    accountViewModel: AccountViewModel,
    selectMode: Boolean = false,
) {

    Log.d("jerboa", "got to community list activity")

    val account = getCurrentAccount(accountViewModel = accountViewModel)

    var search by rememberSaveable { mutableStateOf("") }

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    Surface(color = MaterialTheme.colors.background) {
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
            content = {
                if (communityListViewModel.loading.value) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    CommunityListings(
                        communities = communityListViewModel.communityList,
                        onClickCommunity = {
                            if (selectMode) {
                                communityListViewModel.selectCommunity(it)
                                navController.navigateUp()
                            } else {
                                communityClickWrapper(
                                    communityViewModel = communityViewModel,
                                    communityId = it.id,
                                    account = account,
                                    navController = navController,
                                    ctx = ctx,
                                )
                            }
                        }
                    )
                }
            }
        )
    }
}
