package com.jerboa.ui.components.blocks

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
private fun BlockedElementListItem(
    id: Long,
    name: String,
    icon: String?,
    onUnblock: (id: Long) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularIcon(
            icon = icon ?: "https://lemmy.ml/pictrs/image/LqURxPzFNW.jpg",
            contentDescription = "",
            size = 26.dp,
        )
        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
        Text(name)
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = { onUnblock(id) }) {
            Text(
                text = "X",
                style = TextStyle(
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                ),
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
)
@Composable
fun BlockedElementListItemPreview() {
    BlockedElementListItem(
        id = 1,
        name = "placeholder",
        icon = "https://lemmy.ml/pictrs/image/LqURxPzFNW.jpg",
        onUnblock = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlocksActivity(
    siteViewModel: SiteViewModel,
    onUnblockUser: (userId: Long) -> Unit,
    onUnblockCommunity: (communityId: Long) -> Unit,
    onUnblockInstance: (instanceId: Long) -> Unit,
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
                    val personBlocks = siteRes.data.my_user?.person_blocks
                    val communityBlocks = siteRes.data.my_user?.community_blocks
                    val instanceBlocks = siteRes.data.my_user?.instance_blocks
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .padding(padding)
                            .padding(start = 8.dp)
                            .simpleVerticalScrollbar(listState),
                    ) {
                        item { Text("Blocked users") }
                        if (personBlocks.isNullOrEmpty()) item {
                            Text("You have no blocked users")
                        } else {
                            items(
                                personBlocks,
                                key = { ++key },
                                contentType = { "personBlock" },
                            ) { person ->
                                val blockedPerson = person.target
                                BlockedElementListItem(
                                    id = blockedPerson.id,
                                    name = blockedPerson.name,
                                    icon = blockedPerson.avatar,
                                    onUnblock = onUnblockUser,
                                )
                            }
                        }

                        item { Text("Blocked communities") }

                        if (communityBlocks.isNullOrEmpty()) item {
                            Text("You have no blocked communities")
                        } else {
                            items(
                                communityBlocks,
                                key = { ++key },
                                contentType = { "communityBlock" },
                            ) { communityView ->
                                val community = communityView.community
                                BlockedElementListItem(
                                    id = community.id,
                                    name = community.title,
                                    icon = community.icon,
                                    onUnblock = onUnblockCommunity,
                                )
                            }
                        }

                        item { Text("Blocked instances") }

                        if (instanceBlocks.isNullOrEmpty()) item {
                            Text("You have no blocked instances")
                        } else {
                            items(
                                instanceBlocks,
                                key = { ++key },
                                contentType = { "instanceBlock" },
                            ) { instanceBlock ->
                                val instance = instanceBlock.instance
                                BlockedElementListItem(
                                    id = instance.id,
                                    name = instance.domain,
                                    icon = null,
                                    onUnblock = onUnblockInstance,
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
