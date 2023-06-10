@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TabPosition
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jerboa.api.API
import com.jerboa.api.DEFAULT_INSTANCE
import com.jerboa.datatypes.* // ktlint-disable no-unused-imports
import com.jerboa.datatypes.api.GetUnreadCountResponse
import com.jerboa.db.Account
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.home.SiteViewModel
import com.jerboa.ui.theme.SMALL_PADDING
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.pow

val prettyTime = PrettyTime(Locale.getDefault())

val gson = Gson()

const val LAUNCH_DELAY = 300L
const val DEBOUNCE_DELAY = 1000L
const val MAX_POST_TITLE_LENGTH = 200

val DEFAULT_LEMMY_INSTANCES = listOf(
    "beehaw.org",
    "feddit.de",
    "feddit.it",
    "lemmy.ca",
    "lemmy.ml",
    "lemmy.one",
    "lemmy.world",
    "lemmygrad.ml",
    "midwest.social",
    "mujico.org",
    "sh.itjust.works",
    "slrpnk.net",
    "sopuli.xyz",
    "szmer.info",
)

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

fun toastException(ctx: Context?, error: Exception) {
    Log.e("jerboa", error.toString())
    if (ctx !== null) {
        Toast.makeText(ctx, error.message, Toast.LENGTH_SHORT).show()
    }
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
        voteType =
        voteType,
    )
    val score = newScore(
        instantScores.score,
        instantScores.myVote,
        voteType,
    )
    val votes = newVoteCount(
        Pair(instantScores.upvotes, instantScores.downvotes),
        instantScores
            .myVote,
        voteType,
    )

    return InstantScores(
        myVote = newVote,
        upvotes = votes.first,
        downvotes = votes.second,
        score = score,
    )
}

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
): List<CommentNodeData> {
    return comments.map { c -> CommentNodeData(commentView = c, children = null, depth = 0) }
}

fun buildCommentsTree(
    comments: List<CommentView>?,
    parentComment: Boolean,
): List<CommentNodeData> {
    val map = LinkedHashMap<Number, CommentNodeData>()
    val firstComment = comments?.firstOrNull()?.comment

    val depthOffset = if (!parentComment) { 0 } else {
        getDepthFromComment(firstComment) ?: 0
    }

    comments?.forEach { cv ->
        val depth = getDepthFromComment(cv.comment)?.minus(depthOffset) ?: 0
        val node = CommentNodeData(
            commentView = cv,
            children = mutableStateListOf(),
            depth,
        )
        map[cv.comment.id] = node
    }

    val tree = mutableListOf<CommentNodeData>()

    comments?.forEach { cv ->
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

    return tree
}

fun insertCommentIntoTree(
    commentTree: MutableList<CommentNodeData>,
    cv: CommentView,
    parentComment: Boolean,
) {
    val nodeData = CommentNodeData(
        commentView = cv,
        children = null,
        depth = 0,
    )
    val parentId = getCommentParentId(cv.comment)
    parentId?.also { cParentId ->
        val foundIndex = commentTree.indexOfFirst {
            it.commentView.comment.id == cParentId
        }

        if (foundIndex != -1) {
            val parent = commentTree[foundIndex]
            nodeData.depth = parent.depth.plus(1)

            parent.children?.also { children ->
                children.add(0, nodeData)
            } ?: run {
                commentTree[foundIndex] = parent.copy(children = mutableStateListOf(nodeData))
            }
        } else {
            commentTree.forEach { node ->
                node.children?.also { children ->
                    insertCommentIntoTree(children, cv, parentComment)
                }
            }
        }
    } ?: run {
        if (!parentComment) {
            commentTree.add(0, nodeData)
        }
    }
}

fun findAndUpdateCommentInTree(
    commentTree: SnapshotStateList<CommentNodeData>,
    cv: CommentView?,
) {
    cv?.also {
        val foundIndex = commentTree.indexOfFirst {
            it.commentView.comment.id == cv.comment.id
        }

        if (foundIndex != -1) {
            val updatedComment = commentTree[foundIndex].copy(commentView = cv)
            commentTree[foundIndex] = updatedComment
        } else {
            commentTree.forEach { node ->
                node.children?.also { children ->
                    findAndUpdateCommentInTree(children, cv)
                }
            }
        }
    }
}

fun calculateCommentOffset(depth: Int, multiplier: Int): Dp {
    return if (depth == 0) {
        0.dp
    } else {
        (abs((depth.minus(1) * multiplier)).dp + SMALL_PADDING)
    }
}

// fun LazyListState.isScrolledToEnd() =
//    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

fun LazyListState.isScrolledToEnd(): Boolean {
    val totalItems = layoutInfo.totalItemsCount
    val lastItemVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index

    val out = if (totalItems > 0) {
        lastItemVisible == totalItems - 1
    } else {
        false
    }
//    Log.d("jerboa", layoutInfo.visibleItemsInfo.lastOrNull()?.index.toString())
//    Log.d("jerboa", layoutInfo.totalItemsCount.toString())
//    Log.d("jerboa", out.toString())
    return out
}

fun openLink(url: String, ctx: Context, useCustomTab: Boolean) {
    if (useCustomTab) {
        val intent = CustomTabsIntent.Builder()
            .build()
        intent.launchUrl(ctx, Uri.parse(url))
    } else {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        ctx.startActivity(intent)
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
    val path = split[1]

    return "$host/pictrs/image/$path?thumbnail=$thumbnailSize&format=webp"
}

fun isImage(url: String): Boolean {
    return imageRegex.matches(url)
}

val imageRegex = Regex(
    pattern = "(http)?s?:?(//[^\"']*\\.(?:jpg|jpeg|gif|png|svg|webp))",
)

// Todo is the scope.launch still necessary?
fun closeDrawer(
    scope: CoroutineScope,
    drawerState: DrawerState,
) {
    scope.launch {
        drawerState.close()
    }
}

fun personNameShown(person: PersonSafe, federatedName: Boolean = false): String {
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

fun communityNameShown(community: CommunitySafe): String {
    return if (community.local) {
        community.title
    } else {
        "${community.title}@${hostName(community.actor_id)}"
    }
}

fun hostName(url: String): String? {
    return try {
        URL(url).host
    } catch (e: java.net.MalformedURLException) {
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

fun unreadCountTotal(unreads: GetUnreadCountResponse): Int {
    return unreads.mentions + unreads.private_messages + unreads.replies
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

fun isModerator(person: PersonSafe, moderators: List<CommunityModeratorView>): Boolean {
    return moderators.map { it.moderator.id }.contains(person.id)
}

data class InputField(
    val label: String,
    val hasError: Boolean,
)

fun validatePostName(
    name: String,
): InputField {
    return if (name.isEmpty()) {
        InputField(
            label = "Title required",
            hasError = true,
        )
    } else if (name.length < 3) {
        InputField(
            label = "Title must be > 3 chars",
            hasError = true,
        )
    } else if (name.length >= MAX_POST_TITLE_LENGTH) {
        InputField(
            label = "Title cannot be > 200 chars",
            hasError = true,
        )
    } else {
        InputField(
            label = "Title",
            hasError = false,
        )
    }
}

fun validateUrl(
    url: String,
): InputField {
    return if (url.isNotEmpty() && !Patterns.WEB_URL.matcher(url).matches()) {
        InputField(
            label = "Invalid Url",
            hasError = true,
        )
    } else {
        InputField(
            label = "Url",
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

fun fetchInitialData(
    account: Account?,
    siteViewModel: SiteViewModel,
    homeViewModel: HomeViewModel,
) {
    if (account != null) {
        API.changeLemmyInstance(account.instance)

        homeViewModel.fetchPosts(
            account = account,
            changeListingType = ListingType.values()[account.defaultListingType],
            changeSortType = SortType.values()[account.defaultSortType],
            clear = true,
        )
        homeViewModel.fetchUnreadCounts(account = account)
    } else {
        Log.d("jerboa", "Fetching posts for anonymous user")
        API.changeLemmyInstance(DEFAULT_INSTANCE)
        homeViewModel.fetchPosts(
            account = null,
            clear = true,
            changeListingType = ListingType.Local,
            changeSortType = SortType.Active,
        )
    }

    siteViewModel.fetchSite(
        auth = account?.jwt,
        ctx = null,
    )
}

fun imageInputStreamFromUri(ctx: Context, uri: Uri): InputStream {
    return ctx.contentResolver.openInputStream(uri)!!
}

fun decodeUriToBitmap(ctx: Context, uri: Uri): Bitmap? {
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

enum class ThemeColor {
    Dynamic,
    Green,
    Pink,
    Blue,
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

@ExperimentalPagerApi
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
        val fraction = pagerState.currentPageOffset
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

fun isSameInstance(url: String?, instance: String?): Boolean {
    return url?.let { hostName(it) } == instance
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

// TODO add a check for your account, view nsfw
fun nsfwCheck(postView: PostView): Boolean {
    return postView.post.nsfw || postView.community.nsfw
}

@Throws(IOException::class)
fun saveBitmap(
    context: Context,
    inputStream: InputStream,
    mimeType: String?,
    displayName: String,
): Uri {
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Jerboa")
    }

    val resolver = context.contentResolver
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

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.onAutofill(vararg autofillType: AutofillType, onFill: (String) -> Unit): Modifier = composed {
    val autofillNode = AutofillNode(
        autofillTypes = autofillType.toList(),
        onFill = onFill,
    )
    LocalAutofillTree.current += autofillNode

    val autofill = LocalAutofill.current

    this
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
