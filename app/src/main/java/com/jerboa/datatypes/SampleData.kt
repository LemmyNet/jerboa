package com.jerboa.datatypes

import com.jerboa.datatypes.types.Comment
import com.jerboa.datatypes.types.CommentAggregates
import com.jerboa.datatypes.types.CommentReply
import com.jerboa.datatypes.types.CommentReplyView
import com.jerboa.datatypes.types.CommentView
import com.jerboa.datatypes.types.Community
import com.jerboa.datatypes.types.CommunityAggregates
import com.jerboa.datatypes.types.CommunityView
import com.jerboa.datatypes.types.ListingType
import com.jerboa.datatypes.types.LocalSite
import com.jerboa.datatypes.types.LocalSiteRateLimit
import com.jerboa.datatypes.types.Person
import com.jerboa.datatypes.types.PersonAggregates
import com.jerboa.datatypes.types.PersonMention
import com.jerboa.datatypes.types.PersonMentionView
import com.jerboa.datatypes.types.PersonView
import com.jerboa.datatypes.types.Post
import com.jerboa.datatypes.types.PostAggregates
import com.jerboa.datatypes.types.PostView
import com.jerboa.datatypes.types.PrivateMessage
import com.jerboa.datatypes.types.PrivateMessageView
import com.jerboa.datatypes.types.RegistrationMode
import com.jerboa.datatypes.types.Site
import com.jerboa.datatypes.types.SiteAggregates
import com.jerboa.datatypes.types.SiteView
import com.jerboa.datatypes.types.SubscribedType

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
    featured_local = false,
    featured_community = false,
    embed_title = null,
    embed_description = null,
    embed_video_url = null,
    thumbnail_url = null,
    language_id = 0,
    ap_id = "https://lemmy.ml/post/135129", local = true,
)

val sampleLinkPost = Post(
    id = 135130,
    name = "Omicron is not mild and is crushing health care systems worldwide, WHO warns",
    url = "https://arstechnica.com/science/2022/01/omicron-is-not-mild-and-is-crushing-health-care-systems-worldwide-who-warns/",
    body = "At least for me, finding work is hard and knowing i could be easily fired for the first 6 months is stressful .",
    creator_id = 33401,
    community_id = 14681,
    removed = false,
    locked = true,
    published = "2022-01-01T09:53:46.904077",
    updated = null,
    deleted = false,
    nsfw = false,
    featured_local = false,
    featured_community = false,
    embed_title = null,
    embed_description = "Just like previous variants, omicron is hospitalizing people, and it is killing people.",
    embed_video_url = null,
    thumbnail_url = "https://lemmy.ml/pictrs/image/08967513-afcb-495a-9116-562a0cb1a44f.jpeg",
    ap_id = "https://lemmy.ml/post/135129", local = true,
    language_id = 0,
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
    featured_local = false,
    featured_community = false,
    embed_title = null,
    embed_description = "Just like previous variants, omicron is hospitalizing people, and it is killing people.",
    embed_video_url = null,
    thumbnail_url = null,
    ap_id = "https://lemmy.ml/post/135129", local = true,
    language_id = 0,
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
    featured_local = false,
    featured_community = false,
    embed_title = null,
    embed_description = "Just like previous variants, omicron is hospitalizing people, and it is killing people.",
    embed_video_url = null,
    thumbnail_url = "https://lemmy.ml/pictrs/image/08967513-afcb-495a-9116-562a0cb1a44f.jpeg",
    ap_id = "https://lemmy.ml/post/135129", local = true,
    language_id = 0,
)

val samplePerson = Person(
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
    matrix_user_id = null,
    admin = false,
    bot_account = false,
    ban_expires = null,
    instance_id = 0,
)

val samplePerson2 = Person(
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
    matrix_user_id = null,
    admin = false,
    bot_account = false,
    ban_expires = null,
    instance_id = 0,
)

val sampleCommunity = Community(
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
    banner = "https://lemmy.ml/pictrs/image/386rk5OYWS.jpg",
    instance_id = 0,
    hidden = false,
    posting_restricted_to_mods = false,
)

val samplePostAggregates = PostAggregates(
    id = 56195,
    post_id = 135129,
    comments = 4,
    score = 8,
    upvotes = 8,
    downvotes = 0,
    featured_local = false,
    featured_community = false,
    newest_comment_time_necro = "2022-01-02T04:02:44.592929",
    newest_comment_time = "2022-01-02T04:02:44.592929",
    published = "2022-01-02T04:02:44.592929",
)

val samplePostView = PostView(
    post = samplePost,
    creator = samplePerson,
    community = sampleCommunity,
    creator_banned_from_community = false,
    counts = samplePostAggregates,
    subscribed = SubscribedType.NotSubscribed,
    saved = false,
    read = false,
    creator_blocked = false,
    my_vote = null,
    unread_comments = 0,
)

val sampleLinkPostView = PostView(
    post = sampleLinkPost,
    creator = samplePerson,
    community = sampleCommunity,
    creator_banned_from_community = false,
    counts = samplePostAggregates,
    subscribed = SubscribedType.NotSubscribed,
    saved = false,
    read = false,
    creator_blocked = false,
    my_vote = null,
    unread_comments = 0,
)

val sampleLinkNoThumbnailPostView = PostView(
    post = sampleLinkNoThumbnailPost,
    creator = samplePerson,
    community = sampleCommunity,
    creator_banned_from_community = false,
    counts = samplePostAggregates,
    subscribed = SubscribedType.NotSubscribed,
    saved = false,
    read = false,
    creator_blocked = false,
    my_vote = null,
    unread_comments = 0,
)

val sampleImagePostView = PostView(
    post = sampleImagePost,
    creator = samplePerson,
    community = sampleCommunity,
    creator_banned_from_community = false,
    counts = samplePostAggregates,
    subscribed = SubscribedType.NotSubscribed,
    saved = false,
    read = false,
    creator_blocked = false,
    my_vote = null,
    unread_comments = 0,
)

val sampleComment = Comment(
    id = 1,
    creator_id = 56450,
    post_id = 139549,
    content = "This *looks* really cool and similar to Joplin. **Having issues** getting LaTeX to" +
        " " +
        "work" +
        ".\n\nIts kind of a long comment\n\nbut I don't want...",
    removed = false,
    published = "2022-01-07T03:12:26.398434",
    updated = "2022-01-07T03:15:37.360888",
    deleted = false,
    ap_id = "https://midwest.social/comment/24621",
    local = false,
    distinguished = false,
    language_id = 0,
    path = "0.1",
)

val sampleReplyComment = Comment(
    id = 2,
    creator_id = 423,
    post_id = 139549,
    path = "0.1.2",
    content = "This is a reply comment.\n\n# This is a header\n\n- list one\n\n- list two",
    removed = false,
    published = "2022-01-07T04:12:26.398434",
    updated = "2022-01-07T03:15:37.360888",
    deleted = false,
    ap_id = "https://midwest.social/comment/24622",
    local = false,
    distinguished = false,
    language_id = 0,
)

val sampleSecondReplyComment = Comment(
    id = 3,
    creator_id = 423,
    post_id = 139549,
    path = "0.1.2.3",
    content = "This is a sub-reply comment, mmmmk",
    removed = false,
    published = "2022-01-07T04:12:26.398434",
    updated = "2022-01-07T03:15:37.360888",
    deleted = false,
    ap_id = "https://midwest.social/comment/24622",
    local = false,
    distinguished = false,
    language_id = 0,
)

val sampleCommentAggregates = CommentAggregates(
    id = 28,
    comment_id = 24,
    score = 8,
    upvotes = 12,
    downvotes = 4,
    published = "2022-01-02T04:02:44.592929",
    child_count = 0,
)

val sampleCommentView = CommentView(
    comment = sampleComment,
    creator = samplePerson,
    post = samplePost,
    community = sampleCommunity,
    counts = sampleCommentAggregates,
    creator_banned_from_community = false,
    subscribed = SubscribedType.NotSubscribed,
    saved = false,
    creator_blocked = false,
    my_vote = null,
)

val sampleSecondReplyCommentView = CommentView(
    comment = sampleSecondReplyComment,
    creator = samplePerson,
    post = samplePost,
    community = sampleCommunity,
    counts = sampleCommentAggregates,
    creator_banned_from_community = false,
    subscribed = SubscribedType.NotSubscribed,
    saved = false,
    creator_blocked = false,
    my_vote = null,
)

val sampleReplyCommentView = CommentView(
    comment = sampleReplyComment,
    creator = samplePerson2,
    post = samplePost,
    community = sampleCommunity,
    counts = sampleCommentAggregates,
    creator_banned_from_community = false,
    subscribed = SubscribedType.NotSubscribed,
    saved = false,
    creator_blocked = false,
    my_vote = null,
)

val sampleCommentReply = CommentReply(
    id = 30,
    recipient_id = 20,
    comment_id = 42,
    read = false,
    published = "2022-01-01T09:53:46.904077",
)

val samplePersonMention = PersonMention(
    id = 30,
    recipient_id = 20,
    comment_id = 42,
    read = false,
    published = "2022-01-01T09:53:46.904077",
)

val sampleCommentReplyView = CommentReplyView(
    comment_reply = sampleCommentReply,
    comment = sampleReplyComment,
    creator = samplePerson2,
    recipient = samplePerson,
    post = samplePost,
    community = sampleCommunity,
    counts = sampleCommentAggregates,
    creator_banned_from_community = false,
    subscribed = SubscribedType.NotSubscribed,
    saved = false,
    creator_blocked = false,
    my_vote = null,
)

val samplePersonMentionView = PersonMentionView(
    person_mention = samplePersonMention,
    comment = sampleReplyComment,
    creator = samplePerson2,
    recipient = samplePerson,
    post = samplePost,
    community = sampleCommunity,
    counts = sampleCommentAggregates,
    creator_banned_from_community = false,
    subscribed = SubscribedType.NotSubscribed,
    saved = false,
    creator_blocked = false,
    my_vote = null,
)

val sampleCommunityAggregates = CommunityAggregates(
    id = 84,
    published = "2022-01-02T04:02:44.592929",
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
    community = sampleCommunity,
    subscribed = SubscribedType.NotSubscribed,
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

val samplePersonView = PersonView(
    person = samplePerson,
    counts = samplePersonAggregates,
)

val samplePrivateMessage = PrivateMessage(
    id = 32,
    creator_id = 83,
    recipient_id = 35,
    content = "A message from *me* to **you**",
    deleted = false,
    read = false,
    published = "2022-01-07T04:12:26.398434",
    updated = "2022-01-07T03:15:37.360888",
    ap_id = "https://midwest.social/comment/24622",
    local = false,
)

val samplePrivateMessageView = PrivateMessageView(
    private_message = samplePrivateMessage,
    creator = samplePerson,
    recipient = samplePerson2,
)

val sampleSite = Site(
    id = 23,
    name = "Lemmy.ml",
    sidebar = "# Hello!\n\n**This is** lemmy's sidebar",
    description = "A general purpose instance for lemmy",
    published = "2022-01-07T04:12:26.398434",
    updated = "2022-01-07T03:15:37.360888",
    icon = "https://lemmy.ml/pictrs/image/LqURxPzFNW.jpg",
    banner = "https://lemmy.ml/pictrs/image/386rk5OYWS.jpg",
    actor_id = "https://lemmy.ml",
    inbox_url = "https://lemmy.ml",
    last_refreshed_at = "2023-01-01",
    instance_id = 0,
    public_key = "bleh",
    private_key = null,
)

val sampleLocalSite = LocalSite(
    enable_downvotes = true,
    registration_mode = RegistrationMode.Open,
    enable_nsfw = true,
    community_creation_admin_only = true,
    require_email_verification = false,
    application_question = null,
    private_instance = false,
    actor_name_max_length = 30,
    application_email_admins = false,
    captcha_difficulty = "easy",
    captcha_enabled = false,
    default_post_listing_type = ListingType.All,
    default_theme = "main",
    federation_debug = false,
    federation_enabled = true,
    federation_worker_count = 64,
    id = 1,
    legal_information = null,
    published = "2023-01-01",
    site_setup = true,
    site_id = 1,
    slur_filter_regex = null,
    updated = null,
    hide_modlog_mod_names = true,
    reports_email_admins = false,
)

val sampleSiteAggregates = SiteAggregates(
    id = 23,
    site_id = 84,
    users = 8092,
    posts = 888929,
    comments = 9882,
    communities = 89,
    users_active_day = 21,
    users_active_week = 82,
    users_active_month = 208,
    users_active_half_year = 689,
)

val local_site_rate_limit = LocalSiteRateLimit(
    id = 1,
    local_site_id = 2,
    message = 2,
    message_per_second = 2,
    post = 2,
    post_per_second = 2,
    register = 2,
    register_per_second = 2,
    image = 2,
    image_per_second = 2,
    comment = 2,
    comment_per_second = 2,
    search = 2,
    search_per_second = 2,
    published = "2022-01-02T04:02:44.592929",

)

val sampleSiteView = SiteView(
    site = sampleSite,
    counts = sampleSiteAggregates,
    local_site = sampleLocalSite,
    local_site_rate_limit = local_site_rate_limit,
)
