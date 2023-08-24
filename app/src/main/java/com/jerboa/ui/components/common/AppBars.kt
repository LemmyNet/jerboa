package com.jerboa.ui.components.common

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jerboa.R
import com.jerboa.datatypes.samplePerson
import com.jerboa.datatypes.samplePost
import com.jerboa.datatypes.types.Person
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.feat.isReadyAndIfNotShowSimplifiedInfoToast
import com.jerboa.scrollToNextParentComment
import com.jerboa.scrollToPreviousParentComment
import com.jerboa.siFormat
import com.jerboa.ui.components.home.NavTab
import com.jerboa.ui.components.person.PersonProfileLink
import com.jerboa.ui.theme.*
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SimpleTopAppBar(
    text: String,
    onClickBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = text,
                maxLines = 1,
                modifier = Modifier.basicMarquee(),
            )
        },
        navigationIcon = {
            IconButton(onClick = onClickBack, modifier = Modifier.testTag("jerboa:back")) {
                Icon(
                    Icons.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.topAppBar_back),
                )
            }
        },
        actions = actions,
    )
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopAppBarPreview() {
    SimpleTopAppBar(text = "Preview", onClickBack = {}) {
    }
}

@Composable
fun BottomAppBarAll(
    selectedTab: NavTab,
    onSelect: (NavTab) -> Unit,
    unreadCounts: Int,
    showTextDescriptionsInNavbar: Boolean,
) {
    // Check for preview mode
    if (LocalContext.current is Activity) {
        val window = (LocalContext.current as Activity).window
        val colorScheme = MaterialTheme.colorScheme

        DisposableEffect(Unit) {
            window.navigationBarColor = colorScheme.surfaceColorAtElevation(3.dp).toArgb()

            onDispose {
                window.navigationBarColor = colorScheme.background.toArgb()
            }
        }
    }

    NavigationBar {
        for (tab in NavTab.entries) {
            val selected = tab == selectedTab
            NavigationBarItem(
                icon = {
                    InboxIconAndBadge(
                        iconBadgeCount = if (tab == NavTab.Inbox) unreadCounts else null,
                        icon = if (selected) {
                            tab.iconFilled
                        } else {
                            tab.iconOutlined
                        },
                        contentDescription = stringResource(tab.contentDescriptionId),
                    )
                },
                label = {
                    if (showTextDescriptionsInNavbar) {
                        Text(
                            text = stringResource(tab.textId),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                selected = selected,
                onClick = {
                    onSelect(tab)
                },
            )
        }
    }
}

@Preview
@Composable
fun BottomAppBarAllPreview() {
    BottomAppBarAll(
        selectedTab = NavTab.Home,
        onSelect = {},
        unreadCounts = 30,
        showTextDescriptionsInNavbar = true,
    )
}

@Composable
fun CommentNavigationBottomAppBar(
    scope: CoroutineScope,
    parentListStateIndexes: List<Int>,
    listState: LazyListState,
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.background.copy(alpha = .75f),
        modifier = Modifier.height(50.dp),
        content = {
            IconButton(modifier = Modifier.weight(.5f), onClick = {
                scrollToPreviousParentComment(scope, parentListStateIndexes, listState)
            }) {
                Icon(
                    modifier = Modifier.scale(1.5f),
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = stringResource(R.string.comment_previous_parent),
                )
            }
            IconButton(modifier = Modifier.weight(.5f), onClick = {
                scrollToNextParentComment(scope, parentListStateIndexes, listState)
            }) {
                Icon(
                    modifier = Modifier.scale(1.5f),
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.comment_next_parent),
                )
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun CommentOrPostNodeHeader(
    creator: Person,
    score: Int,
    myVote: Int?,
    published: String,
    updated: String?,
    deleted: Boolean,
    onPersonClick: (personId: Int) -> Unit,
    isPostCreator: Boolean,
    isModerator: Boolean,
    isCommunityBanned: Boolean,
    onClick: () -> Unit,
    onLongCLick: () -> Unit,
    isExpanded: Boolean = true,
    collapsedCommentsCount: Int = 0,
    showAvatar: Boolean,
    showScores: Boolean,
) {
    FlowRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = LARGE_PADDING,
                bottom = MEDIUM_PADDING,
            )
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onLongClick = onLongCLick,
                onClick = onClick,
            ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (deleted) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.commentOrPostHeader_deleted),
                    tint = MaterialTheme.colorScheme.error,
                )
                DotSpacer(style = MaterialTheme.typography.bodyMedium)
            }

            PersonProfileLink(
                person = creator,
                onClick = { onPersonClick(creator.id) },
                showTags = true,
                isPostCreator = isPostCreator,
                isModerator = isModerator,
                isCommunityBanned = isCommunityBanned,
                showAvatar = showAvatar,
            )
        }
        ScoreAndTime(
            score = score,
            myVote = myVote,
            published = published,
            updated = updated,
            isExpanded = isExpanded,
            collapsedCommentsCount = collapsedCommentsCount,
            showScores = showScores,
        )
    }
}

@Preview
@Composable
fun CommentOrPostNodeHeaderPreview() {
    CommentOrPostNodeHeader(
        creator = samplePerson,
        score = 23,
        myVote = 1,
        published = samplePost.published,
        updated = samplePost.updated,
        deleted = false,
        onPersonClick = {},
        isPostCreator = true,
        isModerator = true,
        isCommunityBanned = false,
        onClick = {},
        onLongCLick = {},
        showAvatar = true,
        showScores = true,
    )
}

@Composable
fun ActionBarButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    text: String? = null,
    contentColor: Color = MaterialTheme.colorScheme.onBackground.muted,
    noClick: Boolean = false,
    account: Account,
    requiresAccount: Boolean = true,
) {
    val ctx = LocalContext.current

    val barMod = if (noClick) {
        modifier
    } else {
        modifier.clickable(onClick = {
            if (!requiresAccount || account.isReadyAndIfNotShowSimplifiedInfoToast(ctx)) {
                onClick()
            }
        })
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = barMod,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
        )
        text?.also {
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Preview
@Composable
fun ActionBarButtonAndBadgePreview() {
    ActionBarButtonAndBadge(
        icon = Icons.Outlined.ChatBubbleOutline,
        iconBadgeCount = siFormat(15),
        contentDescription = null,
        text = siFormat(2000),
        noClick = true,
        account = AnonAccount,
        onClick = {},
    )
}

@Composable
fun ActionBarButtonAndBadge(
    onClick: () -> Unit,
    icon: ImageVector,
    iconBadgeCount: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    text: String? = null,
    contentColor: Color = MaterialTheme.colorScheme.onBackground.muted,
    noClick: Boolean = false,
    account: Account,
    requiresAccount: Boolean = true,
) {
    val ctx = LocalContext.current

    val barMod = if (noClick) {
        modifier
    } else {
        modifier.clickable(onClick = {
            if (!requiresAccount || account.isReadyAndIfNotShowSimplifiedInfoToast(ctx)) {
                onClick()
            }
        })
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = barMod,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
        )
        text?.also {
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        iconBadgeCount?.also {
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            TextBadge(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                text = iconBadgeCount,
                textStyle = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                textColor = contentColor,
                verticalTextPadding = 2f,
                horizontalTextPadding = 4f,
            )
        }
    }
}

@Composable
fun DotSpacer(
    padding: Dp = SMALL_PADDING,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    Text(
        text = stringResource(R.string.app_bars_dot_spacer),
        style = style,
        color = MaterialTheme.colorScheme.onBackground.muted,
        modifier = Modifier.padding(horizontal = padding),
    )
}

@Composable
fun scoreColor(myVote: Int?): Color {
    return when (myVote) {
        1 -> MaterialTheme.colorScheme.secondary
        -1 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onBackground.muted
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxIconAndBadge(
    iconBadgeCount: Int?,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    if (iconBadgeCount !== null && iconBadgeCount > 0) {
        BadgedBox(
            modifier = modifier,
            badge = {
                Badge(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    content = {
                        Text(
                            text = iconBadgeCount.toString(),
                        )
                    },
                )
            },
            content = {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = tint,
                )
            },
        )
    } else {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = modifier,
        )
    }
}

@Composable
fun Sidebar(
    title: String?,
    banner: String?,
    icon: String?,
    content: String?,
    published: String,
    postCount: Int,
    commentCount: Int,
    usersActiveDay: Int,
    usersActiveWeek: Int,
    usersActiveMonth: Int,
    usersActiveHalfYear: Int,
    padding: PaddingValues,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(padding)
            .simpleVerticalScrollbar(listState),
        verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomStart,
            ) {
                banner?.also {
                    PictrsBannerImage(
                        url = it,
                        modifier = Modifier.height(PROFILE_BANNER_SIZE),
                    )
                }
                Box(modifier = Modifier.padding(MEDIUM_PADDING)) {
                    icon?.also {
                        LargerCircularIcon(icon = it)
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier.padding(MEDIUM_PADDING),
                verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
            ) {
                title?.also {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                TimeAgo(
                    precedingString = stringResource(R.string.AppBars_created),
                    longTimeFormat = true,
                    published = published,
                )
                CommentsAndPosts(
                    usersActiveDay = usersActiveDay,
                    usersActiveWeek = usersActiveWeek,
                    usersActiveMonth = usersActiveMonth,
                    usersActiveHalfYear = usersActiveHalfYear,
                    postCount = postCount,
                    commentCount = commentCount,
                )
            }
        }
        item {
            Divider()
        }
        item {
            content?.also {
                Column(
                    modifier = Modifier.padding(MEDIUM_PADDING),
                ) {
                    MyMarkdownText(
                        markdown = it,
                        color = MaterialTheme.colorScheme.onBackground.muted,
                        onClick = {},
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommentsAndPosts(
    usersActiveDay: Int,
    usersActiveWeek: Int,
    usersActiveMonth: Int,
    usersActiveHalfYear: Int,
    postCount: Int,
    commentCount: Int,
) {
    FlowRow {
        Text(
            text = stringResource(R.string.AppBars_users_day, siFormat(usersActiveDay)),
            color = MaterialTheme.colorScheme.onBackground.muted,
        )
        DotSpacer(style = MaterialTheme.typography.bodyMedium)
        Text(
            text = stringResource(R.string.AppBars_users_week, siFormat(usersActiveWeek)),
            color = MaterialTheme.colorScheme.onBackground.muted,
        )
        DotSpacer(style = MaterialTheme.typography.bodyMedium)
        Text(
            text = stringResource(R.string.AppBars_users_month, siFormat(usersActiveMonth)),
            color = MaterialTheme.colorScheme.onBackground.muted,
        )
        DotSpacer(style = MaterialTheme.typography.bodyMedium)
        Text(
            text = stringResource(R.string.AppBars_users_6_months, siFormat(usersActiveHalfYear)),
            color = MaterialTheme.colorScheme.onBackground.muted,
        )
        DotSpacer(style = MaterialTheme.typography.bodyMedium)
        Text(
            text = stringResource(R.string.AppBars_posts, siFormat(postCount)),
            color = MaterialTheme.colorScheme.onBackground.muted,
        )
        DotSpacer(style = MaterialTheme.typography.bodyMedium)
        Text(
            text = stringResource(R.string.AppBars_comments, siFormat(commentCount)),
            color = MaterialTheme.colorScheme.onBackground.muted,
        )
    }
}

@SuppressLint("ComposableModifierFactory")
@Composable
fun Modifier.simpleVerticalScrollbar(
    state: LazyListState,
    width: Dp = 4.dp,
): Modifier {
    val targetAlpha = if (state.isScrollInProgress) 0.5f else 0f
    val duration = if (state.isScrollInProgress) 150 else 500
    val color = MaterialTheme.colorScheme.onBackground

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration),
        label = "animateScrollBar",
    )

    return drawWithContent {
        drawContent()

        val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
        val needDrawScrollbar = state.isScrollInProgress || alpha > 0.0f

        // Draw scrollbar if scrolling or if the animation is still running and lazy column has content
        if (needDrawScrollbar && firstVisibleElementIndex != null) {
            val elementHeight = this.size.height / state.layoutInfo.totalItemsCount
            val scrollbarOffsetY = firstVisibleElementIndex * elementHeight
            val scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight

            drawRect(
                color = color,
                topLeft = Offset(this.size.width - width.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarHeight),
                alpha = alpha,
            )
        }
    }
}

@Composable
fun LoadingBar(
    padding: PaddingValues = PaddingValues(0.dp),
) {
    LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth().padding(padding).testTag("jerboa:loading"),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSubmitHeader(
    title: String,
    onClickBack: () -> Unit,
    onSubmitClick: () -> Unit,
    loading: Boolean,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
            )
        },
        actions = {
            IconButton(
                onClick = onSubmitClick,
                enabled = !loading,
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Send,
                        contentDescription = stringResource(R.string.form_submit),
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onClickBack,
            ) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.create_report_back),
                )
            }
        },
    )
}
