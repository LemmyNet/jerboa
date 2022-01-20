package com.jerboa.ui.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.datatypes.SiteView
import com.jerboa.datatypes.sampleSiteView
import com.jerboa.siFormat
import com.jerboa.ui.components.common.*
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.Muted
import com.jerboa.ui.theme.PROFILE_BANNER_SIZE

@Composable
fun Sidebar(siteView: SiteView) {
    val site = siteView.site
    Column(
        modifier = Modifier.padding(MEDIUM_PADDING),
        verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomStart
        ) {
            site.banner?.also {
                PictrsBannerImage(
                    url = it, modifier = Modifier.height(PROFILE_BANNER_SIZE)
                )
            }
            Box(modifier = Modifier.padding(MEDIUM_PADDING)) {
                site.icon?.also {
                    LargerCircularIcon(icon = it)
                }
            }
        }
        site.description?.also {
            Text(
                text = it,
                style = MaterialTheme.typography.subtitle1
            )
        }
        TimeAgo(
            precedingString = "Created",
            includeAgo = true,
            dateStr = site.published
        )
        CommentsAndPosts(siteView = siteView)
        site.sidebar?.also {
            MyMarkdownText(
                markdown = it,
                color = Muted,
            )
        }
    }
}

@Composable
fun CommentsAndPosts(siteView: SiteView) {
    Row {
        Text(
            text = "${siFormat(siteView.counts.users_active_month)} users / month",
            color = Muted,
        )
        DotSpacer()
        Text(
            text = "${siFormat(siteView.counts.posts)} posts",
            color = Muted,
        )
        DotSpacer()
        Text(
            text = "${siFormat(siteView.counts.comments)} comments",
            color = Muted,
        )
    }
}

@Preview
@Composable
fun SidebarPreview() {
    Sidebar(siteView = sampleSiteView)
}
