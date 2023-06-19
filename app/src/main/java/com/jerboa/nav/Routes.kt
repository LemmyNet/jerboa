package com.jerboa.nav

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType

enum class HomeTab {
    Feed, Search, Inbox, Saved, Profile;

    fun needsLogin() = this == Inbox || this == Saved || this == Profile
}

class Route {
    companion object {
        const val LOGIN = "login"
        const val INBOX = "inbox"

        val HOME = HomeArgs.route
        val COMMUNITY_FROM_ID = CommunityFromIdArgs.route
        val COMMUNITY_FROM_URL = CommunityFromUrlArgs.route

        const val COMMUNITY_SIDEBAR = "communitySidebar"

        val PROFILE_FROM_ID = ProfileFromIdArgs.route
        val PROFILE_FROM_URL = ProfileFromUrlArgs.route

        const val COMMUNITY_LIST = "communityList"
        const val CREATE_POST = "createPost"

        val POST = PostArgs.route
        val COMMENT = CommentArgs.route

        const val COMMENT_REPLY = "commentReply"
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
    }

    class HomeArgs(val tab: HomeTab) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(
                when (navBackStackEntry.arguments?.getString(TAB)?.lowercase()) {
                    HomeTab.Feed.name.lowercase() -> HomeTab.Feed
                    HomeTab.Search.name.lowercase() -> HomeTab.Search
                    HomeTab.Inbox.name.lowercase() -> HomeTab.Inbox
                    HomeTab.Saved.name.lowercase() -> HomeTab.Saved
                    HomeTab.Profile.name.lowercase() -> HomeTab.Profile
                    else -> TAB_DEFAULT
                },
            )

        companion object {
            const val TAB = "tab"
            val TAB_TYPE = NavType.StringType
            val TAB_DEFAULT = HomeTab.Feed

            fun makeRoute(tab: String) = "home?tab=$tab"
            internal val route by lazy { makeRoute(tab = "{$TAB}") }
        }
    }

    class CommunityFromIdArgs(val id: Int) {
        constructor(navBackStackEntry: NavBackStackEntry) :
            this(id = navBackStackEntry.arguments?.getInt(ID)!!)

        companion object {
            const val ID = "id"
            val ID_TYPE = NavType.IntType

            fun makeRoute(id: String) = "community/$id"
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
            val NAME_TYPE = NavType.IntType

            fun makeRoute(instance: String, name: String) = "$instance/c/$name"
            internal val route by lazy { makeRoute(instance = "{$INSTANCE}", name = "{$NAME}") }
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
            val NAME_TYPE = NavType.IntType

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
