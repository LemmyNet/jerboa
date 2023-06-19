package com.jerboa.ui.components.community

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.R
import com.jerboa.datatypes.sampleCommunityView
import com.jerboa.datatypes.types.CommunityView
import com.jerboa.datatypes.types.SortType
import com.jerboa.datatypes.types.SubscribedType
import com.jerboa.getLocalizedSortingTypeName
import com.jerboa.ui.components.common.IconAndTextDrawerItem
import com.jerboa.ui.components.common.LargerCircularIcon
import com.jerboa.ui.components.common.PictrsBannerImage
import com.jerboa.ui.components.common.SortOptionsDialog
import com.jerboa.ui.components.common.SortTopOptionsDialog
import com.jerboa.ui.theme.*

@Composable
fun CommunityTopSection(
    communityView: CommunityView,
    modifier: Modifier = Modifier,
    onClickFollowCommunity: (communityView: CommunityView) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            communityView.community.banner?.also {
                PictrsBannerImage(
                    url = it,
                    modifier = Modifier.height(DRAWER_BANNER_SIZE),
                )
            }
            communityView.community.icon?.also {
                LargerCircularIcon(icon = it)
            }
        }
        Column(
            modifier = Modifier.padding(MEDIUM_PADDING),
            verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = communityView.community.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Row {
                Text(
                    text = stringResource(
                        R.string.community_users_month,
                        communityView.counts.users_active_month,
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.muted,
                )
            }
            Row {
                when (communityView.subscribed) {
                    SubscribedType.Subscribed -> {
                        OutlinedButton(
                            onClick = { onClickFollowCommunity(communityView) },
                        ) {
                            Text(stringResource(R.string.community_joined))
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier
                                    .height(ACTION_BAR_ICON_SIZE),
                            )
                        }
                    }
                    SubscribedType.NotSubscribed -> {
                        Button(
                            onClick = { onClickFollowCommunity(communityView) },
                        ) {
                            Text(stringResource(R.string.community_subscribe))
                        }
                    }

                    SubscribedType.Pending -> {
                        Button(
                            onClick = { onClickFollowCommunity(communityView) },
                        ) {
                            Text(stringResource(R.string.community_pending))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CommunityTopSectionPreview() {
    CommunityTopSection(
        communityView = sampleCommunityView,
        onClickFollowCommunity = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityHeader(
    communityName: String,
    onClickSortType: (SortType) -> Unit,
    onBlockCommunityClick: () -> Unit,
    onClickRefresh: () -> Unit,
    selectedSortType: SortType,
    navController: NavController = rememberNavController(),
    scrollBehavior: TopAppBarScrollBehavior,
) {
    var showSortOptions by remember { mutableStateOf(false) }
    var showTopOptions by remember { mutableStateOf(false) }
    var showMoreOptions by remember { mutableStateOf(false) }

    if (showSortOptions) {
        SortOptionsDialog(
            selectedSortType = selectedSortType,
            onDismissRequest = { showSortOptions = false },
            onClickSortType = {
                showSortOptions = false
                onClickSortType(it)
            },
            onClickSortTopOptions = {
                showSortOptions = false
                showTopOptions = !showTopOptions
            },
        )
    }

    if (showTopOptions) {
        SortTopOptionsDialog(
            selectedSortType = selectedSortType,
            onDismissRequest = { showTopOptions = false },
            onClickSortType = {
                showTopOptions = false
                onClickSortType(it)
            },
        )
    }

    if (showMoreOptions) {
        CommunityMoreDialog(
            onDismissRequest = { showMoreOptions = false },
            onClickRefresh = onClickRefresh,
            onBlockCommunityClick = {
                showMoreOptions = false
                onBlockCommunityClick()
            },
            navController = navController,
        )
    }

    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            CommunityHeaderTitle(
                communityName = communityName,
                selectedSortType = selectedSortType,
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.community_back),
                )
            }
        },
        actions = {
            IconButton(onClick = {
                showSortOptions = !showSortOptions
            }) {
                Icon(
                    Icons.Outlined.Sort,
                    contentDescription = stringResource(R.string.community_sortBy),
                )
            }
            IconButton(onClick = {
                showMoreOptions = !showMoreOptions
            }) {
                Icon(
                    Icons.Outlined.MoreVert,
                    contentDescription = stringResource(R.string.moreOptions),
                )
            }
        },
    )
}

@Composable
fun CommunityHeaderTitle(
    communityName: String,
    selectedSortType: SortType,
) {
    val ctx = LocalContext.current
    Column {
        Text(
            text = communityName,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = getLocalizedSortingTypeName(ctx, selectedSortType),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun CommunityMoreDialog(
    onDismissRequest: () -> Unit,
    onBlockCommunityClick: () -> Unit,
    onClickRefresh: () -> Unit,
    navController: NavController,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.community_refresh),
                    icon = Icons.Outlined.Refresh,
                    onClick = {
                        onDismissRequest()
                        onClickRefresh()
                    },
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.community_community_info),
                    icon = Icons.Outlined.Info,
                    onClick = {
                        navController.navigate("communitySidebar")
                        onDismissRequest()
                    },
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.community_block_community),
                    icon = Icons.Outlined.Block,
                    onClick = onBlockCommunityClick,
                )
            }
        },
        confirmButton = {},
    )
}
