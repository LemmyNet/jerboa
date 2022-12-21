package com.jerboa.ui.components.inbox

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.UnreadOrAll
import com.jerboa.ui.components.common.UnreadOrAllOptionsDialog
import com.jerboa.ui.theme.APP_BAR_ELEVATION
import com.jerboa.ui.theme.muted

@Composable
fun InboxHeader(
    navController: NavController = rememberNavController(),
    selectedUnreadOrAll: UnreadOrAll,
    onClickUnreadOrAll: (UnreadOrAll) -> Unit,
    onClickMarkAllAsRead: () -> Unit,
    unreadCount: Int? = null
) {
    var showUnreadOrAllOptions by remember { mutableStateOf(false) }

    if (showUnreadOrAllOptions) {
        UnreadOrAllOptionsDialog(
            selectedUnreadOrAll = selectedUnreadOrAll,
            onDismissRequest = { showUnreadOrAllOptions = false },
            onClickUnreadOrAll = {
                showUnreadOrAllOptions = false
                onClickUnreadOrAll(it)
            }
        )
    }

    val backgroundColor = MaterialTheme.colors.primarySurface
    val contentColor = contentColorFor(backgroundColor)

    TopAppBar(
        title = {
            InboxHeaderTitle(
                unreadCount = unreadCount,
                selectedUnreadOrAll = selectedUnreadOrAll
            )
        },
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = APP_BAR_ELEVATION,
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Outlined.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = {
                showUnreadOrAllOptions = !showUnreadOrAllOptions
            }) {
                Icon(
                    Icons.Outlined.FilterList,
                    contentDescription = "TODO",
                    tint = contentColor
                )
            }
            IconButton(onClick = onClickMarkAllAsRead) {
                Icon(
                    Icons.Outlined.DoneAll,
                    contentDescription = "TODO",
                    tint = contentColor
                )
            }
        }
    )
}

@Composable
fun InboxHeaderTitle(selectedUnreadOrAll: UnreadOrAll, unreadCount: Int? = null) {
    var title = "Inbox"
    if (unreadCount != null && unreadCount > 0) {
        title = "$title ($unreadCount)"
    }
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.h6
        )
        Text(
            text = selectedUnreadOrAll.toString(),
            style = MaterialTheme.typography.subtitle1,
            color = contentColorFor(MaterialTheme.colors.primarySurface).muted
        )
    }
}
