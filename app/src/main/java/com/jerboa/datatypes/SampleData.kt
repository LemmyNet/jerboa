package com.jerboa.datatypes

import com.jerboa.feat.InstantScores
import it.vercruysse.lemmyapi.datatypes.Comment
import it.vercruysse.lemmyapi.datatypes.CommentAggregates
import it.vercruysse.lemmyapi.datatypes.CommentReply
import it.vercruysse.lemmyapi.datatypes.CommentReplyView
import it.vercruysse.lemmyapi.datatypes.CommentReport
import it.vercruysse.lemmyapi.datatypes.CommentReportView
import it.vercruysse.lemmyapi.datatypes.CommentView
import it.vercruysse.lemmyapi.datatypes.Community
import it.vercruysse.lemmyapi.datatypes.CommunityAggregates
import it.vercruysse.lemmyapi.datatypes.CommunityView
import it.vercruysse.lemmyapi.datatypes.GetSiteResponse
import it.vercruysse.lemmyapi.datatypes.LocalSite
import it.vercruysse.lemmyapi.datatypes.LocalSiteRateLimit
import it.vercruysse.lemmyapi.datatypes.LocalUser
import it.vercruysse.lemmyapi.datatypes.Person
import it.vercruysse.lemmyapi.datatypes.PersonAggregates
import it.vercruysse.lemmyapi.datatypes.PersonMention
import it.vercruysse.lemmyapi.datatypes.PersonMentionView
import it.vercruysse.lemmyapi.datatypes.PersonView
import it.vercruysse.lemmyapi.datatypes.Post
import it.vercruysse.lemmyapi.datatypes.PostAggregates
import it.vercruysse.lemmyapi.datatypes.PostReport
import it.vercruysse.lemmyapi.datatypes.PostReportView
import it.vercruysse.lemmyapi.datatypes.PostView
import it.vercruysse.lemmyapi.datatypes.PrivateMessage
import it.vercruysse.lemmyapi.datatypes.PrivateMessageReport
import it.vercruysse.lemmyapi.datatypes.PrivateMessageReportView
import it.vercruysse.lemmyapi.datatypes.PrivateMessageView
import it.vercruysse.lemmyapi.datatypes.RegistrationApplication
import it.vercruysse.lemmyapi.datatypes.RegistrationApplicationView
import it.vercruysse.lemmyapi.datatypes.Site
import it.vercruysse.lemmyapi.datatypes.SiteAggregates
import it.vercruysse.lemmyapi.datatypes.SiteView
import it.vercruysse.lemmyapi.dto.CommunityVisibility
import it.vercruysse.lemmyapi.dto.ListingType
import it.vercruysse.lemmyapi.dto.PostListingMode
import it.vercruysse.lemmyapi.dto.RegistrationMode
import it.vercruysse.lemmyapi.dto.SortType
import it.vercruysse.lemmyapi.dto.SubscribedType

val samplePost =
    Post(
        id = 135129,
        name = "In a socialist world, would jobs still have probation periods ?",
        url = null,
        body = "At least for me, finding work is hard and knowing i could be easily fired for the first 6 months is stressful .",
        creator_id = 33401,
        community_id = 14681,
        removed = false,
        locked = false,
        published = "2022-01-01T09:53:46.904077Z",
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
        ap_id = "https://lemmy.ml/post/135129",
        local = true,
    )

val sampleLinkPost =
    Post(
        id = 135130,
        name = "Omicron is not mild and is crushing health care systems worldwide, WHO warns",
        url = "https://arstechnica.com/science/2022/01/omicron-is-not-mild-and-is-crushing-health-care-systems-worldwide-who-warns/",
        body = "At least for me, finding work is hard and knowing i could be easily fired for the first 6 months is stressful .",
        creator_id = 33401,
        community_id = 14681,
        removed = false,
        locked = true,
        published = "2022-01-01T09:53:46.904077Z",
        updated = null,
        deleted = false,
        nsfw = false,
        featured_local = false,
        featured_community = false,
        embed_title = null,
        embed_description = "Just like previous variants, omicron is hospitalizing people, and it is killing people.",
        embed_video_url = null,
        thumbnail_url = "https://lemmy.ml/pictrs/image/08967513-afcb-495a-9116-562a0cb1a44f.jpeg",
        ap_id = "https://lemmy.ml/post/135129",
        local = true,
        language_id = 0,
    )

val sampleLinkNoThumbnailPost =
    Post(
        id = 135130,
        name = "Omicron is not mild and is crushing health care systems worldwide, WHO warns",
        url = "https://arstechnica.com/science/2022/01/omicron-is-not-mild-and-is-crushing-health-care-systems-worldwide-who-warns/",
        body = "At least for me, finding work is hard and knowing i could be easily fired for the first 6 months is stressful .",
        creator_id = 33401,
        community_id = 14681,
        removed = false,
        locked = false,
        published = "2022-01-01T09:53:46.904077Z",
        updated = null,
        deleted = false,
        nsfw = false,
        featured_local = false,
        featured_community = false,
        embed_title = null,
        embed_description = "Just like previous variants, omicron is hospitalizing people, and it is killing people.",
        embed_video_url = null,
        thumbnail_url = null,
        ap_id = "https://lemmy.ml/post/135129",
        local = true,
        language_id = 0,
    )

val sampleImagePost =
    Post(
        id = 135130,
        name = "This is a large image",
        url = "https://lemmy.ml/pictrs/image/08967513-afcb-495a-9116-562a0cb1a44f.jpeg",
        body = "The body of an image post",
        creator_id = 33401,
        community_id = 14681,
        removed = false,
        locked = false,
        published = "2022-01-01T09:53:46.904077Z",
        updated = null,
        deleted = false,
        nsfw = false,
        featured_local = false,
        featured_community = false,
        embed_title = null,
        embed_description = "Just like previous variants, omicron is hospitalizing people, and it is killing people.",
        embed_video_url = null,
        thumbnail_url = "https://lemmy.ml/pictrs/image/08967513-afcb-495a-9116-562a0cb1a44f.jpeg",
        ap_id = "https://lemmy.ml/post/135129",
        local = true,
        language_id = 0,
    )

val sampleMarkdownPost =
    Post(
        id = 135130,
        name = "This is a markdown post",
        url = null,
        body = "# Heading 1  1. First Item  2. Second Item  3. Third item  And a `code` block",
        creator_id = 33401,
        community_id = 14681,
        removed = false,
        locked = false,
        published = "2022-01-01T09:53:46.904077Z",
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
        ap_id = "https://lemmy.ml/post/135129",
        local = true,
    )

val samplePerson =
    Person(
        id = 33401,
        name = "homeless",
        display_name = "No longer Homeless",
        avatar = "https://lemmy.ml/pictrs/image/LqURxPzFNW.jpg",
        banned = false,
        published = "2021-08-08T01:47:44.437708Z",
        updated = "2021-10-11T07:14:53.548707Z",
        actor_id = "https://lemmy.ml/u/homeless",
        bio =
            "This is my bio.\n\nI like trucks, trains, and geese. This is *one* longer line " +
                "that I have in here. But I'm not sure blah blah blah\n\nI have " +
                "**tres ojos**.",
        local = true,
        banner = null,
        deleted = false,
        matrix_user_id = null,
        bot_account = false,
        ban_expires = null,
        instance_id = 0,
    )

val samplePerson2 =
    Person(
        id = 33403,
        name = "gary_host_laptop",
        display_name = null,
        avatar = "https://lemmy.ml/pictrs/image/kykidJ1ssM.jpg",
        banned = false,
        published = "2021-08-08T01:47:44.437708Z",
        updated = "2021-10-11T07:14:53.548707Z",
        actor_id = "https://lemmy.ml/u/homeless",
        bio = null,
        local = false,
        banner = null,
        deleted = false,
        matrix_user_id = null,
        bot_account = false,
        ban_expires = null,
        instance_id = 0,
    )

val samplePerson3 =
    Person(
        id = 33478,
        name = "witch_power",
        display_name = null,
        banned = false,
        published = "2021-08-08T01:47:44.437708Z",
        updated = "2021-10-11T07:14:53.548707Z",
        actor_id = "https://lemmy.ml/u/witch_power",
        bio = null,
        local = true,
        banner = null,
        deleted = false,
        matrix_user_id = null,
        bot_account = false,
        ban_expires = null,
        instance_id = 0,
    )

val sampleLocalUser = LocalUser(
    id = 24,
    person_id = 82,
    show_nsfw = false,
    theme = "none",
    default_sort_type = SortType.Active,
    default_listing_type = ListingType.Local,
    interface_language = "en",
    show_avatars = false,
    send_notifications_to_email = false,
    show_scores = false,
    show_bot_accounts = false,
    show_read_posts = false,
    email_verified = false,
    accepted_application = false,
    open_links_in_new_tab = false,
    blur_nsfw = false,
    auto_expand = false,
    infinite_scroll_enabled = false,
    admin = false,
    post_listing_mode = PostListingMode.List,
    totp_2fa_enabled = false,
    enable_keyboard_navigation = false,
    enable_animated_images = false,
    collapse_bot_comments = false,
    last_donation_notification = "2019-04-30T13:28:35.965035Z",
)

val sampleCommunity =
    Community(
        id = 14681,
        name = "socialism",
        title = "Socialism",
        description = "This is the r/socialism community",
        removed = false,
        published = "2019-04-30T13:28:35.965035Z",
        updated = "2021-01-25T16:27:15.804739Z",
        deleted = false,
        nsfw = false,
        actor_id = "https://lemmy.ml/c/socialism",
        local = true,
        icon = "https://lemmy.ml/pictrs/image/QtiqDmp9XY.png",
        banner = "https://lemmy.ml/pictrs/image/386rk5OYWS.jpg",
        instance_id = 0,
        hidden = false,
        posting_restricted_to_mods = false,
        visibility = CommunityVisibility.Public,
    )

val sampleCommunityFederated = sampleCommunity.copy(local = false)

val samplePostAggregates =
    PostAggregates(
        post_id = 135129,
        comments = 4,
        score = 5,
        upvotes = 8,
        downvotes = 3,
        published = "2022-01-02T04:02:44.592929Z",
        newest_comment_time = "2022-01-02T04:02:44.592929Z",
    )

val samplePostView =
    PostView(
        post = samplePost,
        creator = samplePerson,
        community = sampleCommunity,
        creator_banned_from_community = false,
        creator_is_moderator = false,
        creator_is_admin = false,
        counts = samplePostAggregates,
        subscribed = SubscribedType.NotSubscribed,
        saved = false,
        read = false,
        creator_blocked = false,
        unread_comments = 1,
        my_vote = 0,
        banned_from_community = false,
        hidden = false,
    )

val sampleLinkPostView =
    PostView(
        post = sampleLinkPost,
        creator = samplePerson,
        community = sampleCommunity,
        creator_banned_from_community = false,
        creator_is_moderator = false,
        creator_is_admin = false,
        counts = samplePostAggregates,
        subscribed = SubscribedType.NotSubscribed,
        saved = false,
        read = false,
        creator_blocked = false,
        unread_comments = 1,
        my_vote = 0,
        banned_from_community = false,
        hidden = false,
    )

val sampleLinkNoThumbnailPostView =
    PostView(
        post = sampleLinkNoThumbnailPost,
        creator = samplePerson,
        community = sampleCommunity,
        creator_banned_from_community = false,
        creator_is_moderator = false,
        creator_is_admin = false,
        counts = samplePostAggregates,
        subscribed = SubscribedType.NotSubscribed,
        saved = false,
        read = false,
        creator_blocked = false,
        unread_comments = 1,
        my_vote = 0,
        banned_from_community = false,
        hidden = false,
    )

val sampleImagePostView =
    PostView(
        post = sampleImagePost,
        creator = samplePerson,
        community = sampleCommunity,
        creator_banned_from_community = false,
        creator_is_moderator = false,
        creator_is_admin = false,
        counts = samplePostAggregates,
        subscribed = SubscribedType.NotSubscribed,
        saved = false,
        read = false,
        creator_blocked = false,
        unread_comments = 1,
        my_vote = 0,
        banned_from_community = false,
        hidden = false,
    )

val sampleMarkdownPostView =
    PostView(
        post = sampleMarkdownPost,
        creator = samplePerson,
        community = sampleCommunity,
        creator_banned_from_community = false,
        creator_is_moderator = false,
        creator_is_admin = false,
        counts = samplePostAggregates,
        subscribed = SubscribedType.NotSubscribed,
        saved = false,
        read = false,
        creator_blocked = false,
        unread_comments = 1,
        my_vote = 0,
        banned_from_community = false,
        hidden = false,
    )

val sampleComment =
    Comment(
        id = 1,
        creator_id = 56450,
        post_id = 139549,
        content =
            "This *looks* really cool and similar to Joplin. **Having issues** getting LaTeX to" +
                " " +
                "work" +
                ".\n\nIts kind of a long comment\n\nbut I don't want...",
        removed = false,
        published = "2022-01-07T03:12:26.398434Z",
        updated = "2022-01-07T03:15:37.360888Z",
        deleted = false,
        ap_id = "https://midwest.social/comment/24621",
        local = false,
        distinguished = false,
        language_id = 0,
        path = "0.1",
    )

val sampleReplyComment =
    Comment(
        id = 2,
        creator_id = 423,
        post_id = 139549,
        path = "0.1.2",
        content = "This is a reply comment.\n\n# This is a header\n\n- list one\n\n- list two",
        removed = false,
        published = "2022-01-07T04:12:26.398434Z",
        updated = "2022-01-07T03:15:37.360888Z",
        deleted = false,
        ap_id = "https://midwest.social/comment/24622",
        local = false,
        distinguished = false,
        language_id = 0,
    )

val sampleSecondReplyComment =
    Comment(
        id = 3,
        creator_id = 423,
        post_id = 139549,
        path = "0.1.2.3",
        content = "This is a sub-reply comment, mmmmk",
        removed = false,
        published = "2022-01-07T04:12:26.398434Z",
        updated = "2022-01-07T03:15:37.360888Z",
        deleted = false,
        ap_id = "https://midwest.social/comment/24622",
        local = false,
        distinguished = false,
        language_id = 0,
    )

val sampleCommentAggregates =
    CommentAggregates(
        comment_id = 24,
        score = 8,
        upvotes = 12,
        downvotes = 4,
        published = "2022-01-02T04:02:44.592929Z",
        child_count = 0,
    )

val sampleCommentView =
    CommentView(
        comment = sampleComment,
        creator = samplePerson,
        post = samplePost,
        community = sampleCommunity,
        counts = sampleCommentAggregates,
        creator_banned_from_community = false,
        subscribed = SubscribedType.NotSubscribed,
        saved = false,
        creator_blocked = false,
        creator_is_moderator = false,
        creator_is_admin = false,
        banned_from_community = false,
    )

val sampleSecondReplyCommentView =
    CommentView(
        comment = sampleSecondReplyComment,
        creator = samplePerson,
        post = samplePost,
        community = sampleCommunity,
        counts = sampleCommentAggregates,
        creator_banned_from_community = false,
        subscribed = SubscribedType.NotSubscribed,
        saved = false,
        creator_blocked = false,
        creator_is_moderator = false,
        creator_is_admin = false,
        banned_from_community = false,
    )

val sampleReplyCommentView =
    CommentView(
        comment = sampleReplyComment,
        creator = samplePerson2,
        post = samplePost,
        community = sampleCommunity,
        counts = sampleCommentAggregates,
        creator_banned_from_community = false,
        subscribed = SubscribedType.NotSubscribed,
        saved = false,
        creator_blocked = false,
        creator_is_moderator = false,
        creator_is_admin = false,
        banned_from_community = false,
    )

val sampleCommentReply =
    CommentReply(
        id = 30,
        recipient_id = 20,
        comment_id = 42,
        read = false,
        published = "2022-01-01T09:53:46.904077Z",
    )

val samplePersonMention =
    PersonMention(
        id = 30,
        recipient_id = 20,
        comment_id = 42,
        read = false,
        published = "2022-01-01T09:53:46.904077Z",
    )

val sampleCommentReplyView =
    CommentReplyView(
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
        creator_is_moderator = false,
        creator_is_admin = false,
        banned_from_community = false,
    )

val samplePersonMentionView =
    PersonMentionView(
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
        creator_is_moderator = false,
        creator_is_admin = false,
        banned_from_community = false,
    )

val sampleCommunityAggregates =
    CommunityAggregates(
        published = "2022-01-02T04:02:44.592929Z",
        community_id = 834,
        subscribers = 52,
        subscribers_local = 21,
        posts = 82,
        comments = 987,
        users_active_day = 28,
        users_active_week = 98,
        users_active_month = 82,
        users_active_half_year = 91,
    )

val sampleCommunityView =
    CommunityView(
        community = sampleCommunity,
        subscribed = SubscribedType.NotSubscribed,
        blocked = false,
        counts = sampleCommunityAggregates,
        banned_from_community = false,
    )

val samplePersonAggregates =
    PersonAggregates(
        person_id = 54,
        post_count = 28,
        comment_count = 98,
    )

val samplePersonView =
    PersonView(
        person = samplePerson,
        counts = samplePersonAggregates,
        is_admin = false,
    )

val samplePrivateMessage =
    PrivateMessage(
        id = 32,
        creator_id = 83,
        recipient_id = 35,
        content = "A message from *me* to **you**",
        deleted = false,
        read = false,
        published = "2022-01-07T04:12:26.398434Z",
        updated = "2022-01-07T03:15:37.360888Z",
        ap_id = "https://midwest.social/comment/24622",
        local = false,
    )

val samplePrivateMessageView =
    PrivateMessageView(
        private_message = samplePrivateMessage,
        creator = samplePerson,
        recipient = samplePerson2,
    )

val sampleSite =
    Site(
        id = 23,
        name = "Lemmy.ml",
        sidebar = "# Hello!\n\n**This is** lemmy's sidebar",
        description = "A general purpose instance for lemmy",
        published = "2022-01-07T04:12:26.398434Z",
        updated = "2022-01-07T03:15:37.360888Z",
        icon = "https://lemmy.ml/pictrs/image/LqURxPzFNW.jpg",
        banner = "https://lemmy.ml/pictrs/image/386rk5OYWS.jpg",
        actor_id = "https://lemmy.ml",
        inbox_url = "https://lemmy.ml",
        last_refreshed_at = "2023-01-01",
        instance_id = 0,
    )

val sampleLocalSite =
    LocalSite(
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
        federation_enabled = true,
        id = 1,
        legal_information = null,
        published = "2023-01-01",
        site_setup = true,
        site_id = 1,
        slur_filter_regex = null,
        updated = null,
        hide_modlog_mod_names = true,
        federation_signed_fetch = false,
        reports_email_admins = false,
        default_sort_type = SortType.Active,
        default_post_listing_mode = PostListingMode.Card,
    )

val sampleSiteAggregates =
    SiteAggregates(
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

val local_site_rate_limit =
    LocalSiteRateLimit(
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
        import_user_settings = 2,
        import_user_settings_per_second = 2,
        published = "2022-01-01T09:53:46.904077",
    )

val sampleSiteView =
    SiteView(
        site = sampleSite,
        counts = sampleSiteAggregates,
        local_site = sampleLocalSite,
        local_site_rate_limit = local_site_rate_limit,
    )

val sampleGetSiteRes = GetSiteResponse(
    site_view = sampleSiteView,
    admins = listOf(samplePersonView),
    my_user = null,
    all_languages = emptyList(),
    custom_emojis = emptyList(),
    taglines = emptyList(),
    discussion_languages = emptyList(),
    version = "0.0.1",
    blocked_urls = emptyList(),
)

val samplePendingRegistrationApplication =
    RegistrationApplication(
        id = 23,
        local_user_id = 28,
        answer = "**Please** let me in",
        published = "2022-01-01T09:53:46.904077",
    )

val samplePendingRegistrationApplicationView =
    RegistrationApplicationView(
        registration_application = samplePendingRegistrationApplication,
        creator = samplePerson,
        creator_local_user = sampleLocalUser,
    )

val sampleApprovedRegistrationApplication =
    RegistrationApplication(
        id = 24,
        local_user_id = 29,
        answer = "**Please** let me in",
        published = "2022-01-01T09:53:46.904077",
        admin_id = samplePerson2.id,
    )

val sampleApprovedRegistrationApplicationView =
    RegistrationApplicationView(
        registration_application = sampleApprovedRegistrationApplication,
        creator = samplePerson,
        creator_local_user = sampleLocalUser,
        admin = samplePerson2,
    )

val sampleDeniedRegistrationApplication =
    RegistrationApplication(
        id = 24,
        local_user_id = 29,
        answer = "**Please** let me in",
        published = "2022-01-01T09:53:46.904077",
        admin_id = samplePerson2.id,
        deny_reason = "I'm not letting you in, sorry.",
    )

val sampleDeniedRegistrationApplicationView =
    RegistrationApplicationView(
        registration_application = sampleDeniedRegistrationApplication,
        creator = samplePerson,
        creator_local_user = sampleLocalUser,
        admin = samplePerson2,
    )

val samplePostReport =
    PostReport(
        creator_id = 28,
        id = 89,
        original_post_name = samplePost.name,
        post_id = samplePost.id,
        published = samplePost.published,
        reason = "This post is *peak* **cringe**",
        resolved = true,
        resolver_id = samplePerson3.id,
    )

val samplePostReportView =
    PostReportView(
        post_creator = samplePerson,
        creator = samplePerson2,
        resolver = samplePerson3,
        post = samplePost,
        post_report = samplePostReport,
        community = sampleCommunity,
        counts = samplePostAggregates,
        creator_banned_from_community = false,
        hidden = false,
        read = false,
        creator_is_admin = false,
        subscribed = SubscribedType.NotSubscribed,
        creator_is_moderator = false,
        saved = false,
        creator_blocked = false,
        unread_comments = 1,
    )

val sampleCommentReport =
    CommentReport(
        creator_id = 28,
        id = 89,
        original_comment_text = sampleComment.content,
        comment_id = sampleComment.id,
        published = sampleComment.published,
        reason = "This is a bad comment, remove it plz.",
        resolved = true,
        resolver_id = samplePerson3.id,
    )

val sampleCommentReportView =
    CommentReportView(
        comment_creator = samplePerson,
        creator = samplePerson2,
        resolver = samplePerson3,
        post = samplePost,
        comment = sampleComment,
        comment_report = sampleCommentReport,
        community = sampleCommunity,
        counts = sampleCommentAggregates,
        creator_banned_from_community = false,
        subscribed = SubscribedType.NotSubscribed,
        saved = false,
        creator_is_admin = false,
        creator_is_moderator = false,
        creator_blocked = false,
    )

val samplePrivateMessageReport =
    PrivateMessageReport(
        creator_id = 28,
        id = 89,
        original_pm_text = samplePrivateMessage.content,
        private_message_id = samplePrivateMessage.id,
        published = sampleComment.published,
        reason = "This PM is from a spammer",
        resolved = true,
        resolver_id = samplePerson3.id,
    )

val samplePrivateMessageReportView =
    PrivateMessageReportView(
        private_message_report = samplePrivateMessageReport,
        private_message = samplePrivateMessage,
        private_message_creator = samplePerson,
        creator = samplePerson2,
        resolver = samplePerson3,
    )

val sampleInstantScores =
    InstantScores(
        myVote = samplePostView.my_vote,
        score = samplePostView.counts.score,
        upvotes = samplePostView.counts.upvotes,
        downvotes = samplePostView.counts.downvotes,
    )
