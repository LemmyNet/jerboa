package com.jerboa.ui.components.person

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.datatypes.PersonViewSafe
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.samplePersonView
import com.jerboa.personNameShown
import com.jerboa.ui.components.common.DotSpacer
import com.jerboa.ui.components.common.IconAndTextDrawerItem
import com.jerboa.ui.components.common.LargerCircularIcon
import com.jerboa.ui.components.common.MyMarkdownText
import com.jerboa.ui.components.common.PictrsBannerImage
import com.jerboa.ui.components.common.SortOptionsDialog
import com.jerboa.ui.components.common.SortTopOptionsDialog
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.theme.APP_BAR_ELEVATION
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.PROFILE_BANNER_SIZE
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.muted

@Composable
fun PersonProfileTopSection(
    personView: PersonViewSafe,
    modifier: Modifier = Modifier
) {
    Column {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomStart
        ) {
            personView.person.banner?.also {
                PictrsBannerImage(
                    url = it,
                    modifier = Modifier.height(PROFILE_BANNER_SIZE)
                )
            }
            Box(modifier = Modifier.padding(MEDIUM_PADDING)) {
                personView.person.avatar?.also {
                    LargerCircularIcon(icon = it)
                }
            }
        }
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
                Text(
                    text = personNameShown(personView.person, true),
                    style = MaterialTheme.typography.h6
                )

                TimeAgo(
                    precedingString = "Joined",
                    includeAgo = true,
                    published = personView.person.published
                )
                CommentsAndPosts(personView)
                personView.person.bio?.also {
                    MyMarkdownText(
                        markdown = it,
                        color = MaterialTheme.colors.onBackground.muted
                    )
                }
            }
        }
    }
}

@Composable
fun CommentsAndPosts(personView: PersonViewSafe) {
    Row {
        Text(
            text = "${personView.counts.post_count} posts",
            color = MaterialTheme.colors.onBackground.muted
        )
        DotSpacer()
        Text(
            text = "${personView.counts.comment_count} comments",
            color = MaterialTheme.colors.onBackground.muted
        )
    }
}

@Preview
@Composable
fun CommentsAndPostsPreview() {
    CommentsAndPosts(personView = samplePersonView)
}

@Preview
@Composable
fun PersonProfileTopSectionPreview() {
    PersonProfileTopSection(personView = samplePersonView)
}

@Composable
fun PersonProfileHeader(
    personName: String,
    myProfile: Boolean,
    onClickSortType: (SortType) -> Unit,
    onBlockPersonClick: () -> Unit,
    selectedSortType: SortType,
    navController: NavController = rememberNavController()
) {
    var showSortOptions by remember { mutableStateOf(false) }
    var showTopOptions by remember { mutableStateOf(false) }
    var showMoreOptions by remember { mutableStateOf(false) }

    if (showSortOptions) {
        SortOptionsDialog(
            selectedSortType = selectedSortType,
            onDismissRequest = { showSortOptions = false },
            onClickSortType = {
                showSortOptions = false
                onClickSortType(it)
            },
            onClickSortTopOptions = {
                showSortOptions = false
                showTopOptions = !showTopOptions
            }
        )
    }

    if (showTopOptions) {
        SortTopOptionsDialog(
            selectedSortType = selectedSortType,
            onDismissRequest = { showTopOptions = false },
            onClickSortType = {
                showTopOptions = false
                onClickSortType(it)
            }
        )
    }

    if (showMoreOptions) {
        PersonProfileMoreDialog(
            onDismissRequest = { showMoreOptions = false },
            onBlockPersonClick = {
                showMoreOptions = false
                onBlockPersonClick()
            }
        )
    }

    val backgroundColor = MaterialTheme.colors.primarySurface
    val contentColor = contentColorFor(backgroundColor)

    TopAppBar(
        title = {
            PersonProfileHeaderTitle(
                personName = personName,
                selectedSortType = selectedSortType
            )
        },
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = APP_BAR_ELEVATION,
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = {
                showSortOptions = !showSortOptions
            }) {
                Icon(
                    Icons.Default.Sort,
                    contentDescription = "TODO",
                    tint = contentColor
                )
            }
            if (!myProfile) {
                IconButton(onClick = {
                    showMoreOptions = !showMoreOptions
                }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "TODO",
                        tint = contentColor
                    )
                }
            }
        }
    )
}

@Composable
fun PersonProfileHeaderTitle(
    personName: String,
    selectedSortType: SortType
) {
    Column {
        Text(
            text = personName,
            style = MaterialTheme.typography.h6
        )
        Text(
            text = selectedSortType.toString(),
            style = MaterialTheme.typography.subtitle1,
            color = contentColorFor(MaterialTheme.colors.primarySurface).muted
        )
    }
}

@Composable
fun PersonProfileMoreDialog(
    onDismissRequest: () -> Unit,
    onBlockPersonClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "Block Person",
                    icon = Icons.Default.Block,
                    onClick = onBlockPersonClick
                )
            }
        },
        buttons = {}
    )
}
