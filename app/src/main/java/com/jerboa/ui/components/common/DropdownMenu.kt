package com.jerboa.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.MarkunreadMailbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.jerboa.R
import com.jerboa.UnreadOrAll
import com.jerboa.api.API
import com.jerboa.datatypes.data
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.POPUP_MENU_WIDTH_RATIO
import com.jerboa.ui.theme.Shapes
import it.vercruysse.lemmyapi.dto.CommentSortType
import it.vercruysse.lemmyapi.dto.SortType
import it.vercruysse.lemmyapi.dto.getSupportedEntries
import me.saket.cascade.CascadeColumnScope
import me.saket.cascade.CascadeDropdownMenu

val isTopSort = { sort: SortType -> sort.name.startsWith("Top") }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SortOptionsDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onClickSortType: (SortType) -> Unit,
    selectedSortType: SortType,
) {
    CascadeDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.semantics { testTagsAsResourceId = true },
    ) {
        getSupportedEntries<SortType>(API.version).filter { !isTopSort(it) }.forEach {
            DropdownMenuItem(
                text = { Text(stringResource(it.data.longForm)) },
                leadingIcon = { Icon(it.data.icon, contentDescription = null) },
                onClick = { onClickSortType(it) },
                modifier =
                    Modifier.ifDo(selectedSortType == it) {
                        this.background(MaterialTheme.colorScheme.onBackground.copy(alpha = .1f))
                    },
            )
        }

        DropdownMenuItem(
            text = { Text(stringResource(R.string.dialogs_top)) },
            leadingIcon = { Icon(Icons.Outlined.BarChart, contentDescription = null) },
            modifier =
                if (isTopSort(selectedSortType)) {
                    Modifier.background(MaterialTheme.colorScheme.onBackground.copy(alpha = .1f))
                } else {
                    Modifier
                },
            children = {
                getSupportedEntries<SortType>(API.version).filter(isTopSort).forEach {
                    DropdownMenuItem(
                        text = { Text(stringResource(it.data.longForm)) },
                        onClick = {
                            onDismissRequest()
                            onClickSortType(it)
                        },
                        modifier =
                            Modifier.ifDo(selectedSortType == it) {
                                this.background(MaterialTheme.colorScheme.onBackground.copy(alpha = .1f))
                            },
                    )
                }
            },
        )
    }
}

@Composable
fun CommentSortOptionsDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onClickSortType: (CommentSortType) -> Unit,
    selectedSortType: CommentSortType,
) {
    CascadeDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        getSupportedEntries<CommentSortType>(API.version).forEach {
            DropdownMenuItem(
                text = { Text(stringResource(it.data.text)) },
                leadingIcon = { Icon(imageVector = it.data.icon, contentDescription = null) },
                onClick = {
                    onDismissRequest()
                    onClickSortType(it)
                },
                modifier =
                    Modifier.ifDo(selectedSortType == it) {
                        this.background(MaterialTheme.colorScheme.onBackground.copy(alpha = .1f))
                    },
            )
        }
    }
}

@Composable
fun MenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    icon: ImageVector? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                style = textStyle,
                modifier = textModifier,
            )
        },
        leadingIcon = {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                )
            }
        },
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
fun MenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    icon: @Composable (() -> Unit),
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                style = textStyle,
                modifier = textModifier,
            )
        },
        leadingIcon = icon,
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
fun MenuItem(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    highlight: Boolean,
) {
    MenuItem(
        text = text,
        icon = icon,
        onClick = onClick,
        modifier =
            if (highlight) {
                modifier.background(MaterialTheme.colorScheme.onBackground.copy(alpha = .1f))
            } else {
                modifier
            },
    )
}

@Composable
fun PopupMenuItem(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                style = textStyle,
                modifier = textModifier.padding(start = LARGE_PADDING),
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = text,
            )
        },
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
fun CascadeColumnScope.PopupMenuItem(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    children: @Composable CascadeColumnScope.() -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                style = textStyle,
                modifier = textModifier.padding(start = LARGE_PADDING),
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = text,
            )
        },
        children = children,
        modifier = modifier,
    )
}

@Composable
fun CenteredPopupMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    tonalElevation: Dp = 3.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (expanded) {
        Popup(
            alignment = Alignment.Center,
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(focusable = true),
        ) {
            Surface(
                shape = Shapes.extraSmall,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = tonalElevation,
                shadowElevation = 6.dp,
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth(POPUP_MENU_WIDTH_RATIO)
                            .padding(vertical = LARGE_PADDING),
                    content = content,
                )
            }
        }
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
