package com.jerboa.ui.components.blocks

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.CircularIcon
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.theme.ICON_SIZE
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.SMALL_ICON_SIZE
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.Title
import it.vercruysse.lemmyapi.v0x19.datatypes.CommunityId
import it.vercruysse.lemmyapi.v0x19.datatypes.Instance
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonId

@Composable
private fun BlockedElementListItem(
    apiState: ApiState<*>,
    icon: String?,
    name: String,
    onUnblock: () -> Unit,
    onSuccessfulUnblock: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon?.let {
            CircularIcon(
                icon = it,
                contentDescription = null,
                size = ICON_SIZE,
            )
            Spacer(modifier = Modifier.padding(horizontal = SMALL_PADDING))
        }
        Text(name, modifier = Modifier.weight(1f))
        TextButton(onClick = onUnblock, colors = ButtonDefaults.buttonColors(Color.Transparent)) {
            when (apiState) {
                ApiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(SMALL_ICON_SIZE),
                    color = MaterialTheme.colorScheme.secondary,
                )

                is ApiState.Success -> onSuccessfulUnblock()
                else -> Icon(
                    imageVector = Icons.Rounded.Close,
                    modifier = Modifier.size(SMALL_ICON_SIZE),
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = null,
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun BlockedElementListItemPreview() {
    BlockedElementListItem(
        apiState = ApiState.Empty,
        icon = "",
        name = "Element name",
        onUnblock = { },
        onSuccessfulUnblock = { },
    )
}

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
                                val id = blockedPerson.id
                                val viewModel = makeBlockedPersonViewModel(id)

                                BlockedElementListItem(
                                    apiState = viewModel.blockPersonRes,
                                    icon = blockedPerson.avatar,
                                    name = blockedPerson.name,
                                    onUnblock = { viewModel.blockPerson(false, context) },
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
                                val id = blockedCommunity.id
                                val viewModel = makeBlockedCommunityViewModel(id)

                                BlockedElementListItem(
                                    apiState = viewModel.blockCommunityRes,
                                    icon = blockedCommunity.icon,
                                    name = blockedCommunity.name,
                                    onUnblock = { viewModel.blockCommunity(false, context) },
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
                                val viewModel = makeBlockedInstanceViewModel(instance)

                                BlockedElementListItem(
                                    apiState = viewModel.blockInstanceRes,
                                    icon = null,
                                    name = instance.domain,
                                    onUnblock = { viewModel.blockInstance(false, context) },
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

@Composable
private fun makeBlockedPersonViewModel(blockedPersonId: PersonId): BlockedPersonViewModel =
    viewModel(factory = BlockedPersonViewModel.Companion.Factory(blockedPersonId))

@Composable
private fun makeBlockedCommunityViewModel(blockedCommunityId: CommunityId): BlockedCommunityViewModel =
    viewModel(factory = BlockedCommunityViewModel.Companion.Factory(blockedCommunityId))

@Composable
private fun makeBlockedInstanceViewModel(blockedInstance: Instance): BlockedInstanceViewModel =
    viewModel(factory = BlockedInstanceViewModel.Companion.Factory(blockedInstance))
