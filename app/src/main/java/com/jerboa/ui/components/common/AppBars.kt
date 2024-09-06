package com.jerboa.ui.components.common

import android.annotation.SuppressLint
import android.app.Activity
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.datatypes.UserViewType
import com.jerboa.datatypes.data
import com.jerboa.datatypes.samplePerson
import com.jerboa.datatypes.samplePost
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.feat.isReadyAndIfNotShowSimplifiedInfoToast
import com.jerboa.scrollToNextParentComment
import com.jerboa.scrollToPreviousParentComment
import com.jerboa.siFormat
import com.jerboa.ui.components.home.NavTab
import com.jerboa.ui.components.person.PersonProfileLink
import com.jerboa.ui.theme.*
import it.vercruysse.lemmyapi.datatypes.CommunityModeratorView
import it.vercruysse.lemmyapi.datatypes.Person
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PersonView
import it.vercruysse.lemmyapi.dto.SortType
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
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
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                modifier = Modifier.customMarquee(),
            )
        },
        navigationIcon = {
            IconButton(onClick = onClickBack, modifier = Modifier.testTag("jerboa:back")) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
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
    userViewType: UserViewType,
    unreadCounts: Long,
    unreadAppCount: Long?,
    unreadReportCount: Long?,
    showTextDescriptionsInNavbar: Boolean,
) {
    // Check for preview mode
    if (LocalContext.current is Activity) {
        val window = (LocalContext.current as Activity).window
        val colorScheme = MaterialTheme.colorScheme

        DisposableEffect(Unit) {
            window.navigationBarColor = colorScheme.surfaceContainer.toArgb()

            onDispose {
                window.navigationBarColor = colorScheme.background.toArgb()
            }
        }
    }
    // If descriptions are hidden, make the bar shorter
    val modifier = if (showTextDescriptionsInNavbar) Modifier else Modifier.navigationBarsPadding().height(56.dp)
    NavigationBar(
        modifier = modifier,
    ) {
        for (tab in NavTab.getEntries(userViewType)) {
            val selected = tab == selectedTab
            val iconBadgeCount = when (tab) {
                NavTab.Inbox -> unreadCounts
                NavTab.RegistrationApplications -> unreadAppCount
                NavTab.Reports -> unreadReportCount
                else -> null
            }

            NavigationBarItem(
                icon = {
                    NavbarIconAndBadge(
                        iconBadgeCount = iconBadgeCount,
                        icon =
                            if (selected) {
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
                            textAlign = TextAlign.Center,
                            fontSize = TextUnit(10f, TextUnitType.Sp),
                            text = stringResource(tab.textId),
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
        unreadAppCount = 2,
        unreadReportCount = 8,
        userViewType = UserViewType.AdminOnly,
        showTextDescriptionsInNavbar = true,
    )
}

@Preview
@Composable
fun BottomAppBarAllNoDescriptionsPreview() {
    BottomAppBarAll(
        selectedTab = NavTab.Home,
        onSelect = {},
        unreadCounts = 30,
        unreadAppCount = null,
        unreadReportCount = null,
        userViewType = UserViewType.Normal,
        showTextDescriptionsInNavbar = false,
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
        modifier = Modifier.navigationBarsPadding().height(50.dp),
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
    isNsfw: Boolean,
    published: String,
    updated: String?,
    deleted: Boolean,
    onPersonClick: (personId: PersonId) -> Unit,
    isPostCreator: Boolean,
    isDistinguished: Boolean,
    isCommunityBanned: Boolean,
    onClick: () -> Unit,
    onLongCLick: () -> Unit,
    isExpanded: Boolean = true,
    collapsedCommentsCount: Long = 0,
    showAvatar: Boolean,
) {
    FlowRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier =
            Modifier
                .fillMaxWidth()
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onLongClick = onLongCLick,
                    onClick = onClick,
                ).padding(
                    top = LARGE_PADDING,
                    bottom = MEDIUM_PADDING,
                ),
    ) {
        val centerMod = Modifier.align(Alignment.CenterVertically)
        Row(
            horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
            modifier = centerMod,
        ) {
            if (deleted) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.commentOrPostHeader_deleted),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = centerMod,
                )
                DotSpacer(modifier = centerMod)
            }

            PersonProfileLink(
                person = creator,
                onClick = { onPersonClick(creator.id) },
                showTags = true,
                isPostCreator = isPostCreator,
                isDistinguished = isDistinguished,
                isCommunityBanned = isCommunityBanned,
                showAvatar = showAvatar,
                modifier = centerMod,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
            modifier = centerMod,
        ) {
            NsfwBadge(
                visible = isNsfw,
                modifier = centerMod,
            )
            CollapsedIndicator(
                visible = !isExpanded,
                descendants = collapsedCommentsCount,
                modifier = centerMod,
            )
            TimeAgo(
                published = published,
                updated = updated,
                modifier = centerMod,
            )
        }
    }
}

@Preview
@Composable
fun CommentOrPostNodeHeaderPreview() {
    CommentOrPostNodeHeader(
        creator = samplePerson,
        published = samplePost.published,
        updated = samplePost.updated,
        deleted = false,
        onPersonClick = {},
        isPostCreator = true,
        isDistinguished = false,
        isCommunityBanned = false,
        onClick = {},
        onLongCLick = {},
        showAvatar = true,
        isNsfw = true,
    )
}

@Composable
fun ActionBarButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    text: String? = null,
    contentColor: Color = MaterialTheme.colorScheme.outline,
    noClick: Boolean = false,
    account: Account,
    requiresAccount: Boolean = true,
) {
    val ctx = LocalContext.current

    val barMod =
        if (noClick) {
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
        horizontalArrangement = Arrangement.spacedBy(SMALLER_PADDING),
        modifier = barMod,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
        )
        text?.also {
            Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.labelMedium,
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
    contentColor: Color = MaterialTheme.colorScheme.outline,
    noClick: Boolean = false,
    account: Account,
    requiresAccount: Boolean = true,
) {
    val ctx = LocalContext.current

    val barMod =
        if (noClick) {
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
                style = MaterialTheme.typography.labelMedium,
            )
        }
        iconBadgeCount?.also {
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            TextBadge(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                text = iconBadgeCount,
                textStyle = MaterialTheme.typography.labelSmall.copy(fontStyle = FontStyle.Italic),
                textColor = contentColor,
                verticalTextPadding = 2f,
                horizontalTextPadding = 4f,
            )
        }
    }
}

@Composable
fun DotSpacer(
    modifier: Modifier = Modifier,
    padding: Dp = 0.dp,
    style: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Text(
        text = stringResource(R.string.app_bars_dot_spacer),
        style = style,
        color = MaterialTheme.colorScheme.outline,
        modifier = modifier.padding(horizontal = padding),
    )
}

@Composable
fun scoreColor(myVote: Int?): Color =
    when (myVote) {
        1 -> MaterialTheme.colorScheme.secondary
        -1 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }

@Composable
fun NavbarIconAndBadge(
    iconBadgeCount: Long?,
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
    postCount: Long,
    commentCount: Long,
    usersActiveDay: Long,
    usersActiveWeek: Long,
    usersActiveMonth: Long,
    usersActiveHalfYear: Long,
    moderators: List<CommunityModeratorView>,
    admins: List<PersonView>,
    showAvatar: Boolean,
    onPersonClick: (PersonId) -> Unit,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier =
            Modifier
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
                        style = MaterialTheme.typography.titleMedium,
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
            HorizontalDivider()
        }
        item {
            content?.also {
                Column(
                    modifier = Modifier.padding(MEDIUM_PADDING),
                ) {
                    MyMarkdownText(
                        markdown = it,
                        color = MaterialTheme.colorScheme.outline,
                        onClick = {},
                    )
                }
            }
        }

        personList(
            titleResource = R.string.moderators,
            persons = moderators.map { it.moderator },
            onPersonClick = onPersonClick,
            showAvatar = showAvatar,
        )

        personList(
            titleResource = R.string.admins,
            persons = admins.map { it.person },
            onPersonClick = onPersonClick,
            showAvatar = showAvatar,
        )
    }
}

private fun LazyListScope.personList(
    titleResource: Int,
    persons: List<Person>,
    onPersonClick: (PersonId) -> Unit,
    showAvatar: Boolean,
) {
    if (persons.isNotEmpty()) {
        item {
            HorizontalDivider()
        }
        item {
            Text(
                text = stringResource(titleResource),
                modifier = Modifier.padding(MEDIUM_PADDING),
                style = MaterialTheme.typography.titleLarge,
            )
        }
        items(
            items = persons,
            contentType = { "person" },
        ) { person ->
            PersonProfileLink(
                person = person,
                onClick = onPersonClick,
                showAvatar = showAvatar,
                modifier = Modifier.padding(horizontal = MEDIUM_PADDING),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommentsAndPosts(
    usersActiveDay: Long,
    usersActiveWeek: Long,
    usersActiveMonth: Long,
    usersActiveHalfYear: Long,
    postCount: Long,
    commentCount: Long,
) {
    FlowRow {
        Text(
            text = stringResource(R.string.AppBars_users_day, siFormat(usersActiveDay)),
            color = MaterialTheme.colorScheme.outline,
        )
        DotSpacer()
        Text(
            text = stringResource(R.string.AppBars_users_week, siFormat(usersActiveWeek)),
            color = MaterialTheme.colorScheme.outline,
        )
        DotSpacer(style = MaterialTheme.typography.bodyMedium)
        Text(
            text = stringResource(R.string.AppBars_users_month, siFormat(usersActiveMonth)),
            color = MaterialTheme.colorScheme.outline,
        )
        DotSpacer()
        Text(
            text = stringResource(R.string.AppBars_users_6_months, siFormat(usersActiveHalfYear)),
            color = MaterialTheme.colorScheme.outline,
        )
        DotSpacer()
        Text(
            text = stringResource(R.string.AppBars_posts, siFormat(postCount)),
            color = MaterialTheme.colorScheme.outline,
        )
        DotSpacer()
        Text(
            text = stringResource(R.string.AppBars_comments, siFormat(commentCount)),
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

@Preview
@Composable
fun CommentsAndPostsPreview() {
    CommentsAndPosts(
        usersActiveDay = 2,
        usersActiveWeek = 22,
        usersActiveMonth = 222,
        usersActiveHalfYear = 2222,
        postCount = 20,
        commentCount = 5,
    )
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

        val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo
            .firstOrNull()
            ?.index
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
fun LoadingBar(padding: PaddingValues = PaddingValues(0.dp)) {
    LinearProgressIndicator(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(padding)
                .zIndex(99F)
                .testTag("jerboa:loading"),
    )
}

@Composable
fun JerboaLoadingBar(apiState: ApiState<*>) {
    if (apiState.isLoading()) {
        LoadingBar()
    }
}

/**
 * A simple top bar with a action that defaults to save
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionTopBar(
    onBackClick: () -> Unit,
    onActionClick: () -> Unit,
    loading: Boolean,
    title: String,
    @StringRes actionText: Int = R.string.save,
    actionIcon: ImageVector = Icons.Outlined.Save,
    formValid: Boolean = true,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        actions = {
            IconButton(
                onClick = onActionClick,
                enabled = formValid && !loading,
            ) {
                if (loading) {
                    CircularProgressIndicator()
                } else {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = stringResource(actionText),
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.topAppBar_back),
                )
            }
        },
    )
}

@Composable
fun DualHeaderTitle(
    topText: String,
    bottomText: String,
    @SuppressLint("ModifierParameter") topModifier: Modifier = Modifier,
) {
    Column {
        Text(
            text = topText,
            maxLines = 1,
            style = MaterialTheme.typography.titleSmall,
            modifier = topModifier,
        )
        Text(
            text = bottomText,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
fun DualHeaderTitle(
    topText: String,
    selectedSortType: SortType,
    @SuppressLint("ModifierParameter") topModifier: Modifier = Modifier,
) = DualHeaderTitle(
    topText = topText,
    bottomText = stringResource(selectedSortType.data.shortForm),
    topModifier = topModifier,
)

@Composable
fun CollapsedIndicator(
    modifier: Modifier = Modifier,
    visible: Boolean,
    descendants: Long,
) {
    AnimatedVisibility(
        visible = visible && descendants > 0,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Column(modifier = modifier.wrapContentSize(Alignment.Center)) {
            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(horizontal = SMALL_PADDING),
            ) {
                Text(
                    text = "+$descendants",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                )
            }
        }
    }
}

@Preview
@Composable
fun CollapsedIndicatorPreview() {
    CollapsedIndicator(visible = true, descendants = 23)
}

@Composable
fun NsfwBadge(
    modifier: Modifier = Modifier,
    visible: Boolean,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Column(modifier = modifier.wrapContentSize(Alignment.Center)) {
            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(horizontal = SMALL_PADDING),
            ) {
                Text(
                    text = "NSFW",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                )
            }
        }
    }
}

@Preview
@Composable
fun NsfwBadgePreview() {
    NsfwBadge(visible = true)
}
