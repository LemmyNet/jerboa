package com.jerboa.ui.components.person

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.datatypes.PersonViewSafe
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.samplePersonView
import com.jerboa.personNameShown
import com.jerboa.ui.components.common.*
import com.jerboa.ui.components.home.IconAndTextDrawerItem
import com.jerboa.ui.theme.*

@Composable
fun PersonProfileTopSection(
    personView: PersonViewSafe,
    modifier: Modifier = Modifier,
) {

    Column {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomStart
        ) {
            personView.person.banner?.also {
                PictrsBannerImage(
                    url = it, modifier = Modifier.height(PROFILE_BANNER_SIZE)
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
                    text = personNameShown(personView.person),
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
                        color = Muted,
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
            color = Muted,
        )
        DotSpacer()
        Text(
            text = "${personView.counts.comment_count} comments",
            color = Muted,
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
    navController: NavController = rememberNavController(),
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
            },
        )
    }

    TopAppBar(
        title = {
            PersonProfileHeaderTitle(
                personName = personName,
                selectedSortType = selectedSortType,
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
        actions = {
            IconButton(onClick = {
                showSortOptions = !showSortOptions
            }) {
                Icon(
                    Icons.Default.Sort,
                    contentDescription = "TODO",
                    tint = MaterialTheme.colors.onSurface
                )
            }
            if (!myProfile) {
                IconButton(onClick = {
                    showMoreOptions = !showMoreOptions
                }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "TODO",
                        tint = MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    )
}

@Composable
fun PersonProfileHeaderTitle(
    personName: String,
    selectedSortType: SortType,
) {
    Column {
        Text(
            text = personName,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface,
        )
        Text(
            text = selectedSortType.toString(),
            style = MaterialTheme.typography.body1,
            color = Muted,
        )
    }
}

@Composable
fun PersonProfileMoreDialog(
    onDismissRequest: () -> Unit,
    onBlockPersonClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column {
                IconAndTextDrawerItem(
                    text = "Block Person",
                    icon = Icons.Default.Block,
                    onClick = onBlockPersonClick,
                )
            }
        },
        buttons = {},
    )
}
