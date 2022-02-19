package com.jerboa.ui.components.common

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.datatypes.PersonSafe
import com.jerboa.datatypes.api.GetUnreadCountResponse
import com.jerboa.db.Account
import com.jerboa.siFormat
import com.jerboa.ui.components.person.PersonProfileLink
import com.jerboa.ui.theme.*
import com.jerboa.unreadCountTotal

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
        elevation = APP_BAR_ELEVATION,
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

@Composable
fun BottomAppBarAll(
    navController: NavController = rememberNavController(),
    screen: String,
    unreadCounts: GetUnreadCountResponse? = null,
    onClickSaved: () -> Unit,
    onClickProfile: () -> Unit,
    onClickInbox: () -> Unit,
) {
    val totalUnreads = unreadCounts?.let { unreadCountTotal(it) }

    BottomAppBar(
        elevation = APP_BAR_ELEVATION,
        backgroundColor = MaterialTheme.colors.background,
    ) {
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "TODO",
                )
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Muted,
            onClick = {
                navController.navigate("home")
            },
            selected = screen == "home"
        )

        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "TODO",
                )
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Muted,
            onClick = {
                navController.navigate("communityList")
            },
            selected = screen == "communityList"
        )
        BottomNavigationItem(
            icon = {
                InboxIconAndBadge(
                    iconBadgeCount = totalUnreads,
                    icon = Icons.Default.Email,
                    tint = if (screen == "inbox") {
                        MaterialTheme.colors.primary
                    } else {
                        Muted
                    },
                )
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Muted,
            onClick = {
                onClickInbox()
            },
            selected = screen == "inbox"
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "TODO",
                )
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Muted,
            onClick = {
                onClickSaved()
            },
            selected = screen == "saved"
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "TODO",
                )
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Muted,
            onClick = onClickProfile,
            selected = screen == "profile"
        )
    }
}

@Preview
@Composable
fun BottomAppBarAllPreview() {
    BottomAppBarAll(
        onClickInbox = {},
        onClickProfile = {},
        onClickSaved = {},
        screen = "home",
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentOrPostNodeHeader(
    creator: PersonSafe,
    score: Int,
    myVote: Int?,
    published: String,
    updated: String?,
    onPersonClick: (personId: Int) -> Unit,
    isPostCreator: Boolean,
    isModerator: Boolean,
    isCommunityBanned: Boolean,
    onLongClick: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = LARGE_PADDING)
            .combinedClickable(
                onLongClick = onLongClick,
                onClick = {},
            )
    ) {
        Row {
            PersonProfileLink(
                person = creator,
                onClick = { onPersonClick(creator.id) },
                showTags = true,
                isPostCreator = isPostCreator,
                isModerator = isModerator,
                isCommunityBanned = isCommunityBanned,
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
            TimeAgo(published = published, updated = updated)
        }
    }
}

@Composable
fun ActionBarButton(
    onClick: () -> Unit,
    icon: ImageVector,
    text: String? = null,
    contentColor: Color = Muted,
    noClick: Boolean = false,
    account: Account?
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
        Modifier.clickable(onClick = onClick, enabled = (account !== null))
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
fun DotSpacer(padding: Dp = MEDIUM_PADDING) {
    Text(
        text = "·",
        modifier = Modifier.padding(horizontal = padding)
    )
}

@Composable
fun scoreColor(myVote: Int?): Color {
    return when (myVote) {
        1 -> MaterialTheme.colors.secondary
        -1 -> MaterialTheme.colors.error
        else -> Muted
    }
}

@Composable
fun InboxIconAndBadge(
    iconBadgeCount: Int?,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    if (iconBadgeCount !== null && iconBadgeCount > 0) {
        BadgedBox(
            modifier = modifier,
            badge = {
                Badge(
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
                    contentDescription = "TODO",
                    tint = tint,
                )
            },
        )
    } else {
        Icon(
            imageVector = icon,
            contentDescription = "TODO",
            tint = tint,
            modifier = modifier
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
    usersActiveMonth: Int,
    postCount: Int,
    commentCount: Int,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.padding(MEDIUM_PADDING)
            .simpleVerticalScrollbar(listState),
        verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING)
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomStart
            ) {
                banner?.also {
                    PictrsBannerImage(
                        url = it, modifier = Modifier.height(PROFILE_BANNER_SIZE)
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
            Card(
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .padding(vertical = SMALL_PADDING)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(MEDIUM_PADDING),
                    verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING)
                ) {
                    title?.also {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                    TimeAgo(
                        precedingString = "Created",
                        includeAgo = true,
                        published = published
                    )
                    CommentsAndPosts(
                        usersActiveMonth = usersActiveMonth,
                        postCount = postCount,
                        commentCount = commentCount,
                    )
                }
            }
        }
        item {
            content?.also {
                MyMarkdownText(
                    markdown = it,
                    color = Muted,
                )
            }
        }
    }
}

@Composable
fun CommentsAndPosts(
    usersActiveMonth: Int,
    postCount: Int,
    commentCount: Int,
) {
    Row {
        Text(
            text = "${siFormat(usersActiveMonth)} users / month",
            color = Muted,
        )
        DotSpacer()
        Text(
            text = "${siFormat(postCount)} posts",
            color = Muted,
        )
        DotSpacer()
        Text(
            text = "${siFormat(commentCount)} comments",
            color = Muted,
        )
    }
}

@SuppressLint("ComposableModifierFactory")
@Composable
fun Modifier.simpleVerticalScrollbar(
    state: LazyListState,
    width: Dp = 4.dp
): Modifier {
    val targetAlpha = if (state.isScrollInProgress) 0.5f else 0f
    val duration = if (state.isScrollInProgress) 150 else 500

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration)
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
                color = Color.LightGray,
                topLeft = Offset(this.size.width - width.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarHeight),
                alpha = alpha
            )
        }
    }
}
