
package com.jerboa.ui.components.post.edit

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.R
import com.jerboa.db.Account
import com.jerboa.ui.components.common.MarkdownTextField
import com.jerboa.ui.components.common.PickImage
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.validatePostName
import com.jerboa.validateUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostHeader(
    navController: NavController = rememberNavController(),
    onEditPostClick: () -> Unit,
    formValid: Boolean,
    loading: Boolean,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.post_edit_edit_post),
            )
        },
        actions = {
            IconButton(
                enabled = formValid && !loading,
                onClick = onEditPostClick,
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                } else {
                    // Todo add are you sure cancel dialog
                    Icon(
                        Icons.Outlined.Save,
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
                    contentDescription = stringResource(R.string.post_edit_close),
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostBody(
    name: String,
    onNameChange: (name: String) -> Unit,
    body: TextFieldValue,
    onBodyChange: (body: TextFieldValue) -> Unit,
    url: String,
    onUrlChange: (url: String) -> Unit,
    onPickedImage: (image: Uri) -> Unit,
    formValid: (valid: Boolean) -> Unit,
    account: Account?,
    modifier: Modifier = Modifier,
) {
    val nameField = validatePostName(name)
    val urlField = validateUrl(url)

    val scrollState = rememberScrollState()

    formValid(
        !nameField.hasError &&
            !urlField.hasError,
    )

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .imePadding()
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
        MarkdownTextField(
            text = body,
            onTextChange = onBodyChange,
            modifier = Modifier
                .fillMaxWidth(),
            outlined = true,
            account = account,
            focusImmediate = false,
            placeholder = stringResource(R.string.post_edit_body_placeholder),
        )
    }
}

@Preview
@Composable
fun EditPostBodyPreview() {
    EditPostBody(
        name = "",
        body = TextFieldValue(""),
        url = "",
        formValid = {},
        onBodyChange = {},
        onNameChange = {},
        onPickedImage = {},
        onUrlChange = {},
        account = null,
    )
}
