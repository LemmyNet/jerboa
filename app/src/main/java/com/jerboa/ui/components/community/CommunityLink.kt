package com.jerboa.ui.components.community

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.jerboa.R
import com.jerboa.datatypes.sampleCommunity
import com.jerboa.datatypes.sampleCommunityFederated
import com.jerboa.datatypes.sampleCommunityView
import com.jerboa.datatypes.sampleMyUserInfo
import com.jerboa.db.APP_SETTINGS_DEFAULT
import com.jerboa.db.entity.AppSettings
import com.jerboa.feat.BlurNSFW
import com.jerboa.feat.needBlur
import com.jerboa.showAvatar
import com.jerboa.toEnumSafe
import com.jerboa.ui.components.common.CircularIcon
import com.jerboa.ui.components.common.ItemAndInstanceTitle
import com.jerboa.ui.theme.DRAWER_ITEM_SPACING
import com.jerboa.ui.theme.ICON_SIZE
import com.jerboa.ui.theme.ICON_THUMBNAIL_SIZE
import com.jerboa.ui.theme.LARGER_ICON_THUMBNAIL_SIZE
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.LINK_ICON_SIZE
import com.jerboa.ui.theme.SMALL_PADDING
import it.vercruysse.lemmyapi.datatypes.Community
import it.vercruysse.lemmyapi.datatypes.CommunityId
import it.vercruysse.lemmyapi.datatypes.CommunityView
import it.vercruysse.lemmyapi.datatypes.MyUserInfo

@Composable
fun CommunityName(
    community: Community,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?,
    color: Color = MaterialTheme.colorScheme.primary,
    style: TextStyle = MaterialTheme.typography.labelMedium,
) {
    ItemAndInstanceTitle(
        title = community.title ?: community.name,
        onClick = onClick,
        apId = community.ap_id,
        local = community.local,
        modifier = modifier,
        itemColor = color,
        itemStyle = style,
    )
}

@Preview
@Composable
fun CommunityNamePreview() {
    CommunityName(sampleCommunity, onClick = null)
}

@Preview
@Composable
fun CommunityFederatedNamePreview() {
    CommunityName(community = sampleCommunityFederated, onClick = null)
}

@Composable
fun CommunityLink(
    modifier: Modifier = Modifier,
    community: Community,
    usersPerMonth: Long? = null,
    color: Color = MaterialTheme.colorScheme.primary,
    spacing: Dp = SMALL_PADDING,
    size: Dp = ICON_SIZE,
    thumbnailSize: Int = ICON_THUMBNAIL_SIZE,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    clickable: Boolean = true,
    showDefaultIcon: Boolean,
    appSettings: AppSettings,
    myUserInfo: MyUserInfo?,
    onClick: (CommunityId) -> Unit,
) {
    val ctx = LocalContext.current
    val showAvatar = myUserInfo.showAvatar(appSettings, ctx)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        modifier =
            if (clickable) {
                Modifier.clickable { onClick(community.id) }.then(modifier)
            } else {
                modifier
            },
    ) {
        if (showAvatar) {
            community.icon?.let {
                CircularIcon(
                    icon = it,
                    contentDescription = null,
                    size = size,
                    thumbnailSize = thumbnailSize,
                    blur = appSettings.blurNSFW.toEnumSafe<BlurNSFW>().needBlur(community.nsfw),
                )
            } ?: run {
                if (showDefaultIcon) {
                    Icon(
                        imageVector = Icons.Outlined.Forum,
                        contentDescription = "",
                        modifier = Modifier.size(size),
                    )
                }
            }
        }
        Column {
            CommunityName(community = community, color = color, style = style, onClick = null)
            usersPerMonth?.also {
                Text(
                    text = stringResource(R.string.community_link_users_month, usersPerMonth),
                )
            }
        }
    }
}

@Composable
fun CommunityLinkLarger(
    community: Community,
    myUserInfo: MyUserInfo?,
    appSettings: AppSettings,
    showDefaultIcon: Boolean,
    onClick: (CommunityId) -> Unit,
) {
    CommunityLink(
        community = community,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.titleMedium,
        size = LINK_ICON_SIZE,
        thumbnailSize = LARGER_ICON_THUMBNAIL_SIZE,
        spacing = DRAWER_ITEM_SPACING,
        modifier =
            Modifier
                .padding(LARGE_PADDING)
                .fillMaxWidth(),
        onClick = onClick,
        showDefaultIcon = showDefaultIcon,
        myUserInfo = myUserInfo,
        appSettings = appSettings,
    )
}

@Composable
fun CommunityLinkLargerWithUserCount(
    communityView: CommunityView,
    showDefaultIcon: Boolean,
    myUserInfo: MyUserInfo?,
    appSettings: AppSettings,
    onClick: (CommunityId) -> Unit,
) {
    CommunityLink(
        community = communityView.community,
        usersPerMonth = communityView.community.users_active_month,
        color = MaterialTheme.colorScheme.onSurface,
        size = LINK_ICON_SIZE,
        thumbnailSize = LARGER_ICON_THUMBNAIL_SIZE,
        spacing = DRAWER_ITEM_SPACING,
        modifier =
            Modifier
                .padding(LARGE_PADDING)
                .fillMaxWidth(),
        style = MaterialTheme.typography.titleMedium,
        onClick = onClick,
        showDefaultIcon = showDefaultIcon,
        myUserInfo = myUserInfo,
        appSettings = appSettings,
    )
}

@Preview
@Composable
fun CommunityLinkPreview() {
    CommunityLink(
        community = sampleCommunity,
        onClick = {},
        showDefaultIcon = true,
        myUserInfo = sampleMyUserInfo,
        appSettings = APP_SETTINGS_DEFAULT,
    )
}

@Preview
@Composable
fun CommunityLinkWithUsersPreview() {
    CommunityLinkLargerWithUserCount(
        communityView = sampleCommunityView,
        onClick = {},
        showDefaultIcon = true,
        myUserInfo = sampleMyUserInfo,
        appSettings = APP_SETTINGS_DEFAULT,
    )
}
