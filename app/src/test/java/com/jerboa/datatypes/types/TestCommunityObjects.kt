package com.jerboa.datatypes.types

object TestCommunityObjects {
    val TEST_COMMUNITY = Community(
        id = 1,
        name = "Cool Community",
        title = "Cool Stuff",
        description = "Everything over 0 degree celsius",
        removed = false,
        published = "foo",
        updated = "bar",
        deleted = false,
        nsfw = false,
        actor_id = "actorid",
        local = false,
        icon = "some icon",
        banner = "https://gitarre.banner.jpg",
        hidden = false,
        posting_restricted_to_mods = false,
        instance_id = 1,
    )

    val TEST_COMMUNITY_AGGREGATES = CommunityAggregates(
        id = 11,
        community_id = 111,
        comments = 1500,
        subscribers = 150,
        posts = 500,
        published = "yes",
        users_active_day = 10,
        users_active_week = 40,
        users_active_month = 100,
        users_active_half_year = 2000,
    )

    val TEST_COMMUNITY_VIEW = CommunityView(
        community = TEST_COMMUNITY,
        blocked = false,
        subscribed = SubscribedType.Subscribed,
        counts = TEST_COMMUNITY_AGGREGATES,
    )

    val TEST_FOLLOW_COMMUNITY = FollowCommunity(
        community_id = 1,
        follow = true,
        auth = "validauth",
    )

    val TEST_COMMUNITY_RESPONSE = CommunityResponse(
        community_view = TEST_COMMUNITY_VIEW,
        discussion_languages = listOf(1, 2),
    )

    val TEST_GET_COMMUNITY_RESPONSE = GetCommunityResponse(
        community_view = TEST_COMMUNITY_VIEW,
        site = null,
        moderators = emptyList(),
        online = 4,
        discussion_languages = listOf(1, 2),
        default_post_language = 1,
    )
}
