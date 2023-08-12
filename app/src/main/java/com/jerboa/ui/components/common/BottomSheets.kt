package com.jerboa.ui.components.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.BrightnessLow
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Moving
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.jerboa.R
import com.jerboa.datatypes.types.CommentSortType
import com.jerboa.datatypes.types.SortType
import com.jerboa.getLocalizedSortingTypeLongName
import com.jerboa.ui.theme.LARGE_PADDING

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CommentSortOptionsModalBottomSheet(
    onDismissRequest: () -> Unit,
    onClickSortType: (CommentSortType) -> Unit,
    selectedSortType: CommentSortType,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
        Column {
            IconAndTextDrawerItem(
                text = stringResource(R.string.dialogs_hot),
                icon = Icons.Outlined.LocalFireDepartment,
                onClick = { onClickSortType(CommentSortType.Hot) },
                highlight = (selectedSortType == CommentSortType.Hot),
            )
            IconAndTextDrawerItem(
                text = stringResource(R.string.dialogs_top),
                icon = Icons.Outlined.BarChart,
                onClick = { onClickSortType(CommentSortType.Top) },
                highlight = (selectedSortType == CommentSortType.Top),
            )
            IconAndTextDrawerItem(
                text = stringResource(R.string.dialogs_new),
                icon = Icons.Outlined.NewReleases,
                onClick = { onClickSortType(CommentSortType.New) },
                highlight = (selectedSortType == CommentSortType.New),
            )
            IconAndTextDrawerItem(
                text = stringResource(R.string.dialogs_old),
                icon = Icons.Outlined.History,
                onClick = { onClickSortType(CommentSortType.Old) },
                highlight = (selectedSortType == CommentSortType.Old),
            )
            Spacer(modifier = Modifier.height(LARGE_PADDING))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortOptionsModalBottomSheet(
    onDismissRequest: () -> Unit,
    onClickSortType: (SortType) -> Unit,
    selectedSortType: SortType,
) {
    val ctx = LocalContext.current

    var showTopOptions by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
        Column {
            if (!showTopOptions) {
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_active),
                    icon = Icons.Outlined.Moving,
                    onClick = { onClickSortType(SortType.Active) },
                    highlight = (selectedSortType == SortType.Active),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_hot),
                    icon = Icons.Outlined.LocalFireDepartment,
                    onClick = { onClickSortType(SortType.Hot) },
                    highlight = (selectedSortType == SortType.Hot),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_new),
                    icon = Icons.Outlined.BrightnessLow,
                    onClick = { onClickSortType(SortType.New) },
                    highlight = (selectedSortType == SortType.New),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_old),
                    icon = Icons.Outlined.History,
                    onClick = { onClickSortType(SortType.Old) },
                    highlight = (selectedSortType == SortType.Old),
                )
                IconAndTextDrawerItem(
                    modifier = Modifier.testTag("jerboa:sortoption_mostcomments"),
                    text = stringResource(R.string.dialogs_most_comments),
                    icon = Icons.Outlined.FormatListNumbered,
                    onClick = { onClickSortType(SortType.MostComments) },
                    highlight = (selectedSortType == SortType.MostComments),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_new_comments),
                    icon = Icons.Outlined.NewReleases,
                    onClick = { onClickSortType(SortType.NewComments) },
                    highlight = (selectedSortType == SortType.NewComments),
                )
                IconAndTextDrawerItem(
                    text = stringResource(R.string.dialogs_top),
                    icon = Icons.Outlined.BarChart,
                    onClick = { showTopOptions = !showTopOptions },
                    more = true,
                    highlight = (topSortTypes.contains(selectedSortType)),
                )
            } else {
                topSortTypes.forEach {
                    IconAndTextDrawerItem(
                        text = getLocalizedSortingTypeLongName(ctx, it),
                        onClick = { onClickSortType(it) },
                        highlight = (selectedSortType == it),
                    )
                }
            }
            Spacer(modifier = Modifier.height(LARGE_PADDING))
        }
    }
}
