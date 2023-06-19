package com.jerboa.ui.components.post.create

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.R
import com.jerboa.datatypes.sampleCommunity
import com.jerboa.datatypes.types.Community
import com.jerboa.db.Account
import com.jerboa.ui.components.common.CircularIcon
import com.jerboa.ui.components.common.MarkdownTextField
import com.jerboa.ui.components.common.PickImage
import com.jerboa.ui.theme.ICON_SIZE
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.THUMBNAIL_SIZE
import com.jerboa.ui.theme.muted
import com.jerboa.validatePostName
import com.jerboa.validateUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostHeader(
    navController: NavController = rememberNavController(),
    onCreatePostClick: () -> Unit,
    formValid: Boolean,
    loading: Boolean,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.create_post_create_post),
            )
        },
        actions = {
            IconButton(
                enabled = formValid && !loading,
                onClick = onCreatePostClick,
            ) {
                if (loading) {
                    // TODO is this color necessary? If not, remove all of them
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                } else {
                    // Todo add are you sure cancel dialog+
                    Icon(
                        if (formValid) {
                            Icons.Filled.Send
                        } else {
                            Icons.Outlined.Send
                        },
                        contentDescription = stringResource(R.string.form_submit),
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
            ) {
                // Todo add are you sure cancel dialog
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.create_post_close),
                )
            }
        },
    )
}

@Composable
fun CreatePostBody(
    name: String,
    onNameChange: (name: String) -> Unit,
    body: TextFieldValue,
    onBodyChange: (body: TextFieldValue) -> Unit,
    url: String,
    onUrlChange: (url: String) -> Unit,
    onPickedImage: (image: Uri) -> Unit,
    image: Uri? = null,
    community: Community? = null,
    navController: NavController = rememberNavController(),
    formValid: (valid: Boolean) -> Unit,
    account: Account?,
    padding: PaddingValues,
    suggestedTitle: String?,
    suggestedTitleLoading: Boolean,
) {
    val nameField = validatePostName(name)
    val urlField = validateUrl(url)

    formValid(
        !nameField.hasError &&
            !urlField.hasError &&
            (community !== null),
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = padding.calculateTopPadding(), horizontal = MEDIUM_PADDING)
            .verticalScroll(scrollState)
            .imePadding(),
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
        if (suggestedTitleLoading) {
            CircularProgressIndicator()
        } else {
            suggestedTitle?.let {
                Text(
                    text = stringResource(R.string.create_post_copy_suggested_title, it),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.muted,
                    modifier = Modifier.clickable { onNameChange(it) },
                )
            }
        }
        PickImage(
            onPickedImage = onPickedImage,
            image = image,
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
        )
        MarkdownTextField(
            text = body,
            onTextChange = onBodyChange,
            modifier = Modifier.fillMaxWidth(),
            outlined = true,
            account = account,
            focusImmediate = false,
            placeholder = "Body",
        )
        Box {
            community?.also {
                OutlinedTextField(
                    value = community.name,
                    readOnly = true,
                    onValueChange = {}, // TODO what?
                    label = {
                        Text(stringResource(R.string.create_post_community))
                    },
                    leadingIcon = {
                        community.icon?.let {
                            CircularIcon(
                                icon = it,
                                contentDescription = stringResource(R.string.community_icon),
                                size = ICON_SIZE,
                                thumbnailSize = THUMBNAIL_SIZE,
                            )
                        }
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.ArrowDropDown,
                            contentDescription = stringResource(R.string.createPost_selectCommunity),
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            } ?: run {
                OutlinedTextField(
                    value = "",
                    onValueChange = {}, // TODO what?
                    label = {
                        Text(stringResource(R.string.create_post_community))
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }
            // A box to draw over the textview and override clicks
            Box(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("communityList?select=true")
                    },
            )
        }
    }
}

@Preview
@Composable
fun CreatePostHeaderPreview() {
    CreatePostHeader(
        onCreatePostClick = {},
        formValid = true,
        loading = false,
    )
}

@Preview
@Composable
fun CreatePostBodyPreview() {
    CreatePostBody(
        name = "",
        onNameChange = {},
        body = TextFieldValue(""),
        onBodyChange = {},
        url = "",
        onUrlChange = {},
        onPickedImage = {},
        community = sampleCommunity,
        formValid = {},
        account = null,
        padding = PaddingValues(),
        suggestedTitle = null,
        suggestedTitleLoading = false,
    )
}

@Preview
@Composable
fun CreatePostBodyPreviewNoCommunity() {
    CreatePostBody(
        name = "",
        onNameChange = {},
        body = TextFieldValue(""),
        onBodyChange = {},
        url = "",
        onUrlChange = {},
        onPickedImage = {},
        formValid = {},
        suggestedTitle = stringResource(R.string.create_post_a_title_here),
        suggestedTitleLoading = false,
        account = null,
        padding = PaddingValues(),
    )
}
