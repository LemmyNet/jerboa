package com.jerboa.datatypes.types

import com.jerboa.datatypes.types.TestCommunityObjects.TEST_COMMUNITY
import com.jerboa.datatypes.types.TestPersonObjects.TEST_PERSON

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
    val TEST_POST_AGGREGATES = PostAggregates(
        id = 1234,
        post_id = 9800,
        score = 50,
        upvotes = 50,
        downvotes = 150,
        published = "yes",
        newest_comment_time_necro = "foobar",
        newest_comment_time = "baz",
        featured_community = true,
        featured_local = true,
        comments = 5,
    )

    val TEST_POST_VIEW = PostView(
        post = TEST_POST,
        creator = TEST_PERSON,
        community = TEST_COMMUNITY,
        creator_banned_from_community = false,
        counts = TEST_POST_AGGREGATES,
        subscribed = SubscribedType.Subscribed,
        saved = true,
        read = true,
        creator_blocked = false,
        my_vote = 5,
        unread_comments = 3,
    )
}
