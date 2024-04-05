package com.jerboa.ui.components.blocks

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.simpleVerticalScrollbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlocksActivity(
    siteViewModel: SiteViewModel,
    onBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    var key = 0

    Scaffold(
        snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = stringResource(R.string.blocks), onClickBack = onBack)
        },
        content = { padding ->
            when (val siteRes = siteViewModel.siteRes) {
                ApiState.Empty -> ApiEmptyText()
                ApiState.Loading -> LoadingBar(padding)

                is ApiState.Failure -> ApiErrorText(siteRes.msg)

                is ApiState.Success -> {
                    siteRes.data.my_user?.person_blocks?.let { personBlocks ->
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .padding(padding)
                                .simpleVerticalScrollbar(listState),
                        ) {
                            items(
                                personBlocks,
                                key = { ++key },
                                contentType = { "personBlock" },
                            ) { person ->
                                Text(person.target.name)
                            }
                        }
                    }
                    siteRes.data.my_user?.community_blocks?.let { communityBlocks ->
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .padding(padding)
                                .simpleVerticalScrollbar(listState),
                        ) {
                            items(
                                communityBlocks,
                                key = { ++key },
                                contentType = { "communityBlock" },
                            ) { community ->
                                Text(community.community.title)
                            }
                        }
                    }
                }

                else -> Unit
            }
        },
    )
}
