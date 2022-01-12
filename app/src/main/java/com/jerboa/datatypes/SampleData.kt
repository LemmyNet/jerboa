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

val sampleLinkPost = Post(
    id = 135130,
    name = "Omicron is not mild and is crushing health care systems worldwide, WHO warns",
    url = "https://arstechnica.com/science/2022/01/omicron-is-not-mild-and-is-crushing-health-care-systems-worldwide-who-warns/",
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
    embed_description = "Just like previous variants, omicron is hospitalizing people, and it is killing people.",
    embed_html = null,
    thumbnail_url = "https://lemmy.ml/pictrs/image/08967513-afcb-495a-9116-562a0cb1a44f.jpeg",
    ap_id = "https://lemmy.ml/post/135129", local = true
)

val sampleLinkNoThumbnailPost = Post(
    id = 135130,
    name = "Omicron is not mild and is crushing health care systems worldwide, WHO warns",
    url = "https://arstechnica.com/science/2022/01/omicron-is-not-mild-and-is-crushing-health-care-systems-worldwide-who-warns/",
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
    embed_description = "Just like previous variants, omicron is hospitalizing people, and it is killing people.",
    embed_html = null,
    thumbnail_url = null,
    ap_id = "https://lemmy.ml/post/135129", local = true
)

val sampleImagePost = Post(
    id = 135130,
    name = "This is a large image",
    url = "https://lemmy.ml/pictrs/image/08967513-afcb-495a-9116-562a0cb1a44f.jpeg",
    body = "The body of an image post",
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
    embed_description = "Just like previous variants, omicron is hospitalizing people, and it is killing people.",
    embed_html = null,
    thumbnail_url = "https://lemmy.ml/pictrs/image/08967513-afcb-495a-9116-562a0cb1a44f.jpeg",
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
    bio = "This is my bio.\n\nI like trucks, trains, and geese. This is *one* longer line " +
        "that I have in here. But I'm not sure blah blah blah\n\nI have " +
        "**tres ojos**.",
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

val samplePersonSafe2 = PersonSafe(
    id = 33403,
    name = "gary_host_laptop",
    display_name = null,
    avatar = "https://lemmy.ml/pictrs/image/kykidJ1ssM.jpg",
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
    description = "This is the r/socialism community",
    removed = false,
    published = "2019-04-30T13:28:35.965035",
    updated = "2021-01-25T16:27:15.804739",
    deleted = false,
    nsfw = false,
    actor_id = "https://lemmy.ml/c/socialism",
    local = true,
    icon = "https://lemmy.ml/pictrs/image/QtiqDmp9XY.png",
    banner = "https://lemmy.ml/pictrs/image/386rk5OYWS.jpg"
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

val sampleLinkPostView = PostView(
    post = sampleLinkPost,
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

val sampleLinkNoThumbnailPostView = PostView(
    post = sampleLinkNoThumbnailPost,
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

val sampleImagePostView = PostView(
    post = sampleImagePost,
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

val sampleComment = Comment(
    id = 1,
    creator_id = 56450,
    post_id = 139549,
    parent_id = null,
    content = "This *looks* really cool and similar to Joplin. **Having issues** getting LaTeX to" +
        " " +
        "work" +
        ".\n\nIts kind of a long comment\n\nbut I don't want...",
    removed = false,
    read = false,
    published = "2022-01-07T03:12:26.398434",
    updated = "2022-01-07T03:15:37.360888",
    deleted = false,
    ap_id = "https://midwest.social/comment/24621",
    local = false
)

val sampleCommentReply = Comment(
    id = 2,
    creator_id = 423,
    post_id = 139549,
    parent_id = 1,
    content = "This is a reply comment.\n\n# This is a header\n\n- list one\n\n- list two",
    removed = false,
    read = false,
    published = "2022-01-07T04:12:26.398434",
    updated = "2022-01-07T03:15:37.360888",
    deleted = false,
    ap_id = "https://midwest.social/comment/24622",
    local = false
)

val sampleSecondCommentReply = Comment(
    id = 3,
    creator_id = 423,
    post_id = 139549,
    parent_id = 2,
    content = "This is a sub-reply comment, mmmmk",
    removed = false,
    read = false,
    published = "2022-01-07T04:12:26.398434",
    updated = "2022-01-07T03:15:37.360888",
    deleted = false,
    ap_id = "https://midwest.social/comment/24622",
    local = false
)

val sampleCommentAggregates = CommentAggregates(
    id = 28, comment_id = 24, score = 8,
    upvotes = 12, downvotes = 4
)

val sampleCommentView = CommentView(
    comment = sampleComment,
    creator = samplePersonSafe,
    recipient = null,
    post = samplePost,
    community = sampleCommunitySafe,
    counts = sampleCommentAggregates,
    creator_banned_from_community = false,
    subscribed = false,
    saved = false,
    creator_blocked = false,
    my_vote = null,
)

val sampleCommentReplyView = CommentView(
    comment = sampleCommentReply,
    creator = samplePersonSafe2,
    recipient = null,
    post = samplePost,
    community = sampleCommunitySafe,
    counts = sampleCommentAggregates,
    creator_banned_from_community = false,
    subscribed = false,
    saved = false,
    creator_blocked = false,
    my_vote = null,
)

val sampleSecondCommentReplyView = CommentView(
    comment = sampleSecondCommentReply,
    creator = samplePersonSafe,
    recipient = null,
    post = samplePost,
    community = sampleCommunitySafe,
    counts = sampleCommentAggregates,
    creator_banned_from_community = false,
    subscribed = false,
    saved = false,
    creator_blocked = false,
    my_vote = null,
)

val sampleCommunityAggregates = CommunityAggregates(
    id = 84,
    community_id = 834,
    subscribers = 52,
    posts = 82,
    comments = 987,
    users_active_day = 28,
    users_active_week = 98,
    users_active_month = 82,
    users_active_half_year = 91,
)

val sampleCommunityView = CommunityView(
    community = sampleCommunitySafe,
    subscribed = false,
    blocked = false,
    counts = sampleCommunityAggregates,
)

val samplePersonAggregates = PersonAggregates(
    id = 23,
    person_id = 54,
    post_count = 28,
    post_score = 38,
    comment_count = 98,
    comment_score = 168,
)

val samplePersonView = PersonViewSafe(
    person = samplePersonSafe,
    counts = samplePersonAggregates
)
