package com.jerboa.ui.components.common

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType

object Route {
    object Graph {
        const val ROOT = "graph_root"
        const val COMMUNITY = "graph_community"
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
    val COMMENT_REPLY = CommentReplyArgs.route

    const val SITE_SIDEBAR = "siteSidebar"
    const val COMMENT_EDIT = "commentEdit"
    const val POST_EDIT = "postEdit"
    const val PRIVATE_MESSAGE_REPLY = "privateMessageReply"

    val COMMENT_REPORT = CommentReportArgs.route
    val POST_REPORT = PostReportArgs.route

    const val SETTINGS = "settings"
    const val LOOK_AND_FEEL = "lookAndFeel"
    const val ACCOUNT_SETTINGS = "accountSettings"
    const val ABOUT = "about"

    class CommunityFromIdArgs(val id: Int) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(id = navBackStackEntry.arguments?.getInt(ID)!!)

        companion object {
            const val ID = "id"
            val ID_TYPE = NavType.IntType

            internal fun makeRoute(id: String) = "community/$id"
            internal val route by lazy { makeRoute(id = "{$ID}") }
        }
    }

    class CommunityFromUrlArgs(val instance: String, val name: String) {
        constructor(navBackStackEntry: NavBackStackEntry) : this(
            instance = navBackStackEntry.arguments?.getString(INSTANCE)!!,
            name = navBackStackEntry.arguments?.getString(NAME)!!,
        )

        companion object {
            const val INSTANCE = "instance"
            val INSTANCE_TYPE = NavType.StringType

            const val NAME = "name"
            val NAME_TYPE = NavType.StringType

            fun makeRoute(instance: String, name: String) = "$instance/c/$name"
            internal val route by lazy { makeRoute(instance = "{$INSTANCE}", name = "{$NAME}") }
        }
    }

    class CommunityListArgs(val select: Boolean) {
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

    class ProfileFromIdArgs(val id: Int, val saved: Boolean) {
        constructor(navBackStackEntry: NavBackStackEntry) : this(
            id = navBackStackEntry.arguments?.getInt(ID)!!,
            saved = navBackStackEntry.arguments?.getBoolean(SAVED)!!,
        )

        companion object {
            const val ID = "id"
            val ID_TYPE = NavType.IntType

            const val SAVED = "saved"
            val SAVED_TYPE = NavType.BoolType
            const val SAVED_DEFAULT = false

            fun makeRoute(id: String, saved: String) = "profile/$id?saved=$saved"
            internal val route by lazy { makeRoute(id = "{$ID}", saved = "{$SAVED}") }
        }
    }

    class ProfileFromUrlArgs(val instance: String, val name: String) {
        constructor(navBackStackEntry: NavBackStackEntry) : this(
            instance = navBackStackEntry.arguments?.getString(INSTANCE)!!,
            name = navBackStackEntry.arguments?.getString(NAME)!!,
        )

        companion object {
            const val INSTANCE = "instance"
            val INSTANCE_TYPE = NavType.StringType

            const val NAME = "name"
            val NAME_TYPE = NavType.StringType

            fun makeRoute(instance: String, name: String) = "$instance/u/$name"
            internal val route by lazy { makeRoute(instance = "{$INSTANCE}", name = "{$NAME}") }
        }
    }

    class PostArgs(val id: Int) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(id = navBackStackEntry.arguments?.getInt(ID)!!)

        companion object {
            const val ID = "id"
            val ID_TYPE = NavType.IntType

            fun makeRoute(id: String) = "post/$id"
            internal val route by lazy { makeRoute(id = "{$ID}") }
        }
    }

    class CommentArgs(val id: Int) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(id = navBackStackEntry.arguments?.getInt(ID)!!)

        companion object {
            const val ID = "id"
            val ID_TYPE = NavType.IntType

            fun makeRoute(id: String) = "comment/$id"
            internal val route by lazy { makeRoute(id = "{$ID}") }
        }
    }

    class CommentReplyArgs(val isModerator: Boolean) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(isModerator = navBackStackEntry.arguments?.getBoolean(IS_MODERATOR)!!)

        companion object {
            const val IS_MODERATOR = "isModerator"
            val IS_MODERATOR_TYPE = NavType.BoolType

            fun makeRoute(isModerator: String) = "commentReply?isModerator=$isModerator"
            internal val route by lazy { makeRoute(isModerator = "{$IS_MODERATOR}") }
        }
    }

    class CommentReportArgs(val id: Int) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(id = navBackStackEntry.arguments?.getInt(ID)!!)

        companion object {
            const val ID = "id"
            val ID_TYPE = NavType.IntType

            fun makeRoute(id: String) = "commentReport/$id"
            internal val route by lazy { makeRoute(id = "{$ID}") }
        }
    }

    class PostReportArgs(val id: Int) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(id = navBackStackEntry.arguments?.getInt(ID)!!)

        companion object {
            const val ID = "id"
            val ID_TYPE = NavType.IntType

            fun makeRoute(id: String) = "postReport/$id"
            internal val route by lazy { makeRoute(id = "{$ID}") }
        }
    }
}
