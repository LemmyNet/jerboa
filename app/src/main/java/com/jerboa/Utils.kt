package com.jerboa

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jerboa.api.API
import com.jerboa.datatypes.* // ktlint-disable no-unused-imports
import com.jerboa.datatypes.api.GetUnreadCountResponse
import com.jerboa.db.Account
import com.jerboa.ui.components.home.HomeViewModel
import com.jerboa.ui.components.home.SiteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import java.io.InputStream
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import kotlin.math.pow

val prettyTime = PrettyTime(Locale.getDefault())

val gson = Gson()

const val LAUNCH_DELAY = 300L
const val MAX_POST_TITLE_LENGTH = 200

val DEFAULT_LEMMY_INSTANCES = listOf(
    "lemmy.ml", "szmer.info", "lemmygrad.ml", "lemmy.eus",
    "lemmy.pt"
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
        object : TypeToken<O>() {}.type
    )
}

fun toastException(ctx: Context?, error: Exception) {
    Log.e("jerboa", error.toString())
    if (ctx !== null) {
        Toast.makeText(ctx, error.toString(), Toast.LENGTH_SHORT).show()
    }
}

enum class VoteType {
    Upvote,
    Downvote,
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

data class CommentNodeData(
    val commentView: CommentView,
    val children: MutableList<CommentNodeData>?,
    var depth: Int?,
)

fun commentsToFlatNodes(
    comments: List<CommentView>
): List<CommentNodeData> {
    return comments.map { c -> CommentNodeData(commentView = c, children = null, depth = null) }
}

fun buildCommentsTree(
    comments: List<CommentView>?,
//    commentSortType: CommentSortType,
): List<CommentNodeData> {

    val map = LinkedHashMap<Number, CommentNodeData>()
    comments?.forEach { cv ->
        val node = CommentNodeData(
            commentView = cv,
            children = mutableListOf(),
            depth = null,
        )
        map[cv.comment.id] = node
    }

    val tree = mutableListOf<CommentNodeData>()
    comments?.forEach { cv ->
        val child = map[cv.comment.id]
        child?.also { cChild ->
            val parentId = cv.comment.parent_id
            parentId?.also { cParentId ->
                val parent = map[cParentId]

                // Necessary because blocked comment might not exist
                parent?.also { cParent ->
                    cParent.children?.add(cChild)
                }
            } ?: run {
                tree.add(cChild)
            }
            setDepth(cChild)
        }
    }

    return sortNodes(tree)
}

fun setDepth(node: CommentNodeData, i: Int = 0) {
    node.children?.forEach { child ->
        child.depth = i
        setDepth(child, i + 1)
    }
}

// TODO get rid of this if you can
fun colorShade(color: Color, factor: Float): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(color.toArgb(), hsl)
    hsl[2] *= factor
    return Color(ColorUtils.HSLToColor(hsl))
}

val colorList = listOf(
    hsl(0f),
    hsl(100f),
    hsl(150f),
    hsl(200f),
    hsl(250f),
    hsl(300f),
)

fun hsl(num: Float): Color {
    return Color(ColorUtils.HSLToColor(floatArrayOf(num, .35f, .5f)))
}

fun calculateCommentOffset(depth: Int?, multiplier: Int): Dp {
    return if (depth == null) {
        0.dp
    } else {
        ((depth + 1) * multiplier).dp
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

fun openLink(url: String, ctx: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    ctx.startActivity(intent)
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
    pattern = "(http)?s?:?(//[^\"']*\\.(?:jpg|jpeg|gif|png|svg|webp))"
)

fun closeDrawer(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    scope.launch {
        scaffoldState.drawerState.close()
    }
}

fun personNameShown(person: PersonSafe): String {
    val name = person.display_name ?: person.name
    return if (person.local) {
        name
    } else {
        "$name@${hostName(person.actor_id)}"
    }
}

fun communityNameShown(community: CommunitySafe): String {
    return if (community.local) {
        community.title
    } else {
        "${community.title}@${hostName(community.actor_id)}"
    }
}

fun hostName(url: String): String {
    return URL(url).host
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

fun handleInstantUpvote(
    myVote: MutableState<Int?>,
    score: MutableState<Int>,
    upvotes: MutableState<Int>,
    downvotes: MutableState<Int>
) {
    val newVote = if (myVote.value == 1) {
        0
    } else {
        1
    }

    when (myVote.value) {
        1 -> {
            score.value--
            upvotes.value--
        }
        -1 -> {
            downvotes.value--
            upvotes.value++
            score.value += 2
        }
        else -> {
            upvotes.value++
            score.value++
        }
    }

    myVote.value = newVote
}

fun handleInstantDownvote(
    myVote: MutableState<Int?>,
    score: MutableState<Int>,
    upvotes: MutableState<Int>,
    downvotes: MutableState<Int>
) {
    val newVote = if (myVote.value == -1) {
        0
    } else {
        -1
    }

    when (myVote.value) {
        1 -> {
            score.value -= 2
            upvotes.value--
            downvotes.value++
        }
        -1 -> {
            downvotes.value--
            score.value++
        }
        else -> {
            downvotes.value++
            score.value--
        }
    }

    myVote.value = newVote
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
    shareEnd: Boolean = true
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
        color = border.color
    )
}

private fun DrawScope.drawBottomBorder(
    border: Border,
    shareStart: Boolean,
    shareEnd: Boolean
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
        color = border.color
    )
}

private fun DrawScope.drawStartBorder(
    border: Border,
    shareTop: Boolean = true,
    shareBottom: Boolean = true
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
        color = border.color
    )
}

private fun DrawScope.drawEndBorder(
    border: Border,
    shareTop: Boolean = true,
    shareBottom: Boolean = true
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
        color = border.color
    )
}

fun sortNodes(nodes: List<CommentNodeData>): List<CommentNodeData> {
    return nodes.sortedBy { it.commentView.comment.deleted || it.commentView.comment.removed }
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
            hasError = true
        )
    } else if (name.length < 3) {
        InputField(
            label = "Title must be > 3 chars",
            hasError = true
        )
    } else if (name.length >= MAX_POST_TITLE_LENGTH) {
        InputField(
            label = "Title cannot be > 200 chars",
            hasError = true
        )
    } else {
        InputField(
            label = "Title",
            hasError = false
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
    var value = num.toDouble()
    val suffix = " KMBT"
    val formatter = DecimalFormat("#,###.#")
    val power = StrictMath.log10(value).toInt()
    value /= 10.0.pow((power / 3 * 3).toDouble())
    var formattedNumber = formatter.format(value)
    formattedNumber += suffix[power / 3]
    return if (formattedNumber.length > 4) formattedNumber.replace(
        "\\.[0-9]+".toRegex(),
        ""
    ) else formattedNumber
}

fun fetchInitialData(
    account: Account?,
    siteViewModel: SiteViewModel,
    homeViewModel: HomeViewModel
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
        homeViewModel.fetchPosts(
            account = account,
            clear = true,
        )
    }

    siteViewModel.fetchSite(
        auth = account?.jwt,
    )
}

fun imageInputStreamFromUri(ctx: Context, uri: Uri): InputStream {
    return ctx.contentResolver.openInputStream(uri)!!
}
