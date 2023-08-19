package com.jerboa.ui.components.common

import android.content.ActivityNotFoundException
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.OpenInFull
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.jerboa.JerboaAppState
import com.jerboa.PostType
import com.jerboa.R
import com.jerboa.feat.shareLink
import com.jerboa.feat.shareMedia
import com.jerboa.feat.storeMedia
import com.jerboa.isMedia
import com.jerboa.rememberJerboaAppState
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.Shapes

@Composable
fun LinkDropDownMenu(
    link: String?,
    onDismissRequest: () -> Unit,
    appState: JerboaAppState,
    useCustomTabs: Boolean,
    usePrivateTabs: Boolean,
) {
    val localClipboardManager = LocalClipboardManager.current
    val ctx = LocalContext.current

    if (link != null) {
        val mediaType = PostType.fromURL(link)

        Popup(
            alignment = Alignment.Center,
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(focusable = true),
        ) {
            Surface(
                shape = Shapes.extraSmall,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 6.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.86f)
                        .padding(vertical = LARGE_PADDING),
                ) {
                    Text(
                        text = link,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(LARGE_PADDING),
                        color = MaterialTheme.colorScheme.tertiary,
                    )

                    MenuItem(
                        text = stringResource(R.string.open_link),
                        icon = Icons.Outlined.OpenInFull,
                        onClick = {
                            if (mediaType == PostType.Image) {
                                appState.openImageViewer(link)
                            } else {
                                appState.openLink(link, useCustomTabs, usePrivateTabs)
                            }
                            onDismissRequest()
                        },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        textModifier = Modifier.padding(start = LARGE_PADDING),
                    )
                    MenuItem(
                        text = stringResource(R.string.open_link_external),
                        icon = Icons.Outlined.OpenInBrowser,
                        onClick = {
                            appState.openLinkRaw(link, useCustomTabs, usePrivateTabs)

                            try {
                            } catch (e: ActivityNotFoundException) {
                                Log.d("jerboa", "failed open activity", e)
                                Toast.makeText(ctx, ctx.getText(R.string.no_activity_found), Toast.LENGTH_SHORT).show()
                            }

                            onDismissRequest()
                        },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        textModifier = Modifier.padding(start = LARGE_PADDING),
                    )
                    Divider()

                    MenuItem(
                        text = stringResource(R.string.post_listing_copy_link),
                        icon = Icons.Outlined.Link,
                        onClick = {
                            localClipboardManager.setText(AnnotatedString(link))
                            Toast.makeText(
                                ctx,
                                ctx.getString(R.string.post_listing_link_copied),
                                Toast.LENGTH_SHORT,
                            ).show()
                            onDismissRequest()
                        },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        textModifier = Modifier.padding(start = LARGE_PADDING),
                    )

                    MenuItem(
                        text = stringResource(R.string.post_listing_share_link),
                        icon = Icons.Outlined.Share,
                        onClick = {
                            shareLink(link, ctx)
                            onDismissRequest()
                        },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        textModifier = Modifier.padding(start = LARGE_PADDING),
                    )

                    when (mediaType) {
                        PostType.Image -> {
                            Divider()
                            MenuItem(
                                text = stringResource(R.string.share_image),
                                icon = Icons.Outlined.Share,
                                onClick = {
                                    shareMedia(appState.coroutineScope, ctx, link, mediaType)
                                    onDismissRequest()
                                },
                                textStyle = MaterialTheme.typography.bodyLarge,
                                textModifier = Modifier.padding(start = LARGE_PADDING),
                            )
                            MenuItem(
                                text = stringResource(R.string.save_image),
                                icon = Icons.Outlined.Download,
                                onClick = {
                                    storeMedia(appState.coroutineScope, ctx, link, mediaType)
                                    onDismissRequest()
                                },
                                textStyle = MaterialTheme.typography.bodyLarge,
                                textModifier = Modifier.padding(start = LARGE_PADDING),
                            )
                        }

                        PostType.Video -> {
                            Divider()
                            MenuItem(
                                text = stringResource(R.string.share_video),
                                icon = Icons.Outlined.Share,
                                onClick = {
                                    shareMedia(appState.coroutineScope, ctx, link, mediaType)
                                    onDismissRequest()
                                },
                                textStyle = MaterialTheme.typography.bodyLarge,
                                textModifier = Modifier.padding(start = LARGE_PADDING),
                            )
                            MenuItem(
                                text = stringResource(R.string.save_video),
                                icon = Icons.Outlined.Download,
                                onClick = {
                                    storeMedia(appState.coroutineScope, ctx, link, mediaType)
                                    onDismissRequest()
                                },
                                textStyle = MaterialTheme.typography.bodyLarge,
                                textModifier = Modifier.padding(start = LARGE_PADDING),
                            )
                        }

                        PostType.Link -> {
                            if (isMedia(link)) {
                                Divider()
                                MenuItem(
                                    text = stringResource(R.string.share),
                                    icon = Icons.Outlined.Share,
                                    onClick = {
                                        shareMedia(appState.coroutineScope, ctx, link, PostType.Link)
                                        onDismissRequest()
                                    },
                                    textStyle = MaterialTheme.typography.bodyLarge,
                                    textModifier = Modifier.padding(start = LARGE_PADDING),
                                )
                                MenuItem(
                                    text = stringResource(R.string.save),
                                    icon = Icons.Outlined.Download,
                                    onClick = {
                                        storeMedia(appState.coroutineScope, ctx, link, PostType.Link)
                                        onDismissRequest()
                                    },
                                    textStyle = MaterialTheme.typography.bodyLarge,
                                    textModifier = Modifier.padding(start = LARGE_PADDING),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun Test() {
    LinkDropDownMenu(
        "linkDropdownExpanded.value",
        {},
        rememberJerboaAppState(),
        useCustomTabs = false,
        usePrivateTabs = false,
    )
}
