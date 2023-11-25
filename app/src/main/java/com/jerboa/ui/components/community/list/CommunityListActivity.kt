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
import com.jerboa.DEBOUNCE_DELAY
import com.jerboa.JerboaAppState
import com.jerboa.api.ApiState
import it.vercruysse.lemmyapi.v0x19.datatypes.CommunityFollowerView
import it.vercruysse.lemmyapi.v0x19.datatypes.Search
import com.jerboa.db.entity.getJWT
import com.jerboa.model.AccountViewModel
import com.jerboa.model.CommunityListViewModel
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.getCurrentAccount
import it.vercruysse.lemmyapi.dto.SearchType
import it.vercruysse.lemmyapi.dto.SortType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var fetchCommunitiesJob: Job? = null

object CommunityListReturn {
    const val COMMUNITY = "community-list::return(community)"
}

@Composable
fun CommunityListActivity(
    appState: JerboaAppState,
    selectMode: Boolean = false,
    followList: ImmutableList<CommunityFollowerView>,
    blurNSFW: Int,
    drawerState: DrawerState,
) {
    Log.d("jerboa", "got to community list activity")

    val communityListViewModel: CommunityListViewModel =
        viewModel(factory = CommunityListViewModel.Companion.Factory(followList))

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
                        fetchCommunitiesJob =
                            scope.launch {
                                delay(DEBOUNCE_DELAY)
                                communityListViewModel.searchCommunities(
                                    form =
                                        Search(
                                            q = search,
                                            type_ = SearchType.Communities,
                                            sort = SortType.TopAll,
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
                                    appState.apply {
                                        addReturn(CommunityListReturn.COMMUNITY, cs)
                                        navigateUp()
                                    }
                                } else {
                                    appState.toCommunity(id = cs.id)
                                }
                            },
                            modifier =
                                Modifier
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
