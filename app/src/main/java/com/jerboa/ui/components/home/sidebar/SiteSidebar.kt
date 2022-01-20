package com.jerboa.ui.components.home.sidebar

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.datatypes.SiteView
import com.jerboa.datatypes.sampleSiteView
import com.jerboa.ui.components.common.Sidebar

@Composable
fun SiteSidebar(siteView: SiteView) {
    val site = siteView.site
    val counts = siteView.counts
    Sidebar(
        title = site.description,
        content = site.sidebar,
        banner = site.banner,
        icon = site.icon,
        published = site.published,
        usersActiveMonth = counts.users_active_month,
        postCount = counts.posts,
        commentCount = counts.comments,
    )
}

@Preview
@Composable
fun SiteSidebarPreview() {
    SiteSidebar(siteView = sampleSiteView)
}
