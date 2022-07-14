package com.jerboa.ui.components.post.create

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.datatypes.CommunitySafe
import com.jerboa.datatypes.sampleCommunitySafe
import com.jerboa.ui.components.common.CircularIcon
import com.jerboa.ui.components.common.PickImage
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.theme.*
import com.jerboa.validatePostName
import com.jerboa.validateUrl

@Composable
fun CreatePostHeader(
    navController: NavController = rememberNavController(),
    onCreatePostClick: () -> Unit,
    formValid: Boolean,
    loading: Boolean,
) {
    val backgroundColor = MaterialTheme.colors.primarySurface
    val contentColor = contentColorFor(backgroundColor)
    TopAppBar(
        title = {
            Text(
                text = "Create post",
            )
        },
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = APP_BAR_ELEVATION,
        actions = {
            IconButton(
                enabled = formValid && !loading,
                onClick = onCreatePostClick,
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.onSurface
                    )
                } else {
                    // Todo add are you sure cancel dialog
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "TODO"
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                // Todo add are you sure cancel dialog
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close"
                )
            }
        },
    )
}

@Composable
fun CreatePostBody(
    name: String,
    onNameChange: (name: String) -> Unit,
    body: String,
    onBodyChange: (body: String) -> Unit,
    url: String,
    onUrlChange: (url: String) -> Unit,
    onPickedImage: (image: Uri) -> Unit,
    image: Uri? = null,
    community: CommunitySafe? = null,
    navController: NavController = rememberNavController(),
    formValid: (valid: Boolean) -> Unit,
    suggestedTitle: String? = null,
) {

    val nameField = validatePostName(name)
    val urlField = validateUrl(url)

    formValid(
        !nameField.hasError &&
            !urlField.hasError &&
            (community !== null)
    )

    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(MEDIUM_PADDING)
            .fillMaxWidth()
            .simpleVerticalScrollbar(listState),
        verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
    ) {
        item {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                isError = nameField.hasError,
                label = {
                    Text(text = nameField.label)
                },
                modifier = Modifier
                    .fillMaxWidth(),
            )
        }
        item {
            OutlinedTextField(
                label = {
                    Text(text = urlField.label)
                },
                value = url,
                isError = urlField.hasError,
                onValueChange = onUrlChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier
                    .fillMaxWidth(),
            )
        }
        item {
            suggestedTitle?.also {
                Text(
                    text = "copy suggested title: $it",
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onBackground.muted,
                    modifier = Modifier.clickable { onNameChange(it) }
                )
            }
        }
        item {
            PickImage(
                onPickedImage = onPickedImage,
                image = image,
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            )
        }
        // TODO change this to reply text field at some point
        item {
            OutlinedTextField(
                label = {
                    Text("Body")
                },
                value = body,
                onValueChange = onBodyChange,
                modifier = Modifier
                    .fillMaxWidth(),
            )
        }
        item {
            Box {
                community?.also {

                    OutlinedTextField(
                        value = community.name,
                        readOnly = true,
                        onValueChange = {}, // TODO what?
                        label = {
                            Text("Community")
                        },
                        leadingIcon = {
                            community.icon?.let {
                                CircularIcon(
                                    icon = it,
                                    size = ICON_SIZE,
                                    thumbnailSize = THUMBNAIL_SIZE
                                )
                            }
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "TODO"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                } ?: run {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {}, // TODO what?
                        label = {
                            Text("Community")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                // A box to draw over the textview and override clicks
                Box(
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("communityList?select=true")
                        }
                )
            }
        }
    }
}

@Preview
@Composable
fun CreatePostBodyPreview() {
    CreatePostBody(
        name = "",
        body = "",
        url = "",
        community = sampleCommunitySafe,
        formValid = {},
        onPickedImage = {},
        onUrlChange = {},
        onBodyChange = {},
        onNameChange = {},
    )
}

@Preview
@Composable
fun CreatePostBodyPreviewNoCommunity() {
    CreatePostBody(
        name = "",
        body = "",
        url = "",
        suggestedTitle = "a title here....",
        formValid = {},
        onNameChange = {},
        onBodyChange = {},
        onUrlChange = {},
        onPickedImage = {},
    )
}
