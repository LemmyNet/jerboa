@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.inbox

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.FilterList
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.UnreadOrAll
import com.jerboa.ui.components.common.UnreadOrAllOptionsDialog

@Composable
fun InboxHeader(
    navController: NavController = rememberNavController(),
    selectedUnreadOrAll: UnreadOrAll,
    onClickUnreadOrAll: (UnreadOrAll) -> Unit,
    onClickMarkAllAsRead: () -> Unit,
    unreadCount: Int? = null,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    var showUnreadOrAllOptions by remember { mutableStateOf(false) }

    if (showUnreadOrAllOptions) {
        UnreadOrAllOptionsDialog(
            selectedUnreadOrAll = selectedUnreadOrAll,
            onDismissRequest = { showUnreadOrAllOptions = false },
            onClickUnreadOrAll = {
                showUnreadOrAllOptions = false
                onClickUnreadOrAll(it)
            },
        )
    }

    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            InboxHeaderTitle(
                unreadCount = unreadCount,
                selectedUnreadOrAll = selectedUnreadOrAll,
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
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
                )
            }
            IconButton(onClick = onClickMarkAllAsRead) {
                Icon(
                    Icons.Outlined.DoneAll,
                    contentDescription = "TODO",
                )
            }
        },
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
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = selectedUnreadOrAll.toString(),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
