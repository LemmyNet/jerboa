package com.jerboa

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.model.ReplyItem
import com.jerboa.state.VideoAppState
import com.jerboa.ui.components.ban.BanFromCommunityReturn
import com.jerboa.ui.components.ban.BanPersonReturn
import com.jerboa.ui.components.comment.edit.CommentEditReturn
import com.jerboa.ui.components.comment.reply.CommentReplyReturn
import com.jerboa.ui.components.common.Route
import com.jerboa.ui.components.community.sidebar.CommunityViewSidebar
import com.jerboa.ui.components.post.create.CreatePostReturn
import com.jerboa.ui.components.post.edit.PostEditReturn
import com.jerboa.ui.components.privatemessage.PrivateMessage
import com.jerboa.ui.components.remove.comment.CommentRemoveReturn
import com.jerboa.ui.components.remove.post.PostRemoveReturn
import it.vercruysse.lemmyapi.datatypes.Comment
import it.vercruysse.lemmyapi.datatypes.CommentId
import it.vercruysse.lemmyapi.datatypes.CommentView
import it.vercruysse.lemmyapi.datatypes.Community
import it.vercruysse.lemmyapi.datatypes.CommunityId
import it.vercruysse.lemmyapi.datatypes.GetCommunityResponse
import it.vercruysse.lemmyapi.datatypes.Person
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.Post
import it.vercruysse.lemmyapi.datatypes.PostId
import it.vercruysse.lemmyapi.datatypes.PostView
import it.vercruysse.lemmyapi.datatypes.PrivateMessageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun rememberJerboaAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): JerboaAppState =
    remember(
        navController,
        coroutineScope,
    ) {
        JerboaAppState(
            navController,
            coroutineScope,
        )
    }

@Stable
class JerboaAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
) {
    val linkDropdownExpanded = mutableStateOf<String?>(null)
    val videoAppState: VideoAppState = VideoAppState()

    fun release() {
        videoAppState.releaseExoPlayer()
    }

    fun toPrivateMessageReply(privateMessageView: PrivateMessageView) {
        sendReturnForwards(PrivateMessage.PM_VIEW, privateMessageView)
        navController.navigate(Route.PRIVATE_MESSAGE_REPLY)
    }

    fun toCommentReport(id: CommentId) {
        navController.navigate(Route.CommentReportArgs.makeRoute(id = "$id"))
    }

    fun toPostReport(id: PostId) {
        navController.navigate(Route.PostReportArgs.makeRoute(id = "$id"))
    }

    fun toPostRemove(post: Post) {
        sendReturnForwards(PostRemoveReturn.POST_SEND, post)
        navController.navigate(Route.POST_REMOVE)
    }

    fun toCommentRemove(comment: Comment) {
        sendReturnForwards(CommentRemoveReturn.COMMENT_SEND, comment)
        navController.navigate(Route.COMMENT_REMOVE)
    }

    fun toBanPerson(person: Person) {
        sendReturnForwards(BanPersonReturn.PERSON_SEND, person)
        navController.navigate(Route.BAN_PERSON)
    }

    fun toBanFromCommunity(banData: BanFromCommunityData) {
        sendReturnForwards(BanFromCommunityReturn.BAN_DATA_SEND, banData)
        navController.navigate(Route.BAN_FROM_COMMUNITY)
    }

    fun toSettings() = navController.navigate(Route.SETTINGS)

    fun toAccountSettings() = navController.navigate(Route.ACCOUNT_SETTINGS)

    fun toLookAndFeel() = navController.navigate(Route.LOOK_AND_FEEL)

    fun toBlockView() = navController.navigate(Route.BLOCK_VIEW)

    fun toAbout() = navController.navigate(Route.ABOUT)

    fun toCrashLogs() = navController.navigate(Route.CRASH_LOGS)

    fun toBackupAndRestore() = navController.navigate(Route.BACKUP_AND_RESTORE)

    fun openImageViewer(url: String) {
        val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.name())
        navController.navigate(Route.ImageViewArgs.makeRoute(encodedUrl))
    }

    fun openVideoViewer(url: String) {
        val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.name())
        navController.navigate(Route.VideoViewArgs.makeRoute(encodedUrl))
    }

    fun openMediaViewer(
        url: String,
        mediaType: PostLinkType? = null,
    ) {
        val fullType = mediaType ?: PostLinkType.fromURL(url)
        if (fullType == PostLinkType.Video) {
            openVideoViewer(url)
        } else {
            openImageViewer(url)
        }
    }

    fun toPostEdit(postView: PostView) {
        sendReturnForwards(PostEditReturn.POST_SEND, postView)
        navController.navigate(Route.POST_EDIT)
    }

    fun toCommentEdit(commentView: CommentView) {
        sendReturnForwards(CommentEditReturn.COMMENT_SEND, commentView)
        navController.navigate(Route.COMMENT_EDIT)
    }

    fun toSiteSideBar() = navController.navigate(Route.SITE_SIDEBAR)

    fun toSiteLegal() = navController.navigate(Route.SITE_LEGAL)

    fun toCommentReply(replyItem: ReplyItem) {
        sendReturnForwards(CommentReplyReturn.COMMENT_SEND, replyItem)
        navController.navigate(Route.COMMENT_REPLY)
    }

    fun toComment(id: CommentId) {
        navController.navigate(Route.CommentArgs.makeRoute(id = "$id"))
    }

    fun toCreatePost(community: Community?) {
        if (community != null) {
            sendReturnForwards(CreatePostReturn.COMMUNITY_SEND, community)
        }
        navController.navigate(Route.CREATE_POST)
    }

    fun toPost(id: PostId) {
        navController.navigate(Route.PostArgs.makeRoute(id = "$id"))
    }

    fun toLogin() = navController.navigate(Route.LOGIN)

    fun toHome() = navController.navigate(Route.HOME) { popUpTo(navController.graph.id) }

    fun toCommunity(id: CommunityId) {
        navController.navigate(Route.CommunityFromIdArgs.makeRoute(id = "$id"))
    }

    fun toCommunitySideBar(communityRes: GetCommunityResponse) {
        sendReturnForwards(CommunityViewSidebar.COMMUNITY_RES, communityRes)
        navController.navigate(Route.COMMUNITY_SIDEBAR)
    }

    fun toProfile(
        id: PersonId,
        saved: Boolean = false,
    ) {
        navController.navigate(Route.ProfileFromIdArgs.makeRoute(id = "$id", saved = "$saved"))
    }

    fun toPostLikes(postId: PostId) {
        navController.navigate(Route.PostLikesArgs.makeRoute(id = "$postId"))
    }

    fun toCommentLikes(commentId: CommentId) {
        navController.navigate(Route.CommentLikesArgs.makeRoute(id = "$commentId"))
    }

    fun toCommunityList(select: Boolean = Route.CommunityListArgs.SELECT_DEFAULT) {
        navController.navigate(Route.CommunityListArgs.makeRoute(select = "$select"))
    }

    fun popBackStack(): Boolean = navController.popBackStack()

    fun navigateUp(): Boolean = navController.navigateUp()

    fun openLinkRaw(
        url: String,
        useCustomTabs: Boolean,
        usePrivateTabs: Boolean,
    ) {
        openLinkRaw(url, navController, useCustomTabs, usePrivateTabs)
    }

    fun openLink(
        url: String,
        useCustomTabs: Boolean,
        usePrivateTabs: Boolean,
    ) {
        // Navigation must be done on the main thread
        coroutineScope.launch(Dispatchers.Main) {
            openLink(url, navController, useCustomTabs, usePrivateTabs)
        }
    }

    /**
     * Stores the parcelable on the previous route
     *
     * Use this with [ConsumeReturn]
     *
     * When you want to pass a [Parcelable] to the previous screen/activity you came from
     */
    inline fun <reified T> addReturn(
        key: String,
        value: T,
    ) {
        navController.previousBackStackEntry?.savedStateHandle?.set(key, Json.encodeToString(value))
    }

    /**
     * Stores the parcelable on the current route
     *
     * Use this with [getPrevReturn] [usePrevReturn] [getPrevReturnNullable]
     *
     * When you want to pass a [Parcelable] to another screen/activity
     */
    inline fun <reified T> sendReturnForwards(
        key: String,
        value: T,
    ) {
        navController.currentBackStackEntry?.savedStateHandle?.set(key, Json.encodeToString(value))
    }

    fun toPostWithPopUpTo(postId: PostId) {
        navController.navigate(
            Route.PostArgs.makeRoute(id = "$postId"),
        ) {
            popUpTo(Route.CREATE_POST) { inclusive = true }
        }
    }

    fun toCreatePrivateMessage(
        id: PersonId,
        name: String,
    ) {
        navController.navigate(Route.CreatePrivateMessageArgs.makeRoute(personId = "$id", personName = name))
    }

    fun hideLinkPopup() {
        linkDropdownExpanded.value = null
    }

    fun showLinkPopup(url: String) {
        linkDropdownExpanded.value = url
    }

    /**
     * Gets the parcelable from the current route, and consume it (removes it)
     * So that the action will not be repeated
     */

    @Composable
    inline fun <reified T> ConsumeReturn(
        key: String,
        crossinline consumeBlock: (T) -> Unit,
    ) {
        LaunchedEffect(key) {
            val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
            if (savedStateHandle?.contains(key) == true) {
                savedStateHandle.get<String>(key)?.let {
                    consumeBlock(Json.decodeFromString<T>(it))
                }
                savedStateHandle.remove<String>(key)
            }
        }
    }

    /**
     * Gets the parcelable from the previous route, but does not consume it
     * This is important as, we could navigate further up the tree and return again
     * which wouldn't work
     *
     * This function makes a few assumptions, and will throw a error else
     * - There is a backstack entry, e.g. you are not calling this from the root
     * - It actually contains the key
     */
    @Composable
    inline fun <reified D : Any> getPrevReturn(key: String): D {
        // This will survive process death
        return rememberSaveable(saver = getKotlinxSerializerSaver<D>()) {
            Json.decodeFromString<D>(
                navController.previousBackStackEntry!!.savedStateHandle.get<String>(key)
                    ?: throw IllegalStateException("This route doesn't contain this key `$key`"),
            )
        }
    }

    /**
     * Gets the parcelable from the previous route, but does not consume it
     * This is important as, we could navigate further up the tree and back again
     * but when you consume on second return it will be gone
     */
    inline fun <reified T> getPrevReturnNullable(key: String): T? {
        val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle

        if (savedStateHandle?.contains(key) == true) {
            return savedStateHandle.get<String>(key)?.let { Json.decodeFromString<T>(it) }
        }
        return null
    }

    /**
     * Gets the parcelable from the previous route, but does not consume it
     * This is important as, we could navigate further up the tree and back again
     * but when you consume on second return it will be gone
     *
     */
    @Composable
    inline fun <reified T> usePrevReturn(
        key: String,
        crossinline consumeBlock: (T) -> Unit,
    ) {
        LaunchedEffect(key) {
            val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle
            if (savedStateHandle?.contains(key) == true) {
                savedStateHandle
                    .get<String>(key)
                    ?.let { Json.decodeFromString<T>(it) }
                    ?.also(consumeBlock)
            }
        }
    }
}

inline fun <reified T> getKotlinxSerializerSaver(): Saver<T, String> =
    Saver(
        save = { Json.encodeToString(it) },
        restore = { Json.decodeFromString<T>(it) },
    )

inline fun <reified T> getKotlinxSerializerSaver3(): Saver<T?, String> =
    Saver(
        save = { Json.encodeToString(it) },
        restore = { Json.decodeFromString<T>(it) },
    )
