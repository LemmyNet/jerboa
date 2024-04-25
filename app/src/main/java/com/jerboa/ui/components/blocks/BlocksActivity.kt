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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.model.SiteViewModel
import com.jerboa.showBlockCommunityToast
import com.jerboa.showBlockInstanceToast
import com.jerboa.showBlockPersonToast
import com.jerboa.ui.components.common.ApiEmptyText
import com.jerboa.ui.components.common.ApiErrorText
import com.jerboa.ui.components.common.CircularIcon
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.LoadingBar
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.theme.Title
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockCommunity
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockCommunityResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockInstance
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockInstanceResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockPerson
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockPersonResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val UNBLOCK_BUTTON_SIZE = 18

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
                contentDescription = "",
                size = 26.dp,
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
        }
        Text(name, modifier = Modifier.weight(1f))
        TextButton(onClick = onUnblock, colors = ButtonDefaults.buttonColors(Color.Transparent)) {
            when (apiState) {
                ApiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(UNBLOCK_BUTTON_SIZE.dp),
                    color = Color.Gray,
                )
                is ApiState.Success -> onSuccessfulUnblock()
                else -> Icon(
                    imageVector = Icons.Rounded.Close,
                    modifier = Modifier.size(UNBLOCK_BUTTON_SIZE.dp),
                    tint = Color.Red,
                    contentDescription = "",
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
        icon = null,
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
                            .padding(start = 8.dp)
                            .simpleVerticalScrollbar(listState),
                    ) {
                        item { Title(stringResource(R.string.blocked_users)) }
                        item { Spacer(modifier = Modifier.padding(vertical = 4.dp)) }

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
                                val scope = rememberCoroutineScope()
                                var apiState: ApiState<BlockPersonResponse> by remember {
                                    mutableStateOf(ApiState.Empty)
                                }
                                BlockedElementListItem(
                                    apiState = apiState,
                                    icon = blockedPerson.avatar,
                                    name = blockedPerson.name,
                                    onUnblock = {
                                        apiState = ApiState.Loading
                                        val form = BlockPerson(blockedPerson.id, false)
                                        scope.launch {
                                            apiState =
                                                API.getInstance().blockPerson(form).toApiState()
                                            withContext(Dispatchers.Main) {
                                                showBlockPersonToast(apiState, context)
                                            }
                                        }
                                    },
                                    onSuccessfulUnblock = {
                                        personBlocks.removeIf { it.target.id == blockedPerson.id }
                                    },
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.padding(vertical = 8.dp)) }
                        item { Title(stringResource(R.string.blocked_communities)) }
                        item { Spacer(modifier = Modifier.padding(vertical = 4.dp)) }

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
                                val scope = rememberCoroutineScope()
                                var apiState: ApiState<BlockCommunityResponse> by remember {
                                    mutableStateOf(ApiState.Empty)
                                }
                                BlockedElementListItem(
                                    apiState = apiState,
                                    icon = blockedCommunity.icon,
                                    name = blockedCommunity.name,
                                    onUnblock = {
                                        apiState = ApiState.Loading
                                        val form = BlockCommunity(blockedCommunity.id, false)
                                        scope.launch {
                                            apiState =
                                                API.getInstance().blockCommunity(form).toApiState()
                                            withContext(Dispatchers.Main) {
                                                showBlockCommunityToast(apiState, context)
                                            }
                                        }
                                    },
                                    onSuccessfulUnblock = {
                                        communityBlocks.removeIf { it.community.id == blockedCommunity.id }
                                    },
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.padding(vertical = 8.dp)) }
                        item { Title(stringResource(R.string.blocked_instances)) }
                        item { Spacer(modifier = Modifier.padding(vertical = 4.dp)) }

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
                                val scope = rememberCoroutineScope()
                                var apiState: ApiState<BlockInstanceResponse> by remember {
                                    mutableStateOf(ApiState.Empty)
                                }
                                BlockedElementListItem(
                                    apiState = apiState,
                                    icon = null,
                                    name = instance.domain,
                                    onUnblock = {
                                        apiState = ApiState.Loading
                                        val form = BlockInstance(instance.id, false)
                                        scope.launch {
                                            apiState =
                                                API.getInstance().blockInstance(form).toApiState()
                                            withContext(Dispatchers.Main) {
                                                showBlockInstanceToast(apiState, instance, context)
                                            }
                                        }
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
