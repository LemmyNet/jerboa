package com.jerboa.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.db.AppSettings
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.ui.theme.DRAWER_ITEM_SPACING
import com.jerboa.ui.theme.LARGE_PADDING

@Composable
fun IconAndTextDrawerItem(
    text: String,
    icon: ImageVector? = null,
    iconBadgeCount: Int? = null,
    onClick: () -> Unit,
    more: Boolean = false,
    highlight: Boolean = false,
) {
    val spacingMod = Modifier
        .padding(LARGE_PADDING)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                color = if (highlight) {
                    MaterialTheme.colorScheme.onBackground.copy(alpha = .1f)
                } else {
                    Color.Transparent
                },
            ),
    ) {
        Row {
            icon?.also { ico ->
                InboxIconAndBadge(
                    iconBadgeCount = iconBadgeCount,
                    modifier = spacingMod.size(DRAWER_ITEM_SPACING),
                    icon = ico,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                text = text,
                modifier = spacingMod,
            )
        }
        if (more) {
            Icon(
                imageVector = Icons.Outlined.ArrowRight,
                contentDescription = "TODO",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

fun UpdateSettings(
    appSettingsViewModel: AppSettingsViewModel,
    appSettings: AppSettings?,
): ((Boolean) -> Unit)? {
    appSettingsViewModel.update(
        AppSettings(
            id = 1,
            viewedChangelog = appSettingsViewModel.appSettings.value?.viewedChangelog ?: 0,
            theme = appSettings?.theme!!,
            themeColor = appSettings.themeColor,
            fontSize = appSettings.fontSize,
            postViewMode = appSettings.postViewMode,
            showBottomNav = appSettings.showBottomNav,
            showCollapsedCommentContent = appSettings.showCollapsedCommentContent,
            showCommentActionBarByDefault = appSettings.showCommentActionBarByDefault,
            showVotingArrowsInListView = appSettings.showVotingArrowsInListView,
            blurNSFW = !appSettings.blurNSFW,
        ),
    )
    return null
}

@Preview
@Composable
fun IconAndTextDrawerItemPreview() {
    IconAndTextDrawerItem(
        text = "A test item",
        onClick = {},
    )
}

@Preview
@Composable
fun IconAndTextDrawerItemWithMorePreview() {
    IconAndTextDrawerItem(
        text = "A test item",
        onClick = {},
        more = true,
    )
}
