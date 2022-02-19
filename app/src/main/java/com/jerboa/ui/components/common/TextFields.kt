package com.jerboa.ui.components.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.sp
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun ReplyTextField(
    reply: String,
    onReplyChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

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
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )

    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        onDispose { }
    }
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
