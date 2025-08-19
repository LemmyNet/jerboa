package com.jerboa.ui.components.post.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CancelPresentation
import androidx.compose.material.icons.outlined.CommentsDisabled
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.GppBad
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.jerboa.PostLinkType
import com.jerboa.R
import com.jerboa.api.API.getInstanceOrNull
import com.jerboa.communityNameShown
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.datatypes.PostFeatureData
import com.jerboa.feat.blockCommunity
import com.jerboa.feat.blockPerson
import com.jerboa.feat.copyTextToClipboard
import com.jerboa.feat.getInstanceFromCommunityUrl
import com.jerboa.feat.shareLink
import com.jerboa.feat.shareMedia
import com.jerboa.feat.showBlockInstanceToast
import com.jerboa.isMedia
import com.jerboa.ui.components.common.BanFromCommunityPopupMenuItem
import com.jerboa.ui.components.common.BanPersonPopupMenuItem
import com.jerboa.ui.components.common.PopupMenuItem
import com.jerboa.util.cascade.CascadeCenteredDropdownMenu
import it.vercruysse.lemmyapi.datatypes.BlockCommunity
import it.vercruysse.lemmyapi.datatypes.BlockInstance
import it.vercruysse.lemmyapi.datatypes.BlockPerson
import it.vercruysse.lemmyapi.datatypes.Community
import it.vercruysse.lemmyapi.datatypes.Person
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PostId
import it.vercruysse.lemmyapi.datatypes.PostView
import it.vercruysse.lemmyapi.dto.PostFeatureType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PostOptionsDropdown(
    postView: PostView,
    onDismissRequest: () -> Unit,
    onCommunityClick: (Community) -> Unit,
    onPersonClick: (PersonId) -> Unit,
    onEditPostClick: (PostView) -> Unit,
    onDeletePostClick: (PostView) -> Unit,
    onHidePostClick: (PostView) -> Unit,
    onReportClick: (PostView) -> Unit,
    onRemoveClick: (PostView) -> Unit,
    onBanPersonClick: (person: Person) -> Unit,
    onBanFromCommunityClick: (banData: BanFromCommunityData) -> Unit,
    onLockPostClick: (PostView) -> Unit,
    onFeaturePostClick: (PostFeatureData) -> Unit,
    onViewVotesClick: (PostId) -> Unit,
    onViewSourceClick: () -> Unit,
    isCreator: Boolean,
    canMod: Boolean,
    amMod: Boolean,
    amAdmin: Boolean,
    viewSource: Boolean,
    showViewSource: Boolean,
    scope: CoroutineScope,
) {
    val ctx = LocalContext.current
    val api = getInstanceOrNull()
    val (featureIcon, unFeatureIcon) = Pair(Icons.Outlined.PushPin, Icons.Outlined.CancelPresentation)

    CascadeCenteredDropdownMenu(
        expanded = true,
        onDismissRequest = onDismissRequest,
    ) {
        PopupMenuItem(
            text =
                stringResource(
                    R.string.post_listing_go_to,
                    communityNameShown(postView.community),
                ),
            icon = Icons.Outlined.Forum,
            onClick = {
                onDismissRequest()
                onCommunityClick(postView.community)
            },
        )

        PopupMenuItem(
            text = stringResource(R.string.post_listing_go_to, postView.creator.name),
            icon = Icons.Outlined.Person,
            onClick = {
                onDismissRequest()
                onPersonClick(postView.creator.id)
            },
        )

        PopupMenuItem(
            text = stringResource(R.string.copy),
            icon = Icons.Outlined.CopyAll,
        ) {
            postView.post.url?.also {
                PopupMenuItem(
                    text = stringResource(R.string.copy_link),
                    icon = Icons.Outlined.Link,
                    onClick = {
                        onDismissRequest()
                        copyTextToClipboard(ctx, it, "Link", R.string.link_copied)
                    },
                )
            }

            PopupMenuItem(
                text = stringResource(R.string.post_listing_copy_permalink),
                icon = Icons.Outlined.Link,
                onClick = {
                    onDismissRequest()
                    val permalink = postView.post.ap_id
                    copyTextToClipboard(ctx, permalink, "Permalink", R.string.permalink_copied)
                },
            )

            postView.post.thumbnail_url?.also {
                PopupMenuItem(
                    text = stringResource(R.string.post_listing_copy_thumbnail_link),
                    icon = Icons.Outlined.Link,
                    onClick = {
                        onDismissRequest()
                        copyTextToClipboard(
                            ctx,
                            it,
                            "thumbnail link",
                            R.string.post_listing_thumbnail_link_copied,
                        )
                    },
                )
            }

            postView.post.embed_description?.also {
                PopupMenuItem(
                    text = stringResource(R.string.post_listing_copy_title),
                    icon = Icons.Outlined.ContentCopy,
                    onClick = {
                        onDismissRequest()
                        copyTextToClipboard(
                            ctx,
                            it,
                            "post title",
                            R.string.post_listing_title_copied,
                        )
                    },
                )
            }

            postView.post.name.also {
                PopupMenuItem(
                    text = stringResource(R.string.post_listing_copy_name),
                    icon = Icons.Outlined.ContentCopy,
                    onClick = {
                        onDismissRequest()
                        copyTextToClipboard(ctx, postView.post.name, "post name", R.string.post_listing_name_copied)
                    },
                )
            }

            postView.post.body?.also {
                PopupMenuItem(
                    text = stringResource(R.string.post_listing_copy_text),
                    icon = Icons.Outlined.ContentCopy,
                    onClick = {
                        onDismissRequest()
                        copyTextToClipboard(ctx, it, "post text", R.string.post_listing_text_copied)
                    },
                )
            }
        }

        PopupMenuItem(
            text = stringResource(R.string.share),
            icon = Icons.Outlined.Share,
        ) {
            postView.post.url?.also { url ->
                PopupMenuItem(
                    text = stringResource(R.string.share_link),
                    icon = Icons.Outlined.Share,
                    onClick = {
                        onDismissRequest()
                        shareLink(url, ctx)
                    },
                )

                when (val mediaType = PostLinkType.fromURL(url)) {
                    PostLinkType.Image ->
                        PopupMenuItem(
                            text = stringResource(R.string.share_image),
                            icon = Icons.Outlined.Share,
                            onClick = {
                                onDismissRequest()
                                shareMedia(scope, ctx, url, mediaType)
                            },
                        )

                    PostLinkType.Video ->
                        PopupMenuItem(
                            text = stringResource(R.string.share_video),
                            icon = Icons.Outlined.Share,
                            onClick = {
                                onDismissRequest()
                                shareMedia(scope, ctx, url, mediaType)
                            },
                        )

                    PostLinkType.Link ->
                        if (isMedia(url)) {
                            PopupMenuItem(
                                text = stringResource(R.string.share_media),
                                icon = Icons.Outlined.Share,
                                onClick = {
                                    onDismissRequest()
                                    shareMedia(scope, ctx, url, PostLinkType.Link)
                                },
                            )
                        }
                }
            }

            PopupMenuItem(
                text = stringResource(R.string.share_post),
                icon = Icons.Outlined.Share,
                onClick = {
                    onDismissRequest()
                    shareLink(postView.post.ap_id, ctx)
                },
            )
        }

        // Hide / unhide post
        if (api != null && api.FF.hidePost()) {
            if (postView.hidden) {
                PopupMenuItem(
                    text = stringResource(R.string.unhide_post),
                    icon = Icons.Outlined.Visibility,
                    onClick = {
                        onDismissRequest()
                        onHidePostClick(postView)
                    },
                )
            } else {
                PopupMenuItem(
                    text = stringResource(R.string.hide_post),
                    icon = Icons.Outlined.VisibilityOff,
                    onClick = {
                        onDismissRequest()
                        onHidePostClick(postView)
                    },
                )
            }
        }

        // Only visible from PostActivity
        if (showViewSource) {
            postView.post.body?.also {
                PopupMenuItem(
                    text =
                        if (viewSource) {
                            stringResource(R.string.view_original)
                        } else {
                            stringResource(R.string.view_source)
                        },
                    icon = Icons.Outlined.Description,
                    onClick = {
                        onDismissRequest()
                        onViewSourceClick()
                    },
                )
            }
        }

        HorizontalDivider()

        if (isCreator) {
            PopupMenuItem(
                text = stringResource(R.string.edit),
                icon = Icons.Outlined.Edit,
                onClick = {
                    onDismissRequest()
                    onEditPostClick(postView)
                },
            )

            if (postView.post.deleted) {
                PopupMenuItem(
                    text = stringResource(R.string.restore),
                    icon = Icons.Outlined.Restore,
                    onClick = {
                        onDismissRequest()
                        onDeletePostClick(postView)
                    },
                )
            } else {
                PopupMenuItem(
                    text = stringResource(R.string.delete),
                    icon = Icons.Outlined.Delete,
                    onClick = {
                        onDismissRequest()
                        onDeletePostClick(postView)
                    },
                )
            }
        } else {
            PopupMenuItem(
                // Reuse existing translations
                text = stringResource(R.string.block_person, ""),
                icon = Icons.Outlined.Block,
            ) {
                PopupMenuItem(
                    text = stringResource(R.string.block_person, postView.creator.name),
                    icon = Icons.Outlined.Block,
                    onClick = {
                        onDismissRequest()
                        blockPerson(
                            scope,
                            BlockPerson(
                                person_id = postView.creator.id,
                                block = true,
                            ),
                            ctx,
                        )
                    },
                )

                PopupMenuItem(
                    text = stringResource(R.string.block_person, postView.community.name),
                    icon = Icons.Outlined.Block,
                    onClick = {
                        onDismissRequest()
                        blockCommunity(
                            scope,
                            BlockCommunity(
                                community_id = postView.community.id,
                                block = true,
                            ),
                            ctx,
                        )
                    },
                )

                if (api != null && api.FF.instanceBlock()) {
                    val instance = getInstanceFromCommunityUrl(postView.community.actor_id)
                    PopupMenuItem(
                        text = stringResource(R.string.block_person, instance),
                        icon = Icons.Outlined.Block,
                        onClick = {
                            onDismissRequest()
                            scope.launch(Dispatchers.IO) {
                                val resp =
                                    api.blockInstance(
                                        BlockInstance(
                                            postView.community.instance_id,
                                            true,
                                        ),
                                    )
                                withContext(Dispatchers.Main) {
                                    showBlockInstanceToast(resp, instance, ctx)
                                }
                            }
                        },
                    )
                }
            }

            PopupMenuItem(
                text = stringResource(R.string.post_listing_report_post),
                icon = Icons.Outlined.Flag,
                onClick = {
                    onDismissRequest()
                    onReportClick(postView)
                },
            )
        }

        // The moderation subfield
        if (amMod || amAdmin) {
            PopupMenuItem(
                text = stringResource(R.string.moderation),
                icon = Icons.Outlined.Shield,
            ) {
                // Moddable items limited to mods below you
                if (canMod) {
                    val (removeText, removeIcon) =
                        if (postView.post.removed) {
                            Pair(stringResource(R.string.restore_post), Icons.Outlined.Restore)
                        } else {
                            Pair(stringResource(R.string.remove_post), Icons.Outlined.GppBad)
                        }

                    PopupMenuItem(
                        text = removeText,
                        icon = removeIcon,
                        onClick = {
                            onDismissRequest()
                            onRemoveClick(postView)
                        },
                    )
                    if (amAdmin) {
                        BanPersonPopupMenuItem(postView.creator, onDismissRequest, onBanPersonClick)
                    }

                    // Only show ban from community button if its a local community
                    if (postView.community.local) {
                        BanFromCommunityPopupMenuItem(
                            BanFromCommunityData(
                                person = postView.creator,
                                community = postView.community,
                                banned = postView.creator_banned_from_community,
                            ),
                            onDismissRequest,
                            onBanFromCommunityClick,
                        )
                    }
                }

                // You can do these actions on mods above you

                // These are all amMod || amAdmin
                if (amAdmin && getInstanceOrNull()?.FF?.listAdminVotes() == true) {
                    PopupMenuItem(
                        text = stringResource(R.string.view_votes),
                        icon = ImageVector.vectorResource(R.drawable.up_filled),
                        onClick = {
                            onDismissRequest()
                            onViewVotesClick(postView.post.id)
                        },
                    )
                }

                val (lockText, lockIcon) =
                    if (postView.post.locked) {
                        Pair(stringResource(R.string.unlock_post), Icons.Outlined.LockOpen)
                    } else {
                        Pair(stringResource(R.string.lock_post), Icons.Outlined.CommentsDisabled)
                    }

                PopupMenuItem(
                    text = lockText,
                    icon = lockIcon,
                    onClick = {
                        onDismissRequest()
                        onLockPostClick(postView)
                    },
                )

                val (featureInCommunityText, featureIconUsed) =
                    if (postView.post.featured_community) {
                        Pair(stringResource(R.string.unfeature_in_community), unFeatureIcon)
                    } else {
                        Pair(stringResource(R.string.feature_in_community), featureIcon)
                    }

                PopupMenuItem(
                    text = featureInCommunityText,
                    icon = featureIconUsed,
                    onClick = {
                        onDismissRequest()
                        onFeaturePostClick(
                            PostFeatureData(
                                post = postView.post,
                                featured = postView.post.featured_community,
                                type = PostFeatureType.Community,
                            ),
                        )
                    },
                )

                if (amAdmin) {
                    val (featureInLocalText, featureLocalIconUsed) =
                        if (postView.post.featured_local) {
                            Pair(stringResource(R.string.unfeature_in_local), unFeatureIcon)
                        } else {
                            Pair(stringResource(R.string.feature_in_local), featureIcon)
                        }

                    PopupMenuItem(
                        text = featureInLocalText,
                        icon = featureLocalIconUsed,
                        onClick = {
                            onDismissRequest()
                            onFeaturePostClick(
                                PostFeatureData(
                                    post = postView.post,
                                    featured = postView.post.featured_local,
                                    type = PostFeatureType.Local,
                                ),
                            )
                        },
                    )
                }
            }
        }
    }
}
