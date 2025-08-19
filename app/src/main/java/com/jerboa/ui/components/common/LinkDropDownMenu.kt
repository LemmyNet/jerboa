package com.jerboa.ui.components.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.OpenInFull
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jerboa.JerboaAppState
import com.jerboa.PostLinkType
import com.jerboa.R
import com.jerboa.feat.copyImageToClipboard
import com.jerboa.feat.copyTextToClipboard
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
    val ctx = LocalContext.current

    if (link != null) {
        val mediaType = PostLinkType.fromURL(link)

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
                    if (mediaType == PostLinkType.Link) {
                        appState.openLink(link, useCustomTabs, usePrivateTabs)
                    } else {
                        appState.openMediaViewer(link, mediaType)
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

            HorizontalDivider()

            PopupMenuItem(
                text = stringResource(R.string.copy_link),
                icon = Icons.Outlined.Link,
                onClick = {
                    onDismissRequest()
                    copyTextToClipboard(ctx, link, "Link", R.string.link_copied)
                },
            )

            PopupMenuItem(
                text = stringResource(R.string.share_link),
                icon = Icons.Outlined.Share,
                onClick = {
                    onDismissRequest()
                    shareLink(link, ctx)
                },
            )

            when (mediaType) {
                PostLinkType.Image -> {
                    HorizontalDivider()
                    PopupMenuItem(
                        text = stringResource(R.string.copy_image),
                        icon = Icons.Outlined.ContentCopy,
                        onClick = {
                            onDismissRequest()
                            copyImageToClipboard(appState.coroutineScope, ctx, link)
                        },
                    )
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

                PostLinkType.Video -> {
                    HorizontalDivider()
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

                PostLinkType.Link -> {
                    if (isMedia(link)) {
                        HorizontalDivider()
                        PopupMenuItem(
                            text = stringResource(R.string.share_media),
                            icon = Icons.Outlined.Share,
                            onClick = {
                                onDismissRequest()
                                shareMedia(appState.coroutineScope, ctx, link, PostLinkType.Link)
                            },
                        )
                        PopupMenuItem(
                            text = stringResource(R.string.save),
                            icon = Icons.Outlined.Download,
                            onClick = {
                                onDismissRequest()
                                storeMedia(appState.coroutineScope, ctx, link, PostLinkType.Link)
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
