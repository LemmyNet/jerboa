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
}
