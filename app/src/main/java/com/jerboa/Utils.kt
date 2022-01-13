package com.jerboa

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.PersonSafe
import com.jerboa.datatypes.SortType
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.components.home.IconAndTextDrawerItem
import com.jerboa.ui.components.person.PersonProfileLink
import com.jerboa.ui.theme.ACTION_BAR_ICON_SIZE
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.Muted
import com.jerboa.ui.theme.SMALL_PADDING
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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

@Composable
fun getCurrentAccount(accountViewModel: AccountViewModel): Account? {
    val accounts by accountViewModel.allAccounts.observeAsState()
    return getCurrentAccount(accounts)
}

fun getCurrentAccount(accounts: List<Account>?): Account? {
    return accounts?.firstOrNull { it.current }
}

fun toastException(ctx: Context, error: Exception) {
    Log.e("jerboa", error.toString())
    Toast.makeText(ctx, error.toString(), Toast.LENGTH_SHORT).show()
}

@Composable
fun upvoteColor(myVote: Int?): Color {
    return when (myVote) {
        1 -> MaterialTheme.colors.secondary
        else -> Muted
    }
}

@Composable
fun downvoteColor(myVote: Int?): Color {
    return when (myVote) {
        -1 -> MaterialTheme.colors.error
        else -> Muted
    }
}

@Composable
fun scoreColor(myVote: Int?): Color {
    return when (myVote) {
        1 -> MaterialTheme.colors.secondary
        -1 -> MaterialTheme.colors.error
        else -> Muted
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
fun ActionBarButton(
    onClick: () -> Unit = {},
    icon: ImageVector,
    text: String? = null,
    contentColor: Color = Muted,
    noClick: Boolean = false,
) {
//    Button(
//        onClick = onClick,
//        colors = ButtonDefaults.buttonColors(
//            backgroundColor = Color.Transparent,
//            contentColor = contentColor,
//        ),
//        shape = MaterialTheme.shapes.large,
//        contentPadding = PaddingValues(SMALL_PADDING),
//        elevation = null,
//        content = content,
//        modifier = Modifier
//            .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
//    )
    val barMod = if (noClick) {
        Modifier
    } else {
        Modifier.clickable(onClick = onClick)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = barMod,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "TODO",
            tint = contentColor,
            modifier = Modifier.height(ACTION_BAR_ICON_SIZE)
        )
        text?.also {
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = text,
                color = contentColor,
            )
        }
    }
}

@Composable
fun <T> VoteGeneric(
    myVote: Int?,
    votes: Int,
    item: T,
    type: VoteType,
    onVoteClick: (item: T) -> Unit = {},
    showNumber: Boolean = true,
) {
    val voteColor =
        when (type) {
            VoteType.Upvote -> upvoteColor(myVote = myVote)
            else -> downvoteColor(myVote = myVote)
        }
    val voteIcon = when (type) {
        VoteType.Upvote -> Icons.Default.ThumbUpAlt
        else -> Icons.Default.ThumbDownAlt
    }

    val votesStr = if (showNumber) {
        if (type == VoteType.Downvote && votes == 0) {
            null
        } else {
            votes.toString()
        }
    } else {
        null
    }

    ActionBarButton(
        onClick = { onVoteClick(item) },
        contentColor = voteColor,
        icon = voteIcon,
        text = votesStr,
    )
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun MyMarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    preview: Boolean = false,
    color: Color = MaterialTheme.typography.body1.color,
) {

//    val fontSize = TextUnit(MaterialTheme.typography.body1.fontSize.value, type = TextUnitType.Sp)

    // Note, this actually scales down the font size quite a lot, so you need to use a bigger one
    MarkdownText(
        markdown = markdown,
        style = MaterialTheme.typography.body1,
        fontSize = 18.sp,
        modifier = modifier,
        color = color,
// TODO Markdown preview doesn't make too much sense
//        maxLines = if (preview) {
//            5
//        } else {
//            Int.MAX_VALUE
//        }
    )
}

@Composable
fun CommentOrPostNodeHeader(
    creator: PersonSafe,
    score: Int,
    myVote: Int?,
    published: String,
    onPersonClick: (personId: Int) -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SMALL_PADDING)
    ) {
        Row {
            PersonProfileLink(
                person = creator,
                onClick = { onPersonClick(creator.id) },
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = score.toString(),
                color = scoreColor(myVote = myVote)
            )
            DotSpacer(0.dp)
            TimeAgo(dateStr = published)
        }
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

@Composable
fun PreviewLines(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        maxLines = 5,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
        fontSize = 14.sp,
    )
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

@Composable
fun SimpleTopAppBar(
    text: String,
    navController: NavController,
) {
    TopAppBar(
        title = {
            Text(
                text = text,
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
    )
}

fun personNameShown(person: PersonSafe): String {
    return person.display_name ?: person.name
}


@Composable
fun SortOptionsDialog(
    onDismissRequest: () -> Unit = {},
    onClickSortType: (SortType) -> Unit = {},
    onClickSortTopOptions: () -> Unit = {},
    selectedSortType: SortType,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "Active",
                    icon = Icons.Default.Moving,
                    onClick = { onClickSortType(SortType.Active) },
                    highlight = (selectedSortType == SortType.Active),
                )
                IconAndTextDrawerItem(
                    text = "Hot",
                    icon = Icons.Default.LocalFireDepartment,
                    onClick = { onClickSortType(SortType.Hot) },
                    highlight = (selectedSortType == SortType.Hot),
                )
                IconAndTextDrawerItem(
                    text = "New",
                    icon = Icons.Default.BrightnessLow,
                    onClick = { onClickSortType(SortType.New) },
                    highlight = (selectedSortType == SortType.New),
                )
                IconAndTextDrawerItem(
                    text = "Top",
                    icon = Icons.Default.BarChart,
                    onClick = onClickSortTopOptions,
                    more = true,
                    highlight = (topSortTypes.contains(selectedSortType)),
                )
            }
        },
        buttons = {},
    )
}

val topSortTypes = listOf(
    SortType.TopDay,
    SortType.TopWeek,
    SortType.TopMonth,
    SortType.TopYear,
    SortType.TopAll,
)

@Composable
fun SortTopOptionsDialog(
    onDismissRequest: () -> Unit = {},
    onClickSortType: (SortType) -> Unit = {},
    selectedSortType: SortType,
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "Top Day",
                    onClick = { onClickSortType(SortType.TopDay) },
                    highlight = (selectedSortType == SortType.TopDay),
                )
                IconAndTextDrawerItem(
                    text = "Top Week",
                    onClick = { onClickSortType(SortType.TopWeek) },
                    highlight = (selectedSortType == SortType.TopWeek),
                )
                IconAndTextDrawerItem(
                    text = "Top Month",
                    onClick = { onClickSortType(SortType.TopMonth) },
                    highlight = (selectedSortType == SortType.TopMonth),
                )
                IconAndTextDrawerItem(
                    text = "Top Year",
                    onClick = { onClickSortType(SortType.TopYear) },
                    highlight = (selectedSortType == SortType.TopYear),
                )
                IconAndTextDrawerItem(
                    text = "Top All Time",
                    onClick = { onClickSortType(SortType.TopAll) },
                    highlight = (selectedSortType == SortType.TopAll),
                )
            }
        },
        buttons = {},
    )
}

@Preview
@Composable
fun SortOptionsDialogPreview() {
    SortOptionsDialog(selectedSortType = SortType.Hot)
}

@Composable
fun ListingTypeOptionsDialog(
    onDismissRequest: () -> Unit = {},
    onClickListingType: (ListingType) -> Unit = {},
    selectedListingType: ListingType,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "Subscribed",
                    icon = Icons.Default.Bookmarks,
                    onClick = { onClickListingType(ListingType.Subscribed) },
                    highlight = (selectedListingType == ListingType.Subscribed),
                )
                // TODO hide local for non-federated instances
                IconAndTextDrawerItem(
                    text = "Local",
                    icon = Icons.Default.LocationCity,
                    onClick = { onClickListingType(ListingType.Local) },
                    highlight = (selectedListingType == ListingType.Local),
                )
                IconAndTextDrawerItem(
                    text = "All",
                    icon = Icons.Default.Public,
                    onClick = { onClickListingType(ListingType.All) },
                    highlight = (selectedListingType == ListingType.All),
                )
            }
        },
        buttons = {},
    )
}

@Preview
@Composable
fun ListingTypeOptionsDialogPreview() {
    ListingTypeOptionsDialog(selectedListingType = ListingType.Local)
}
