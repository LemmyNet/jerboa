package com.jerboa.ui.components.common

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.OpenInFull
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.JerboaAppState
import com.jerboa.PostType
import com.jerboa.R
import com.jerboa.feat.shareLink
import com.jerboa.feat.shareMedia
import com.jerboa.feat.storeMedia
import com.jerboa.isMedia
import com.jerboa.rememberJerboaAppState
import com.jerboa.ui.theme.LARGE_PADDING

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

        CenteredPopupMenu(
            expanded = true,
            onDismissRequest = onDismissRequest,
            tonalElevation = 6.dp,
        ) {
            Text(
                text = link,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(LARGE_PADDING),
                color = MaterialTheme.colorScheme.tertiary,
            )

            PopupMenuItem(
                text = stringResource(R.string.open_link),
                icon = Icons.Outlined.OpenInFull,
                onClick = {
                    onDismissRequest()
                    if (mediaType == PostType.Image) {
                        appState.openImageViewer(link)
                    } else {
                        appState.openLink(link, useCustomTabs, usePrivateTabs)
                    }
                },
            )
            PopupMenuItem(
                text = stringResource(R.string.open_link_external),
                icon = Icons.Outlined.OpenInBrowser,
                onClick = {
                    onDismissRequest()
                    appState.openLinkRaw(link, useCustomTabs, usePrivateTabs)
                },
            )

            Divider()

            PopupMenuItem(
                text = stringResource(R.string.post_listing_copy_link),
                icon = Icons.Outlined.Link,
                onClick = {
                    onDismissRequest()
                    localClipboardManager.setText(AnnotatedString(link))
                    Toast.makeText(
                        ctx,
                        ctx.getString(R.string.post_listing_link_copied),
                        Toast.LENGTH_SHORT,
                    ).show()
                },
            )

            PopupMenuItem(
                text = stringResource(R.string.post_listing_share_link),
                icon = Icons.Outlined.Share,
                onClick = {
                    onDismissRequest()
                    shareLink(link, ctx)
                },
            )

            when (mediaType) {
                PostType.Image -> {
                    Divider()
                    PopupMenuItem(
                        text = stringResource(R.string.share_image),
                        icon = Icons.Outlined.Share,
                        onClick = {
                            onDismissRequest()
                            shareMedia(appState.coroutineScope, ctx, link, mediaType)
                        },
                    )
                    PopupMenuItem(
                        text = stringResource(R.string.save_image),
                        icon = Icons.Outlined.Download,
                        onClick = {
                            onDismissRequest()
                            storeMedia(appState.coroutineScope, ctx, link, mediaType)
                        },
                    )
                }

                PostType.Video -> {
                    Divider()
                    PopupMenuItem(
                        text = stringResource(R.string.share_video),
                        icon = Icons.Outlined.Share,
                        onClick = {
                            onDismissRequest()
                            shareMedia(appState.coroutineScope, ctx, link, mediaType)
                        },
                    )
                    PopupMenuItem(
                        text = stringResource(R.string.save_video),
                        icon = Icons.Outlined.Download,
                        onClick = {
                            onDismissRequest()
                            storeMedia(appState.coroutineScope, ctx, link, mediaType)
                        },
                    )
                }

                PostType.Link -> {
                    if (isMedia(link)) {
                        Divider()
                        PopupMenuItem(
                            text = stringResource(R.string.share_media),
                            icon = Icons.Outlined.Share,
                            onClick = {
                                onDismissRequest()
                                shareMedia(appState.coroutineScope, ctx, link, PostType.Link)
                            },
                        )
                        PopupMenuItem(
                            text = stringResource(R.string.save),
                            icon = Icons.Outlined.Download,
                            onClick = {
                                onDismissRequest()
                                storeMedia(appState.coroutineScope, ctx, link, PostType.Link)
                            },
                        )
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
