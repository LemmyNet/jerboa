package com.jerboa.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    unreadCounts: GetUnreadCountResponse? = null,
    onClickProfile: () -> Unit = {},
    onClickInbox: () -> Unit = {},
) {
    var selectedState by remember { mutableStateOf("home") }
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
                selectedState = "home"
                navController.navigate("home")
            },
            selected = selectedState == "home"
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
                selectedState = "communityList"
                navController.navigate("communityList")
            },
            selected = selectedState == "communityList"
        )
        BottomNavigationItem(
            icon = {
                InboxIconAndBadge(
                    iconBadgeCount = totalUnreads,
                    icon = Icons.Default.Email,
                    tint = if (selectedState == "inbox") {
                        MaterialTheme.colors.primary
                    } else {
                        Muted
                    },
                )
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = Muted,
            onClick = {
                selectedState = "inbox"
                onClickInbox()
            },
            selected = selectedState == "inbox"
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
            onClick = {
                selectedState = "personProfile"
                onClickProfile()
            },
            selected = selectedState == "personProfile"
        )
    }
}

@Preview
@Composable
fun BottomAppBarAllPreview() {
    BottomAppBarAll()
}

@Composable
fun CommentOrPostNodeHeader(
    creator: PersonSafe,
    score: Int,
    myVote: Int?,
    published: String,
    updated: String?,
    onPersonClick: (personId: Int) -> Unit = {},
    isPostCreator: Boolean,
    isModerator: Boolean,
    isCommunityBanned: Boolean,
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
    onClick: () -> Unit = {},
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
