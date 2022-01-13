package com.jerboa.ui.components.community

import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.datatypes.CommunityView
import com.jerboa.datatypes.sampleCommunityView
import com.jerboa.ui.components.common.LargerCircularIcon
import com.jerboa.ui.components.common.PictrsBannerImage
import com.jerboa.ui.theme.ACTION_BAR_ICON_SIZE
import com.jerboa.ui.theme.DRAWER_BANNER_SIZE
import com.jerboa.ui.theme.Muted

@Composable
fun CommunityTopSection(
    communityView: CommunityView,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Box(
            modifier = modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            communityView.community.banner?.also {
                PictrsBannerImage(
                    url = it, modifier = Modifier.height(DRAWER_BANNER_SIZE)
                )
            }
            communityView.community.icon?.also {
                LargerCircularIcon(icon = it)
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = communityView.community.title,
                    style = MaterialTheme.typography.h6
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "TODO",
                    tint = if (communityView.subscribed) {
                        Color.Green
                    } else {
                        Muted
                    },
                    modifier = Modifier.height(ACTION_BAR_ICON_SIZE)
                )
            }
            Row {
                Text(
                    text = "${communityView.counts.users_active_month} users / month",
                    style = MaterialTheme.typography.body1,
                    color = Muted,
                )
            }
        }
//            communityView.community.description?.also {
//                Text(
//                    text = it,
//                    style = MaterialTheme.typography.subtitle1
//                )
//            }
    }
}

@Preview
@Composable
fun CommunityTopSectionPreview() {
    CommunityTopSection(communityView = sampleCommunityView)
}
