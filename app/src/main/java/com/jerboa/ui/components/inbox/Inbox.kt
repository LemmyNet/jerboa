@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.inbox

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.MarkunreadMailbox
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.jerboa.R
import com.jerboa.UnreadOrAll
import com.jerboa.datatypes.getLocalizedUnreadOrAllName
import com.jerboa.ui.components.common.MenuItem

@Composable
fun InboxHeader(
    openDrawer: () -> Unit,
    selectedUnreadOrAll: UnreadOrAll,
    onClickUnreadOrAll: (UnreadOrAll) -> Unit,
    onClickMarkAllAsRead: () -> Unit,
    unreadCount: Int? = null,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    var showUnreadOrAllOptions by remember { mutableStateOf(false) }

    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            InboxHeaderTitle(
                unreadCount = unreadCount,
                selectedUnreadOrAll = selectedUnreadOrAll,
            )
        },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    Icons.Outlined.Menu,
                    contentDescription = stringResource(R.string.home_menu),
                )
            }
        },
        actions = {
            Box {
                IconButton(onClick = {
                    showUnreadOrAllOptions = !showUnreadOrAllOptions
                }) {
                    Icon(
                        Icons.Outlined.FilterList,
                        contentDescription = stringResource(R.string.inbox_filter),
                    )
                }

                UnreadOrAllOptionsDropDown(
                    expanded = showUnreadOrAllOptions,
                    selectedUnreadOrAll = selectedUnreadOrAll,
                    onDismissRequest = { showUnreadOrAllOptions = false },
                    onClickUnreadOrAll = {
                        showUnreadOrAllOptions = false
                        onClickUnreadOrAll(it)
                    },
                )
            }

            IconButton(onClick = onClickMarkAllAsRead) {
                Icon(
                    Icons.Outlined.DoneAll,
                    contentDescription = stringResource(R.string.inbox_markAllRead),
                )
            }
        },
    )
}

@Composable
fun InboxHeaderTitle(
    selectedUnreadOrAll: UnreadOrAll,
    unreadCount: Int? = null,
) {
    var title = stringResource(R.string.inbox_inbox)
    val ctx = LocalContext.current
    if (unreadCount != null && unreadCount > 0) {
        title = "$title ($unreadCount)"
    }
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = getLocalizedUnreadOrAllName(ctx, selectedUnreadOrAll),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun UnreadOrAllOptionsDropDown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onClickUnreadOrAll: (UnreadOrAll) -> Unit,
    selectedUnreadOrAll: UnreadOrAll,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        MenuItem(
            text = stringResource(R.string.dialogs_all),
            icon = Icons.AutoMirrored.Outlined.List,
            onClick = { onClickUnreadOrAll(UnreadOrAll.All) },
            highlight = (selectedUnreadOrAll == UnreadOrAll.All),
        )
        MenuItem(
            text = stringResource(R.string.dialogs_unread),
            icon = Icons.Outlined.MarkunreadMailbox,
            onClick = { onClickUnreadOrAll(UnreadOrAll.Unread) },
            highlight = (selectedUnreadOrAll == UnreadOrAll.Unread),
        )
    }
}
