package com.jerboa.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jerboa.datatypes.types.CommentView
import com.jerboa.datatypes.types.Community
import com.jerboa.datatypes.types.PostView
import com.jerboa.datatypes.types.PrivateMessageView
import com.jerboa.gson
import com.jerboa.ui.components.comment.reply.ReplyItem

// A view model stored higher up the tree used for moving navigation arguments from one route
// to another. Since this will be reused, the value inside this should be moved out ASAP.
// The value inside this will also not survive process death, so should be saved elsewhere.
class RouteChannel<D> : ViewModel() {
    private var value: D? = null

    fun put(value: D) {
        this.value = value
    }

    fun take(): D? {
        val value = this.value
        this.value = null
        return value
    }
}

@Composable
fun<D> NavController.rootChannel(): RouteChannel<D> {
    // This will create a ViewModel<D> on the fly and will be stored on the nav stack entry
    //  with route = Route.Graph.ROOT.
    val root = remember(currentBackStackEntry) { getBackStackEntry(Route.Graph.ROOT) }
    return viewModel(root)
}

@Composable
inline fun <reified D> NavController.takeDepsFromRoot(): State<D> {
    // HACK: the autogen types aren't serializable or parcelable. If they were we don't
    //  have to do this manual conversion to json and back.
    // TODO: Fix this once autogen types are serializable or parcelable

    val deps = rootChannel<D>().take()

    // This will survive process death
    val depsJson = rememberSaveable { gson.toJson(deps) }

    // After process death, deps will be null
    return remember(depsJson) {
        derivedStateOf {
            deps ?: gson.fromJson(depsJson, D::class.java)
        }
    }
}

fun NavController.toLogin() = navigate(Route.LOGIN)

fun NavController.toHome() = navigate(Route.HOME) { popUpTo(0) }

fun NavController.toInbox() = navigate(Route.INBOX)

fun NavController.toCommunity(id: Int) {
    navigate(Route.CommunityFromIdArgs.makeRoute(id = "$id"))
}

fun NavController.toCommunitySideBar() = navigate(Route.COMMUNITY_SIDEBAR)

fun NavController.toProfile(id: Int, saved: Boolean = false) {
    navigate(Route.ProfileFromIdArgs.makeRoute(id = "$id", saved = "$saved"))
}

fun NavController.toCommunityList(select: Boolean = Route.CommunityListArgs.SELECT_DEFAULT) {
    navigate(Route.CommunityListArgs.makeRoute(select = "$select"))
}

typealias CreatePostDeps = Community?
fun NavController.toCreatePost(
    channel: RouteChannel<CreatePostDeps>,
    community: Community?,
) {
    channel.put(community)
    navigate(Route.CREATE_POST)
}

fun NavController.toPost(id: Int) {
    navigate(Route.PostArgs.makeRoute(id = "$id"))
}

fun NavController.toComment(id: Int) {
    navigate(Route.CommentArgs.makeRoute(id = "$id"))
}

typealias CommentReplyDeps = ReplyItem
fun NavController.toCommentReply(
    channel: RouteChannel<CommentReplyDeps>,
    replyItem: ReplyItem,
    isModerator: Boolean,
) {
    channel.put(replyItem)
    navigate(Route.CommentReplyArgs.makeRoute(isModerator = "$isModerator"))
}

fun NavController.toSiteSideBar() = navigate(Route.SITE_SIDEBAR)

typealias CommentEditDeps = CommentView
fun NavController.toCommentEdit(
    channel: RouteChannel<CommentEditDeps>,
    commentView: CommentView,
) {
    channel.put(commentView)
    navigate(Route.COMMENT_EDIT)
}

typealias PostEditDeps = PostView
fun NavController.toPostEdit(
    channel: RouteChannel<PostEditDeps>,
    postView: PostView,
) {
    channel.put(postView)
    navigate(Route.POST_EDIT)
}

typealias PrivateMessageDeps = PrivateMessageView
fun NavController.toPrivateMessageReply(
    channel: RouteChannel<PrivateMessageDeps>,
    privateMessageView: PrivateMessageView,
) {
    channel.put(privateMessageView)
    navigate(Route.PRIVATE_MESSAGE_REPLY)
}

fun NavController.toCommentReport(id: Int) {
    navigate(Route.CommentReportArgs.makeRoute(id = "$id"))
}

fun NavController.toPostReport(id: Int) {
    navigate(Route.PostReportArgs.makeRoute(id = "$id"))
}

fun NavController.toSettings() = navigate(Route.SETTINGS)

fun NavController.toAccountSettings() = navigate(Route.ACCOUNT_SETTINGS)

fun NavController.toLookAndFeel() = navigate(Route.LOOK_AND_FEEL)

fun NavController.toAbout() = navigate(Route.ABOUT)
