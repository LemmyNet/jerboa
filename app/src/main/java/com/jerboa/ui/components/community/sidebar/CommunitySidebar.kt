package com.jerboa.ui.components.community.sidebar

import androidx.compose.runtime.Composable
import com.jerboa.ui.components.common.Sidebar
import it.vercruysse.lemmyapi.datatypes.GetCommunityResponse
import it.vercruysse.lemmyapi.datatypes.PersonId

@Composable
fun CommunitySidebar(
    communityRes: GetCommunityResponse,
    showAvatar: Boolean,
    onPersonClick: (PersonId) -> Unit,
) {
    val community = communityRes.community_view.community
    val counts = communityRes.community_view.counts
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
        commentCount = counts.comments,
        moderators = communityRes.moderators,
        admins = emptyList(),
        showAvatar = showAvatar,
        onPersonClick = onPersonClick,
    )
}
