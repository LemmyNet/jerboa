package com.jerboa.ui.components.post.create

import android.net.Uri
import android.webkit.URLUtil
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.MAX_POST_TITLE_LENGTH
import com.jerboa.PickImage
import com.jerboa.datatypes.CommunitySafe
import com.jerboa.datatypes.sampleCommunitySafe
import com.jerboa.ui.components.common.CircularIcon
import com.jerboa.ui.theme.ICON_SIZE
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.THUMBNAIL_SIZE

@Composable
fun CreatePostHeader(
    navController: NavController = rememberNavController(),
    onCreatePostClick: () -> Unit = {},
    formValid: Boolean,
) {
    TopAppBar(
        title = {
            Text(
                text = "Create post",
            )
        },
        actions = {
            IconButton(
                enabled = formValid,
                onClick = onCreatePostClick,
            ) {
                // Todo add are you sure cancel dialog
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "TODO"
                )
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
    onNameChange: (name: String) -> Unit = {},
    body: String,
    onBodyChange: (body: String) -> Unit = {},
    url: String,
    onUrlChange: (url: String) -> Unit = {},
    onPickedImage: (image: Uri) -> Unit = {},
    community: CommunitySafe? = null,
    navController: NavController = rememberNavController(),
    formValid: (valid: Boolean) -> Unit = {},
) {

    val ctx = LocalContext.current
    val nameField = validatePostName(name)
    val urlField = validateUrl(url)

    formValid(
        !nameField.hasError &&
            !urlField.hasError &&
            (community !== null)
    )

    Column(
        modifier = Modifier
            .padding(MEDIUM_PADDING)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
    ) {
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
        PickImage(
            onPickedImage = onPickedImage,
        )
        OutlinedTextField(
            label = {
                Text("Body")
            },
            value = body,
            onValueChange = onBodyChange,
            modifier = Modifier
                .fillMaxWidth(),
        )
        Box {
            community?.also {

                OutlinedTextField(
                    value = community.name,
                    readOnly = true,
                    onValueChange = {},
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
                    onValueChange = {},
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

data class InputField(
    val label: String,
    val hasError: Boolean,
)

private fun validatePostName(
    name: String,
): InputField {
    return if (name.isEmpty()) {
        InputField(
            label = "Title required",
            hasError = true
        )
    } else if (name.length < 3) {
        InputField(
            label = "Title must be > 3 chars",
            hasError = true
        )
    } else if (name.length >= MAX_POST_TITLE_LENGTH) {
        InputField(
            label = "Title cannot be > 200 chars",
            hasError = true
        )
    } else {
        InputField(
            label = "Title",
            hasError = false
        )
    }
}

private fun validateUrl(
    url: String,
): InputField {
    return if (url.isNotEmpty() && !URLUtil.isValidUrl(url)) {
        InputField(
            label = "Invalid Url",
            hasError = true,
        )
    } else {
        InputField(
            label = "Url",
            hasError = false,
        )
    }
}

@Preview
@Composable
fun CreatePostBodyPreview() {
    CreatePostBody(name = "", body = "", url = "", community = sampleCommunitySafe)
}

@Preview
@Composable
fun CreatePostBodyPreviewNoCommunity() {
    CreatePostBody(name = "", body = "", url = "")
}
