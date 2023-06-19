
package com.jerboa.ui.components.comment.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.R
import com.jerboa.db.Account
import com.jerboa.ui.components.common.MarkdownTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentEditHeader(
    navController: NavController = rememberNavController(),
    onSaveClick: () -> Unit,
    loading: Boolean,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.comment_edit_edit),
            )
        },
        actions = {
            IconButton(
                onClick = onSaveClick,
                enabled = !loading,
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Save,
                        contentDescription = stringResource(R.string.commentEdit_save),
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
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.comment_edit_back),
                )
            }
        },
    )
}

@Composable
fun CommentEdit(
    content: TextFieldValue,
    onContentChange: (TextFieldValue) -> Unit,
    account: Account?,
    padding: PaddingValues,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(padding)
            .imePadding(),
    ) {
        MarkdownTextField(
            text = content,
            onTextChange = onContentChange,
            account = account,
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(R.string.comment_edit_type_your_comment),
        )
    }
}
