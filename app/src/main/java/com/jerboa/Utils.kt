package com.jerboa

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TabPosition
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.Autofill
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillTree
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.core.os.LocaleListCompat
import androidx.core.util.PatternsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavController
import arrow.core.compareTo
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jerboa.api.API
import com.jerboa.api.API.Companion.checkIfLemmyInstance
import com.jerboa.api.ApiState
import com.jerboa.datatypes.types.*
import com.jerboa.db.APP_SETTINGS_DEFAULT
import com.jerboa.db.entity.AppSettings
import com.jerboa.ui.components.common.Route
import com.jerboa.ui.components.inbox.InboxTab
import com.jerboa.ui.components.person.UserTab
import com.jerboa.ui.theme.SMALL_PADDING
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import org.ocpsoft.prettytime.PrettyTime
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.pow

val gson = Gson()

const val DEBOUNCE_DELAY = 1000L
const val MAX_POST_TITLE_LENGTH = 200

// convert a data class to a map
fun <T> T.serializeToMap(): Map<String, String> {
    return convert()
}

// convert an object of type I to type O
inline fun <I, reified O> I.convert(): O {
    val json = gson.toJson(this)
    return gson.fromJson(
        json,
        object : TypeToken<O>() {}.type,
    )
}

// / This should be done in a UI wrapper
fun toastException(ctx: Context, error: Exception) {
    Log.e("jerboa", "error", error)
    Toast.makeText(ctx, error.message, Toast.LENGTH_SHORT).show()
}

fun loginFirstToast(ctx: Context) {
    Toast.makeText(ctx, ctx.getString(R.string.utils_login_first), Toast.LENGTH_SHORT).show()
}

enum class VoteType {
    Upvote,
    Downvote,
}

fun calculateNewInstantScores(instantScores: InstantScores, voteType: VoteType): InstantScores {
    val newVote = newVote(
        currentVote = instantScores.myVote,
        voteType = voteType,
    )
    val score = newScore(
        instantScores.score,
        instantScores.myVote,
        voteType,
    )
    val votes = newVoteCount(
        Pair(instantScores.upvotes, instantScores.downvotes),
        instantScores.myVote,
        voteType,
    )

    return InstantScores(
        myVote = newVote,
        upvotes = votes.first,
        downvotes = votes.second,
        score = score,
    )
}

/*
 * User changed their vote, so calculate score difference given this user's new vote.
 */
fun newVote(currentVote: Int?, voteType: VoteType): Int {
    return if (voteType == VoteType.Upvote) {
        if (currentVote == 1) {
            0
        } else {
            1
        }
    } else {
        if (currentVote == -1) {
            0
        } else {
            -1
        }
    }
}

/*
 * Calculate the new score after the user votes.
 */
fun newScore(currentScore: Int, currentVote: Int?, voteType: VoteType): Int {
    return if (voteType == VoteType.Upvote) {
        when (currentVote) {
            1 -> {
                currentScore - 1
            }

            -1 -> {
                currentScore + 2
            }

            else -> {
                currentScore + 1
            }
        }
    } else {
        when (currentVote) {
            -1 -> {
                currentScore + 1
            }

            1 -> {
                currentScore - 2
            }

            else -> {
                currentScore - 1
            }
        }
    }
}

fun newVoteCount(votes: Pair<Int, Int>, currentVote: Int?, voteType: VoteType): Pair<Int, Int> {
    return if (voteType == VoteType.Upvote) {
        when (currentVote) {
            1 -> {
                Pair(votes.first - 1, votes.second)
            }

            -1 -> {
                Pair(votes.first + 1, votes.second - 1)
            }

            else -> {
                Pair(votes.first + 1, votes.second)
            }
        }
    } else {
        when (currentVote) {
            -1 -> {
                Pair(votes.first, votes.second - 1)
            }

            1 -> {
                Pair(votes.first - 1, votes.second + 1)
            }

            else -> {
                Pair(votes.first, votes.second + 1)
            }
        }
    }
}

/**
 * This stores live info about votes / scores, in order to update the front end without waiting
 * for an API result
 */
data class InstantScores(
    val myVote: Int?,
    val score: Int,
    val upvotes: Int,
    val downvotes: Int,
)

data class CommentNodeData(
    val commentView: CommentView,
    // Must use a SnapshotStateList and not a MutableList here, otherwise changes in the tree children won't trigger a UI update
    val children: SnapshotStateList<CommentNodeData>?,
    var depth: Int,
)

fun commentsToFlatNodes(
    comments: List<CommentView>,
): ImmutableList<CommentNodeData> {
    return comments.map { c -> CommentNodeData(commentView = c, children = null, depth = 0) }.toImmutableList()
}

fun buildCommentsTree(
    comments: List<CommentView>,
    isCommentView: Boolean,
): ImmutableList<CommentNodeData> {
    val map = LinkedHashMap<Number, CommentNodeData>()
    val firstComment = comments.firstOrNull()?.comment

    val depthOffset = if (!isCommentView) {
        0
    } else {
        getDepthFromComment(firstComment) ?: 0
    }

    comments.forEach { cv ->
        val depth = getDepthFromComment(cv.comment)?.minus(depthOffset) ?: 0
        val node = CommentNodeData(
            commentView = cv,
            children = mutableStateListOf(),
            depth,
        )
        map[cv.comment.id] = node
    }

    val tree = mutableListOf<CommentNodeData>()

    comments.forEach { cv ->
        val child = map[cv.comment.id]
        child?.let { cChild ->
            val parentId = getCommentParentId(cv.comment)
            parentId?.let { cParentId ->
                val parent = map[cParentId]

                // Necessary because blocked comment might not exist
                parent?.let { cParent ->
                    cParent.children?.add(cChild)
                }
            } ?: run {
                tree.add(cChild)
            }
        }
    }

    return tree.toImmutableList()
}

fun LazyListState.isScrolledToEnd(): Boolean {
    val totalItems = layoutInfo.totalItemsCount
    val lastItemVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index

    return if (totalItems > 0) {
        lastItemVisible == totalItems - 1
    } else {
        false
    }
}

/**
 * Parses a "url" and returns a spec-compliant URL:
 *
 * - https://host/path - leave as-is
 * - http://host/path - leave as-is
 * - /c/community -> https://currentInstance/c/community
 * - /c/community@instance -> https://instance/c/community
 * - !community@instance -> https://instance/c/community
 * - @user@instance -> https://instance/u/user
 *
 * @return A pair of a boolean and a string where the
 * string represents the spec-compliant URL. The boolean
 * represents true if the given string as argument was
 * formatted in a lemmy specific format. Such as "/c/community"
 */
fun parseUrl(url: String): Pair<Boolean, String>? {
    if (url.startsWith("https://") || url.startsWith("http://")) {
        return Pair(false, url)
    } else if (url.startsWith("/c/")) {
        if (url.count { c -> c == '@' } == 1) {
            val (community, host) = url.split("@", limit = 2)
            return Pair(true, "https://$host$community")
        }
        return Pair(true, "https://${API.currentInstance}$url")
    } else if (url.startsWith("/u/")) {
        if (url.count { c -> c == '@' } == 1) {
            val (userPath, host) = url.split("@", limit = 2)
            return Pair(true, "https://$host$userPath")
        }
        return Pair(true, "https://${API.currentInstance}$url")
    } else if (url.startsWith("!")) {
        if (url.count { c -> c == '@' } == 1) {
            val (community, host) = url.substring(1).split("@", limit = 2)
            return Pair(true, "https://$host/c/$community")
        }
        return Pair(true, "https://${API.currentInstance}/c/${url.substring(1)}")
    } else if (url.startsWith("@")) {
        if (url.count { c -> c == '@' } == 2) {
            val (user, host) = url.substring(1).split("@", limit = 2)
            return Pair(true, "https://$host/u/$user")
        }
        return Pair(true, "https://${API.currentInstance}/u/${url.substring(1)}")
    }
    return null
}

fun looksLikeCommunityUrl(url: String): Pair<String, String>? {
    val pattern = Regex("^https?://([^/]+)/c/([^/&?]+)")
    val match = pattern.find(url)
    if (match != null) {
        val (host, community) = match.destructured
        return Pair(host, community)
    }
    return null
}

fun looksLikeUserUrl(url: String): Pair<String, String>? {
    val pattern = Regex("^https?://([^/]+)/u/([^/&?]+)")
    val match = pattern.find(url)
    if (match != null) {
        val (host, user) = match.destructured
        return Pair(host, user)
    }
    return null
}

/**
 * Open a sharesheet for the given URL.
 */
fun shareLink(url: String, ctx: Context) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, url)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(intent, null)
    ctx.startActivity(shareIntent)
}

// Current logic is that if the url matches a community url or user url then it confirms
// if the host is an actual lemmy instance unless it was originally formatted in a user/community format

suspend fun openLink(url: String, navController: NavController, useCustomTab: Boolean, usePrivateTab: Boolean) {
    val (formatted, parsedUrl) = parseUrl(url) ?: return

    val userUrl = looksLikeUserUrl(parsedUrl)
    val communityUrl = looksLikeCommunityUrl(parsedUrl)

    if (userUrl != null && (formatted || checkIfLemmyInstance(url))) {
        val route = Route.ProfileFromUrlArgs.makeRoute(instance = userUrl.first, name = userUrl.second)
        navController.navigate(route)
    } else if (communityUrl != null && (formatted || checkIfLemmyInstance(url))) {
        val route = Route.CommunityFromUrlArgs.makeRoute(instance = communityUrl.first, name = communityUrl.second)
        navController.navigate(route)
    } else {
        openLinkRaw(url, navController, useCustomTab, usePrivateTab)
    }
}

fun openLinkRaw(url: String, navController: NavController, useCustomTab: Boolean, usePrivateTab: Boolean) {
    if (useCustomTab) {
        val intent = CustomTabsIntent.Builder()
            .build().apply {
                if (usePrivateTab) {
                    intent.putExtra("com.google.android.apps.chrome.EXTRA_OPEN_NEW_INCOGNITO_TAB", true)
                }
            }
        intent.launchUrl(navController.context, Uri.parse(url))
    } else {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        navController.context.startActivity(intent)
    }
}

var prettyTime = PrettyTime(Locale.getDefault())
var prettyTimeEnglish = PrettyTime(Locale.ENGLISH)
val invalidPrettyDateRegex = "^[0123456789 ]+$".toRegex()
fun formatDuration(date: Date, longTimeFormat: Boolean = false): String {
    if (prettyTime.locale != Locale.getDefault()) {
        prettyTime = PrettyTime(Locale.getDefault())
    }

    var prettyDate = prettyTime.formatDuration(date)

    // A bug in PrettyTime means that some languages (pl, ru, uk, kk) will not include any time unit
    if (prettyDate.matches(invalidPrettyDateRegex)) {
        prettyDate = prettyTimeEnglish.formatDuration(date)
    }

    return if (longTimeFormat) {
        prettyDate
    } else {
        prettyTimeShortener(prettyDate)
    }
}

fun prettyTimeShortener(timeString: String): String {
    return if (prettyTime.locale.language == "en") {
        if (timeString.isEmpty()) {
            "Now"
        } else {
            timeString
                .replace(Regex("minutes?"), "m")
                .replace(Regex("hours?"), "h")
                .replace(Regex("days?"), "d")
                .replace(Regex("weeks?"), "w")
                .replace(Regex("months?"), "M")
                .replace(Regex("years?"), "Y")
                .filter { !it.isWhitespace() }
        }
    } else {
        timeString
    }
}

fun pictrsImageThumbnail(src: String, thumbnailSize: Int): String {
    // sample url:
    // http://localhost:8535/pictrs/image/file.png?thumbnail=256&format=jpg

    val split = src.split("/pictrs/image/")

    // If theres not multiple, then its not a pictrs image
    if (split.size == 1) {
        return src
    }

    val host = split[0]
    var path = split[1]
    // eliminate the query param portion of the path so we can replace it later
    // without this, we'd end up with something like host/path?thumbnail=...?thumbnail=...
    if ("?" in path) {
        path = path.replaceAfter('?', "").dropLast(1)
    }

    return "$host/pictrs/image/$path?thumbnail=$thumbnailSize&format=webp"
}

fun isImage(url: String): Boolean {
    return imageRegex.matches(url)
}

fun getPostType(url: String): PostType {
    return if (isImage(url)) PostType.Image else PostType.Link
}

val imageRegex = Regex(
    pattern = "(http)?s?:?(//[^\"']*\\.(?:jpg|jpeg|gif|png|svg|webp))",
)

fun closeDrawer(
    scope: CoroutineScope,
    drawerState: DrawerState,
) {
    scope.launch {
        drawerState.close()
    }
}

fun personNameShown(person: Person, federatedName: Boolean = false): String {
    return if (!federatedName) {
        person.display_name ?: person.name
    } else {
        val name = person.display_name ?: person.name
        if (person.local) {
            name
        } else {
            "$name@${hostName(person.actor_id)}"
        }
    }
}

fun communityNameShown(community: Community): String {
    return if (community.local) {
        community.title
    } else {
        "${community.title}@${hostName(community.actor_id)}"
    }
}

fun hostName(url: String): String? {
    return try {
        URL(url).host
    } catch (e: MalformedURLException) {
        null
    }
}

enum class UnreadOrAll {
    All,
    Unread,
}

fun unreadOrAllFromBool(b: Boolean): UnreadOrAll {
    return if (b) {
        UnreadOrAll.Unread
    } else {
        UnreadOrAll.All
    }
}

fun appendMarkdownImage(text: String, url: String): String {
    return "$text\n\n![]($url)"
}

/**
 * Border definition can be extended to provide border style or [androidx.compose.ui.graphics.Brush]
 * One more way is make it sealed class and provide different implementations:
 * SolidBorder, DashedBorder etc
 */
data class Border(val strokeWidth: Dp, val color: Color)

@Stable
fun Modifier.border(
    start: Border? = null,
    top: Border? = null,
    end: Border? = null,
    bottom: Border? = null,
) =
    drawBehind {
        start?.let {
            drawStartBorder(it, shareTop = top != null, shareBottom = bottom != null)
        }
        top?.let {
            drawTopBorder(it, shareStart = start != null, shareEnd = end != null)
        }
        end?.let {
            drawEndBorder(it, shareTop = top != null, shareBottom = bottom != null)
        }
        bottom?.let {
            drawBottomBorder(border = it, shareStart = start != null, shareEnd = end != null)
        }
    }

private fun DrawScope.drawTopBorder(
    border: Border,
    shareStart: Boolean = true,
    shareEnd: Boolean = true,
) {
    val strokeWidthPx = border.strokeWidth.toPx()
    if (strokeWidthPx == 0f) return
    drawPath(
        Path().apply {
            moveTo(0f, 0f)
            lineTo(if (shareStart) strokeWidthPx else 0f, strokeWidthPx)
            val width = size.width
            lineTo(if (shareEnd) width - strokeWidthPx else width, strokeWidthPx)
            lineTo(width, 0f)
            close()
        },
        color = border.color,
    )
}

private fun DrawScope.drawBottomBorder(
    border: Border,
    shareStart: Boolean,
    shareEnd: Boolean,
) {
    val strokeWidthPx = border.strokeWidth.toPx()
    if (strokeWidthPx == 0f) return
    drawPath(
        Path().apply {
            val width = size.width
            val height = size.height
            moveTo(0f, height)
            lineTo(if (shareStart) strokeWidthPx else 0f, height - strokeWidthPx)
            lineTo(if (shareEnd) width - strokeWidthPx else width, height - strokeWidthPx)
            lineTo(width, height)
            close()
        },
        color = border.color,
    )
}

private fun DrawScope.drawStartBorder(
    border: Border,
    shareTop: Boolean = true,
    shareBottom: Boolean = true,
) {
    val strokeWidthPx = border.strokeWidth.toPx()
    if (strokeWidthPx == 0f) return
    drawPath(
        Path().apply {
            moveTo(0f, 0f)
            lineTo(strokeWidthPx, if (shareTop) strokeWidthPx else 0f)
            val height = size.height
            lineTo(strokeWidthPx, if (shareBottom) height - strokeWidthPx else height)
            lineTo(0f, height)
            close()
        },
        color = border.color,
    )
}

private fun DrawScope.drawEndBorder(
    border: Border,
    shareTop: Boolean = true,
    shareBottom: Boolean = true,
) {
    val strokeWidthPx = border.strokeWidth.toPx()
    if (strokeWidthPx == 0f) return
    drawPath(
        Path().apply {
            val width = size.width
            val height = size.height
            moveTo(width, 0f)
            lineTo(width - strokeWidthPx, if (shareTop) strokeWidthPx else 0f)
            lineTo(width - strokeWidthPx, if (shareBottom) height - strokeWidthPx else height)
            lineTo(width, height)
            close()
        },
        color = border.color,
    )
}

fun isPostCreator(commentView: CommentView): Boolean {
    return commentView.creator.id == commentView.post.creator_id
}

fun isModerator(person: Person, moderators: List<CommunityModeratorView>): Boolean {
    return moderators.map { it.moderator.id }.contains(person.id)
}

data class InputField(
    val label: String,
    val hasError: Boolean,
)

fun validatePostName(
    ctx: Context,
    name: String,
): InputField {
    return if (name.isEmpty()) {
        InputField(
            label = ctx.getString(R.string.title_required),
            hasError = true,
        )
    } else if (name.length < 3) {
        InputField(
            label = ctx.getString(R.string.title_min_3_chars),
            hasError = true,
        )
    } else if (name.length >= MAX_POST_TITLE_LENGTH) {
        InputField(
            label = ctx.getString(R.string.title_less_than_200_chars),
            hasError = true,
        )
    } else {
        InputField(
            label = ctx.getString(R.string.title),
            hasError = false,
        )
    }
}

fun validateUrl(
    ctx: Context,
    url: String,
): InputField {
    return if (url.isNotEmpty() && !PatternsCompat.WEB_URL.matcher(url).matches()) {
        InputField(
            label = ctx.getString(R.string.url_invalid),
            hasError = true,
        )
    } else {
        InputField(
            label = ctx.getString(R.string.url),
            hasError = false,
        )
    }
}

fun siFormat(num: Int): String {
    // Weird bug where if num is zero, it won't format
    if (num == 0) return "0"
    var value = num.toDouble()
    val suffix = " KMBT"
    val formatter = DecimalFormat("#,###.#")
    val power = StrictMath.log10(value).toInt()
    value /= 10.0.pow((power / 3 * 3).toDouble())
    var formattedNumber = formatter.format(value)
    formattedNumber += suffix[power / 3]
    return if (formattedNumber.length > 4) {
        formattedNumber.replace(
            "\\.[0-9]+".toRegex(),
            "",
        )
    } else {
        formattedNumber
    }
}
fun imageInputStreamFromUri(ctx: Context, uri: Uri): InputStream {
    return ctx.contentResolver.openInputStream(uri)!!
}

fun decodeUriToBitmap(ctx: Context, uri: Uri): Bitmap? {
    Log.d("jerboa", "decodeUriToBitmap INPUT: $uri")
    return if (SDK_INT < 28) {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(ctx.contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(ctx.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}

fun scrollToTop(
    scope: CoroutineScope,
    listState: LazyListState,
) {
    scope.launch {
        listState.animateScrollToItem(index = 0)
    }
}

fun showSnackbar(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    message: String,
    actionLabel: String?,
    withDismissAction: Boolean = false,
    snackbarDuration: SnackbarDuration,
) {
    scope.launch {
        snackbarHostState.showSnackbar(
            message,
            actionLabel,
            withDismissAction,
            snackbarDuration,
        )
    }
}

// https://stackoverflow.com/questions/69234880/how-to-get-intent-data-in-a-composable
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

enum class ThemeMode(val mode: Int) {
    System(R.string.look_and_feel_theme_system),
    SystemBlack(R.string.look_and_feel_theme_system_black),
    Light(R.string.look_and_feel_theme_light),
    Dark(R.string.look_and_feel_theme_dark),
    Black(R.string.look_and_feel_theme_black),
}

enum class ThemeColor(val mode: Int) {
    Dynamic(R.string.look_and_feel_theme_color_dynamic),
    Beach(R.string.look_and_feel_theme_color_beach),
    Blue(R.string.look_and_feel_theme_color_blue),
    Crimson(R.string.look_and_feel_theme_color_crimson),
    Green(R.string.look_and_feel_theme_color_green),
    Grey(R.string.look_and_feel_theme_color_grey),
    Pink(R.string.look_and_feel_theme_color_pink),
    Purple(R.string.look_and_feel_theme_color_purple),
    Woodland(R.string.look_and_feel_theme_color_woodland),
}

enum class PostViewMode(val mode: Int) {
    /**
     * The full size post view card. For image posts, this expands them to their full height. For
     * link posts, the thumbnail is shown to the right of the title.
     */
    Card(R.string.look_and_feel_post_view_card),

    /**
     * The same as regular card, except image posts only show a thumbnail image.
     */
    SmallCard(R.string.look_and_feel_post_view_small_card),

    /**
     * A list view that has no action bar.
     */
    List(R.string.look_and_feel_post_view_list),
}

/**
 * For a given post, what sort of content Jerboa treats it as.
 */
enum class PostType {
    /**
     * A Link to an external website. Opens the browser.
     */
    Link,

    /**
     * An Image. Opens the built-in image viewer.
     */
    Image,

    /**
     * A Video. Should open the built-in video viewer.
     * (Not currently available).
     */
    Video,
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.pagerTabIndicatorOffset2(
    pagerState: PagerState,
    tabPositions: List<TabPosition>,
    pageIndexMapping: (Int) -> Int = { it },
): Modifier = layout { measurable, constraints ->
    if (tabPositions.isEmpty()) {
        // If there are no pages, nothing to show
        layout(constraints.maxWidth, 0) {}
    } else {
        val currentPage = minOf(tabPositions.lastIndex, pageIndexMapping(pagerState.currentPage))
        val currentTab = tabPositions[currentPage]
        val previousTab = tabPositions.getOrNull(currentPage - 1)
        val nextTab = tabPositions.getOrNull(currentPage + 1)
        val fraction = pagerState.currentPageOffsetFraction
        val indicatorWidth = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.width, nextTab.width, fraction).roundToPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.width, previousTab.width, -fraction).roundToPx()
        } else {
            currentTab.width.roundToPx()
        }
        val indicatorOffset = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.left, nextTab.left, fraction).roundToPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.left, previousTab.left, -fraction).roundToPx()
        } else {
            currentTab.left.roundToPx()
        }
        val placeable = measurable.measure(
            Constraints(
                minWidth = indicatorWidth,
                maxWidth = indicatorWidth,
                minHeight = 0,
                maxHeight = constraints.maxHeight,
            ),
        )
        layout(constraints.maxWidth, maxOf(placeable.height, constraints.minHeight)) {
            placeable.placeRelative(
                indicatorOffset,
                maxOf(constraints.minHeight - placeable.height, 0),
            )
        }
    }
}

fun isSameInstance(url: String, instance: String): Boolean {
    return hostName(url) == instance
}

fun getCommentParentId(comment: Comment?): Int? {
    val split = comment?.path?.split(".")?.toMutableList()
    // remove the 0
    split?.removeFirst()
    return if (split !== null && split.size > 1) {
        split[split.size - 2].toInt()
    } else {
        null
    }
}

fun getDepthFromComment(comment: Comment?): Int? {
    return comment?.path?.split(".")?.size?.minus(2)
}

fun nsfwCheck(postView: PostView): Boolean {
    return postView.post.nsfw || postView.community.nsfw
}

@Throws(IOException::class)
fun saveBitmap(
    ctx: Context,
    inputStream: InputStream,
    mimeType: String?,
    displayName: String,
): Uri {
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Jerboa")
    }

    val resolver = ctx.contentResolver
    var uri: Uri? = null

    try {
        uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: throw IOException("Failed to create new MediaStore record.")

        resolver.openOutputStream(uri)?.use {
            inputStream.copyTo(it)
        } ?: throw IOException("Failed to open output stream.")

        return uri
    } catch (e: IOException) {
        uri?.let { orphanUri ->
            // Don't leave an orphan entry in the MediaStore
            resolver.delete(orphanUri, null, null)
        }

        throw e
    }
}

// saveBitmap that works for Android 9 and below
fun saveBitmapP(
    context: Context,
    inputStream: InputStream,
    mimeType: String?,
    displayName: String,
) {
    val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val picsDir = File(dir, "Jerboa")
    val dest = File(picsDir, displayName)

    picsDir.mkdirs() // make if not exist

    inputStream.use { input ->
        dest.outputStream().use {
            input.copyTo(it)
        }
    }
    // Makes it show up in gallery
    val mimeTypes = if (mimeType == null) null else arrayOf(mimeType)
    MediaScannerConnection.scanFile(context, arrayOf(dest.absolutePath), mimeTypes, null)
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.onAutofill(
    tree: AutofillTree,
    autofill: Autofill?,
    autofillTypes: ImmutableList<AutofillType>,
    onFill: (String) -> Unit,
): Modifier {
    val autofillNode = AutofillNode(
        autofillTypes = autofillTypes,
        onFill = onFill,
    )
    tree += autofillNode

    return this
        .onGloballyPositioned {
            autofillNode.boundingBox = it.boundsInWindow()
        }
        .onFocusChanged { focusState ->
            autofill?.run {
                if (focusState.isFocused) {
                    requestAutofillForNode(autofillNode)
                } else {
                    cancelAutofillForNode(autofillNode)
                }
            }
        }
}

/**
 * Converts a scalable pixel (sp) to an actual pixel (px)
 */
fun convertSpToPx(sp: TextUnit, ctx: Context): Int {
    return (sp.value * ctx.resources.displayMetrics.scaledDensity).toInt()
}

/**
 * Returns localized Strings for SortingType Enum
 */

fun getLocalizedSortingTypeShortName(ctx: Context, sortingType: SortType): String {
    return ctx.getString(
        MAP_SORT_TYPE_SHORT_FORM[sortingType]
            ?: throw IllegalStateException("Someone forgot to update the MAP_SORT_TYPE_SHORT_FORM"),
    )
}

// ORDER MUST BE THE SAME AS THE ENUM
val MAP_SORT_TYPE_SHORT_FORM = mapOf(
    SortType.Active to R.string.sorttype_active,
    SortType.Hot to R.string.sorttype_hot,
    SortType.New to R.string.sorttype_new,
    SortType.Old to R.string.sorttype_old,
    SortType.TopDay to R.string.sorttype_topday,
    SortType.TopWeek to R.string.sorttype_topweek,
    SortType.TopMonth to R.string.sorttype_topmonth,
    SortType.TopYear to R.string.sorttype_topyear,
    SortType.TopAll to R.string.sorttype_topall,
    SortType.MostComments to R.string.sorttype_mostcomments,
    SortType.NewComments to R.string.sorttype_newcomments,
    SortType.TopHour to R.string.sorttype_tophour,
    SortType.TopSixHour to R.string.sorttype_topsixhour,
    SortType.TopTwelveHour to R.string.sorttype_toptwelvehour,
    SortType.TopThreeMonths to R.string.sorttype_topthreemonths,
    SortType.TopSixMonths to R.string.sorttype_topsixmonths,
    SortType.TopNineMonths to R.string.sorttype_topninemonths,
)

/**
 * Returns localized Strings for SortingType Enum
 */

fun getLocalizedSortingTypeLongName(ctx: Context, sortingType: SortType): String {
    return ctx.getString(
        MAP_SORT_TYPE_LONG_FORM[sortingType]
            ?: throw IllegalStateException("Someone forgot to update the MAP_SORT_TYPE_LONG_FORM"),
    )
}

val MAP_SORT_TYPE_LONG_FORM = mapOf(
    SortType.Active to R.string.sorttype_active,
    SortType.Hot to R.string.sorttype_hot,
    SortType.New to R.string.sorttype_new,
    SortType.Old to R.string.sorttype_old,
    SortType.TopDay to R.string.dialogs_top_day,
    SortType.TopWeek to R.string.dialogs_top_week,
    SortType.TopMonth to R.string.dialogs_top_month,
    SortType.TopYear to R.string.dialogs_top_year,
    SortType.TopAll to R.string.dialogs_top_all,
    SortType.MostComments to R.string.dialogs_most_comments,
    SortType.NewComments to R.string.dialogs_new_comments,
    SortType.TopHour to R.string.dialogs_top_hour,
    SortType.TopSixHour to R.string.dialogs_top_six_hour,
    SortType.TopTwelveHour to R.string.dialogs_top_twelve_hour,
    SortType.TopThreeMonths to R.string.dialogs_top_three_month,
    SortType.TopSixMonths to R.string.dialogs_top_six_month,
    SortType.TopNineMonths to R.string.dialogs_top_nine_month,
)

/**
 * Returns localized Strings for UserTab Enum
 */
fun getLocalizedStringForUserTab(ctx: Context, tab: UserTab): String {
    val returnString = when (tab) {
        UserTab.About -> ctx.getString(R.string.person_profile_activity_about)
        UserTab.Posts -> ctx.getString(R.string.person_profile_activity_posts)
        UserTab.Comments -> ctx.getString(R.string.person_profile_activity_comments)
    }
    return returnString
}

/**
 * Returns localized Strings for ListingType Enum
 */
fun getLocalizedListingTypeName(ctx: Context, listingType: ListingType): String {
    val returnString = when (listingType) {
        ListingType.All -> ctx.getString(R.string.home_all)
        ListingType.Local -> ctx.getString(R.string.home_local)
        ListingType.Subscribed -> ctx.getString(R.string.home_subscribed)
    }
    return returnString
}

/**
 * Returns localized Strings for CommentSortType Enum
 */
fun getLocalizedCommentSortTypeName(ctx: Context, commentSortType: CommentSortType): String {
    val returnString = when (commentSortType) {
        CommentSortType.Hot -> ctx.getString(R.string.sorttype_hot)
        CommentSortType.New -> ctx.getString(R.string.sorttype_new)
        CommentSortType.Old -> ctx.getString(R.string.sorttype_old)
        CommentSortType.Top -> ctx.getString(R.string.dialogs_top)
    }
    return returnString
}

/**
 * Returns localized Strings for UnreadOrAll Enum
 */
fun getLocalizedUnreadOrAllName(ctx: Context, unreadOrAll: UnreadOrAll): String {
    val returnString = when (unreadOrAll) {
        UnreadOrAll.Unread -> ctx.getString(R.string.dialogs_unread)
        UnreadOrAll.All -> ctx.getString(R.string.dialogs_all)
    }
    return returnString
}

/**
 * Returns localized Strings for InboxTab Enum
 */
fun getLocalizedStringForInboxTab(ctx: Context, tab: InboxTab): String {
    val returnString = when (tab) {
        InboxTab.Replies -> ctx.getString(R.string.inbox_activity_replies)
        InboxTab.Mentions -> ctx.getString(R.string.inbox_activity_mentions)
        InboxTab.Messages -> ctx.getString(R.string.inbox_activity_messages)
    }
    return returnString
}

fun findAndUpdatePrivateMessage(
    messages: List<PrivateMessageView>,
    updated: PrivateMessageView,
): List<PrivateMessageView> {
    val foundIndex = messages.indexOfFirst {
        it.private_message.id == updated.private_message.id
    }
    return if (foundIndex != -1) {
        val mutable = messages.toMutableList()
        mutable[foundIndex] = updated
        mutable.toList()
    } else {
        messages
    }
}

fun showBlockPersonToast(blockPersonRes: ApiState<BlockPersonResponse>, ctx: Context) {
    when (blockPersonRes) {
        is ApiState.Success -> {
            Toast.makeText(
                ctx,
                "${blockPersonRes.data.person_view.person.name} Blocked",
                Toast.LENGTH_SHORT,
            )
                .show()
        }

        else -> {}
    }
}

fun showBlockCommunityToast(blockCommunityRes: ApiState<BlockCommunityResponse>, ctx: Context) {
    when (blockCommunityRes) {
        is ApiState.Success -> {
            Toast.makeText(
                ctx,
                "${blockCommunityRes.data.community_view.community.name} Blocked",
                Toast.LENGTH_SHORT,
            )
                .show()
        }

        else -> {}
    }
}

fun findAndUpdatePersonMention(
    mentions: List<PersonMentionView>,
    updatedCommentView: CommentView,
): List<PersonMentionView> {
    val foundIndex = mentions.indexOfFirst {
        it.person_mention.comment_id == updatedCommentView.comment.id
    }
    return if (foundIndex != -1) {
        val mutable = mentions.toMutableList()
        mutable[foundIndex] = mentions[foundIndex].copy(
            my_vote = updatedCommentView.my_vote,
            counts = updatedCommentView.counts,
            saved = updatedCommentView.saved,
            comment = updatedCommentView.comment,
        )
        mutable.toList()
    } else {
        mentions
    }
}

fun findAndUpdateMention(
    mentions: List<PersonMentionView>,
    updated: PersonMentionView,
): List<PersonMentionView> {
    val foundIndex = mentions.indexOfFirst {
        it.person_mention.id == updated.person_mention.id
    }
    return if (foundIndex != -1) {
        val mutable = mentions.toMutableList()
        mutable[foundIndex] = updated
        mutable.toList()
    } else {
        mentions
    }
}

fun findAndUpdateComment(comments: List<CommentView>, updated: CommentView): List<CommentView> {
    val foundIndex = comments.indexOfFirst {
        it.comment.id == updated.comment.id
    }
    return if (foundIndex != -1) {
        val mutable = comments.toMutableList()
        mutable[foundIndex] = updated
        mutable.toList()
    } else {
        comments
    }
}

fun findAndUpdateCommentReply(
    replies: List<CommentReplyView>,
    updatedCommentView: CommentView,
): List<CommentReplyView> {
    val foundIndex = replies.indexOfFirst {
        it.comment_reply.comment_id == updatedCommentView.comment.id
    }
    return if (foundIndex != -1) {
        val mutable = replies.toMutableList()
        mutable[foundIndex] = replies[foundIndex].copy(
            my_vote = updatedCommentView.my_vote,
            counts = updatedCommentView.counts,
            saved = updatedCommentView.saved,
            comment = updatedCommentView.comment,
        )
        mutable.toList()
    } else {
        replies
    }
}

fun calculateCommentOffset(depth: Int, multiplier: Int): Dp {
    return if (depth == 0) {
        0.dp
    } else {
        (abs((depth.minus(1) * multiplier)).dp + SMALL_PADDING)
    }
}

fun dedupePosts(
    more: List<PostView>,
    existing: List<PostView>,
): List<PostView> {
    val mapIds = existing.map { it.post.id }
    return more.filterNot { mapIds.contains(it.post.id) }
}

fun <T> appendData(existing: List<T>, more: List<T>): List<T> {
    val appended = existing.toMutableList()
    appended.addAll(more)
    return appended.toList()
}

fun mergePosts(old: List<PostView>, new: List<PostView>): List<PostView> {
    return appendData(old, dedupePosts(new, old))
}

fun findAndUpdatePost(posts: List<PostView>, updatedPostView: PostView): List<PostView> {
    val foundIndex = posts.indexOfFirst {
        it.post.id == updatedPostView.post.id
    }
    return if (foundIndex != -1) {
        val mutable = posts.toMutableList()
        mutable[foundIndex] = updatedPostView
        mutable.toList()
    } else {
        posts
    }
}

fun scrollToNextParentComment(
    scope: CoroutineScope,
    parentListStateIndexes: List<Int>,
    listState: LazyListState,
) {
    scope.launch {
        parentListStateIndexes.firstOrNull { parentIndex -> parentIndex > listState.firstVisibleItemIndex }
            ?.let { nearestNextIndex ->
                listState.animateScrollToItem(nearestNextIndex)
            }
    }
}

fun scrollToPreviousParentComment(
    scope: CoroutineScope,
    parentListStateIndexes: List<Int>,
    listState: LazyListState,
) {
    scope.launch {
        parentListStateIndexes.lastOrNull { parentIndex -> parentIndex < listState.firstVisibleItemIndex }
            ?.let { nearestPreviousIndex ->
                listState.animateScrollToItem(nearestPreviousIndex)
            }
    }
}

/**
 * Accepts a string that MAY be an URL, trims any protocol and extracts only the host, removing anything after a :, / or ?
 */
fun getHostFromInstanceString(
    input: String,
): String {
    if (input.isBlank()) {
        return input
    }

    return try {
        URL(input).host.toString()
    } catch (e: MalformedURLException) {
        input
    }
}

/**
 * Compare two version strings.
 *
 * This attempts to do a natural comparison assuming it's a typical semver (e.g. x.y.z),
 * but it ignores anything it doesn't understand. Since we're highly confident that these verisons
 * will be properly formed, this is safe enough without overcomplicating it.
 */
fun compareVersions(a: String, b: String): Int {
    val versionA: List<Int> = a.split('.').mapNotNull { it.toIntOrNull() }
    val versionB: List<Int> = b.split('.').mapNotNull { it.toIntOrNull() }

    val comparison = versionA.compareTo(versionB)
    if (comparison == 0) {
        return a.compareTo(b)
    }
    return comparison
}

/**
 * Copy a given text to the clipboard, using the Kotlin context
 *
 * @param context The app context
 * @param textToCopy Text to copy to the clipboard
 * @param clipLabel Label
 *
 * @return true if successful, false otherwise
 */
fun copyToClipboard(context: Context, textToCopy: CharSequence, clipLabel: CharSequence): Boolean {
    val activity = context.findActivity()
    activity?.let {
        val clipboard: ClipboardManager = it.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(clipLabel, textToCopy)
        clipboard.setPrimaryClip(clip)
        return true
    }
    return false
}

fun getLocaleListFromXml(ctx: Context): LocaleListCompat {
    val tagsList = mutableListOf<CharSequence>()
    try {
        val xpp: XmlPullParser = ctx.resources.getXml(R.xml.locales_config)
        while (xpp.eventType != XmlPullParser.END_DOCUMENT) {
            if (xpp.eventType == XmlPullParser.START_TAG) {
                if (xpp.name == "locale") {
                    tagsList.add(xpp.getAttributeValue(0))
                }
            }
            xpp.next()
        }
    } catch (e: XmlPullParserException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return LocaleListCompat.forLanguageTags(tagsList.joinToString(","))
}

fun getLangPreferenceDropdownEntries(ctx: Context): Map<Locale, String> {
    val localeList = getLocaleListFromXml(ctx)
    val map = mutableMapOf<Locale, String>()

    for (i in 0 until localeList.size()) {
        localeList[i]?.let {
            map.put(it, it.getDisplayName(it))
        }
    }
    return map
}

fun matchLocale(localeMap: Map<Locale, String>): Locale {
    return Locale.lookup(
        AppCompatDelegate.getApplicationLocales().convertToLanguageRange(),
        localeMap.keys.toList(),
    ) ?: Locale.ENGLISH
}

fun LocaleListCompat.convertToLanguageRange(): MutableList<Locale.LanguageRange> {
    val l = mutableListOf<Locale.LanguageRange>()

    for (i in 0 until this.size()) {
        l.add(i, Locale.LanguageRange(this[i]!!.toLanguageTag()))
    }
    return l
}
inline fun <reified E : Enum<E>> getEnumFromIntSetting(
    appSettings: LiveData<AppSettings>,
    getter: (AppSettings) -> Int,
): E {
    val enums = enumValues<E>()
    val setting = appSettings.value ?: APP_SETTINGS_DEFAULT
    val index = getter(setting)

    return if (index >= enums.size) { // Fallback to default
        enums[getter(APP_SETTINGS_DEFAULT)]
    } else {
        enums[index]
    }
}

fun triggerRebirth(context: Context) {
    val packageManager = context.packageManager
    val intent = packageManager.getLaunchIntentForPackage(context.packageName)
    val componentName = intent!!.component
    val mainIntent = Intent.makeRestartActivityTask(componentName)
    context.startActivity(mainIntent)
    Runtime.getRuntime().exit(0)
}

fun CreationExtras.jerboaApplication(): JerboaApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as JerboaApplication)

@Throws(IndexOutOfBoundsException::class)
inline fun <reified T : Enum<T>> Int.toEnum(): T {
    return enumValues<T>()[this]
}

inline fun <reified T : Enum<T>> Int.toEnumSafe(): T {
    val vals = enumValues<T>()
    return if (vals.size >= this) vals[this] else vals[0]
}

fun matchLoginErrorMsgToStringRes(ctx: Context, e: Throwable): String {
    return when (e.message) {
        "incorrect_login" -> ctx.getString(R.string.login_view_model_incorrect_login)
        "email_not_verified" -> ctx.getString(R.string.login_view_model_email_not_verified)
        "registration_denied" -> ctx.getString(R.string.login_view_model_registration_denied)
        "registration_application_pending", "registration_application_is_pending" ->
            ctx.getString(R.string.login_view_model_registration_pending)
        "missing_totp_token" -> ctx.getString(R.string.login_view_model_missing_totp)
        "incorrect_totp_token" -> ctx.getString(R.string.login_view_model_incorrect_totp)
        else -> {
            Log.d("login", "failed", e)
            ctx.getString(R.string.login_view_model_login_failed)
        }
    }
}

fun ConnectivityManager?.isCurrentlyConnected(): Boolean =
    this?.activeNetwork
        ?.let(::getNetworkCapabilities)
        ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        ?: false

/**
 * When calling this, you must call ActivityResultLauncher.unregister()
 * on the returned ActivityResultLauncher when the launcher is no longer
 * needed to release any values that might be captured in the registered callback.
 */
fun <I, O> ComponentActivity.registerActivityResultLauncher(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>,
): ActivityResultLauncher<I> {
    val key = UUID.randomUUID().toString()
    return activityResultRegistry.register(key, contract, callback)
}

/**
 *  Returns a [InputStream] for the data of the URL, but it also checks the cache first!
 */
@OptIn(ExperimentalCoilApi::class)
fun Context.getInputStream(url: String): InputStream {
    val snapshot = this.imageLoader.diskCache?.openSnapshot(url)

    return snapshot?.use {
        it.data.toFile().inputStream()
    } ?: API.httpClient.newCall(Request(url.toHttpUrl())).execute().use { response ->
        response.body.byteStream()
    }
}
