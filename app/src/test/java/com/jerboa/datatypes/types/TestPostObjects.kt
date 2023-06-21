package com.jerboa.datatypes.types

object TestPostObjects {
    val TEST_POST = Post(
        id = 1,
        name = "SomeUser",
        url = "my.instance/post1",
        body = "some body text",
        creator_id = 1,
        community_id = 1,
        removed = false,
        locked = false,
        published = "yes",
        updated = "yes",
        deleted = false,
        nsfw = false,
        embed_title = "some title",
        embed_description = "some description",
        thumbnail_url = "my.instance/thumbmail1",
        ap_id = "apid",
        local = false,
        embed_video_url = "my.instance/video1",
        language_id = 1,
        featured_community = false,
        featured_local = false,
    )
}
