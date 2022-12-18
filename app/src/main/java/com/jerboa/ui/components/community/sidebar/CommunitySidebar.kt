package com.jerboa.ui.components.community.sidebar

import androidx.compose.runtime.Composable
import com.jerboa.datatypes.CommunityView
import com.jerboa.ui.components.common.Sidebar

@Composable
fun CommunitySidebar(communityView: CommunityView) {
    val community = communityView.community
    val counts = communityView.counts
    Sidebar(
        title = community.title,
        content = community.description,
        banner = community.banner,
        icon = community.icon,
        published = community.published,
        usersActiveDay = counts.users_active_day,
        usersActiveWeek = counts.users_active_week,
        usersActiveMonth = counts.users_active_month,
        usersActiveHalfYear = counts.users_active_half_year,
        postCount = counts.posts,
        commentCount = counts.comments
    )
}
