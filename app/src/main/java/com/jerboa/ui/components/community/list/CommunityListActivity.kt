package com.jerboa.ui.components.community.list

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jerboa.api.ApiState
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.InitializeRoute
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.addReturn
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.toCommunity
import com.jerboa.ui.components.home.SiteViewModel
import kotlinx.coroutines.Job
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
                    navController = navController,
                    search = search,
                    onSearchChange = {
                        search = it
                        if (it.isEmpty()) {
                            communityListViewModel.resetSearch()
                            return@CommunityListHeader
                        }
                        scope.launch {
                            communityListViewModel.searchAllCommunities(search, account?.jwt, true)
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
                                .imePadding(),
                        ) {
                            val history by communityListViewModel.searchHistory.collectAsState(
                                emptyList(),
                            )
                            if (history.isNotEmpty()) {
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = "Recent searches",
                                            color = MaterialTheme.colorScheme.onBackground,
                                            style = MaterialTheme.typography.labelLarge,
                                        )
                                    },
                                )
                            }
                            history.forEach {
                                ListItem(
                                    modifier = Modifier.clickable {
                                        scope.launch {
                                            search = it.text
                                            communityListViewModel.searchAllCommunities(
                                                it.text,
                                                account?.jwt,
                                            )
                                        }
                                    },
                                    headlineContent = {
                                        Text(
                                            text = it.text,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.bodyLarge,
                                        )
                                    },
                                    trailingContent = {
                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    communityListViewModel.deleteSearchHistory(it)
                                                }
                                            },
                                            content = {
                                                Icon(
                                                    Icons.Rounded.Close,
                                                    contentDescription = "Delete ${it.text}",
                                                    tint = MaterialTheme.colorScheme.surfaceTint,
                                                )
                                            },
                                        )
                                    },
                                )
                            }
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = "Communities",
                                        color = MaterialTheme.colorScheme.onBackground,
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                },
                            )
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
                }
            },
        )
    }
}
