package com.jerboa

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jerboa.datatypes.types.CommentView
import com.jerboa.datatypes.types.Community
import com.jerboa.datatypes.types.CommunityView
import com.jerboa.datatypes.types.PostView
import com.jerboa.datatypes.types.PrivateMessageView
import com.jerboa.model.ReplyItem
import com.jerboa.ui.components.common.Route
import com.jerboa.ui.components.community.sidebar.CommunityViewSidebar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun rememberJerboaAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): JerboaAppState {
    return remember(
        navController,
        coroutineScope,
    ) {
        JerboaAppState(
            navController,
            coroutineScope,
        )
    }
}

@Stable
class JerboaAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
) {
    val linkDropdownExpanded = mutableStateOf<String?>(null)

    fun toPrivateMessageReply(
        channel: RouteChannel<PrivateMessageDeps>,
        privateMessageView: PrivateMessageView,
    ) {
        channel.put(privateMessageView)
        navController.navigate(Route.PRIVATE_MESSAGE_REPLY)
    }

    fun toCommentReport(id: Int) {
        navController.navigate(Route.CommentReportArgs.makeRoute(id = "$id"))
    }

    fun toPostReport(id: Int) {
        navController.navigate(Route.PostReportArgs.makeRoute(id = "$id"))
    }

    fun toSettings() = navController.navigate(Route.SETTINGS)

    fun toAccountSettings() = navController.navigate(Route.ACCOUNT_SETTINGS)

    fun toLookAndFeel() = navController.navigate(Route.LOOK_AND_FEEL)

    fun toAbout() = navController.navigate(Route.ABOUT)

    fun toCrashLogs() = navController.navigate(Route.CRASH_LOGS)

    fun openImageViewer(url: String) {
        val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.name())
        navController.navigate(Route.ViewArgs.makeRoute(encodedUrl))
    }

    fun toPostEdit(
        channel: RouteChannel<PostEditDeps>,
        postView: PostView,
    ) {
        channel.put(postView)
        navController.navigate(Route.POST_EDIT)
    }

    fun toCommentEdit(
        channel: RouteChannel<CommentEditDeps>,
        commentView: CommentView,
    ) {
        channel.put(commentView)
        navController.navigate(Route.COMMENT_EDIT)
    }

    fun toSiteSideBar() = navController.navigate(Route.SITE_SIDEBAR)

    fun toCommentReply(
        channel: RouteChannel<CommentReplyDeps>,
        replyItem: ReplyItem,
        isModerator: Boolean,
    ) {
        channel.put(replyItem)
        navController.navigate(Route.CommentReplyArgs.makeRoute(isModerator = "$isModerator"))
    }

    fun toComment(id: Int) {
        navController.navigate(Route.CommentArgs.makeRoute(id = "$id"))
    }

    fun toCreatePost(
        channel: RouteChannel<CreatePostDeps>,
        community: Community?,
    ) {
        channel.put(community)
        navController.navigate(Route.CREATE_POST)
    }

    fun toPost(id: Int) {
        navController.navigate(Route.PostArgs.makeRoute(id = "$id"))
    }

    fun toLogin() = navController.navigate(Route.LOGIN)

    fun toHome() = navController.navigate(Route.HOME) { popUpTo(navController.graph.id) }

    fun toInbox() = navController.navigate(Route.INBOX)

    fun toCommunity(id: Int) {
        navController.navigate(Route.CommunityFromIdArgs.makeRoute(id = "$id"))
    }

    fun toCommunitySideBar(communityView: CommunityView) {
        storeReturn(CommunityViewSidebar.COMMUNITY_VIEW, communityView)
        navController.navigate(Route.COMMUNITY_SIDEBAR)
    }

    fun toProfile(id: Int, saved: Boolean = false) {
        navController.navigate(Route.ProfileFromIdArgs.makeRoute(id = "$id", saved = "$saved"))
    }

    fun toCommunityList(select: Boolean = Route.CommunityListArgs.SELECT_DEFAULT) {
        navController.navigate(Route.CommunityListArgs.makeRoute(select = "$select"))
    }

    fun popBackStack(): Boolean = navController.popBackStack()

    fun navigateUp(): Boolean = navController.navigateUp()

    fun openLinkRaw(url: String, useCustomTabs: Boolean, usePrivateTabs: Boolean) {
        openLinkRaw(url, navController, useCustomTabs, usePrivateTabs)
    }

    fun openLink(url: String, useCustomTabs: Boolean, usePrivateTabs: Boolean) {
        // Navigation must be done on the main thread
        coroutineScope.launch(Dispatchers.Main) {
            openLink(url, navController, useCustomTabs, usePrivateTabs)
        }
    }

    /**
     * Stores the parcelable on the previous route
     *
     * Use this with [ConsumeReturn]
     */
    fun addReturn(key: String, value: Parcelable) {
        navController.previousBackStackEntry?.savedStateHandle?.set(key, value)
    }

    /**
     * Stores the parcelable on the current route
     *
     * Use this with [getPrevReturn]
     */
    fun storeReturn(key: String, value: Parcelable) {
        navController.currentBackStackEntry?.savedStateHandle?.set(key, value)
    }

    fun toPostWithPopUpTo(postId: Int) {
        navController.navigate(
            Route.PostArgs.makeRoute(id = "$postId"),
        ) {
            popUpTo(Route.CREATE_POST) { inclusive = true }
        }
    }

    fun toCreatePrivateMessage(id: Int, name: String) {
        navController.navigate(Route.CreatePrivateMessageArgs.makeRoute(personId = "$id", personName = name))
    }

    fun hideLinkPopup() {
        linkDropdownExpanded.value = null
    }

    fun showLinkPopup(url: String) {
        linkDropdownExpanded.value = url
    }

    @Composable
    fun<D> rootChannel(): RouteChannel<D> {
        // This will create a ViewModel<D> on the fly and will be stored on the nav stack entry
        //  with route = Route.Graph.ROOT.
        val root = remember(this.navController.currentBackStackEntry) { this.navController.getBackStackEntry(Route.Graph.ROOT) }
        return viewModel(root)
    }
}

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

typealias CreatePostDeps = Community?

typealias CommentReplyDeps = ReplyItem

typealias CommentEditDeps = CommentView

typealias PostEditDeps = PostView

typealias PrivateMessageDeps = PrivateMessageView

@Parcelize
data class NullableWrapper<T : Parcelable?>(val data: T) : Parcelable

@Composable
inline fun <reified D : Parcelable> JerboaAppState.takeDepsFromRoot(): State<D> {
    val deps = rootChannel<D>().take()

    // This will survive process death
    val depsSaved = rememberSaveable { deps!! }

    // After process death, deps will be null
    return remember(depsSaved) {
        derivedStateOf {
            deps ?: depsSaved
        }
    }
}

@Composable
inline fun <reified D : Parcelable?> JerboaAppState.takeNullableDepsFromRoot(): State<D?> {
    val deps = rootChannel<D>().take()

    // This will survive process death
    val depsSaved = rememberSaveable { NullableWrapper(deps) }

    // After process death, deps will be null
    return remember(depsSaved) {
        derivedStateOf {
            deps ?: depsSaved.data
        }
    }
}

/**
 * Gets the parcelable from the current route, and consume it (removes it)
 * So that the action will not be repeated
 */

@Composable
inline fun<reified T : Parcelable> JerboaAppState.ConsumeReturn(
    key: String,
    crossinline consumeBlock: (T) -> Unit,
) {
    LaunchedEffect(key) {
        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        if (savedStateHandle?.contains(key) == true) {
            savedStateHandle.get<T>(key)?.also(consumeBlock)
            savedStateHandle.remove<String>(key)
        }
    }
}

/**
 * Gets the parcelable from the previous route, but does not consume it
 * This is important as, we could navigate further up the tree and back again
 * but when you consume on second return it will be gone
 */
@Composable
inline fun<reified T : Parcelable> JerboaAppState.getPrevReturn(
    key: String,
    crossinline consumeBlock: (T) -> Unit,
) {
    LaunchedEffect(key) {
        val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle
        if (savedStateHandle?.contains(key) == true) {
            savedStateHandle.get<T>(key)?.also(consumeBlock)
        }
    }
}
