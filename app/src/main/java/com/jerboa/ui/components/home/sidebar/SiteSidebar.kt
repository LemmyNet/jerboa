package com.jerboa.ui.components.home.sidebar

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.datatypes.sampleGetSiteRes
import com.jerboa.ui.components.common.Sidebar
import it.vercruysse.lemmyapi.datatypes.GetSiteResponse
import it.vercruysse.lemmyapi.datatypes.PersonId

@Composable
fun SiteSidebar(
    siteRes: GetSiteResponse,
    showAvatar: Boolean,
    onPersonClick: (PersonId) -> Unit,
) {
    val site = siteRes.site_view.site
    val counts = siteRes.site_view.counts
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
        showAvatar = showAvatar,
        onPersonClick = onPersonClick,
        admins = siteRes.admins,
        moderators = emptyList(),
    )
}

@Preview
@Composable
fun SiteSidebarPreview() {
    SiteSidebar(
        siteRes = sampleGetSiteRes,
        onPersonClick = {},
        showAvatar = false,
    )
}
