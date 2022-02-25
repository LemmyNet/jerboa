package com.jerboa.ui.components.common

import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.sp
import com.jerboa.api.uploadPictrsImage
import com.jerboa.appendMarkdownImage
import com.jerboa.db.Account
import com.jerboa.imageInputStreamFromUri
import com.jerboa.ui.theme.MEDIUM_PADDING
import com.jerboa.ui.theme.Muted
import com.jerboa.ui.theme.XXL_PADDING
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch

@Composable
fun MarkdownTextField(
    reply: TextFieldValue,
    onReplyChange: (TextFieldValue) -> Unit,
    account: Account?,
) {
    val focusRequester = remember { FocusRequester() }
    val imageUploading = rememberSaveable { mutableStateOf(false) }
    val launcher = imageUploadLauncher(account, onReplyChange, reply, imageUploading)

    var showCreateLink by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }

    if (showCreateLink) {
        CreateLinkDialog(
            value = reply,
            onDismissRequest = { showCreateLink = false },
            onClickOk = {
                showCreateLink = false
                onReplyChange(it)
            },
        )
    }

    if (showPreview) {
        ShowPreviewDialog(
            content = reply.text,
            onDismissRequest = { showPreview = false },
        )
    }

    Column {
        TextField(
            value = reply,
            onValueChange = onReplyChange,
            placeholder = { Text(text = "Type your comment") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
                autoCorrect = true,
            ),
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colors.onSurface,
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
        MarkdownHelperBar(
            imageUploading = imageUploading.value,
            onBoldClick = {
                simpleMarkdownSurround(
                    "**",
                    value = reply,
                    onValueChange = onReplyChange
                )
            },
            onItalicsClick = {
                simpleMarkdownSurround(
                    "*",
                    value = reply,
                    onValueChange = onReplyChange
                )
            },
            onQuoteClick = {
                simpleMarkdownSurround(
                    "> ",
                    value = reply,
                    onValueChange = onReplyChange,
                    surround = false,
                )
            },
            onHeaderClick = {
                simpleMarkdownSurround(
                    "# ",
                    value = reply,
                    onValueChange = onReplyChange,
                    surround = false,
                )
            },
            onCodeClick = {
                simpleMarkdownSurround(
                    "`",
                    value = reply,
                    onValueChange = onReplyChange
                )
            },
            onStrikethroughClick = {
                simpleMarkdownSurround(
                    "~~",
                    value = reply,
                    onValueChange = onReplyChange
                )
            },
            onSubscriptClick = {
                simpleMarkdownSurround(
                    "~",
                    value = reply,
                    onValueChange = onReplyChange
                )
            },
            onSuperscriptClick = {
                simpleMarkdownSurround(
                    "^",
                    value = reply,
                    onValueChange = onReplyChange
                )
            },
            onListClick = {
                simpleMarkdownSurround(
                    "- ",
                    value = reply,
                    onValueChange = onReplyChange,
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
            }
        )
    }

    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        onDispose { }
    }
}

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
                verticalArrangement = Arrangement.spacedBy(MEDIUM_PADDING)
            ) {
                Text(
                    text = "Insert link",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface,
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = {
                        Text(text = "Text")
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = link,
                    onValueChange = { link = it },
                    label = {
                        Text(text = "Link")
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        buttons = {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(horizontal = XXL_PADDING).fillMaxWidth(),
            ) {
                TextButton(
                    onClick = onDismissRequest,
                ) {
                    Text(
                        text = "Cancel",
                        color = Muted,
                    )
                }
                TextButton(
                    onClick = {
                        val replacement = "[$text]($link)"
                        val out = value.text.replaceRange(
                            value.selection.start,
                            value.selection.end,
                            replacement
                        )
                        val end = value.selection.start + replacement.length
                        val cursor = TextRange(end, end)
                        onClickOk(TextFieldValue(out, cursor))
                    },
                ) {
                    Text(
                        text = "OK",
                    )
                }
            }
        }
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
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                MyMarkdownText(
                    markdown = content,
                )
            }
        },
        buttons = {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(horizontal = XXL_PADDING).fillMaxWidth(),
            ) {
                TextButton(
                    onClick = onDismissRequest,
                ) {
                    Text(
                        text = "OK",
                        color = Muted,
                    )
                }
            }
        }
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
    onReplyChange: (TextFieldValue) -> Unit,
    reply: TextFieldValue,
    imageUploading: MutableState<Boolean>,
): ManagedActivityResultLauncher<String, Uri?> {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // Some image upload reqs
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
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
                        onReplyChange(TextFieldValue(appendMarkdownImage(reply.text, it)))
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
    imageUploading: Boolean
) {

    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        IconButton(
            onClick = onPreviewClick,
        ) {
            Icon(
                imageVector = Icons.Default.Preview,
                contentDescription = "TODO",
                tint = Muted,
            )
        }
        IconButton(
            onClick = onLinkClick,
        ) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = "TODO",
                tint = Muted,
            )
        }
        IconButton(
            onClick = onImageClick,
            enabled = !imageUploading
        ) {
            if (imageUploading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.onSurface
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "TODO",
                    tint = Muted,
                )
            }
        }
        IconButton(
            onClick = onBoldClick,
        ) {
            Icon(
                imageVector = Icons.Default.FormatBold,
                contentDescription = "TODO",
                tint = Muted,
            )
        }
        IconButton(
            onClick = onItalicsClick,
        ) {
            Icon(
                imageVector = Icons.Default.FormatItalic,
                contentDescription = "TODO",
                tint = Muted,
            )
        }
        IconButton(
            onClick = onQuoteClick,
        ) {
            Icon(
                imageVector = Icons.Default.FormatQuote,
                contentDescription = "TODO",
                tint = Muted,
            )
        }
        IconButton(
            onClick = onListClick,
        ) {
            Icon(
                imageVector = Icons.Default.FormatListBulleted,
                contentDescription = "TODO",
                tint = Muted,
            )
        }
        IconButton(
            onClick = onHeaderClick,
        ) {
            Icon(
                imageVector = Icons.Default.Title,
                contentDescription = "TODO",
                tint = Muted,
            )
        }
        IconButton(
            onClick = onCodeClick,
        ) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = "TODO",
                tint = Muted,
            )
        }
        IconButton(
            onClick = onStrikethroughClick,
        ) {
            Icon(
                imageVector = Icons.Default.FormatStrikethrough,
                contentDescription = "TODO",
                tint = Muted,
            )
        }
        IconButton(
            onClick = onSubscriptClick,
        ) {
            Icon(
                imageVector = Icons.Default.Subscript,
                contentDescription = "TODO",
                tint = Muted,
            )
        }
        IconButton(
            onClick = onSuperscriptClick,
        ) {
            Icon(
                imageVector = Icons.Default.Superscript,
                contentDescription = "TODO",
                tint = Muted,
            )
        }
    }
}

@Preview
@Composable
fun ReplyMarkdownBarPreview() {
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
        maxLines = 5,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier,
        fontSize = 14.sp,
    )
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun MyMarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface,
) {

//    val fontSize = TextUnit(MaterialTheme.typography.body1.fontSize.value, type = TextUnitType.Sp)

    // Note, this actually scales down the font size quite a lot, so you need to use a bigger one
    MarkdownText(
        markdown = markdown,
        style = MaterialTheme.typography.body1,
        fontSize = 18.sp,
        modifier = modifier,
        color = color,
    )
}

fun String.insert(index: Int, string: String): String {
    return this.substring(0, index) + string + this.substring(index, this.length)
}
