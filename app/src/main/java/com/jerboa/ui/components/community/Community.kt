package com.jerboa.ui.components.community

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.PostViewMode
import com.jerboa.R
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.SubscribedType
import com.jerboa.datatypes.sampleCommunityView
import com.jerboa.datatypes.types.CommunityView
import com.jerboa.feat.BlurTypes
import com.jerboa.feat.needBlur
import com.jerboa.toEnum
import com.jerboa.ui.components.common.LargerCircularIcon
import com.jerboa.ui.components.common.PictrsBannerImage
import com.jerboa.ui.components.common.SortOptionsDropdown
import com.jerboa.ui.theme.*
import me.saket.cascade.CascadeDropdownMenu

@Composable
fun CommunityTopSection(
    communityView: CommunityView,
    modifier: Modifier = Modifier,
    onClickFollowCommunity: (communityView: CommunityView) -> Unit,
    blurNSFW: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .fillMaxWidth(),
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            communityView.community.banner?.also {
                PictrsBannerImage(
                    url = it,
                    modifier = Modifier.height(DRAWER_BANNER_SIZE),
                    blur = blurNSFW.toEnum<BlurTypes>().needBlur(communityView.community.nsfw),
                )
            }
            communityView.community.icon?.also {
                LargerCircularIcon(icon = it, blur = blurNSFW.toEnum<BlurTypes>().needBlur(communityView.community.nsfw))
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
                    text =
                        stringResource(
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
                                modifier =
                                    Modifier
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
        blurNSFW = 1,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityHeader(
    communityName: String,
    onClickSortType: (SortType) -> Unit,
    onBlockCommunityClick: () -> Unit,
    onClickRefresh: () -> Unit,
    onClickPostViewMode: (PostViewMode) -> Unit,
    selectedSortType: SortType,
    selectedPostViewMode: PostViewMode,
    onClickCommunityInfo: () -> Unit,
    onClickCommunityShare: () -> Unit,
    onClickBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    isBlocked: Boolean,
) {
    var showSortOptions by remember { mutableStateOf(false) }
    var showMoreOptions by remember { mutableStateOf(false) }

    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            CommunityHeaderTitle(
                communityName = communityName,
                selectedSortType = selectedSortType,
            )
        },
        navigationIcon = {
            IconButton(onClick = onClickBack) {
                Icon(
                    Icons.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.topAppBar_back),
                )
            }
        },
        actions = {
            Box {
                IconButton(onClick = {
                    showSortOptions = !showSortOptions
                }) {
                    Icon(
                        Icons.Outlined.Sort,
                        contentDescription = stringResource(R.string.community_sortBy),
                    )
                }

                SortOptionsDropdown(
                    expanded = showSortOptions,
                    onDismissRequest = { showSortOptions = false },
                    onClickSortType = {
                        showSortOptions = false
                        onClickSortType(it)
                    },
                    selectedSortType = selectedSortType,
                )
            }

            Box {
                IconButton(onClick = {
                    showMoreOptions = !showMoreOptions
                }) {
                    Icon(
                        Icons.Outlined.MoreVert,
                        contentDescription = stringResource(R.string.moreOptions),
                    )
                }
                CommunityMoreDropdown(
                    expanded = showMoreOptions,
                    onDismissRequest = { showMoreOptions = false },
                    onClickRefresh = onClickRefresh,
                    onBlockCommunityClick = onBlockCommunityClick,
                    onClickCommunityInfo = onClickCommunityInfo,
                    onClickCommunityShare = onClickCommunityShare,
                    onClickPostViewMode = onClickPostViewMode,
                    selectedPostViewMode = selectedPostViewMode,
                    isBlocked = isBlocked,
                )
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
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
            maxLines = 1,
            modifier = Modifier.basicMarquee(),
        )
        Text(
            text = ctx.getString(selectedSortType.shortForm),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun CommunityMoreDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onBlockCommunityClick: () -> Unit,
    onClickRefresh: () -> Unit,
    onClickCommunityInfo: () -> Unit,
    onClickCommunityShare: () -> Unit,
    onClickPostViewMode: (PostViewMode) -> Unit,
    selectedPostViewMode: PostViewMode,
    isBlocked: Boolean,
) {
    CascadeDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.home_refresh)) },
            leadingIcon = { Icon(Icons.Outlined.Refresh, contentDescription = null) },
            onClick = {
                onDismissRequest()
                onClickRefresh()
            },
            modifier = Modifier.testTag("jerboa:refresh"),
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.home_post_view_mode)) },
            leadingIcon = { Icon(Icons.Outlined.ViewAgenda, contentDescription = null) },
            children = {
                PostViewMode.entries.map {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(it.mode)) },
                        onClick = {
                            onDismissRequest()
                            onClickPostViewMode(it)
                        },
                        modifier =
                            if (selectedPostViewMode == it) {
                                Modifier.background(MaterialTheme.colorScheme.onBackground.copy(alpha = .1f))
                            } else {
                                Modifier
                            }.testTag("jerboa:postviewmode_${it.name}"),
                    )
                }
            },
            modifier = Modifier.testTag("jerboa:postviewmode"),
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.community_community_info)) },
            leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) },
            onClick = {
                onDismissRequest()
                onClickCommunityInfo()
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.share)) },
            leadingIcon = { Icon(Icons.Outlined.Share, contentDescription = null) },
            onClick = {
                onDismissRequest()
                onClickCommunityShare()
            },
        )
        Divider()
        DropdownMenuItem(
            text = {
                Text(
                    stringResource(
                        if (isBlocked) {
                            R.string.community_unblock_community
                        } else {
                            R.string.community_block_community
                        },
                    ),
                )
            },
            leadingIcon = { Icon(Icons.Outlined.Block, contentDescription = null) },
            onClick = {
                onDismissRequest()
                onBlockCommunityClick()
            },
        )
    }
}
