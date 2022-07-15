package com.jerboa.ui.components.post.edit

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.datatypes.PostView
import com.jerboa.db.Account
import com.jerboa.ui.components.common.MarkdownTextField
import com.jerboa.ui.components.common.PickImage
import com.jerboa.ui.theme.APP_BAR_ELEVATION
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.validatePostName
import com.jerboa.validateUrl
import org.w3c.dom.Text

@Composable
fun EditPostHeader(
    navController: NavController = rememberNavController(),
    onEditPostClick: () -> Unit,
    formValid: Boolean,
    loading: Boolean,
) {
    val backgroundColor = MaterialTheme.colors.primarySurface
    val contentColor = contentColorFor(backgroundColor)

    TopAppBar(
        title = {
            Text(
                text = "Edit Post",
            )
        },
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = APP_BAR_ELEVATION,
        actions = {
            IconButton(
                enabled = formValid && !loading,
                onClick = onEditPostClick,
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.onSurface
                    )
                } else {
                    // Todo add are you sure cancel dialog
                    Icon(
                        Icons.Filled.Save,
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
fun EditPostBody(
    name: String,
    onNameChange: (name: String) -> Unit,
    body: TextFieldValue,
    onBodyChange: (body: TextFieldValue) -> Unit,
    url: String,
    onUrlChange: (url: String) -> Unit,
    onPickedImage: (image: Uri) -> Unit,
    formValid: (valid: Boolean) -> Unit,
    account: Account?
) {

    val nameField = validatePostName(name)
    val urlField = validateUrl(url)

    formValid(
        !nameField.hasError &&
            !urlField.hasError
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
        MarkdownTextField(
            text = body,
            onTextChange = onBodyChange,
            modifier = Modifier
                .fillMaxWidth(),
            outlined = true,
            account = account,
            focusImmediate = false,
            placeholder = "Body"
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
        account = null
    )
}

fun postEditClickWrapper(
    postEditViewModel: PostEditViewModel,
    postView: PostView,
    navController: NavController,
) {
    postEditViewModel.setPostView(postView)
    navController.navigate("postEdit")
}
