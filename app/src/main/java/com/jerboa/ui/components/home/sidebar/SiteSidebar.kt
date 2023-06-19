package com.jerboa.ui.components.home.sidebar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.datatypes.sampleSiteView
import com.jerboa.datatypes.types.SiteView
import com.jerboa.ui.components.common.Sidebar

@Composable
fun SiteSidebar(siteView: SiteView, padding: PaddingValues) {
    val site = siteView.site
    val counts = siteView.counts
    Sidebar(
        title = site.description,
        banner = site.banner,
        icon = site.icon,
        content = site.sidebar,
        published = site.published,
        postCount = counts.posts,
        commentCount = counts.comments,
        usersActiveDay = counts.users_active_day,
        usersActiveWeek = counts.users_active_week,
        usersActiveMonth = counts.users_active_month,
        usersActiveHalfYear = counts.users_active_half_year,
        padding = padding,
    )
}

@Preview
@Composable
fun SiteSidebarPreview() {
    SiteSidebar(siteView = sampleSiteView, padding = PaddingValues())
}
