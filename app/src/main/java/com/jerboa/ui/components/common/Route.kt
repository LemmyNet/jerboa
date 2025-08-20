package com.jerboa.ui.components.common

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import it.vercruysse.lemmyapi.datatypes.CommentId
import it.vercruysse.lemmyapi.datatypes.CommunityId
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PostId

object Route {
    object Graph {
        const val ROOT = "graph_root"
    }

    const val LOGIN = "login"
    const val INBOX = "inbox"
    const val HOME = "home"

    val COMMUNITY_FROM_ID = CommunityFromIdArgs.route
    val COMMUNITY_FROM_URL = CommunityFromUrlArgs.route

    const val COMMUNITY_SIDEBAR = "communitySidebar"

    val PROFILE_FROM_ID = ProfileFromIdArgs.route
    val PROFILE_FROM_URL = ProfileFromUrlArgs.route

    val COMMUNITY_LIST = CommunityListArgs.route

    const val CREATE_POST = "createPost"

    val POST = PostArgs.route
    val COMMENT = CommentArgs.route
    const val COMMENT_REPLY = "commentReply"

    const val SITE_SIDEBAR = "siteSidebar"
    const val SITE_LEGAL = "siteLegal"
    const val COMMENT_EDIT = "commentEdit"
    const val POST_EDIT = "postEdit"
    const val POST_REMOVE = "postRemove"
    const val PRIVATE_MESSAGE_REPLY = "privateMessageReply"
    const val COMMENT_REMOVE = "commentRemove"
    const val BAN_PERSON = "banPerson"
    const val BAN_FROM_COMMUNITY = "banFromCommunity"
    val CREATE_PRIVATE_MESSAGE = CreatePrivateMessageArgs.route

    val COMMENT_REPORT = CommentReportArgs.route
    val POST_REPORT = PostReportArgs.route

    val COMMENT_LIKES = CommentLikesArgs.route
    val POST_LIKES = PostLikesArgs.route

    const val SETTINGS = "settings"
    const val LOOK_AND_FEEL = "lookAndFeel"
    const val ACCOUNT_SETTINGS = "accountSettings"
    const val ABOUT = "about"
    const val CRASH_LOGS = "crashLogs"
    const val BACKUP_AND_RESTORE = "backupAndRestore"

    const val REGISTRATION_APPLICATIONS = "registrationApplications"
    const val REPORTS = "reports"

    const val BLOCK_VIEW = "blockView"

    val IMAGE_VIEW = ImageViewArgs.route
    val VIDEO_VIEW = VideoViewArgs.route

    class CommunityFromIdArgs(
        val id: CommunityId,
    ) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(id = navBackStackEntry.arguments?.getLong(ID)!!)

        companion object {
            const val ID = "id"
            val ID_TYPE = NavType.LongType

            internal fun makeRoute(id: String) = "community/$id"

            internal val route by lazy { makeRoute(id = "{$ID}") }
        }
    }

    class CommunityFromUrlArgs(
        val instance: String,
        val name: String,
    ) {
        constructor(navBackStackEntry: NavBackStackEntry) : this(
            instance = navBackStackEntry.arguments?.getString(INSTANCE)!!,
            name = navBackStackEntry.arguments?.getString(NAME)!!,
        )

        companion object {
            const val INSTANCE = "instance"
            val INSTANCE_TYPE = NavType.StringType

            const val NAME = "name"
            val NAME_TYPE = NavType.StringType

            fun makeRoute(
                instance: String,
                name: String,
            ) = "$instance/c/$name"

            internal val route by lazy { makeRoute(instance = "{$INSTANCE}", name = "{$NAME}") }
        }
    }

    class CommunityListArgs(
        val select: Boolean,
    ) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(navBackStackEntry.arguments?.getBoolean(SELECT)!!)

        companion object {
            const val SELECT = "select"
            const val SELECT_DEFAULT = false
            val SELECT_TYPE = NavType.BoolType

            fun makeRoute(select: String) = "communityList?select=$select"

            internal val route by lazy { makeRoute(select = "{$SELECT}") }
        }
    }

    class ProfileFromIdArgs(
        val id: PostId,
        val saved: Boolean,
    ) {
        constructor(navBackStackEntry: NavBackStackEntry) : this(
            id = navBackStackEntry.arguments?.getLong(ID)!!,
            saved = navBackStackEntry.arguments?.getBoolean(SAVED)!!,
        )

        companion object {
            const val ID = "id"
            val ID_TYPE = NavType.LongType

            const val SAVED = "saved"
            val SAVED_TYPE = NavType.BoolType
            const val SAVED_DEFAULT = false

            fun makeRoute(
                id: String,
                saved: String,
            ) = "profile/$id?saved=$saved"

            internal val route by lazy { makeRoute(id = "{$ID}", saved = "{$SAVED}") }
        }
    }

    class ProfileFromUrlArgs(
        val instance: String,
        val name: String,
    ) {
        constructor(navBackStackEntry: NavBackStackEntry) : this(
            instance = navBackStackEntry.arguments?.getString(INSTANCE)!!,
            name = navBackStackEntry.arguments?.getString(NAME)!!,
        )

        companion object {
            const val INSTANCE = "instance"
            val INSTANCE_TYPE = NavType.StringType

            const val NAME = "name"
            val NAME_TYPE = NavType.StringType

            fun makeRoute(
                instance: String,
                name: String,
            ) = "$instance/u/$name"

            internal val route by lazy { makeRoute(instance = "{$INSTANCE}", name = "{$NAME}") }
        }
    }

    class PostArgs(
        val id: PostId,
    ) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(id = navBackStackEntry.arguments?.getLong(ID)!!)

        companion object {
            const val ID = "id"
            val ID_TYPE = NavType.LongType

            fun makeRoute(id: String) = "post/$id"

            internal val route by lazy { makeRoute(id = "{$ID}") }
        }
    }

    class CommentArgs(
        val id: CommentId,
    ) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(id = navBackStackEntry.arguments?.getLong(ID)!!)

        companion object {
            const val ID = "id"
            val ID_TYPE = NavType.LongType

            fun makeRoute(id: String) = "comment/$id"

            internal val route by lazy { makeRoute(id = "{$ID}") }
        }
    }

    class CommentReportArgs(
        val id: CommentId,
    ) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(id = navBackStackEntry.arguments?.getLong(ID)!!)

        companion object {
            const val ID = "id"
            val ID_TYPE = NavType.LongType

            fun makeRoute(id: String) = "commentReport/$id"

            internal val route by lazy { makeRoute(id = "{$ID}") }
        }
    }

    class PostReportArgs(
        val id: PostId,
    ) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(id = navBackStackEntry.arguments?.getLong(ID)!!)

        companion object {
            const val ID = "id"
            val ID_TYPE = NavType.LongType

            fun makeRoute(id: String) = "postReport/$id"

            internal val route by lazy { makeRoute(id = "{$ID}") }
        }
    }

    class CommentLikesArgs(
        val id: CommentId,
    ) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(id = navBackStackEntry.arguments?.getLong(ID)!!)

        companion object {
            const val ID = "id"
            val ID_TYPE = NavType.LongType

            fun makeRoute(id: String) = "commentLikes/$id"

            internal val route by lazy { makeRoute(id = "{$ID}") }
        }
    }

    class PostLikesArgs(
        val id: PostId,
    ) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(id = navBackStackEntry.arguments?.getLong(ID)!!)

        companion object {
            const val ID = "id"
            val ID_TYPE = NavType.LongType

            fun makeRoute(id: String) = "postLikes/$id"

            internal val route by lazy { makeRoute(id = "{$ID}") }
        }
    }

    class ImageViewArgs(
        val url: String,
    ) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(url = navBackStackEntry.arguments?.getString(URL)!!)

        companion object {
            const val URL = "url"
            val URL_TYPE = NavType.StringType

            fun makeRoute(url: String) = "image/view/$url"

            internal val route by lazy { makeRoute(url = "{$URL}") }
        }
    }

    class VideoViewArgs(
        val url: String,
    ) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(url = navBackStackEntry.arguments?.getString(URL)!!)

        companion object {
            const val URL = "url"
            val URL_TYPE = NavType.StringType

            fun makeRoute(url: String) = "video/view/$url"

            internal val route by lazy { makeRoute(url = "{$URL}") }
        }
    }

    class CreatePrivateMessageArgs(
        val personId: PersonId,
        val personName: String,
    ) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(
                personId = navBackStackEntry.arguments?.getLong(PERSON_ID)!!,
                personName = navBackStackEntry.arguments?.getString(PERSON_NAME)!!,
            )

        companion object {
            const val PERSON_ID = "person_id"
            val PERSON_ID_TYPE = NavType.LongType

            const val PERSON_NAME = "person_name"
            val PERSON_NAME_TYPE = NavType.StringType

            fun makeRoute(
                personId: String,
                personName: String,
            ) = "createPrivateMessage/$personId/$personName"

            internal val route by lazy { makeRoute(personId = "{$PERSON_ID}", personName = "{$PERSON_NAME}") }
        }
    }
}
