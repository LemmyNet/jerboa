package com.jerboa.ui.components.blocks

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.model.BlockedElementViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.Title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlocksActivity(
    siteViewModel: SiteViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) { siteViewModel.getSite() }

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
                    val personBlocks = siteRes.data.my_user?.person_blocks?.toMutableStateList()
                    val communityBlocks =
                        siteRes.data.my_user?.community_blocks?.toMutableStateList()
                    val instanceBlocks = siteRes.data.my_user?.instance_blocks?.toMutableStateList()
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .padding(padding)
                            .padding(start = MEDIUM_PADDING)
                            .simpleVerticalScrollbar(listState),
                    ) {
                        item { Title(stringResource(R.string.blocked_users)) }
                        item { Spacer(modifier = Modifier.padding(vertical = SMALL_PADDING)) }

                        if (personBlocks.isNullOrEmpty()) {
                            item {
                                Text(stringResource(R.string.you_have_no_blocked_users))
                            }
                        } else {
                            items(
                                items = personBlocks,
                                contentType = { "personBlock" },
                            ) { person ->
                                val blockedPerson = person.target
                                val viewModel = BlockedElementViewModel()

                                BlockedElement(
                                    apiState = viewModel.blockPersonRes,
                                    icon = blockedPerson.avatar,
                                    name = blockedPerson.name,
                                    onUnblock = {
                                        viewModel.blockPerson(false, blockedPerson.id, context)
                                    },
                                    onSuccessfulUnblock = {
                                        personBlocks.removeIf { it.target.id == blockedPerson.id }
                                    },
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.padding(vertical = MEDIUM_PADDING)) }
                        item { Title(stringResource(R.string.blocked_communities)) }
                        item { Spacer(modifier = Modifier.padding(vertical = SMALL_PADDING)) }

                        if (communityBlocks.isNullOrEmpty()) {
                            item {
                                Text(stringResource(R.string.you_have_no_blocked_communities))
                            }
                        } else {
                            items(
                                items = communityBlocks,
                                contentType = { "communityBlock" },
                            ) { communityView ->
                                val blockedCommunity = communityView.community
                                val viewModel = BlockedElementViewModel()

                                BlockedElement(
                                    apiState = viewModel.blockCommunityRes,
                                    icon = blockedCommunity.icon,
                                    name = blockedCommunity.name,
                                    onUnblock = {
                                        viewModel.blockCommunity(false, blockedCommunity.id, context)
                                    },
                                    onSuccessfulUnblock = {
                                        communityBlocks.removeIf { it.community.id == blockedCommunity.id }
                                    },
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.padding(vertical = MEDIUM_PADDING)) }
                        item { Title(stringResource(R.string.blocked_instances)) }
                        item { Spacer(modifier = Modifier.padding(vertical = SMALL_PADDING)) }

                        if (instanceBlocks.isNullOrEmpty()) {
                            item {
                                Text(stringResource(R.string.you_have_no_blocked_instances))
                            }
                        } else {
                            items(
                                items = instanceBlocks,
                                contentType = { "instanceBlock" },
                            ) { instanceBlock ->
                                val instance = instanceBlock.instance
                                val viewModel = BlockedElementViewModel()

                                BlockedElement(
                                    apiState = viewModel.blockInstanceRes,
                                    icon = null,
                                    name = instance.domain,
                                    onUnblock = {
                                        viewModel.blockInstance(false, instance, context)
                                    },
                                    onSuccessfulUnblock = {
                                        instanceBlocks.removeIf { it.instance.id == instance.id }
                                    },
                                )
                            }
                        }
                    }
                }

                else -> Unit
            }
        },
    )
}
