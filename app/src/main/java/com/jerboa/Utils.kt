package com.jerboa

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jerboa.datatypes.CommentView
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.theme.ACTION_BAR_ICON_SIZE
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import dev.jeziellago.compose.markdowntext.MarkdownText
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

val prettyTime = PrettyTime(Locale.getDefault())

val gson = Gson()

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

fun previewLines(text: String): String {
    val min = minOf(300, text.length)
    return text.substring(0, min)
}

@Composable
fun getCurrentAccount(accountViewModel: AccountViewModel): Account? {
    val accounts by accountViewModel.allAccounts.observeAsState()
    return getCurrentAccount(accounts)
}

fun getCurrentAccount(accounts: List<Account>?): Account? {
    return accounts?.firstOrNull { it.default_ }
}

fun toastException(ctx: Context, error: Exception) {
    Log.e("jerboa", error.toString())
    Toast.makeText(ctx, error.toString(), Toast.LENGTH_SHORT).show()
}

@Composable
fun upvoteColor(myVote: Int?): Color {
    return when (myVote) {
        1 -> MaterialTheme.colors.secondary
        else -> LocalContentColor.current
    }
}

@Composable
fun downvoteColor(myVote: Int?): Color {
    return when (myVote) {
        -1 -> MaterialTheme.colors.error
        else -> LocalContentColor.current
    }
}

@Composable
fun scoreColor(myVote: Int?): Color {
    return when (myVote) {
        1 -> MaterialTheme.colors.secondary
        -1 -> MaterialTheme.colors.error
        else -> LocalContentColor.current
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
        child?.let { cChild ->
            val parentId = cv.comment.parent_id
            parentId?.let { cParentId ->
                val parent = map[cParentId]

                // Necessary because blocked comment might not exist
                parent?.let { cParent ->
                    cParent.children?.add(cChild)
                }
            } ?: run {
                tree.add(cChild)
            }
            setDepth(cChild)
        }
    }

//    commentSort(tree, commentSortType);

    return tree
}

fun setDepth(node: CommentNodeData, i: Int = 0) {
    node.children?.forEach { child ->
        child.depth = i
        setDepth(child, i + 1)
    }
}

@Composable
fun DotSpacer(padding: Dp = MEDIUM_PADDING) {
    Text(
        text = "·",
        modifier = Modifier.padding(horizontal = padding)
    )
}

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

fun calculateCommentOffset(depth: Int?): Dp {
    return if (depth == null) {
        0.dp
    } else {
        ((depth + 1) * 2).dp
    }
}

@Composable
fun calculateBorderColor(depth: Int?): Color {
    return if (depth == null) {
        MaterialTheme.colors.background
    } else {
        colorList[depth.mod(colorList.size)]
    }
}

@Composable
fun <T> VoteGeneric(
    myVote: Int?,
    votes: Int,
    item: T,
    type: VoteType,
    onVoteClick: (item: T) -> Unit = {}
) {
    val voteColor =
        when (type) {
            VoteType.Upvote -> upvoteColor(myVote = myVote)
            else -> downvoteColor(myVote = myVote)
        }
    val voteIcon = when (type) {
        VoteType.Upvote -> Icons.Default.ArrowUpward
        else -> Icons.Default.ArrowDownward
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = { onVoteClick(item) })
    ) {
        Icon(
            imageVector = voteIcon,
            tint = voteColor,
            contentDescription = "TODO",
            modifier = Modifier
                .size(ACTION_BAR_ICON_SIZE)
                .padding(end = SMALL_PADDING)
        )
        Text(
            text = votes.toString(),
            style = MaterialTheme.typography.button,
            color = voteColor,
        )
    }
}

@Composable
fun MyMarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
) {

    // Note, this actually scales down the font size quite a lot, so you need to use a bigger one
    MarkdownText(
        markdown = markdown,
        style = MaterialTheme.typography.body1,
        fontSize = MaterialTheme.typography.subtitle1.fontSize,
        modifier = modifier,
    )
}
