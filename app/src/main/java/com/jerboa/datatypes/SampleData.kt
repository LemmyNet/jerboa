package com.jerboa.datatypes

import com.jerboa.feat.InstantScores
import it.vercruysse.lemmyapi.datatypes.Comment
import it.vercruysse.lemmyapi.datatypes.CommentReport
import it.vercruysse.lemmyapi.datatypes.CommentReportView
import it.vercruysse.lemmyapi.datatypes.CommentView
import it.vercruysse.lemmyapi.datatypes.Community
import it.vercruysse.lemmyapi.datatypes.CommunityView
import it.vercruysse.lemmyapi.datatypes.GetSiteResponse
import it.vercruysse.lemmyapi.datatypes.LocalSite
import it.vercruysse.lemmyapi.datatypes.LocalSiteRateLimit
import it.vercruysse.lemmyapi.datatypes.LocalUser
import it.vercruysse.lemmyapi.datatypes.Person
import it.vercruysse.lemmyapi.datatypes.PersonView
import it.vercruysse.lemmyapi.datatypes.Post
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
import it.vercruysse.lemmyapi.datatypes.SiteView
import it.vercruysse.lemmyapi.enums.CommentSortType
import it.vercruysse.lemmyapi.enums.CommunityVisibility
import it.vercruysse.lemmyapi.enums.FederationMode
import it.vercruysse.lemmyapi.enums.ImageMode
import it.vercruysse.lemmyapi.enums.ListingType
import it.vercruysse.lemmyapi.enums.PostListingMode
import it.vercruysse.lemmyapi.enums.RegistrationMode
import it.vercruysse.lemmyapi.enums.SortType
import it.vercruysse.lemmyapi.enums.VoteShow

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
        published_at = "2022-01-01T09:53:46.904077Z",
        updated_at = null,
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
        url_content_type = null,
        alt_text = null,
        scheduled_publish_time_at = null,
        newest_comment_time_at = null,
        comments = 0,
        score = 0,
        upvotes = 0,
        downvotes = 0,
        report_count = 0,
        unresolved_report_count = 0,
        federation_pending = false,
        embed_video_width = null,
        embed_video_height = null,
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
        published_at = "2022-01-01T09:53:46.904077Z",
        updated_at = null,
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
        url_content_type = null,
        alt_text = null,
        scheduled_publish_time_at = null,
        newest_comment_time_at = null,
        comments = 0,
        score = 0,
        upvotes = 0,
        downvotes = 0,
        report_count = 0,
        unresolved_report_count = 0,
        federation_pending = false,
        embed_video_width = null,
        embed_video_height = null,
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
        published_at = "2022-01-01T09:53:46.904077Z",
        updated_at = null,
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
        url_content_type = null,
        alt_text = null,
        scheduled_publish_time_at = null,
        newest_comment_time_at = null,
        comments = 0,
        score = 0,
        upvotes = 0,
        downvotes = 0,
        report_count = 0,
        unresolved_report_count = 0,
        federation_pending = false,
        embed_video_width = null,
        embed_video_height = null,
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
        published_at = "2022-01-01T09:53:46.904077Z",
        updated_at = null,
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
        url_content_type = null,
        alt_text = null,
        scheduled_publish_time_at = null,
        newest_comment_time_at = null,
        comments = 0,
        score = 0,
        upvotes = 0,
        downvotes = 0,
        report_count = 0,
        unresolved_report_count = 0,
        federation_pending = false,
        embed_video_width = null,
        embed_video_height = null,
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
        published_at = "2022-01-01T09:53:46.904077Z",
        updated_at = null,
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
        url_content_type = null,
        alt_text = null,
        scheduled_publish_time_at = null,
        newest_comment_time_at = null,
        comments = 0,
        score = 0,
        upvotes = 0,
        downvotes = 0,
        report_count = 0,
        unresolved_report_count = 0,
        federation_pending = false,
        embed_video_width = null,
        embed_video_height = null,
    )

val samplePerson =
    Person(
        id = 33401,
        name = "homeless",
        display_name = "No longer Homeless",
        avatar = "https://lemmy.ml/pictrs/image/LqURxPzFNW.jpg",
        published_at = "2021-08-08T01:47:44.437708Z",
        updated_at = "2021-10-11T07:14:53.548707Z",
        ap_id = "https://lemmy.ml/u/homeless",
        bio =
            "This is my bio.\n\nI like trucks, trains, and geese. This is *one* longer line " +
                    "that I have in here. But I'm not sure blah blah blah\n\nI have " +
                    "**tres ojos**.",
        local = true,
        banner = null,
        deleted = false,
        matrix_user_id = null,
        bot_account = false,
        instance_id = 0,
        post_count = 28,
        comment_count = 98,
        last_refreshed_at = "2021-08-08T01:47:44.437708Z",
    )

val samplePerson2 =
    Person(
        id = 33403,
        name = "gary_host_laptop",
        display_name = null,
        avatar = "https://lemmy.ml/pictrs/image/kykidJ1ssM.jpg",
        published_at = "2021-08-08T01:47:44.437708Z",
        updated_at = "2021-10-11T07:14:53.548707Z",
        ap_id = "https://lemmy.ml/u/homeless",
        bio = null,
        local = false,
        banner = null,
        deleted = false,
        matrix_user_id = null,
        bot_account = false,
        instance_id = 0,
        post_count = 0,
        comment_count = 0,
        last_refreshed_at = "2021-08-08T01:47:44.437708Z",
    )

val samplePerson3 =
    Person(
        id = 33478,
        name = "witch_power",
        display_name = null,
        published_at = "2021-08-08T01:47:44.437708Z",
        updated_at = "2021-10-11T07:14:53.548707Z",
        ap_id = "https://lemmy.ml/u/witch_power",
        bio = null,
        local = true,
        banner = null,
        deleted = false,
        matrix_user_id = null,
        bot_account = false,
        instance_id = 0,
        post_count = 0,
        comment_count = 0,
        last_refreshed_at = "2021-08-08T01:47:44.437708Z",
    )

val sampleLocalUser = LocalUser(
    id = 24,
    person_id = 82,
    show_nsfw = false,
    theme = "none",
    default_post_sort_type = SortType.Active,
    default_listing_type = ListingType.Local,
    interface_language = "en",
    show_avatars = false,
    send_notifications_to_email = false,
    show_bot_accounts = false,
    show_read_posts = false,
    email_verified = false,
    accepted_application = false,
    open_links_in_new_tab = false,
    blur_nsfw = false,
    infinite_scroll_enabled = false,
    admin = false,
    post_listing_mode = PostListingMode.List,
    totp_2fa_enabled = false,
    collapse_bot_comments = false,
    last_donation_notification_at = "2019-04-30T13:28:35.965035Z",
    email = null,
    animated_images_enabled = false,
    private_messages_enabled = false,
    default_comment_sort_type = CommentSortType.Hot,
    auto_mark_fetched_posts_as_read = false,
    hide_media = false,
    default_post_time_range_seconds = null,
    show_score = true,
    show_upvotes = true,
    show_downvotes = VoteShow.Show,
    show_upvote_percentage = true,
    show_person_votes = true,
    default_items_per_page = 20,
)

val sampleCommunity =
    Community(
        id = 14681,
        name = "socialism",
        title = "Socialism",
        sidebar = "This is the r/socialism community",
        removed = false,
        published_at = "2019-04-30T13:28:35.965035Z",
        updated_at = "2021-01-25T16:27:15.804739Z",
        deleted = false,
        nsfw = false,
        ap_id = "https://lemmy.ml/c/socialism",
        local = true,
        icon = "https://lemmy.ml/pictrs/image/QtiqDmp9XY.png",
        banner = "https://lemmy.ml/pictrs/image/386rk5OYWS.jpg",
        instance_id = 0,
        posting_restricted_to_mods = false,
        visibility = CommunityVisibility.Public,
        last_refreshed_at = "2021-01-25T16:27:15.804739Z",
        summary = null,
        subscribers = 52,
        subscribers_local = 21,
        posts = 82,
        comments = 987,
        users_active_day = 28,
        users_active_week = 98,
        users_active_month = 82,
        users_active_half_year = 91,
        report_count =0,
        unresolved_report_count = 0,
        local_removed = false,
    )

val sampleCommunityFederated = sampleCommunity.copy(local = false)

val samplePostView =
    PostView(
        post = samplePost,
        creator = samplePerson,
        community = sampleCommunity,
        creator_banned_from_community = false,
        creator_is_moderator = false,
        creator_is_admin = false,
        image_details = null,
        community_actions = null,
        person_actions = null,
        post_actions = null,
        tags = emptyList(),
        can_mod = false,
        creator_banned = false,
        creator_ban_expires_at = null,
        creator_community_ban_expires_at = null,
    )

val sampleLinkPostView =
    PostView(
        post = sampleLinkPost,
        creator = samplePerson,
        community = sampleCommunity,
        creator_banned_from_community = false,
        creator_is_moderator = false,
        creator_is_admin = false,
        image_details = null,
        community_actions = null,
        person_actions = null,
        post_actions = null,
        tags = emptyList(),
        can_mod = false,
        creator_banned = false,
        creator_ban_expires_at = null,
        creator_community_ban_expires_at = null,
    )

val sampleLinkNoThumbnailPostView =
    PostView(
        post = sampleLinkNoThumbnailPost,
        creator = samplePerson,
        community = sampleCommunity,
        creator_banned_from_community = false,
        creator_is_moderator = false,
        creator_is_admin = false,
        image_details = null,
        community_actions = null,
        person_actions = null,
        post_actions = null,
        tags = emptyList(),
        can_mod = false,
        creator_banned = false,
        creator_ban_expires_at = null,
        creator_community_ban_expires_at = null,
    )

val sampleImagePostView =
    PostView(
        post = sampleImagePost,
        creator = samplePerson,
        community = sampleCommunity,
        creator_banned_from_community = false,
        creator_is_moderator = false,
        creator_is_admin = false,
        image_details = null,
        community_actions = null,
        person_actions = null,
        post_actions = null,
        tags = emptyList(),
        can_mod = false,
        creator_banned = false,
        creator_ban_expires_at = null,
        creator_community_ban_expires_at = null,
    )

val sampleMarkdownPostView =
    PostView(
        post = sampleMarkdownPost,
        creator = samplePerson,
        community = sampleCommunity,
        creator_banned_from_community = false,
        creator_is_moderator = false,
        creator_is_admin = false,
        image_details = null,
        community_actions = null,
        person_actions = null,
        post_actions = null,
        tags = emptyList(),
        can_mod = false,
        creator_banned = false,
        creator_ban_expires_at = null,
        creator_community_ban_expires_at = null,
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
        published_at = "2022-01-07T03:12:26.398434Z",
        updated_at = "2022-01-07T03:15:37.360888Z",
        deleted = false,
        ap_id = "https://midwest.social/comment/24621",
        local = false,
        distinguished = false,
        language_id = 0,
        path = "0.1",
        score = 0,
        upvotes = 0,
        downvotes = 0,
        child_count = 0,
        report_count = 0,
        unresolved_report_count = 0,
        federation_pending = false,
        locked = false,
    )

val sampleReplyComment =
    Comment(
        id = 2,
        creator_id = 423,
        post_id = 139549,
        path = "0.1.2",
        content = "This is a reply comment.\n\n# This is a header\n\n- list one\n\n- list two",
        removed = false,
        published_at = "2022-01-07T04:12:26.398434Z",
        updated_at = "2022-01-07T03:15:37.360888Z",
        deleted = false,
        ap_id = "https://midwest.social/comment/24622",
        local = false,
        distinguished = false,
        language_id = 0,
        score = 0,
        upvotes = 0,
        downvotes = 0,
        child_count = 0,
        report_count = 0,
        unresolved_report_count = 0,
        federation_pending = false,
        locked = false,
    )

val sampleSecondReplyComment =
    Comment(
        id = 3,
        creator_id = 423,
        post_id = 139549,
        path = "0.1.2.3",
        content = "This is a sub-reply comment, mmmmk",
        removed = false,
        published_at = "2022-01-07T04:12:26.398434Z",
        updated_at = "2022-01-07T03:15:37.360888Z",
        deleted = false,
        ap_id = "https://midwest.social/comment/24622",
        local = false,
        distinguished = false,
        language_id = 0,
        score = 0,
        upvotes = 0,
        downvotes = 0,
        child_count = 0,
        report_count = 0,
        unresolved_report_count = 0,
        federation_pending = false,
        locked = false,
    )

val sampleCommentView =
    CommentView(
        comment = sampleComment,
        creator = samplePerson,
        post = samplePost,
        community = sampleCommunity,
        creator_banned_from_community = false,
        creator_is_moderator = false,
        creator_is_admin = false,
        tags = emptyList(),
        can_mod = false,
        creator_banned = false,
    )

val sampleSecondReplyCommentView =
    CommentView(
        comment = sampleSecondReplyComment,
        creator = samplePerson,
        post = samplePost,
        community = sampleCommunity,
        creator_banned_from_community = false,
        creator_is_moderator = false,
        creator_is_admin = false,
        tags = emptyList(),
        can_mod = false,
        creator_banned = false,
    )

val sampleReplyCommentView =
    CommentView(
        comment = sampleReplyComment,
        creator = samplePerson2,
        post = samplePost,
        community = sampleCommunity,
        creator_banned_from_community = false,
        creator_is_moderator = false,
        creator_is_admin = false,
        tags = emptyList(),
        can_mod = false,
        creator_banned = false,
    )

val sampleCommunityView =
    CommunityView(
        community = sampleCommunity,
        can_mod = false,
        tags = emptyList(),
    )

val samplePersonView =
    PersonView(
        person = samplePerson,
        is_admin = false,
        banned = false,
    )

val samplePrivateMessage =
    PrivateMessage(
        id = 32,
        creator_id = 83,
        recipient_id = 35,
        content = "A message from *me* to **you**",
        deleted = false,
        read = false,
        published_at = "2022-01-07T04:12:26.398434Z",
        updated_at = "2022-01-07T03:15:37.360888Z",
        ap_id = "https://midwest.social/comment/24622",
        local = false,
        removed = false,
        deleted_by_recipient = false,
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
        summary = "A general purpose instance for lemmy",
        published_at = "2022-01-07T04:12:26.398434Z",
        updated_at = "2022-01-07T03:15:37.360888Z",
        icon = "https://lemmy.ml/pictrs/image/LqURxPzFNW.jpg",
        banner = "https://lemmy.ml/pictrs/image/386rk5OYWS.jpg",
        ap_id = "https://lemmy.ml",
        inbox_url = "https://lemmy.ml",
        last_refreshed_at = "2023-01-01",
        instance_id = 0,
    )

val sampleLocalSite =
    LocalSite(
        registration_mode = RegistrationMode.Open,
        community_creation_admin_only = true,
        application_question = null,
        private_instance = false,
        application_email_admins = false,
        default_post_listing_type = ListingType.All,
        default_theme = "main",
        federation_enabled = true,
        id = 1,
        legal_information = null,
        published_at = "2023-01-01",
        site_setup = true,
        site_id = 1,
        slur_filter_regex = null,
        federation_signed_fetch = false,
        reports_email_admins = false,
        default_post_sort_type = SortType.Active,
        default_post_listing_mode = PostListingMode.Card,
        email_verification_required = false,
        default_comment_sort_type = CommentSortType.Hot,
        oauth_registration = false,
        post_upvotes = FederationMode.All,
        post_downvotes = FederationMode.All,
        comment_upvotes = FederationMode.All,
        comment_downvotes = FederationMode.All,
        nsfw_content_disallowed = false,
        users = 22,
        posts = 67,
        comments = 322,
        communities = 6,
        users_active_day = 3,
        users_active_week = 6,
        users_active_month = 9,
        users_active_half_year = 12,
        email_notifications_disabled = false,
        default_items_per_page = 20,
        image_mode = ImageMode.ProxyAllImages,
        image_upload_timeout_seconds = 180,
        image_max_thumbnail_size = 256,
        image_max_avatar_size = 256,
        image_max_banner_size = 1024,
        image_max_upload_size = 5096,
        image_allow_video_uploads = true,
        image_upload_disabled = false,
    )

val sampleLocalSiteRateLimit =
    LocalSiteRateLimit(
        local_site_id = 2,
        message_max_requests = 2,
        message_interval_seconds = 2,
        post_max_requests = 2,
        post_interval_seconds = 2,
        register_max_requests = 2,
        register_interval_seconds = 2,
        image_max_requests = 2,
        image_interval_seconds = 2,
        comment_max_requests = 2,
        comment_interval_seconds = 2,
        search_max_requests = 2,
        search_interval_seconds = 2,
        import_user_settings_max_requests = 2,
        import_user_settings_interval_seconds = 2,
        published_at = "2022-01-01T09:53:46.904077",
    )

val sampleSiteView =
    SiteView(
        site = sampleSite,
        local_site = sampleLocalSite,
        local_site_rate_limit = sampleLocalSiteRateLimit,
        instance = null,
    )

val sampleGetSiteRes = GetSiteResponse(
    site_view = sampleSiteView,
    admins = listOf(samplePersonView),
    all_languages = emptyList(),
    discussion_languages = emptyList(),
    version = "0.0.1",
    blocked_urls = emptyList(),
    oauth_providers = emptyList(),
    admin_oauth_providers = emptyList(),
    active_plugins = emptyList(),
    captcha_enabled =false,
)

val samplePendingRegistrationApplication =
    RegistrationApplication(
        id = 23,
        local_user_id = 28,
        answer = "**Please** let me in",
        published_at = "2022-01-01T09:53:46.904077",
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
        published_at = "2022-01-01T09:53:46.904077",
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
        published_at = "2022-01-01T09:53:46.904077",
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
        published_at = samplePost.published_at,
        reason = "This post is *peak* **cringe**",
        resolved = true,
        resolver_id = samplePerson3.id,
        violates_instance_rules = false,
    )

val samplePostReportView =
    PostReportView(
        post_creator = samplePerson,
        creator = samplePerson2,
        resolver = samplePerson3,
        post = samplePost,
        post_report = samplePostReport,
        community = sampleCommunity,
        creator_banned_from_community = false,
        creator_is_admin = false,
        creator_is_moderator = false,
        creator_banned = false,
    )

val sampleCommentReport =
    CommentReport(
        creator_id = 28,
        id = 89,
        original_comment_text = sampleComment.content,
        comment_id = sampleComment.id,
        published_at = sampleComment.published_at,
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
        creator_banned_from_community = false,
        creator_is_admin = false,
        creator_is_moderator = false,
        creator_banned = false,
    )

val samplePrivateMessageReport =
    PrivateMessageReport(
        creator_id = 28,
        id = 89,
        original_pm_text = samplePrivateMessage.content,
        private_message_id = samplePrivateMessage.id,
        published_at = sampleComment.published_at,
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
        creator_is_admin = false,
        creator_banned = false,
    )

val sampleInstantScores =
    InstantScores(
        myVote = 1,
        score = 4,
        upvotes = 5,
        downvotes = 1,
    )
