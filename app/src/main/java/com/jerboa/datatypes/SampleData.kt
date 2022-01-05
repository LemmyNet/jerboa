package com.jerboa.datatypes

val samplePost = Post(
    id = 135129,
    name = "In a socialist world, would jobs still have probation periods ?",
    url = null,
    body = "At least for me, finding work is hard and knowing i could be easily fired for the first 6 months is stressful .",
    creator_id = 33401,
    community_id = 14681,
    removed = false,
    locked = false,
    published = "2022-01-01T09:53:46.904077",
    updated = null,
    deleted = false,
    nsfw = false,
    stickied = false,
    embed_title = null,
    embed_description = null,
    embed_html = null,
    thumbnail_url = null,
    ap_id = "https://lemmy.ml/post/135129", local = true
)

val samplePersonSafe = PersonSafe(
    id = 33401,
    name = "homeless",
    display_name = "No longer Homeless",
    avatar = "https://lemmy.ml/pictrs/image/LqURxPzFNW.jpg",
    banned = false,
    published = "2021-08-08T01:47:44.437708",
    updated = "2021-10-11T07:14:53.548707",
    actor_id = "https://lemmy.ml/u/homeless",
    bio = null,
    local = true,
    banner = null,
    deleted = false,
    inbox_url = "https://lemmy.ml/u/homeless/inbox",
    shared_inbox_url = "https://lemmy.ml/inbox",
    matrix_user_id = null,
    admin = false,
    bot_account = false,
    ban_expires = null
)

val sampleCommunitySafe = CommunitySafe(
    id = 14681,
    name = "socialism",
    title = "Socialism",
    description = "Rules TBD .",
    removed = false,
    published = "2019-04-30T13:28:35.965035",
    updated = "2021-01-25T16:27:15.804739",
    deleted = false,
    nsfw = false,
    actor_id = "https://lemmy.ml/c/socialism",
    local = true,
    icon = "https://lemmy.ml/pictrs/image/QtiqDmp9XY.png",
    banner = null
)

val samplePostAggregates = PostAggregates(
    id = 56195,
    post_id = 135129,
    comments = 4,
    score = 8,
    upvotes = 8,
    downvotes = 0,
    newest_comment_time_necro = "2022-01-02T04:02:44.592929",
    newest_comment_time = "2022-01-02T04:02:44.592929"
)

val samplePostView = PostView(
    post = samplePost,
    creator = samplePersonSafe,
    community = sampleCommunitySafe,
    creator_banned_from_community = false,
    counts = samplePostAggregates,
    subscribed = false,
    saved = false,
    read = false,
    creator_blocked = false,
    my_vote = null
)
