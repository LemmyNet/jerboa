
package com.jerboa.ui.components.common

import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.FormatStrikethrough
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Preview
import androidx.compose.material.icons.outlined.Subscript
import androidx.compose.material.icons.outlined.Superscript
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.R
import com.jerboa.api.uploadPictrsImage
import com.jerboa.appendMarkdownImage
import com.jerboa.db.Account
import com.jerboa.imageInputStreamFromUri
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.muted
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownTextField(
    text: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    account: Account?,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    focusImmediate: Boolean = true,
    outlined: Boolean = false,
) {
    val focusRequester = remember { FocusRequester() }
    val imageUploading = rememberSaveable { mutableStateOf(false) }
    val launcher = imageUploadLauncher(account, onTextChange, text, imageUploading)

    var showCreateLink by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }

    if (showCreateLink) {
        CreateLinkDialog(
            value = text,
            onDismissRequest = { showCreateLink = false },
            onClickOk = {
                showCreateLink = false
                onTextChange(it)
            },
        )
    }

    if (showPreview) {
        ShowPreviewDialog(
            content = text.text,
            onDismissRequest = { showPreview = false },
        )
    }

    Column(modifier = modifier) {
        if (outlined) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                label = { Text(text = placeholder) },
                modifier = modifier.focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    // autoCorrect = true,
                ),
            )
        } else {
            TextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text(text = placeholder) },
                modifier = modifier.focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    // autoCorrect = true,
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
        }

        MarkdownHelperBar(
            imageUploading = imageUploading.value,
            onBoldClick = {
                simpleMarkdownSurround(
                    "**",
                    value = text,
                    onValueChange = onTextChange,
                )
            },
            onItalicsClick = {
                simpleMarkdownSurround(
                    "*",
                    value = text,
                    onValueChange = onTextChange,
                )
            },
            onQuoteClick = {
                simpleMarkdownSurround(
                    "> ",
                    value = text,
                    onValueChange = onTextChange,
                    surround = false,
                )
            },
            onHeaderClick = {
                simpleMarkdownSurround(
                    "# ",
                    value = text,
                    onValueChange = onTextChange,
                    surround = false,
                )
            },
            onCodeClick = {
                simpleMarkdownSurround(
                    "`",
                    value = text,
                    onValueChange = onTextChange,
                )
            },
            onStrikethroughClick = {
                simpleMarkdownSurround(
                    "~~",
                    value = text,
                    onValueChange = onTextChange,
                )
            },
            onSubscriptClick = {
                simpleMarkdownSurround(
                    "~",
                    value = text,
                    onValueChange = onTextChange,
                )
            },
            onSuperscriptClick = {
                simpleMarkdownSurround(
                    "^",
                    value = text,
                    onValueChange = onTextChange,
                )
            },
            onListClick = {
                simpleMarkdownSurround(
                    "- ",
                    value = text,
                    onValueChange = onTextChange,
                    surround = false,
                )
            },
            onImageClick = {
                launcher.launch("image/*")
            },
            onLinkClick = {
                showCreateLink = true
            },
            onPreviewClick = {
                showPreview = true
            },
        )
    }

    DisposableEffect(Unit) {
        if (focusImmediate) {
            focusRequester.requestFocus()
        }
        onDispose { }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLinkDialog(
    value: TextFieldValue,
    onClickOk: (TextFieldValue) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val initialText = value.getSelectedText().text

    var text by rememberSaveable { mutableStateOf(initialText) }
    var link by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column(
                modifier = Modifier
                    .padding(MEDIUM_PADDING)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
            ) {
                Text(
                    text = stringResource(R.string.input_fields_insert_link),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = {
                        Text(text = stringResource(R.string.input_fields_text))
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = link,
                    onValueChange = { link = it },
                    label = {
                        Text(text = stringResource(R.string.input_fields_link))
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(
                    text = stringResource(R.string.input_fields_cancel),
                    color = MaterialTheme.colorScheme.onBackground.muted,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val replacement = "[$text]($link)"
                    val out = value.text.replaceRange(
                        value.selection.start,
                        value.selection.end,
                        replacement,
                    )
                    val end = value.selection.start + replacement.length
                    val cursor = TextRange(end, end)
                    onClickOk(TextFieldValue(out, cursor))
                },
            ) {
                Text(
                    text = stringResource(R.string.input_fields_ok),
                )
            }
        },
    )
}

@Composable
fun ShowPreviewDialog(
    content: String,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                MyMarkdownText(
                    markdown = content,
                    onClick = {},
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(
                    text = stringResource(R.string.input_fields_ok),
                    color = MaterialTheme.colorScheme.onBackground.muted,
                )
            }
        },
    )
}

@Preview
@Composable
fun CreateLinkDialogPreview() {
    CreateLinkDialog(onClickOk = {}, onDismissRequest = {}, value = TextFieldValue(""))
}

@Composable
private fun imageUploadLauncher(
    account: Account?,
    onTextChange: (TextFieldValue) -> Unit,
    text: TextFieldValue,
    imageUploading: MutableState<Boolean>,
): ManagedActivityResultLauncher<String, Uri?> {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // Some image upload reqs
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        imageUri = uri
        Log.d("jerboa", imageUri.toString())

        uri?.also { cUri ->
            imageUploading.value = true
            val imageIs = imageInputStreamFromUri(ctx, cUri)
            scope.launch {
                account?.also { acct ->
                    val url = uploadPictrsImage(acct, imageIs, ctx)
                    url?.also {
                        imageUploading.value = false
                        onTextChange(TextFieldValue(appendMarkdownImage(text.text, it)))
                    }
                }
            }
        }
    }
    return launcher
}

fun simpleMarkdownSurround(
    markdownChar: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    surround: Boolean = true,
) {
    val out = if (value.selection.start == value.selection.end) {
        var altered = value.text.insert(value.selection.start, markdownChar)
        if (surround) {
            altered = altered.insert(value.selection.start, markdownChar)
        }
        val cursor = TextRange(value.selection.start + markdownChar.length)

        TextFieldValue(altered, cursor)
    } else {
        var altered = value.text
            .insert(value.selection.start, markdownChar)
        if (surround) {
            altered = altered
                .insert(value.selection.end + markdownChar.length, markdownChar)
        }
//        Log.d("jerboa", "start = ${value.selection.start}, end = ${value.selection.end}")

        // TODO weird glitch when its the last item
        val start = value.selection.start + markdownChar.length
        val end = if (surround) {
            value.selection.end + markdownChar.length
        } else {
            value.selection.end
        }
        val cursor = if (value.selection.end == value.text.length) {
            TextRange(start)
        } else {
            TextRange(
                start,
                end,
            )
        }
        TextFieldValue(altered, cursor)
    }

    onValueChange(out)
}

@Composable
fun MarkdownHelperBar(
    onPreviewClick: () -> Unit,
    onHeaderClick: () -> Unit,
    onImageClick: () -> Unit,
    onLinkClick: () -> Unit,
    onListClick: () -> Unit,
    onQuoteClick: () -> Unit,
    onBoldClick: () -> Unit,
    onItalicsClick: () -> Unit,
    onCodeClick: () -> Unit,
    onStrikethroughClick: () -> Unit,
    onSubscriptClick: () -> Unit,
    onSuperscriptClick: () -> Unit,
    imageUploading: Boolean,
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
    ) {
        IconButton(
            onClick = onPreviewClick,
        ) {
            Icon(
                imageVector = Icons.Outlined.Preview,
                contentDescription = stringResource(R.string.markdownHelper_preview),
                tint = MaterialTheme.colorScheme.onBackground.muted,
            )
        }
        IconButton(
            onClick = onLinkClick,
        ) {
            Icon(
                imageVector = Icons.Outlined.Link,
                contentDescription = stringResource(R.string.markdownHelper_insertLink),
                tint = MaterialTheme.colorScheme.onBackground.muted,
            )
        }
        IconButton(
            onClick = onImageClick,
            enabled = !imageUploading,
        ) {
            if (imageUploading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onSurface,
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = stringResource(R.string.markdownHelper_insertImage),
                    tint = MaterialTheme.colorScheme.onBackground.muted,
                )
            }
        }
        IconButton(
            onClick = onBoldClick,
        ) {
            Icon(
                imageVector = Icons.Outlined.FormatBold,
                contentDescription = stringResource(R.string.markdownHelper_formatBold),
                tint = MaterialTheme.colorScheme.onBackground.muted,
            )
        }
        IconButton(
            onClick = onItalicsClick,
        ) {
            Icon(
                imageVector = Icons.Outlined.FormatItalic,
                contentDescription = stringResource(R.string.markdownHelper_formatItalic),
                tint = MaterialTheme.colorScheme.onBackground.muted,
            )
        }
        IconButton(
            onClick = onQuoteClick,
        ) {
            Icon(
                imageVector = Icons.Outlined.FormatQuote,
                contentDescription = stringResource(R.string.markdownHelper_insertQuote),
                tint = MaterialTheme.colorScheme.onBackground.muted,
            )
        }
        IconButton(
            onClick = onListClick,
        ) {
            Icon(
                imageVector = Icons.Outlined.FormatListBulleted,
                contentDescription = stringResource(R.string.markdownHelper_insertList),
                tint = MaterialTheme.colorScheme.onBackground.muted,
            )
        }
        IconButton(
            onClick = onHeaderClick,
        ) {
            Icon(
                imageVector = Icons.Outlined.Title,
                contentDescription = stringResource(R.string.markdownHelper_insertHeader),
                tint = MaterialTheme.colorScheme.onBackground.muted,
            )
        }
        IconButton(
            onClick = onCodeClick,
        ) {
            Icon(
                imageVector = Icons.Outlined.Code,
                contentDescription = stringResource(R.string.markdownHelper_insertCode),
                tint = MaterialTheme.colorScheme.onBackground.muted,
            )
        }
        IconButton(
            onClick = onStrikethroughClick,
        ) {
            Icon(
                imageVector = Icons.Outlined.FormatStrikethrough,
                contentDescription = stringResource(R.string.markdownHelper_formatStrikethrough),
                tint = MaterialTheme.colorScheme.onBackground.muted,
            )
        }
        IconButton(
            onClick = onSubscriptClick,
        ) {
            Icon(
                imageVector = Icons.Outlined.Subscript,
                contentDescription = stringResource(R.string.markdownHelper_formatSubscript),
                tint = MaterialTheme.colorScheme.onBackground.muted,
            )
        }
        IconButton(
            onClick = onSuperscriptClick,
        ) {
            Icon(
                imageVector = Icons.Outlined.Superscript,
                contentDescription = stringResource(R.string.markdownHelper_formatSuperscript),
                tint = MaterialTheme.colorScheme.onBackground.muted,
            )
        }
    }
}

@Preview
@Composable
fun TextMarkdownBarPreview() {
    MarkdownHelperBar(
        onHeaderClick = {},
        onPreviewClick = {},
        onImageClick = {},
        onListClick = {},
        onQuoteClick = {},
        onBoldClick = {},
        onItalicsClick = {},
        onCodeClick = {},
        onStrikethroughClick = {},
        onSubscriptClick = {},
        onSuperscriptClick = {},
        onLinkClick = {},
        imageUploading = false,
    )
}

@Composable
fun PreviewLines(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 5,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
    )
}

@Composable
fun MyMarkdownText(
    markdown: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    MarkdownHelper.CreateMarkdownView(
        markdown = markdown,
        color = color,
        onClick = onClick,
        onLongClick = onLongClick,
    )
}

fun String.insert(index: Int, string: String): String {
    return this.substring(0, index) + string + this.substring(index, this.length)
}
